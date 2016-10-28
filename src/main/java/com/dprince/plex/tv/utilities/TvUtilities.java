package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;
import static com.dprince.plex.settings.PlexSettings.VIDEO_EXTENSIONS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.types.TvShow;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TvUtilities {

    private static final Logger LOG = Logging.getLogger(TvUtilities.class);

    private static final String TWO_DECIMALS = "%02d";
    private static final String REGEX = "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})\\.).*(mkv|mp4|avi|mpg){1}";
    private static final String REGEX_FORMATTED_FILENAME = "(^[^-]*)[ -]{3}[sS]{1}([0-9]{2})[eE]{1}([0-9]{2}).*(mkv|mp4|avi|mpg){1}";

    /**
     * Takes the show's filepath and determines the rawTvShowName, seasonNumber,
     * episodeNumber, extension, formattedShowName, episodeTitle,
     * formattedFileName, destinationFilepath.
     *
     * @param originalFilepath
     * @return {@link TvShow}
     */
    public static TvShow parseFileName(@NonNull final String originalFilepath) {
        final String filename = new File(originalFilepath).getName();
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(filename);

        final Pattern patternFormatted = Pattern.compile(REGEX_FORMATTED_FILENAME);
        final Matcher matcherFormatted = patternFormatted.matcher(filename);

        if (matcher.find()) {
            LOG.info("Matcher found for {}", filename);
            rawShowName = matcher.group(1).replaceAll("\\.", " ").toLowerCase().trim();

            if (matcher.group(3) != null) {
                seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(3)));
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(4)));
            } else if (matcher.group(5) != null) {
                seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(5)));
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(6)));
            } else if (matcher.group(8) != null) {
                seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(8)));
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(9)));
            } else {
                seasonNumber = "01";
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(7)));
            }
            extension = matcher.group(10);

        } else if (matcherFormatted.find()) {
            rawShowName = matcherFormatted.group(1);
            seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcherFormatted.group(2)));
            episodeNumber = String.format(TWO_DECIMALS,
                    Integer.parseInt(matcherFormatted.group(3)));
            extension = matcherFormatted.group(4);
        } else {
            LOG.error("Failed to parse filename {}", originalFilepath);
            return null;
        }

        TvShow tvShow = null;
        try {
            final String formattedShowName = formatRawShowName(rawShowName);
            final String episodeTitle = getEpisodeTitleFromTvDB(formattedShowName, seasonNumber,
                    episodeNumber);
            final String formattedFileName = buildFileName(formattedShowName, seasonNumber,
                    episodeNumber, episodeTitle, extension);
            final String destinationFilepath = buildDestinationFilepath(formattedShowName,
                    formattedFileName, seasonNumber);

            tvShow = TvShow.builder().setDestinationFilepath(destinationFilepath)
                    .setEpisodeNumber(episodeNumber).setEpisodeTitle(episodeTitle)
                    .setExtension(extension).setFormattedFileName(formattedFileName)
                    .setFormattedShowName(formattedShowName).setOriginalFilepath(originalFilepath)
                    .setRawShowName(rawShowName).setSeasonNumber(seasonNumber).build();

            LOG.info(tvShow.toString());
        } catch (final Exception e) {
            LOG.error("Failed to build TvShow", e);
            return null;
        }

        return tvShow;
    }

    /**
     * Builds a properly formatted FileName.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @param episodeTitle
     * @param extension
     * @return A properly formatted FileName.
     */
    @NonNullByDefault
    public static String buildFileName(@NonNull final String formattedShowName,
            @NonNull final String seasonNumber, @NonNull final String episodeNumber,
            @NonNull final String episodeTitle, @NonNull final String extension) {

        return formattedShowName + " - S" + seasonNumber + "E" + episodeNumber + " - "
                + CommonUtilities.filenameEncode(episodeTitle) + "." + extension;
    }

    private static String formatRawShowName(String rawTvShowName) {

        for (final String sharedDirectory : DESKTOP_SHARED_DIRECTORIES) {
            final File[] listFiles = new File(PLEX_PREFIX + sharedDirectory).listFiles();
            for (final File showFolder : listFiles) {
                if (rawTvShowName.equalsIgnoreCase(showFolder.getName())) {
                    LOG.info("Matched folderName");
                    return showFolder.getName();
                }
            }
        }

        final String[][] titles = TvFileUtilities.getTitlesArray();

        String[] titleFromFileArray = null;
        int stringMatches = 0;
        int toReturnMatches = 0;
        String toReturn = null;

        for (final String title[] : titles) {
            stringMatches = 0;
            final String titleFromFoldersFile = title[1].toLowerCase();
            titleFromFileArray = titleFromFoldersFile.split(" ");
            for (int i = 0; i < titleFromFileArray.length; i++) {
                if (!rawTvShowName.contains(titleFromFileArray[i].toLowerCase())) {
                    break;
                }
                if (i == titleFromFileArray.length - 1) {
                    stringMatches++;
                    if (stringMatches > toReturnMatches) {
                        toReturnMatches = stringMatches;
                        toReturn = title[0].toString();
                    }
                }
                stringMatches++;
            }
        }

        if (toReturn == null) {
            for (final AwkwardTvShows awkwardTvShows : AwkwardTvShows.values()) {
                stringMatches = 0;
                titleFromFileArray = awkwardTvShows.match.toLowerCase().split(" ");
                for (int i = 0; i < titleFromFileArray.length; i++) {
                    if (!rawTvShowName.contains(titleFromFileArray[i].toLowerCase())) {
                        break;
                    }
                    if (i == titleFromFileArray.length - 1) {
                        stringMatches++;
                        if (stringMatches > toReturnMatches) {
                            toReturnMatches = stringMatches;
                            toReturn = awkwardTvShows.replacement;
                        }
                    }
                    stringMatches++;
                }
            }
        }

        if (toReturn == null) {
            String formattedShowName = null;
            try {
                formattedShowName = TvFileUtilities.createShowFolder(rawTvShowName);
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return formattedShowName;
        }
        return toReturn;
    }

    /**
     * Queries the TvDB API for the specified episodes title.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @return The episodes title.
     */
    public static String getEpisodeTitleFromTvDB(String formattedShowName, String seasonNumber,
            String episodeNumber) {
        final String showID = getShowIDFromJson(formattedShowName);
        final String episodeTitle = TheTvDbLookup.getEpisodeTitle(showID, seasonNumber,
                episodeNumber);
        return episodeTitle;
    }

    /**
     * Builds the new filepath on the plex server
     *
     * @param formattedFileName
     * @param seasonNumber
     * @return a properly built filepath on the plex server.
     */
    @NonNullByDefault
    public static String buildDestinationFilepath(@NonNull final String formattedShowName,
            @NonNull final String formattedFileName, @NonNull final String seasonNumber) {

        final String showDriveLocation = getShowDriveLocation(formattedShowName);
        if (seasonNumber.equals("00")) {
            return PLEX_PREFIX + showDriveLocation + "/" + formattedShowName + "/Specials/"
                    + formattedFileName;
        } else {
            return PLEX_PREFIX + showDriveLocation + "/" + formattedShowName + "/Season "
                    + seasonNumber + "/" + formattedFileName;
        }
    }

    /**
     * Determines the drive that the show is located on.
     *
     * @param formattedShowName
     * @return The name of the drive that the show is located on.
     */
    public static String getShowDriveLocation(@NonNull final String formattedShowName) {
        File queriedDrive = null;
        String queryString = null;
        for (final String sharedDrive : DESKTOP_SHARED_DIRECTORIES) {
            queryString = PLEX_PREFIX + sharedDrive + "/" + formattedShowName;
            queriedDrive = new File(queryString);
            if (queriedDrive.exists()) {
                return sharedDrive;
            }
        }
        return null;
    }

    /**
     * Searches for the shows root folder and reads the json file to return the
     * show's TvDB ID.
     *
     * @param formattedShowName
     * @return The show's TvDB ID.
     */
    public static String getShowIDFromJson(@NonNull final String formattedShowName) {
        final String showDriveLocation = getShowDriveLocation(formattedShowName);
        final String showDataFile = PlexSettings.PLEX_PREFIX + "/" + showDriveLocation + "/"
                + formattedShowName + "/showData.json";

        if (new File(showDataFile).exists()) {
            String showID = null;
            try {
                final String jsonFileData = new String(Files.readAllBytes(Paths.get(showDataFile)));
                final ObjectMapper mapper = new ObjectMapper();
                final ShowFolderData showFolderData = mapper.readValue(jsonFileData,
                        ShowFolderData.class);
                showID = showFolderData.getShowData().getId();
            } catch (final IOException e) {
                e.printStackTrace();
            }
            return showID;
        } else {
            return TheTvDbLookup.getShowID(formattedShowName);
        }
    }

    /**
     * Determines which metadata editor to use by grabbing the files extension,
     * then calls the appropriate method.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void editMetaData(@NonNull final String filepath, final String episodeTitle) {
        final String extension = CommonUtilities.getExtension(filepath);

        if (extension.equalsIgnoreCase("mp4")) {
            LOG.info("Setting mp4 metadata with title: {}", episodeTitle);
            TvFileUtilities.runMP4EditorForTvShow(filepath, episodeTitle);
        } else if (extension.equalsIgnoreCase("mkv")) {
            LOG.info("Setting mkv metadata with title: {}", episodeTitle);
            TvFileUtilities.runMKVEditorForTvShow(filepath, episodeTitle);
        }
        return;
    }

    /**
     * Moves TvEpisode file to appropriate folder, if season folder does not
     * exist, the season folder and all season folders below it will be created.
     *
     * @param tvShow
     * @return true if file is moved, false otherwise.
     */
    public static boolean moveEpisodeFile(@NonNull final TvShow tvShow) {
        LOG.info("moveEpisodeFile called");
        if (TvFileUtilities.episodeExists(tvShow.getDestinationFilepath(), tvShow.getSeasonNumber(),
                tvShow.getEpisodeNumber())) {
            // TODO: change to pop up with delete file option
            JOptionPane.showMessageDialog(new JFrame(),
                    "Episode " + tvShow.getFormattedFileName() + " exists");
            System.exit(0);
        }

        boolean seasonFolderCreated = false;
        while (!TvFileUtilities.seasonFolderExists(tvShow.getDestinationFilepath())) {
            seasonFolderCreated = TvFileUtilities.createNewSeasonFolder(tvShow);
        }

        final boolean success = CommonUtilities.renameFile(tvShow.getOriginalFilepath(),
                tvShow.getDestinationFilepath());
        LOG.info("File renamed: " + success);

        if (success) {
            return true;
        }

        return false;
    }

    /**
     * Scans a directory for all video files and attempts to parse them and move
     * them. Once all video files are processed, the files parent folder will be
     * attempted to be deleted.
     *
     * @param directory
     */
    // TODO: stops when episode exists
    // TODO: need to get showID's correct, goldbergs...
    public static Set<File> batchMoveEpisodes(@NonNull final String directory) {
        final Set<File> foldersToDelete = new HashSet<File>();

        for (final File showFolder : new File(directory).listFiles()) {
            LOG.info("Folder {} has {} folders/files", new File(directory).getName(),
                    new File(directory).listFiles().length);
            if (showFolder.isDirectory()) {
                LOG.info("Recursively calling method on {}", showFolder.getName());
                foldersToDelete.addAll(batchMoveEpisodes(showFolder.toString()));
            } else {
                final File showFile = showFolder;
                final TvShow tvShow = TvUtilities.parseFileName(showFile.toString());
                if (tvShow != null) {
                    if (moveEpisodeFile(tvShow)) {
                        LOG.info("Moved {} to {}", tvShow.getOriginalFilepath(),
                                tvShow.getDestinationFilepath());
                        LOG.info("Adding folder {} to deleteFolder", showFile.getParentFile());
                        foldersToDelete.add(showFile.getParentFile());
                    } else {
                        LOG.error("Failed to move {} to {}", tvShow.getOriginalFilepath(),
                                tvShow.getDestinationFilepath());
                        System.exit(0);
                    }
                    editMetaData(tvShow.getDestinationFilepath(), tvShow.getEpisodeTitle());
                } else {
                    LOG.info("TvShow is null {}", showFile.getName());
                }
            }
        }
        return foldersToDelete;
    }

    /**
     * Takes a list of folders and attempts to delete them, if the folder
     * contains a video file that is not rarbg.com, then the folder will be
     * deleted
     *
     * @param foldersToDelete
     */
    public static void deleteEmptyShowFolders(final Set<File> foldersToDelete) {
        boolean deleteFolder = true;
        LOG.info("Starting foldersToDelete(), {} folders", foldersToDelete.size());
        for (final File folder : foldersToDelete) {
            for (final File file : folder.listFiles()) {
                if (CommonUtilities.getExtension(file.toString()).matches(VIDEO_EXTENSIONS)) {
                    final boolean matches = file.getName().toLowerCase().matches(FILES_TO_IGNORE);
                    if (!matches) {
                        LOG.info("Not deleting folder {}, contains {}", folder.getName(),
                                file.getName());
                        deleteFolder = false;
                    }
                }
                if (folder.getName().equals("Completed")) {
                    deleteFolder = false;
                }
            }
            if (deleteFolder) {
                // try {
                LOG.info("Folder would be deleted");
                // FileUtils.deleteDirectory(folder);
                // } catch (final IOException e) {
                // LOG.error("Failed to delete folder {}",
                // folder.toString(),
                // e);
                // }
            } else {
                LOG.info("Folder would not be deleted");
            }
            deleteFolder = true;
        }
    }
}
