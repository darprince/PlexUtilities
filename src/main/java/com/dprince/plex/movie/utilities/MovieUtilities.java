package com.dprince.plex.movie.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_MOVIE_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;

import org.eclipse.jdt.annotation.NonNull;
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

    public static String getMovieDriveLocation(@NonNull final String formattedMovieName) {
        File queriedDrive = null;
        String queryString = null;
        String firstChar = null;
        if (formattedMovieName.startsWith("The ")) {
            firstChar = formattedMovieName.substring(4, 5);
        } else {
            firstChar = formattedMovieName.substring(0, 1);
        }

        for (final String sharedDrive : DESKTOP_SHARED_MOVIE_DIRECTORIES) {
            queryString = PLEX_PREFIX + sharedDrive + "/" + firstChar + "/" + formattedMovieName;
            queriedDrive = new File(queryString);
            if (queriedDrive.exists()) {
                return sharedDrive;
            }
        }

        queryString = PLEX_PREFIX + "Kids Movies/" + formattedMovieName;
        queriedDrive = new File(queryString);
        if (queriedDrive.exists()) {
            return "Kids Movies";
        }

        return null;
    }
}
