package com.dprince.plex.tv.utilities;

import org.junit.Test;

public class TvUtilitiesTest {
    private static final String[] RAW_FILENAMES = {
            "Outcast.thing.S01E09.WEB-DL.XviD-FUM[ettv].mkv",
            "Outcast.thing.109.WEB-DL.XviD-FUM[ettv].mp4",
            "Outcast.thing.1x09.WEB-DL.XviD-FUM[ettv].avi",
            "Outcast.thing.0109.WEB-DL.1080p.XviD-FUM[ettv].mpg",
            "Outcast.thing.1of9.WEB-DL.XviD-FUM[ettv].mpg",
            "animal (2016).1of9.WEB-DL.XviD-FUM[ettv].mpg",
            "Outcast.thing.part.9.WEB-DL.XviD-FUM[ettv].mpg"
    };

    @Test
    public void parseFileNameTest() {
        for (final String filename : RAW_FILENAMES) {
            TvUtilities.parseFileName(filename);
        }
    }

}
