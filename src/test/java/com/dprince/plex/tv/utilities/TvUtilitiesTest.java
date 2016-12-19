package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.FILES_WE_WANT;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dprince.plex.common.CommonUtilities;

public class TvUtilitiesTest {

    @Test
    public void ignoreEpisode_Test() throws Exception {
        final String name1 = "doctor.who.2005.s04e13.internal.bdrip.x264-archivist.mkv";
        final String name2 = "RARBG.com.avi";

        assertFalse(name1.toLowerCase().matches(FILES_TO_IGNORE));
        assertTrue(name2.toLowerCase().matches(FILES_TO_IGNORE));
    }

    @Test
    public void matchExtension_Test() throws Exception {
        final String name1 = "doctor.who.2005.s04e13.internal.bdrip.x264-archivist.mkv";
        final String name2 = "RARBG.com.avi";
        final String name3 = "doctor.who.2005.s04e13.internal.bdrip.x264-archivist.mp4";
        final String name4 = "Doctor.Who.2005.S04.BDRip.x264-MIXED.nfo";
        final String name5 = "doctor.who.2005.s04e13.internal.bdrip.x264-archivist.txt";

        assertTrue(CommonUtilities.getExtension(name1).matches(FILES_WE_WANT));
        assertTrue(CommonUtilities.getExtension(name1).matches(FILES_WE_WANT));
        assertTrue(CommonUtilities.getExtension(name3).matches(FILES_WE_WANT));
        assertFalse(CommonUtilities.getExtension(name4).matches(FILES_WE_WANT));
        assertFalse(CommonUtilities.getExtension(name5).matches(FILES_WE_WANT));
    }

}
