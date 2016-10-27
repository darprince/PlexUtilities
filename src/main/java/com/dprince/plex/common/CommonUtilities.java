package com.dprince.plex.common;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

import com.dprince.logger.Logging;

public class CommonUtilities {

    private static final Logger LOG = Logging.getLogger(CommonUtilities.class);

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
        return filepath.substring(filepath.lastIndexOf(".") + 1, filepath.length());
    }
}
