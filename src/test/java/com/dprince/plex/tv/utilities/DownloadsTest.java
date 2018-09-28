package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

public class DownloadsTest {

    @Test
    @Ignore
    public void episodeExists_Test() throws Exception {
        final String episodeExists = TvShowUtilities.episodeExists(
                PLEX_PREFIX
                        + "/tv m-s/Orange Is The New Black/Season 02/Orange Is The New Black - S02E03.avi",
                "02", "03");
        assertNotNull(episodeExists);
    }
}
