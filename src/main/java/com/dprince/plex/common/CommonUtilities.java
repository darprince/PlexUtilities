package com.dprince.plex.common;

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
}
