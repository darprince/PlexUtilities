package com.dprince.plex.tv.utilities;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;

public class TvFileUtilitiesTest {
    @Test
    @Ignore
    public void extractFiles_Test() throws Exception {
        TvFileUtilities.extractTvFiles();
    }

    @Test
    @Ignore
    public void createShowDataFromJSONForShow_Test() throws Exception {
        final File file = new File(PlexSettings.PLEX_PREFIX + "tv a-e/Empire");
        TheTvDbLookup.createShowDataJSONForShow(file, "281617");
    }

    @Test
    @Ignore
    public void createShowDataJSONForAllDirectories_Test() {
        TheTvDbLookup.createShowDataJSONForAllDirectories();
    }
}
