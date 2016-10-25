package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.FOLDERS_FILE_LOCATION;
import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.types.TvShow;

public class TvFileUtilities {
    private static final Logger LOG = Logging.getLogger(TvFileUtilities.class);

    public TvFileUtilities() {
    }

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

    public static String getFilenameFromPath(String originalFilepath) {
        String filename;
        if (originalFilepath.contains("/")) {
            filename = originalFilepath.substring(originalFilepath.lastIndexOf("/") + 1,
                    originalFilepath.length());
        } else {
            filename = originalFilepath.substring(originalFilepath.lastIndexOf("\\") + 1,
                    originalFilepath.length());
        }
        System.out.println("Filename from function: " + filename);
        return filename;
    }

    public static void createNewSeasonFolder(String filepath) throws IOException {
        final TvShow tvShow = TvUtilities.parseFileName(filepath);
        TvUtilities.setFormattedTvShowname(tvShow);
        final String showname = tvShow.getFormattedTvShowName();

        for (final String dir : DESKTOP_SHARED_DIRECTORIES) {
            final File file = new File(PLEX_PREFIX + dir + "\\" + showname);
            if (file.exists()) {
                int season = 1;
                final String seasonFolderPrefix = file.getPath() + "\\Season 0";
                File seasonFolder = new File(seasonFolderPrefix + season);
                while (seasonFolder.exists()) {
                    season++;
                    seasonFolder = new File(seasonFolderPrefix + season);
                }
                final boolean success = seasonFolder.mkdir();
                if (success) {
                    LOG.info(seasonFolder.getName() + " folder created");
                } else {
                    LOG.info("New season folder not created");
                }
                return;
            }
        }
    }

    public static void createNewSeasonFolderFromDir(String filepath) {
        final File file = new File(filepath);
        if (file.exists()) {
            int season = 1;
            final String seasonFolderPrefix = file.getPath() + "\\Season 0";
            File seasonFolder = new File(seasonFolderPrefix + season);
            while (seasonFolder.exists()) {
                season++;
                seasonFolder = new File(seasonFolderPrefix + season);
            }
            final boolean success = seasonFolder.mkdir();
            if (success) {
                LOG.info("New season folder created");
            } else {
                LOG.info("New season folder not created");
            }

            return;
        }
    }

    // TODO: Does setTvEpisodeTitleFromAPI return null???
    public static void runMKVEditorForTvShow(TvShow tvShow) throws IOException {

        if (tvShow.getRawTvShowName() == null) {
            TvUtilities.setFormattedTvShowname(tvShow);
            TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
        }

        String title = tvShow.getTvEpisodeTitle();
        if (title == null || title.equals("null")) {
            title = "";
        }
        final String command = MKVPROPEDIT_LOCATION + " \"" + tvShow.getOriginalFilePath()
                + "\" --set title=\"" + title
                + "\" --edit track:a1 --set name=\"English\" --edit track:v1 --set name=\""
                + tvShow.getFormattedTvShowName() + "\"";

        LOG.info("Command " + command);

        try {
            Runtime.getRuntime().exec(command);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static void showFolderExists(TvShow tvShow) {
        final File file = new File(tvShow.getNewFilepath());
        final File seasonFolder = new File(file.getParent());
        final File showFolder = new File(seasonFolder.getParent());

        if (showFolder.exists()) {
            return;
        }

        final Object result = JOptionPane.showInputDialog(new JFrame(), "Add this show to Plex?",
                WordUtils.capitalize(tvShow.getRawTvShowName()));

        if (result != null) {
            final File rootFolder = new File(showFolder.getParent());
            final File newShowFolder = new File(rootFolder.toString() + "\\" + result.toString());
            newShowFolder.mkdir();
            final File newSeasonFolder = new File(newShowFolder.toString() + "\\Season 01");
            newSeasonFolder.mkdir();

            TvFileUtilities.deleteFoldersFile();
            TvFileUtilities.createFoldersFile();
        }

    }

    public static void createShowFolder(TvShow tvShow) throws IOException {
        final Object result = JOptionPane.showInputDialog(new JFrame(), "Add this show to Plex?",
                WordUtils.capitalize(tvShow.getRawTvShowName()));
        String newSubFolderName = null;

        // TODO: add numerical to regex
        final String firstLetter = result.toString().toLowerCase().substring(0, 1);
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
            return;
        } else {
            folderToCreate.mkdir();
            seasonFolderToCreate.mkdir();
            tvShow.setFormattedTvShowName(resultString);
        }

        // add to folders.txt
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (!tvShowFoldersFile.exists()) {
            LOG.info("folders.txt does not exist");
        }

        final String toWrite = resultString + "^^^" + resultString.toLowerCase();
        Files.write(Paths.get(FOLDERS_FILE_LOCATION), toWrite.getBytes(),
                StandardOpenOption.APPEND);

        // BufferedWriter outputWriter = null;
        // try {
        // outputWriter = new BufferedWriter(new FileWriter(tvShowFoldersFile));
        //
        // outputWriter.append(resultString + "^^^" +
        // resultString.toLowerCase());
        //
        // outputWriter.flush();
        // outputWriter.close();
        // } catch (final IOException e) {
        // LOG.info("Writing to folders file failed.");
        // System.out.println(e.toString());
        // }
    }

    public static boolean seasonFolderExists(TvShow tvShow) {
        // remove filename from filepath
        final File file = new File(tvShow.getNewFilepath());
        final File seasonFolder = new File(file.getParent());

        if (seasonFolder.exists()) {
            return true;
        }
        return false;
    }

    public static boolean episodeExists(TvShow tvShow) {
        final File file = new File(tvShow.getNewFilepath());
        final File folder = new File(file.getParent());

        if (folder.exists()) {
            final String seasonEpisode = "S" + tvShow.getTvSeasonNumber() + "E"
                    + tvShow.getTvEpisodeNumber();

            for (final File episode : folder.listFiles()) {
                if (episode.toString().contains(seasonEpisode)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Extracts all video files from Completed sub-directories to Completed that
     * are smaller than 700 MB and not called rarbg.com.mp4
     *
     * @throws IOException
     */
    public static void extractTvFiles() throws IOException {
        final File folder = new File("\\\\Desktop-Downloa\\Completed");

        for (final File showFolder : folder.listFiles()) {
            if (showFolder.isDirectory()) {
                for (final File showFile : showFolder.listFiles()) {
                    if (getExtension(showFile.toString()).matches(".avi|.mp4|.mkv")
                            && !showFile.getName().toString().toLowerCase().equals("rarbg.com.mp4")
                            && showFile.length() < 700000000) {
                        CommonUtilities.renameFile(showFile.toString(),
                                folder.toString() + "\\" + showFile.getName());
                        final File parentDirectory = new File(showFile.getParent());
                        FileUtils.deleteDirectory(parentDirectory);
                    }
                }
            }
        }
    }

    public static String getExtension(String filename) {
        return filename.substring(filename.lastIndexOf("."), filename.length());
    }
}
