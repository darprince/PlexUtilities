package com.dprince.plex.common;

import static com.dprince.plex.settings.PlexSettings.PLEX_RECYCLE;
import static com.dprince.plex.settings.PlexSettings.PLEX_TEST_FILES;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;

public class CommonUtilitiesTest {

    private static final String TEST_FILE = "atlanta.110-yestv.mkv";

    @Test
    public void renameFile_Test() throws Exception {
        final File sourceFile = new File(PLEX_TEST_FILES + TEST_FILE);
        final File destinationFile = new File(PLEX_RECYCLE + TEST_FILE);

        assertTrue(sourceFile.exists());

        final boolean moveFile = CommonUtilities.renameFile(sourceFile.toString(),
                destinationFile.toString());
        assertTrue(moveFile);
        assertTrue(destinationFile.exists());

        final boolean moveFileBack = CommonUtilities.renameFile(destinationFile.toString(),
                sourceFile.toString());
        assertTrue(moveFileBack);
        assertTrue(sourceFile.exists());
    }

    @Test
    public void getExtension_Test() throws Exception {
        final String extension = CommonUtilities.getExtension(TEST_FILE);
        assertThat("Get Extension", "mkv", equalTo(extension));
    }

    @Test
    public void padString_Test() throws Exception {
        final String padString = CommonUtilities.padString("2");
        assertThat("Pad String", "02", equalTo(padString));
    }

    @Test
    public void padInt_Test() throws Exception {
        final String padString = CommonUtilities.padInt(2);
        assertThat("Pad String", "02", equalTo(padString));
    }

    @Test
    public void buildEpisodeSeasonAndNumber_Test() throws Exception {
        final EpisodeData episodeData = EpisodeData.builder().setAbsoluteNumber(4)
                .setAiredEpisodeNumber(2).setAiredSeason(4).setAiredSeasonID(222)
                .setDvdEpisodeNumber(2).setDvdSeason(4).setEpisodeName("episode name")
                .setFirstAired("first aired").setID(2323).setOverview("overview").build();
        final String epSeasonNum = CommonUtilities.buildEpisodeSeasonAndNumber(episodeData);
        assertThat("Season and Episode string", "S04E02", equalTo(epSeasonNum));
    }
}
