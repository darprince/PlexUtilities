package com.dprince.plex.movie;

import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.PARSER_LOCATION;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.movie.utilities.MovieUtilities;

public class MovieRenamer {

    private static final Logger LOG = Logging.getLogger(MovieRenamer.class);

    private static final String EXTENSIONS = "nfo|txt|jpg";
    private static final String SUB_EXTENSIONS = "srt|sub|idx";

    public static void renameMovieFromFolder(String masterFolderPath) {

        final String formattedMovieName = getMovieNameFromFolder(masterFolderPath);
        final String year = getYear(masterFolderPath);

        final File masterFolderFilePath = new File(masterFolderPath);

        String formattedFileName = null;
        String fileToMetaEdit = null;
        final File[] fileList = masterFolderFilePath.listFiles();

        for (final File file : fileList) {
            if (file.isDirectory()) {
                System.out.println(
                        "FILE IS DIRECTORY " + file.getName() + " " + file.listFiles().length);
                for (final File subFile : file.listFiles()) {
                    final String extension = getExtension(subFile.toString());
                    if (extension.matches(SUB_EXTENSIONS)) {
                        System.out.println("\nTOP SUB MATCH " + subFile.getName());
                        formattedFileName = getNewFilename(formattedMovieName, year, extension);
                        CommonUtilities.renameFile(subFile.toString(),
                                masterFolderPath + "/" + formattedFileName);
                    } else if (extension.matches(EXTENSIONS)
                            || subFile.getName().toLowerCase().contains("sample")) {
                        System.out.println("\nTOP DELETING " + subFile.getName());
                        subFile.delete();
                    }
                }
                System.out.println("\nDELETING FOLDER " + file.getName());
                file.delete();
            } else {
                System.out.println("NOT DIRECTORY " + file.getName());
                final String extension = getExtension(file.toString());
                if (extension.matches(EXTENSIONS) || file.getName().matches(FILES_TO_IGNORE)
                        || file.getName().toLowerCase().contains("sample")) {
                    System.out.println("\nDELETING " + file.getName());
                    file.delete();
                } else {
                    formattedFileName = getNewFilename(formattedMovieName, year, extension);
                    System.out.println("RENAMING " + file.getName());
                    CommonUtilities.renameFile(file.toString(),
                            masterFolderPath + "/" + formattedFileName);
                    if (extension.matches("mp4|mkv")) {
                        fileToMetaEdit = formattedFileName;
                        // setMetaData(masterFolderPath + "/" +
                        // formattedFileName);
                    }
                }
            }
        }

        // rename master folder
        final File folder = new File(masterFolderPath);
        System.out.println("RENAMING FOLDER " + folder.toString());
        CommonUtilities.renameFile(masterFolderPath,
                folder.getParent() + "/" + formattedMovieName + " (" + year + ")");
        try {
            Thread.sleep(1500);
            setMetaData(folder.getParent() + "/" + formattedMovieName + " (" + year + ")" + "/"
                    + fileToMetaEdit);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static void setMetaData(final String renamedFile) {
        LOG.info("setMetaData called with filename: " + renamedFile);
        final String extension = CommonUtilities.getExtension(renamedFile);
        LOG.info("Extension: " + extension);
        if (extension.matches("mp4")) {
            LOG.info("Setting metadata from mp4");
            try {
                final String parser = PARSER_LOCATION;
                final String command = "\"" + System.getProperty("java.home")
                        + "\\bin\\java.exe\" -jar " + parser + " \"" + renamedFile + "\"";

                System.out.println(command);
                try {
                    Runtime.getRuntime().exec(command);
                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } catch (final Exception e) {
                LOG.error("Metadata not cleaned.");
            }
        } else if (extension.matches("mkv")) {
            LOG.info("Setting metadata from mkv");
            MovieUtilities.runMKVEditorForMovie(renamedFile);
        }
    }

    /**
     * Formats a movies folderName into a proper Movie Name. Note: the year is
     * not included.
     *
     * @param folderName
     * @return a properly formatted movie name without the year.
     */
    public static String getMovieNameFromFolder(String folderName) {
        try {
            String movieName = folderName.substring(folderName.lastIndexOf("\\") + 1,
                    folderName.length());
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
            LOG.error("Error getting movie name", e);
            return null;
        }
        LOG.info("Movie name not found");
        return null;
    }

    /**
     * Takes a filepath and returns the file's extension.
     *
     * @return the file's extension
     */
    private static String getExtension(String fileName) {
        try {
            final int extension = fileName.lastIndexOf(".");
            return fileName.substring(extension + 1, fileName.length());
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Takes a filename and returns the year if match is successful.
     *
     * @param filename
     * @return The movie's year.
     */
    public static String getYear(String filename) {
        try {
            final Pattern pattern = Pattern.compile("[0-9]{4}");
            final Matcher matcher = pattern.matcher(filename);
            if (matcher.find()) {
                return matcher.group(0);
            }
        } catch (final Exception e) {
            LOG.error("Error in matching movie's year.");
            return null;
        }
        LOG.info("Movie year not matched.");
        return null;
    }

    /**
     * Constructs a filename from the movieName, year, and extension.
     *
     * @param movieName
     * @param year
     * @param extension
     * @return a properly constructed Movie filename.
     */
    private static String getNewFilename(String movieName, String year, String extension) {
        return movieName + " (" + year + ")." + extension;
    }

    /**
     * Constructs a filename from the movieName and year.
     *
     * @param movieName
     * @param year
     * @return a properly constructed Movie filename without the extension
     */
    private static String getNewFilenameWithoutExtension(String movieName, String year) {
        return movieName + " (" + year + ")";
    }

}
