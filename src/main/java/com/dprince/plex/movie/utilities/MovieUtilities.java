package com.dprince.plex.movie.utilities;

import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.utilities.TvFileUtilities;

public class MovieUtilities {

    private static final Logger LOG = Logging.getLogger(MovieUtilities.class);

    /**
     * Sets the metadata for the title, audiotrack title and videotrack title of
     * an mkv file
     * 
     * @param filename
     *            filepath of mkv file.
     */
    public static void runMKVEditorForMovie(String filename) {
        final String movieFilename = TvFileUtilities.getFilenameFromPath(filename);
        final String movieName = movieFilename.substring(0, movieFilename.lastIndexOf("."));

        final String command = MKVPROPEDIT_LOCATION + " \"" + filename + "\" --set title=\""
                + movieName
                + "\" --edit track:a1 --set name=\"English\" --edit track:v1 --set name=\""
                + movieName + "\"";

        LOG.info("Command " + command);

        try {
            Runtime.getRuntime().exec(command);
        } catch (final Exception e) {
            LOG.error("Meta edit failed for {}", filename, e);
        }
    }
}
