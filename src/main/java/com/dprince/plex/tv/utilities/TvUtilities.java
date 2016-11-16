package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;
import static com.dprince.plex.settings.PlexSettings.VIDEO_EXTENSIONS;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.types.TvShow;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TvUtilities {

    private static final Logger LOG = Logging.getLogger(TvUtilities.class);

    private static final String REGEX = "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})[\\.-]{1}).*(mkv|mp4|avi|mpg){1}";
    private static final String REGEX_FORMATTED_FILENAME = "(^[^-]*)[ -]{3}[sS]{1}([0-9]{2})[eE]{1}([0-9]{2}).*(mkv|mp4|avi|mpg|m4v){1}";

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

        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(filename);

        final Pattern patternFormatted = Pattern.compile(REGEX_FORMATTED_FILENAME);
        final Matcher matcherFormatted = patternFormatted.matcher(filename);

        TvShow tvShow = null;
        if (matcher.find()) {
            tvShow = matcherOneFound(matcher, filename, originalFilepath);
        } else if (matcherFormatted.find()) {
            tvShow = matcherTwoFound(matcherFormatted, filename, originalFilepath);
        } else {
            LOG.error("Failed to parse filename {}", originalFilepath);
            return null;
        }

        return tvShow;
    }

    private static TvShow matcherOneFound(Matcher matcher, String filename,
            String orginalFilepath) {
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        LOG.info("Matcher[1] found for {}", filename);
        rawShowName = matcher.group(1).replaceAll("\\.", " ").toLowerCase().trim();
        LOG.info(rawShowName);
        if (matcher.group(3) != null) {
            seasonNumber = CommonUtilities.padString(matcher.group(3));
            episodeNumber = CommonUtilities.padString(matcher.group(4));
        } else if (matcher.group(5) != null) {
            seasonNumber = CommonUtilities.padString(matcher.group(5));
            episodeNumber = CommonUtilities.padString(matcher.group(6));
        } else if (matcher.group(8) != null) {
            seasonNumber = CommonUtilities.padString(matcher.group(8));
            episodeNumber = CommonUtilities.padString(matcher.group(9));
        } else {
            seasonNumber = "01";
            episodeNumber = CommonUtilities.padString(matcher.group(7));
        }
        extension = matcher.group(10);
        LOG.info("RawShowName: {}", rawShowName);

        final TvShow tvShow = createTvShow(orginalFilepath, filename, rawShowName, seasonNumber,
                episodeNumber, extension);

        return tvShow;
    }

    private static TvShow matcherTwoFound(Matcher matcherFormatted, String filename,
            String orginalFilepath) {
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        LOG.info("Matcher[2] found for {}", filename);
        rawShowName = matcherFormatted.group(1);
        seasonNumber = CommonUtilities.padString(matcherFormatted.group(2));
        episodeNumber = CommonUtilities.padString(matcherFormatted.group(3));
        extension = matcherFormatted.group(4);
        LOG.info("RawShowName: {}", rawShowName);

        final TvShow tvShow = createTvShow(orginalFilepath, filename, rawShowName, seasonNumber,
                episodeNumber, extension);

        return tvShow;
    }

    /**
     * Creates a tvShow object from data retrieved from the parser.
     * 
     * @param originalFilepath
     * @param filename
     * @param rawShowName
     * @param seasonNumber
     * @param episodeNumber
     * @param extension
     * @return a {@link TvShow}
     */
    private static TvShow createTvShow(String originalFilepath, String filename, String rawShowName,
            String seasonNumber, String episodeNumber, String extension) {
        TvShow tvShow = null;
        try {
            final String formattedShowName = formatRawShowName(rawShowName);
            LOG.info("Formatted filename from parser: " + formattedShowName);
            if (formattedShowName == null) {
                LOG.error("Failed to get formatted show name in parser");
            }
            final String episodeTitle = getEpisodeTitle(formattedShowName, seasonNumber,
                    episodeNumber);
            if (episodeTitle == null) {
                LOG.error("Failed to get episode title in parser");
            }
            final String formattedFileName = buildFileName(formattedShowName, seasonNumber,
                    episodeNumber, episodeTitle, extension);
            if (formattedFileName == null) {
                LOG.error("Failed to get formatted file name in parser");
            }
            final String destinationFilepath = buildDestinationFilepath(formattedShowName,
                    formattedFileName, seasonNumber);
            if (destinationFilepath == null) {
                LOG.error("Failed to get destination filepath in parser");
            }

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
     * Queries the shows folder data for the show title. If showCorrectID is
     * false, the TvDB will be queried.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @return the episode title.
     */
    private static String getEpisodeTitle(String formattedShowName, String seasonNumber,
            String episodeNumber) {
        try {
            final ShowFolderData showFolderData = getShowFolderData(formattedShowName);
            if (showFolderData.getCorrectShowID()) {
                for (final SeasonData seasonData : showFolderData.getSeasonData()) {
                    if (seasonData.getSeasonNumber() == Integer.parseInt(seasonNumber)) {
                        for (final EpisodeData episodeData : seasonData.getEpisodeList()) {
                            if (episodeData.getAiredEpisodeNumber() == Integer
                                    .parseInt(episodeNumber)) {
                                LOG.info("Returning episode title from showFolderData");
                                return episodeData.getEpisodeName();
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            return getEpisodeTitleFromTvDB(formattedShowName, seasonNumber, episodeNumber);
        }
        return null;
    }

    /**
     * Builds a properly formatted FileName.
     *
     * @param formattedShowName
     * @param seasonNumber
     *            assumed to be zero padded
     * @param episodeNumber
     *            assumed to be zero padded
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

    /**
     * Attempts to match the rawTvShowName to either an awkwardShowName,
     * PlexFolderName or a title from the titles file.
     *
     * @param rawTvShowName
     * @return The formatted show name
     */
    static String formatRawShowName(String rawTvShowName) {
        String toReturn = null;

        toReturn = matchFromFolderName(rawTvShowName);
        if (toReturn != null) {
            return toReturn;
        }

        toReturn = matchAwkwardTvShow(rawTvShowName);
        if (toReturn != null) {
            return toReturn;
        }

        final String[][] titles = TvFileUtilities.getTitlesArray();

        String[] titleFromFileArray = null;
        int stringMatches = 0;
        int toReturnMatches = 0;

        if (toReturn == null) {
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
        }

        if (toReturn == null) {
            String formattedShowName = null;
            try {
                formattedShowName = TvFileUtilities.createShowFolder(rawTvShowName);
            } catch (final IOException e) {
                LOG.error("Failed to get formated show name", e);
            }
            return formattedShowName;
        }
        return toReturn;
    }

    /**
     * Compares the rawFilename to the list of folder names and returns the
     * formatted showname if there is a match.
     *
     * @param rawTvShowName
     * @return The formatted tv show name if there is a match, null otherwise.
     */
    private static String matchFromFolderName(String rawTvShowName) {
        for (final String sharedDirectory : DESKTOP_SHARED_DIRECTORIES) {
            final File[] listFiles = new File(PLEX_PREFIX + sharedDirectory).listFiles();

            for (final File showFolder : listFiles) {
                String folderSubstring = showFolder.getName();
                if (showFolder.getName().contains("(")) {
                    folderSubstring = showFolder.getName()
                            .substring(0, showFolder.getName().indexOf("(")).trim();
                }
                if (rawTvShowName.contains("(")) {
                    rawTvShowName = rawTvShowName.substring(0, rawTvShowName.indexOf("(")).trim();
                }
                if (rawTvShowName.equalsIgnoreCase(folderSubstring)) {
                    LOG.info("Matched folderName \"{}\", {}", showFolder.getName(), rawTvShowName);
                    return showFolder.getName();
                }
            }
        }
        return null;
    }

    /**
     * Compares the rawFilename to the awkwardTvShow and returns the formatted
     * showname if there is a match.
     *
     * @param rawTvShowName
     * @return The formatted tv show name if there is a match, null otherwise.
     */
    public static String matchAwkwardTvShow(@NonNull final String rawTvShowName) {
        for (final AwkwardTvShows awkwardTvShows : AwkwardTvShows.values()) {
            if (rawTvShowName.equalsIgnoreCase(awkwardTvShows.match)) {
                return awkwardTvShows.replacement;
            }
        }
        return null;
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
        String showID = getShowIDFromJson(formattedShowName);
        if (showID == null) {
            showID = TheTvDbLookup.getShowID(formattedShowName);
            LOG.info("Failed to receive showID from JSON, received from the TvDB instead");
        }
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
     * Reads the showID from a ShowFolderData object.
     *
     * @param formattedShowName
     * @return The show's TvDB ID.
     */
    public static String getShowIDFromJson(@NonNull final String formattedShowName) {
        String showID = null;
        final ShowFolderData showFolderData = getShowFolderData(formattedShowName);
        if (showFolderData == null) {
            LOG.info("Failed to read showID from ShowFolderData");
            return TheTvDbLookup.getShowID(formattedShowName);
        } else {
            showID = showFolderData.getShowData().getId();
        }
        return showID;
    }

    /**
     * Reads in a shows folder data and returns a ShowFolderData object.
     *
     * @param formattedShowName
     * @return a{@link ShowFolderData} object.
     */
    public static ShowFolderData getShowFolderData(@NonNull final String formattedShowName) {
        final String showDriveLocation = getShowDriveLocation(formattedShowName);
        final String showDataFile = PlexSettings.PLEX_PREFIX + "/" + showDriveLocation + "/"
                + formattedShowName + "/showData.json";

        if (new File(showDataFile).exists()) {
            try {
                final String jsonFileData = new String(Files.readAllBytes(Paths.get(showDataFile)));
                final ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(jsonFileData, ShowFolderData.class);
            } catch (final IOException e) {
                LOG.info("Failed to read showFolderData {}", e.getMessage());
                return null;
            }
        } else {
            // TODO: create showFolderData file.
        }
        return null;
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
        final String episodeExists = TvFileUtilities.episodeExists(tvShow.getDestinationFilepath(),
                tvShow.getSeasonNumber(), tvShow.getEpisodeNumber());
        if (episodeExists != null) {
            final int result = JOptionPane.showConfirmDialog((Component) null,
                    "File \"" + episodeExists + "\" exists,\nWould you like to delete "
                            + tvShow.getFormattedFileName() + "?",
                    "File Exists", JOptionPane.OK_CANCEL_OPTION);
            if (result == 0) {// OK, delete file. (0)
                LOG.info("Deleting file {}", tvShow.getFormattedFileName());
                CommonUtilities.recycle(tvShow.getOriginalFilepath());
                System.exit(0);
            } else { // cancel, dont delete file. (2)
                System.exit(0);
            }
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
    // TODO: need to get showID's correct
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
                        LOG.info("Adding folder {} to deleteFolderList", showFile.getParentFile());
                        foldersToDelete.add(showFile.getParentFile());
                    } else {
                        LOG.error("Failed to move {} to {}", tvShow.getOriginalFilepath(),
                                tvShow.getDestinationFilepath());
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
