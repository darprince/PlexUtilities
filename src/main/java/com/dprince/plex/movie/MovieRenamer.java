package com.dprince.plex.movie;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.movie.utilities.MovieUtilities;
import com.dprince.plex.tv.utilities.TvFileUtilities;

public class MovieRenamer {
    private static final String PARSER_LOCATION = "\\\\Desktop-downloa\\TVShowRenamer\\parser.jar";

    private static final Logger LOG = Logging.getLogger(MovieRenamer.class);

    private static final String RARBG = "rarbg.com.mp4";
    private static final List<String> EXTENSIONS = Arrays.asList("nfo", "txt", "jpg");
    private static final List<String> SUB_EXTENSIONS = Arrays.asList("srt", "sub", "idx");

    public static void renameMovie(String filename) {
        final String movieName = getMovieName(filename);
        final String year = getYear(filename);

        final File file = new File(filename);
        final String basePath = file.getParentFile().toString();

        String newMovieFilename = null;
        String renamedFile = null;
        if (!file.isDirectory()) {
            final String extension = getExtension(filename);
            renamedFile = getNewFilename(movieName, year, extension);
            setMetaData(filename);
        } else {
            renamedFile = getNewFilenameWithoutExtension(movieName, year);

            final File[] listOfFiles = file.listFiles();

            for (final File movieFiles : listOfFiles) {
                final String movieFolder = movieFiles.getParentFile().toString();
                if (movieFiles.isFile()) {
                    if (EXTENSIONS.contains(getExtension(movieFiles.toString()))
                            || movieFiles.getName().toString().equalsIgnoreCase(RARBG)) {
                        movieFiles.delete();
                        break;
                    }
                    if (SUB_EXTENSIONS.contains(getExtension(movieFiles.toString()))) {
                        CommonUtilities.renameFile(movieFiles.toString(),
                                movieFiles.getParentFile().toString() + "\\" + renamedFile + ".eng."
                                        + getExtension(movieFiles.toString()));
                    } else {
                        newMovieFilename = renamedFile + "." + getExtension(movieFiles.toString());
                        final String newFilepath = movieFiles.getParentFile().toString() + "\\"
                                + newMovieFilename;
                        CommonUtilities.renameFile(movieFiles.toString(), newFilepath);

                    }
                } else if (movieFiles.isDirectory()) {
                    final File[] listOfSubFiles = movieFiles.listFiles();
                    for (final File subFile : listOfSubFiles) {
                        final String newSubFileName = movieFolder + "\\" + movieName + " (" + year
                                + ").eng." + getExtension(subFile.toString());
                        CommonUtilities.renameFile(subFile.toString(), newSubFileName);
                    }
                    movieFiles.delete();
                }
            }
        }

        if (CommonUtilities.renameFile(filename, basePath + "\\" + renamedFile) == false) {
            LOG.error("Renaming movie failed.");
        }

        final String newMovieFilePath = basePath + "\\" + renamedFile + "\\" + newMovieFilename;

        setMetaData(newMovieFilePath);
    }

    private static void setMetaData(final String renamedFile) {
        LOG.info("setMetaData called with filename: " + renamedFile);
        final String extension = TvFileUtilities.getExtension(renamedFile);
        LOG.info("Extension: " + extension);
        if (extension.matches(".mp4")) {
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
        } else if (extension.matches(".mkv")) {
            LOG.info("Setting metadata from mkv");
            MovieUtilities.runMKVEditorForMovie(renamedFile);
        }
    }

    public static String getMovieName(String filename) {
        try {
            String movieName = filename.substring(filename.lastIndexOf("\\") + 1,
                    filename.length());
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
    private static String getYear(String filename) {
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
