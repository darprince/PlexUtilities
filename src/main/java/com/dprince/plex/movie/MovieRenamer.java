package com.dprince.plex.movie;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.utilities.TvFileUtilities;

public class MovieRenamer {
    private static final Logger LOG = Logging.getLogger(MovieRenamer.class);

    static String fileName = null;
    static List<String> extensions = Arrays.asList("nfo", "txt", "jpg");
    static List<String> subExtensions = Arrays.asList("srt", "sub", "idx");

    public static void renameMovie(String filename) {
        fileName = filename;
        System.out.println("Filename: " + fileName);
        final String movieName = getMovieName();
        System.out.println("MovieName: " + movieName);
        final String year = getYear();
        System.out.println("Year: " + year);
        String renamedFile = null;

        final File file = new File(fileName);
        final String basePath = file.getParentFile().toString();
        System.out.println("Basepath: " + basePath);

        if (!file.isDirectory()) {
            final String extension = getExtension();
            renamedFile = getNewFilename(movieName, year, extension);
            System.out.println("Extension: " + extension);
            System.out.println(movieName + " (" + year + ")." + extension);
            System.out.println("RenamedFile: " + renamedFile);
            setMetaData(fileName);
        } else {
            System.out.println("Directory");
            renamedFile = getNewFilename(movieName, year);

            final File[] listOfFiles = file.listFiles();

            for (final File movieFiles : listOfFiles) {
                final String movieFolder = movieFiles.getParentFile().toString();
                System.out.println("\nfile: " + movieFiles.toString());
                if (movieFiles.isFile()) {
                    if (extensions.contains(getExtension(movieFiles.toString()))) {
                        movieFiles.delete();
                    }
                    if (movieFiles.getName().toString().equalsIgnoreCase("rarbg.com.mp4")) {
                        movieFiles.delete();
                    }
                    if (subExtensions.contains(getExtension(movieFiles.toString()))) {
                        renameFile(movieFiles.toString(),
                                movieFiles.getParentFile().toString() + "\\" + renamedFile + ".eng."
                                        + getExtension(movieFiles.toString()));
                    } else {
                        final String newFilepath = movieFiles.getParentFile().toString() + "\\"
                                + renamedFile + "." + getExtension(movieFiles.toString());
                        renameFile(movieFiles.toString(), newFilepath);
                        setMetaData(newFilepath);
                    }
                } else if (movieFiles.isDirectory()) {
                    final File[] listOfSubFiles = movieFiles.listFiles();
                    for (final File subFile : listOfSubFiles) {
                        final String newSubFileName = movieFolder + "\\" + movieName + " (" + year
                                + ").eng." + getExtension(subFile.toString());
                        renameFile(subFile.toString(), newSubFileName);
                    }
                    movieFiles.delete();
                }
            }
        }

        if (renameFile(fileName, basePath + "\\" + renamedFile) == false) {
            System.out.println("Renaming failed.");
        }
    }

    private static void setMetaData(final String renamedFile) {
        LOG.info("setMetaData called with filename: " + renamedFile);
        final String extension = TvFileUtilities.getExtension(renamedFile);
        LOG.info("Extension: " + extension);
        if (extension.matches(".mp4")) {
            LOG.info("Setting metadata from mp4");
            try {
                final String parser = "C:\\FileManipulation\\TVShowRenamer\\parser.jar";
                final String prefix = fileName.substring(0, fileName.lastIndexOf("\\"));
                final String command = "\"" + System.getProperty("java.home")
                        + "\\bin\\java.exe\" -jar " + parser + " \"" + renamedFile + "\"";

                System.out.println(command);
                try {
                    Runtime.getRuntime().exec(command);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } catch (final Exception e) {
                System.out.println("Metadata not cleaned.");
            }
        } else if (extension.matches(".mkv")) {
            LOG.info("Setting metadata from mkv");
            TvFileUtilities.runMKVEditorForMovie(renamedFile);
        }
    }

    public static String getMovieName() {
        try {
            String movieName = fileName.substring(fileName.lastIndexOf("\\") + 1,
                    fileName.length());
            final Pattern pattern = Pattern.compile("[0-9]{4}");
            final Matcher matcher = pattern.matcher(movieName);

            if (matcher.find()) {
                movieName = movieName.substring(0, movieName.indexOf(matcher.group(0)) - 1);
                movieName = movieName.replaceAll("\\.", " ").toLowerCase();
                final String splitMovie[] = movieName.split(" ");
                String renamedMovie = "";
                for (final String bit : splitMovie) {
                    renamedMovie += bit.substring(0, 1).toUpperCase()
                            + bit.substring(1, bit.length()) + " ";
                }
                renamedMovie = renamedMovie.trim();
                return renamedMovie;
            }
        } catch (final Exception e) {
            System.out.println("catch");
            e.printStackTrace();
        }
        System.out.println("returning null");
        return null;
    }

    private static String getExtension() {
        try {
            final int extension = fileName.lastIndexOf(".");
            return fileName.substring(extension + 1, fileName.length());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getExtension(String fileName) {
        try {
            final int extension = fileName.lastIndexOf(".");
            return fileName.substring(extension + 1, fileName.length());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getYear() {
        try {
            final Pattern pattern = Pattern.compile("[0-9]{4}");
            final Matcher matcher = pattern.matcher(fileName);
            if (matcher.find()) {
                return matcher.group(0);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getNewFilename(String movieName, String year, String extension) {
        return movieName + " (" + year + ")." + extension;
    }

    private static String getNewFilename(String movieName, String year) {
        return movieName + " (" + year + ")";
    }

    private static boolean renameFile(String originalFilename, String newFileName) {
        final File oldName = new File(originalFilename);
        final File newName = new File(newFileName);

        System.out.println("Old Name: " + oldName);
        System.out.println("New Name: " + newName);
        if (oldName.renameTo(newName)) {
            System.out.println("renamed");
            return true;
        } else {
            System.out.println("not renamed");
            return false;
        }
    }
}
