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

public class TvFileUtilities {

    private static final String FOLDERS_FILE_LOCATION = "\\\\DESKTOP-DOWNLOA\\TVShowRenamer\\folders.txt";

    // TODO: This is from the old version, should be updated
    public static void createFoldersFile() {
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (tvShowFoldersFile.exists()) {
            return;
        }

        String[][] titles = null;

        try {
            tvShowFoldersFile.createNewFile();
        } catch (final IOException e) {
            System.out.println("Creating folders.txt file failed");
            System.out.println(e.toString());
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
        } catch (final IOException e) {
            System.out.println("Writing to folders file failed.");
            System.out.println(e.toString());
        }
    }

    public static void deleteFoldersFile() {
        final File tvShowFoldersFile = new File(FOLDERS_FILE_LOCATION);
        if (tvShowFoldersFile.exists()) {
            tvShowFoldersFile.delete();
        }
    }

    // TODO: also from old version
    public static String[][] setTitlesFromDirectories() {
        final List<File> finalFileList = new ArrayList<File>();
        try {
            final String downloadsDirectories[] = {
                    "M:\\", "N:\\", "O:\\", "R:\\"
            };

            File file = null;
            File[] files = null;

            for (final String dir : downloadsDirectories) {
                file = new File(dir);
                files = file.listFiles();
                for (final File tempFile : files) {
                    finalFileList.add(tempFile);
                }
            }
        } catch (final Exception e) {
            final String downloadsDirectories[] = {
                    "K:\\", "H:\\TV J-S", "J:\\TV T-Z", "I:\\Kids\\TV Shows"
            };

            File file = null;
            File[] files = null;

            for (final String dir : downloadsDirectories) {
                file = new File(dir);
                files = file.listFiles();
                for (final File tempFile : files) {
                    finalFileList.add(tempFile);
                }
            }
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
            System.out.println("Reading folders.txt failed");
            System.out.println(e.toString());
        }
        return titles;
    }
}
