package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.FILES_WE_WANT;
import static com.dprince.plex.settings.PlexSettings.FOLDERS_FILE_LOCATION;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.metadata.MetaData;
import com.dprince.plex.tv.types.TvShow;

public class TvFileUtilities {
    private static final Logger LOG = Logging.getLogger(TvFileUtilities.class);

    // TODO: This is from the old version, should be updated
    @Deprecated
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
    @Deprecated
    public static void deleteFoldersFile() {
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (tvShowFoldersFile.exists()) {
            LOG.info("Folders file deleted: " + tvShowFoldersFile.delete());
        }
    }

    // TODO: also from old version
    @Deprecated
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

    @Deprecated
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
     * Scans a directory for all video files and attempts to parse them and move
     * them. Once all video files are processed, the files parent folder will be
     * attempted to be deleted.
     *
     * @param directory
     */
    @Deprecated
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
                final TvShow tvShow = ParseFileName.parseFileName(showFile.toString(), true);
                if (tvShow != null) {
                    if (Downloads.moveEpisodeFile(tvShow)) {
                        LOG.info("Moved {} to {}", tvShow.getOriginalFilepath(),
                                tvShow.getDestinationFilepath());
                        LOG.info("Adding folder {} to deleteFolderList", showFile.getParentFile());
                        foldersToDelete.add(showFile.getParentFile());
                    } else {
                        LOG.error("Failed to move {} to {}", tvShow.getOriginalFilepath(),
                                tvShow.getDestinationFilepath());
                    }
                    MetaData.editMetaData(tvShow.getDestinationFilepath(),
                            tvShow.getEpisodeTitle());
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
    @Deprecated
    public static void deleteEmptyShowFolders(final Set<File> foldersToDelete) {
        boolean deleteFolder = true;
        LOG.info("Starting foldersToDelete(), {} folders", foldersToDelete.size());
        for (final File folder : foldersToDelete) {
            for (final File file : folder.listFiles()) {
                if (CommonUtilities.getExtension(file.toString()).matches(FILES_WE_WANT)) {
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
