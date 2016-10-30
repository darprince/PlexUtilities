package com.dprince.plex.common;

import static com.dprince.plex.settings.PlexSettings.TWO_DECIMALS;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;

public class CommonUtilities {

    private static final Logger LOG = Logging.getLogger(CommonUtilities.class);

    public static void main(String[] args) {
        final String extension = getExtension("c:\\jlkjl.jfldjfl.fjdlkasj.avi");
        System.out.println(extension);
    }

    /**
     * Moves file
     *
     * @param originalFilename
     *            original location of file with filename
     * @param destinationFileName
     *            destination location of file with filename
     * @return boolean for success
     */
    @NonNullByDefault
    public static boolean renameFile(@NonNull final String originalFilename,
            @NonNull final String destinationFileName) {
        final File oldName = new File(originalFilename);
        final File newName = new File(destinationFileName);
        LOG.info("Moving file {} to {}", originalFilename, destinationFileName);
        if (oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Removes characters that can't be used in a url.
     *
     * @param input
     * @return a String that is safe to use in a url.
     */
    @NonNullByDefault
    public static String urlEncode(@NonNull final String input) {
        try {
            return URLEncoder.encode(input, "UTF-8");
        } catch (final UnsupportedEncodingException e) {
            LOG.error("Failed to encode String");
            return null;
        }
    }

    /**
     * Removes characters that can't be used in a url.
     *
     * @param input
     * @return a String that is safe to use in a url.
     */
    @NonNullByDefault
    public static String filenameEncode(@NonNull final String input) {
        final String output = input.replaceAll("[^a-zA-Z0-9']", " ");
        return output.replaceAll("[\\s]+", " ").trim();
    }

    /**
     * Returns the extension of a filepath.
     *
     * @param filepath
     * @return the extension of the specified filepath.
     */
    public static String getExtension(@NonNull final String filepath) {
        return filepath.substring(filepath.lastIndexOf(".") + 1, filepath.length()).toLowerCase();
    }

    /**
     * Writes a string to a file. Deletes and recreates file if it exists.
     *
     * @param toWrite
     * @param filepath
     */
    public static void writeStringToFile(@NonNull final String toWrite,
            @NonNull final String filepath) {
        final File fileToWriteTo = new File(filepath);
        if (fileToWriteTo.exists()) {
            fileToWriteTo.delete();
            try {
                fileToWriteTo.createNewFile();
            } catch (final IOException e) {
                LOG.error("Failed to create file", e);
            }
        }

        try (FileWriter file = new FileWriter(filepath)) {
            file.write(toWrite);
        } catch (final IOException e) {
            LOG.error("Failed to write data to file", e);
        }
    }

    /**
     * Writes a List of Strings to a file. Deletes and recreates file if it
     * exists.
     *
     * @param toWrite
     * @param filepath
     */
    public static void writeListToFile(@NonNull final List<String> toWrite,
            @NonNull final String filepath) {
        final File fileToWriteTo = new File(filepath);
        if (fileToWriteTo.exists()) {
            fileToWriteTo.delete();
            try {
                fileToWriteTo.createNewFile();
            } catch (final IOException e) {
                LOG.error("Failed to create file", e);
            }
        }

        try (FileWriter file = new FileWriter(filepath)) {
            for (final String line : toWrite) {
                file.write(line);
                file.write(System.getProperty("line.separator"));
            }
        } catch (final IOException e) {
            LOG.error("Failed to write list to file", e);
        }
    }

    /**
     * Creates a new FileWriter
     *
     * @param filepath
     * @return a new {@link FileWriter}
     */
    public static FileWriter getFileWriter(@NonNull final String filepath) {
        final File fileToWriteTo = new File(filepath);
        if (fileToWriteTo.exists()) {
            fileToWriteTo.delete();
            try {
                fileToWriteTo.createNewFile();
            } catch (final IOException e) {
                LOG.error("Failed to create new file", e);
            }
        }

        try {
            return new FileWriter(filepath);
        } catch (final IOException e) {
            LOG.error("Failed to create new FileWriter, Exiting", e);
        }
        System.exit(0);
        return null;
    }

    /**
     * Formats a numerical String to two characters with a leading zero if
     * needed.
     *
     * @param value
     * @return A numerical String formatted with a leading zero.
     */
    public static String padString(@NonNull final String value) {
        return String.format(TWO_DECIMALS, Integer.parseInt(value));
    }

    /**
     * Formats an int to two characters with a leading zero if needed.
     *
     * @param value
     * @return A numerical String formatted with a leading zero.
     */
    public static String padInt(@NonNull final int value) {
        return String.format(TWO_DECIMALS, value);
    }

    /**
     * Determines if the folder should be treated as a show folder or not
     *
     * @param folder
     * @return true if folder is to be ignore, false otherwise.
     */
    public static boolean isSystemFolder(@NonNull final File folder) {
        if (folder.getName().startsWith("$")
                || folder.getName().equals("System Volume Information")) {
            return true;
        }
        return false;
    }

    /**
     * Builds a season number and episode number string, ie. S01E04
     *
     * @param episodeData
     * @param showFolder
     * @return A formatted String of season number and episode number
     */
    public static String buildEpisodeSeasonAndNumber(@NonNull final EpisodeData episodeData) {
        return "S" + CommonUtilities.padInt(episodeData.getAiredSeason()) + "E"
                + CommonUtilities.padInt(episodeData.getAiredEpisodeNumber());
    }
}
