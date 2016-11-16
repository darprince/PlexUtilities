package com.dprince.plex.movie.utilities;

import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;

import java.io.File;

import org.slf4j.Logger;

import com.dprince.logger.Logging;

public class MovieUtilities {

    private static final Logger LOG = Logging.getLogger(MovieUtilities.class);

    /**
     * Sets the metadata for the title, audiotrack title and videotrack title of
     * an mkv file
     *
     * @param filepath
     *            filepath of mkv file.
     */
    public static void runMKVEditorForMovie(String filepath) {
        final File file = new File(filepath);
        if (file.exists()) {
            try {
                Thread.sleep(1000);
            } catch (final InterruptedException e1) {
                e1.printStackTrace();
            }
            final String movieFilename = new File(filepath).getName();
            final String movieName = movieFilename.substring(0, movieFilename.lastIndexOf("."));

            final String command = MKVPROPEDIT_LOCATION + " \"" + filepath + "\" --set title=\""
                    + movieName
                    + "\" --edit track:a1 --set name=\"English\" --edit track:v1 --set name=\"\"";

            LOG.info("Command " + command);

            try {
                Runtime.getRuntime().exec(command);
            } catch (final Exception e) {
                LOG.error("Meta edit failed for {}", filepath, e);
            }
        } else {
            LOG.info("Failed to edit mkv metadata, file does not exist");
        }
    }
}
