package com.dprince.plex.tv.utilities;

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
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.types.TvShow;

public class TvFileUtilities {

    public TvFileUtilities() {

    }

    private static final String FOLDERS_FILE_LOCATION = "\\\\DESKTOP-DOWNLOA\\TVShowRenamer\\folders.txt";

    public static final String DESKTOP_SHARED_DIRECTORIES[] = {
            "tv a-e", "tv f-l", "tv m-s", "tv t-z", "Kids Tv"
    };
    private static final String PLEX_PREFIX = "\\\\Desktop-plex\\";

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

    public static void deleteFoldersFile() {
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (tvShowFoldersFile.exists()) {
            LOG.info("Folders file deleted: " + tvShowFoldersFile.delete());
        }
    }

    // TODO: also from old version
    public static String[][] setTitlesFromDirectories() {
        final List<File> finalFileList = new ArrayList<File>();
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

    static String getFilenameFromPath(String originalFilepath) {
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

    public static void createNewSeasonFolder(String filepath) {
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
    public static void runMKVEditorForTvShow(TvShow tvShow) {
        final String parserPath = "\\\\Desktop-downloa\\TVShowRenamer\\mkvpropedit.exe";

        if (tvShow.getRawTvShowName() == null) {
            TvUtilities.setFormattedTvShowname(tvShow);
            TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
        }

        String title = tvShow.getTvEpisodeTitle();
        if (title == null || title.equals("null")) {
            title = "";
        }
        final String command = parserPath + " \"" + tvShow.getOriginalFilePath()
                + "\" --set title=\"" + title + "\"";

        LOG.info("Command " + command);

        try {
            Runtime.getRuntime().exec(command);
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean renameFile(String originalFilename, String newFileName) {
        final File oldName = new File(originalFilename);
        final File newName = new File(newFileName);
        if (oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
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
        }
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
        final String seasonEpisode = "S" + tvShow.getTvSeasonNumber() + "E"
                + tvShow.getTvEpisodeNumber();

        for (final File episode : folder.listFiles()) {
            if (episode.toString().contains(seasonEpisode)) {
                return true;
            }
        }
        return false;
    }
}
