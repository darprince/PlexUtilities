package com.dprince.plex.tv.episodecheck;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.EpisodeData;

public class EpisodeCheck {

    private static final Logger LOG = Logging.getLogger(EpisodeCheck.class);

    public static void main(String[] args) {
        checkEpisodes();
    }

    public static void checkEpisodes() {
        for (final String drive : DESKTOP_SHARED_DIRECTORIES) {
            final File driveLocation = new File(PLEX_PREFIX + drive);
            final File[] folderList = driveLocation.listFiles();

            for (final File folder : folderList) {
                final String showTitle = folder.getName();
                final String showID = TheTvDbLookup.getShowID("30 for 30");

                final List<EpisodeData> allEpisodesForShow = TheTvDbLookup
                        .getAllEpisodesForShow(showID);
                for (final EpisodeData episode : allEpisodesForShow) {
                    System.out.println(episode);
                }
                System.out.println("Show id: " + showID);
                System.exit(0);
            }
        }
    }
}
