package com.dprince.plex.tv.episodecheck;

import java.io.File;

import org.slf4j.Logger;

import com.dprince.logger.Logging;

public class EpisodeCheck {
    public static final String DESKTOP_SHARED_DIRECTORIES[] = {
            "tv a-e", "tv f-l", "tv m-s", "tv t-z", "Kids Tv"
    };
    private static final String PLEX_PREFIX = "\\\\Desktop-plex\\";

    private static final Logger LOG = Logging.getLogger(EpisodeCheck.class);

    public static void checkEpisodes() {
        for (final String drive : DESKTOP_SHARED_DIRECTORIES) {
            final File driveLocation = new File(PLEX_PREFIX + drive);
            final File[] folderList = driveLocation.listFiles();

            for (final File folder : folderList) {

            }
        }
    }
}
