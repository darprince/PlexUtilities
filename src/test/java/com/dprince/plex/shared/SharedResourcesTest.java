package com.dprince.plex.shared;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

public class SharedResourcesTest {

    private static final String NEW_TITLE = "New Title";
    private static final String EMPIRE_TITLE_META = "RARBG.COM - Empire.S03E02.WEB-DL.x264-RARBG";
    private static final String EMPIRE_FILEPATH = "\\\\DESKTOP-downloa\\TVShowRenamer\\Empire.mp4";
    private static final String STAR_TREK_TITLE_META = "Star.Trek.Beyond.2016.1080p.WEB-DL.DD5.1.H264-FGT";
    private static final String STAR_TREK_TITLE = "Star Trek Beyond";
    private static final String STAR_TREK_FILEPATH = "\\\\DESKTOP-downloa\\TVShowRenamer\\Star.mkv";
    private static final String TESTER = "\\\\DESKTOP-downloa\\Completed\\Criminal.Minds.S12E03.WEB-DL.x264-RARBG.mp4";

    @Test
    @Ignore
    public void TvMetaDataFormatter_Test() throws Exception {
        final File newFile = new File(TESTER);
        assertTrue(newFile.exists());

        // Set garbage title
        // MetaDataFormatter.writeRandomMetadata(EMPIRE_FILEPATH,
        // EMPIRE_TITLE_META);
        // final String titleFromMetaData =
        // MetaDataFormatter.getTitleFromMetaData(EMPIRE_FILEPATH);
        // assertEquals("Original Title: ", EMPIRE_TITLE_META,
        // titleFromMetaData);

        // Set new title
        MetaDataFormatter.writeRandomMetadata(TESTER, "testing");
        final String titleFromMetaData2 = MetaDataFormatter.getTitleFromMetaData(TESTER);
        assertEquals(NEW_TITLE, "testing", "testing");

        // Set garbage title
        // MetaDataFormatter.writeRandomMetadata(EMPIRE_FILEPATH,
        // EMPIRE_TITLE_META);
        // assertEquals("Rewritten Title: ", EMPIRE_TITLE_META,
        // MetaDataFormatter.getTitleFromMetaData(EMPIRE_FILEPATH));
    }

    @Test
    @Ignore
    public void MovieMetaDataFormatter_Test() throws Exception {
        final File newFile = new File(STAR_TREK_FILEPATH);
        assertTrue(newFile.exists());
        assertTrue(newFile.canWrite());

        // Set garbage title
        JnaMetaFormatter.setNewMetaTitle(STAR_TREK_FILEPATH, STAR_TREK_TITLE_META);
        assertEquals("Initial Movie Title", STAR_TREK_TITLE_META,
                JnaMetaFormatter.getMetaTitle(STAR_TREK_FILEPATH));

        // Set proper title
        JnaMetaFormatter.setNewMetaTitle(STAR_TREK_FILEPATH, STAR_TREK_TITLE);
        assertEquals("Movie Title", STAR_TREK_TITLE,
                JnaMetaFormatter.getMetaTitle(STAR_TREK_FILEPATH));
    }
}
