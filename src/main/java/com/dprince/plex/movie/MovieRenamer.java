package com.dprince.plex.movie;

import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.MOVIES_AD;
import static com.dprince.plex.settings.PlexSettings.MOVIES_EH;
import static com.dprince.plex.settings.PlexSettings.MOVIES_IO;
import static com.dprince.plex.settings.PlexSettings.MOVIES_PS;
import static com.dprince.plex.settings.PlexSettings.MOVIES_TZ;
import static com.dprince.plex.settings.PlexSettings.PARSER_LOCATION;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.movie.utilities.MovieUtilities;
import com.dprince.plex.settings.PlexSettings;

public class MovieRenamer {

    private static final Logger LOG = Logging.getLogger(MovieRenamer.class);

    private static final String MOVIE_FILE_EXTENSIONS = "avi|mkv|mp4";
    private static final String EXTENSIONS_TO_DELETE = "nfo|txt|jpg|png|exe";
    private static final String SUB_EXTENSIONS = "srt|sub|idx";

    public static void main(String[] args) {
        // final String folder =
        // "C:\\temp\\The.Tragedy.of.Macbeth.2021.1080p.WEBRip.x265-RARBG -
        // Copy";
        //
        // renameMovieFromFolder(folder, false, true);
    }

    public static void renameMovieFromFolder(String sourceFolderPath, boolean kidsMovie,
            boolean moveFiles) {

        final String formattedMovieName = getMovieNameFromFolder(sourceFolderPath);
        final String year = getYear(sourceFolderPath);

        if (formattedMovieName == null || year == null) {
            System.out.println("Movie name could not be parsed.");
            System.exit(0);
        }
        final File sourceDirectory = new File(sourceFolderPath);

        final String fileToMetaEdit = findAndMoveFilesToRoot(sourceDirectory, formattedMovieName,
                year, sourceDirectory);

        String destinationPath = null;
        if (moveFiles) {
            if (kidsMovie) {
                destinationPath = PlexSettings.PLEX_PREFIX + PlexSettings.KIDS_MOVIES;
            } else {
                destinationPath = deriveDestinationFolder(formattedMovieName);
            }
        }

        System.out.println("FormattedMovieName = " + formattedMovieName);
        System.out.println("DestinationPath = " + destinationPath);

        System.out.println("RENAMING FOLDER " + sourceDirectory.toString());
        if (destinationPath == null) {
            destinationPath = sourceDirectory.getParent();
            CommonUtilities.renameFile(sourceFolderPath,
                    sourceDirectory.getParent() + "\\" + formattedMovieName + " (" + year + ")");
        } else {
            final Path source = Paths.get(sourceFolderPath);
            final Path target = Paths
                    .get(destinationPath + "\\" + formattedMovieName + " (" + year + ")");

            recursiveMoveFolder(source, target);

        }

        if (new File(destinationPath + "\\" + formattedMovieName + " (" + year + ")").exists()) {
            LOG.info("File has been written to " + destinationPath + "\\" + formattedMovieName
                    + " (" + year + ")");
        }

        try {
            Thread.sleep(1500);
            setMetaData(destinationPath + "\\" + formattedMovieName + " (" + year + ")" + "\\"
                    + fileToMetaEdit);
        } catch (final InterruptedException e) {
            e.printStackTrace();
        }

        if (moveFiles) {
            final File masterFolder = new File(sourceFolderPath);
            if (masterFolder.listFiles().length == 0) {
                masterFolder.delete();
                LOG.info("Folder has been deleted");
            }
        }

    }

    private static String findAndMoveFilesToRoot(final File sourceDirectory,
            final String formattedMovieName, final String year, final File folder) {

        String fileToMetaEdit = null;
        List.of(folder.listFiles()).stream().forEach(System.out::println);
        for (final File file : folder.listFiles()) {
            System.out.println(file.getName());
            if (file.isDirectory()) {
                fileToMetaEdit = findAndMoveFilesToRoot(sourceDirectory,
                        formattedMovieName, year, file);
                if (file.listFiles().length == 0) {
                    LOG.info("\nDELETING " + file.getName());
                    file.delete();
                }
            } else if (getExtension(file).toLowerCase().matches(EXTENSIONS_TO_DELETE)
                    || file.getName().toLowerCase().matches(FILES_TO_IGNORE)
                    || file.getName().toLowerCase().contains("sample")) {
                LOG.info("\nDELETING " + file.getName());
                file.delete();
            } else if (getExtension(file).toLowerCase().matches(SUB_EXTENSIONS)) {
                if (file.getName().toLowerCase().contains("eng")) {
                    CommonUtilities.renameFile(file.getAbsolutePath(),
                            sourceDirectory + "\\" + file.getName());
                } else {
                    file.delete();
                }
            } else if (getExtension(file).toLowerCase().matches(MOVIE_FILE_EXTENSIONS)) {
                final String formattedFileName = getNewFilename(formattedMovieName, year,
                        getExtension(file).toLowerCase());
                CommonUtilities.renameFile(file.toString(),
                        sourceDirectory + "\\" + formattedFileName);
                if (getExtension(file).toLowerCase().matches("mp4|mkv")) {
                    fileToMetaEdit = formattedFileName;
                }
            }
        }

        return fileToMetaEdit;
    }

    private static void recursiveMoveFolder(final Path source, final Path target) {
        target.toFile().mkdir();

        if (target.toFile().exists()) {
            final List<File> filesToMoveList = new ArrayList<>();
            for (final File file : source.toFile().listFiles()) {
                if (file.isDirectory()) {
                    new File(target.toString() + file.getName()).mkdir();
                    recursiveMoveFolder(Paths.get(file.toString()),
                            Paths.get(target + "/" + file.getName()));
                } else if (CommonUtilities.getExtension(file.getName()).equals("exe")) {
                    try {
                        Files.delete(Paths.get(file.getPath()));
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    filesToMoveList.add(file);
                }
            }

            for (final File file2 : filesToMoveList) {
                try {
                    LOG.info("Moving {} to {}", file2.getName(), target + "\\" + file2.getName());
                    Files.move(Paths.get(file2.toURI()),
                            Paths.get(target + "\\" + file2.getName()));
                } catch (final IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            LOG.info("Root folder ({}) not created...", target.toString());
        }
    }

    public static String deriveDestinationFolder(String formattedMovieName) {
        String firstLetter;
        if (formattedMovieName.startsWith("The ")) {
            firstLetter = formattedMovieName.substring(4, 5);
        } else {
            firstLetter = formattedMovieName.substring(0, 1);
        }
        firstLetter = firstLetter.toUpperCase();

        if (firstLetter.matches("[A-D]{1}")) {
            return PLEX_PREFIX + MOVIES_AD + "/" + firstLetter;
        } else if (firstLetter.matches("[E-H]{1}")) {
            return PLEX_PREFIX + MOVIES_EH + "/" + firstLetter;
        } else if (firstLetter.matches("[I-O]{1}")) {
            return PLEX_PREFIX + MOVIES_IO + "/" + firstLetter;
        } else if (firstLetter.matches("[P-S]{1}")) {
            return PLEX_PREFIX + MOVIES_PS + "/" + firstLetter;
        } else if (firstLetter.matches("[T-Z]{1}")) {
            return PLEX_PREFIX + MOVIES_TZ + "/" + firstLetter;
        } else {
            return null;
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
            final Matcher yearMatcher = pattern.matcher(movieName);

            if (yearMatcher.find()) {
                movieName = movieName.substring(0, movieName.indexOf(yearMatcher.group(0)) - 1);
                movieName = movieName.replaceAll("\\.+", " ").replaceAll(" {1,}", " ")
                        .toLowerCase();
                final String splitMovie[] = movieName.split(" ");
                String renamedMovie = "";
                for (final String bit : splitMovie) {
                    if (bit.equals("mr")) {
                        renamedMovie += "Mr. ";
                    } else {
                        renamedMovie += bit.substring(0, 1).toUpperCase()
                                + bit.substring(1, bit.length()) + " ";
                    }
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
     * Takes a filepath and returns the file's extension.
     *
     * @return the file's extension
     */
    private static String getExtension(File fileName) {
        return getExtension(fileName.getName());
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
