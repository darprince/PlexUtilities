package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.DOWNLOADS_DIRECTORY;
import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.FILES_WE_WANT;
import static com.dprince.plex.settings.PlexSettings.FOLDERS_FILE_LOCATION;
import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;
import static com.dprince.plex.settings.PlexSettings.VIDEO_FILES;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.shared.MetaDataFormatter;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.types.TvShow;

public class TvFileUtilities {
    private static final Logger LOG = Logging.getLogger(TvFileUtilities.class);

    // TODO: This is from the old version, should be updated
    public static void createFoldersFile() {
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (tvShowFoldersFile.exists()) {
            return;
        }

        String[][] titles = null;

        if (!tvShowFoldersFile.exists()) {
            try {
                tvShowFoldersFile.createNewFile();
            } catch (final IOException e) {
                System.out.println("Creating folders.txt file failed");
                System.out.println(e.toString());
            }
        }
        titles = TvFileUtilities.setTitlesFromDirectories();

        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new FileWriter(tvShowFoldersFile));

            outputWriter.write(String.valueOf(titles.length));
            outputWriter.newLine();

            for (final String[] folderLine : titles) {
                outputWriter.write(folderLine[0] + "^^^" + folderLine[1]);
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputWriter.close();
            LOG.info("Folders file created");
        } catch (final IOException e) {
            LOG.info("Writing to folders file failed.");
            System.out.println(e.toString());
        }
    }

    /**
     * Deletes the folder that holds all the show names.
     */
    public static void deleteFoldersFile() {
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (tvShowFoldersFile.exists()) {
            LOG.info("Folders file deleted: " + tvShowFoldersFile.delete());
        }
    }

    // TODO: also from old version
    public static String[][] setTitlesFromDirectories() {
        final List<File> finalFileList = new ArrayList<>();
        try {
            LOG.info("Reading Downloads directories");
            File file = null;
            File[] files = null;

            for (final String dir : DESKTOP_SHARED_DIRECTORIES) {
                file = new File(PLEX_PREFIX + dir);
                files = file.listFiles();
                for (final File tempFile : files) {
                    finalFileList.add(tempFile);
                }
            }
        } catch (final Exception e) {
            LOG.info("Setting titles from directories failed");
        }

        final String[][] allFiles = new String[finalFileList.size()][2];
        String tempString = null;
        String properShowName = null;

        for (int i = 0; i < finalFileList.size(); i++) {
            tempString = finalFileList.get(i).toString();
            properShowName = tempString.substring(tempString.lastIndexOf("\\") + 1,
                    tempString.length());
            allFiles[i][0] = properShowName;

            allFiles[i][1] = properShowName.toLowerCase().replaceAll("'", "").replaceAll("\\.", "")
                    .replaceAll(",", "").replaceAll("marvels ", " ").replaceAll(" the ", " ")
                    .replaceAll("the ", "").replaceAll(" on ", " ").replaceAll(" a ", " ")
                    .replaceAll(" of ", " ").trim();
        }

        return allFiles;
    }

    public static String[][] getTitlesArray() {
        String[][] titles = null;

        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (!tvShowFoldersFile.exists()) {
            System.out.println("Creating folders.txt file at: " + tvShowFoldersFile);
            createFoldersFile();
        }

        try {
            final BufferedReader br = Files
                    .newBufferedReader(Paths.get(tvShowFoldersFile.getCanonicalPath()));
            final int fileLength = Integer.parseInt(br.readLine());
            titles = new String[fileLength][2];
            for (int i = 0; i < fileLength; i++) {
                final String line = br.readLine();
                final String[] splitLine = line.split("\\^\\^\\^");
                titles[i][0] = splitLine[0];
                titles[i][1] = splitLine[1];
            }
        } catch (final IOException e) {
            LOG.info("Reading folders.txt failed");
            System.out.println(e.toString());
        }
        return titles;
    }

    /**
     * Creates the next season folder that does not exist for a show.
     *
     * @param formattedShowName
     * @return true if folder is created, false otherwise.
     */
    public static boolean createNewSeasonFolder(@NonNull final TvShow tvShow) {
        LOG.info("Create season folder called");
        final String formattedShowName = tvShow.getFormattedShowName();
        final String showDriveLocation = TvUtilities.getShowDriveLocation(formattedShowName);

        final File file = new File(PLEX_PREFIX + showDriveLocation + "/" + formattedShowName);

        if (file.exists()) {
            if (tvShow.getSeasonNumber().equals("00")) {
                final File specialsFolder = new File(file.getPath() + "\\Specials");
                LOG.info("Creating \"Specials\" folder");
                return specialsFolder.mkdir();
            } else {
                int season = 1;
                final String seasonFolderPrefix = file.getPath() + "\\Season 0";
                File seasonFolder = new File(seasonFolderPrefix + season);
                while (seasonFolder.exists()) {
                    season++;
                    seasonFolder = new File(seasonFolderPrefix + season);
                }
                LOG.info("Creating \"Season 0{}\"  folder", season);
                return seasonFolder.mkdir();
            }
        }
        return false;
    }

    /**
     * Creates a new season folder in the current folder.
     *
     * @param filepath
     * @return true if folder is created, false otherwise.
     */
    public static boolean createNewSeasonFolderFromDir(String filepath) {
        final File file = new File(filepath);
        if (file.exists()) {
            int season = 1;
            final String seasonFolderPrefix = file.getPath() + "\\Season 0";
            File seasonFolder = new File(seasonFolderPrefix + season);
            while (seasonFolder.exists()) {
                season++;
                seasonFolder = new File(seasonFolderPrefix + season);
            }
            return seasonFolder.mkdir();
        }
        return false;
    }

    /**
     * Edits the meta data of a mkv file.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void runMKVEditorForTvShow(@NonNull final String filepath, String episodeTitle) {

        if (episodeTitle == null || episodeTitle.equals("null")) {
            episodeTitle = "";
        }

        final String command = MKVPROPEDIT_LOCATION + " \"" + filepath + "\" --set title=\""
                + episodeTitle
                + "\" --edit track:a1 --set name=\"\" --edit track:v1 --set name=\"\"";

        LOG.info("Mkv metadata edit command: " + command);

        try {
            Runtime.getRuntime().exec(command);
        } catch (final Exception e) {
            LOG.error("Failed to edit mkv metadata", e);
        }
    }

    /**
     * Edits the metadata of a MP4 file.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void runMP4EditorForTvShow(String filepath, String episodeTitle) {
        try {
            if (episodeTitle != null) {
                MetaDataFormatter.writeRandomMetadata(filepath, episodeTitle);
            } else {
                MetaDataFormatter.writeRandomMetadata(filepath, "");
            }
        } catch (final IOException e) {
            LOG.error("Failed to call MetaDataFormatter", e);
        }
    }

    public static String createShowFolder(String rawTvShowName) throws IOException {
        final Object result = JOptionPane.showInputDialog(new JFrame(), "Add this show to Plex?",
                WordUtils.capitalize(rawTvShowName));
        String newSubFolderName = null;

        // TODO: add numerical to regex
        String resultWithoutPre = result.toString();
        if (result.toString().startsWith("The ")) {
            resultWithoutPre = resultWithoutPre.substring(4, resultWithoutPre.length()).trim();
        }
        final String firstLetter = resultWithoutPre.toLowerCase().substring(0, 1);
        if (firstLetter.matches("[a-e]")) {
            newSubFolderName = "tv a-e\\";
        } else if (firstLetter.matches("[f-l]")) {
            newSubFolderName = "tv f-l\\";
        } else if (firstLetter.matches("[m-s]")) {
            newSubFolderName = "tv m-s\\";
        } else if (firstLetter.matches("[t-z]")) {
            newSubFolderName = "tv t-z\\";
        }

        final String resultString = result.toString();
        final File folderToCreate = new File(PLEX_PREFIX + newSubFolderName + resultString);
        final File seasonFolderToCreate = new File(folderToCreate.toString() + "\\Season 01");

        // test if folder already exists
        if (folderToCreate.exists()) {
            return resultString;
        } else {
            folderToCreate.mkdir();
            seasonFolderToCreate.mkdir();
        }

        // TvFileUtilities.deleteFoldersFile();
        // TvFileUtilities.createFoldersFile();
        TheTvDbLookup.createShowDataJSONForShow(folderToCreate);
        return resultString;
    }

    /**
     * Determines if the season folder for the file exists.
     *
     * @param filepath
     * @return true if season folder exists, false otherwise.
     */
    public static boolean seasonFolderExists(String filepath) {
        final File file = new File(filepath);
        final File seasonFolder = new File(file.getParent());

        if (seasonFolder.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Takes a filepath, seasonNumber, episodeNumber and determines if that
     * episode already exisits.
     *
     * @param filepath
     * @param seasonNumber
     * @param episodeNumber
     * @return true if episode exisits, false otherwise.
     */
    public static String episodeExists(@NonNull final String filepath,
            @NonNull final String seasonNumber, @NonNull final String episodeNumber) {
        final File file = new File(filepath);
        final File folder = new File(file.getParent());

        if (folder.exists()) {
            final String seasonEpisode = "S" + seasonNumber + "E" + episodeNumber;

            for (final File episode : folder.listFiles()) {
                if (episode.getName().contains(seasonEpisode)) {
                    return episode.getName();
                }
            }
        }
        return null;
    }

    /**
     * Extracts all video files from Completed sub-directories to Completed that
     * are smaller than 700 MB and not called rarbg.com.mp4 or rarbg.com.avi
     *
     * @throws IOException
     */
    public static void extractTvFiles() throws IOException {
        final File folder = new File(DOWNLOADS_DIRECTORY);

        for (final File showFolder : folder.listFiles()) {
            if (showFolder.isDirectory()) {
                for (final File showFile : showFolder.listFiles()) {
                    if (CommonUtilities.getExtension(showFile.getName()).matches(VIDEO_FILES)
                            && !showFile.getName().toLowerCase().matches(FILES_TO_IGNORE)
                            && showFile.length() < 700000000) {

                        if (!CommonUtilities.renameFile(showFile.toString(),
                                folder.toString() + "\\" + showFile.getName())) {
                            LOG.info("Failed to move file {}", showFile.getName());
                            continue;
                        }
                    } else {
                        LOG.info("Not Moving: " + showFile.getName());
                    }
                }

                if (!showFolderContainsVideoFile(showFolder)) {
                    for (final File file : showFolder.listFiles()) {
                        if (file.isDirectory()) {
                            for (final File file2 : file.listFiles()) {
                                CommonUtilities.recycle(file2.toString());
                            }
                        }
                        CommonUtilities.recycle(file.toString());
                    }
                    CommonUtilities.recycle(showFolder.toString());
                }
            }
        }
    }

    /**
     * Determines if a folder contains a video file that we care about. ie. not
     * a rarbg.mkv
     *
     * @param showFolder
     * @return true if folder contains file, false otherwise
     */
    private static boolean showFolderContainsVideoFile(File showFolder) {
        for (final File showFile : showFolder.listFiles()) {
            if (CommonUtilities.getExtension(showFile.getName()).matches(FILES_WE_WANT)
                    && !showFile.getName().toLowerCase().matches(FILES_TO_IGNORE)) {
                return true;
            }
        }
        return false;
    }
}
