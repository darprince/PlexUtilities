package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.dprince.plex.tv.types.TvShow;

public class TvUtilitiesTest {
    private static final String[] RAW_FILENAMES = {
            "Outcast.thing.S01E09.WEB-DL.XviD-FUM[ettv].mkv",
            "Outcast.thing.109.WEB-DL.XviD-FUM[ettv].mp4",
            "Outcast.thing.1x09.WEB-DL.XviD-FUM[ettv].avi",
            "Outcast.thing.0109.WEB-DL.1080p.XviD-FUM[ettv].mpg",
            "Outcast.thing.1of9.WEB-DL.XviD-FUM[ettv].mpg",
            "outcast.thing (2016).1of9.WEB-DL.XviD-FUM[ettv].mpg",
            "Outcast.thing.part.9.WEB-DL.XviD-FUM[ettv].mpg"
    };

    @Test
    public void parseFileNameTest() {
        for (final String filename : RAW_FILENAMES) {
            final TvShow tvShow = TvUtilities.parseFileName(filename);
            assertEquals("Tv Raw Showname ", tvShow.getRawTvShowName(), "Outcast Thing");
            assertEquals("Episode Number ", tvShow.getTvEpisodeNumber(), "09");
            assertEquals("Episode Season ", tvShow.getTvSeasonNumber(), "01");
            System.out.println(filename + " passed.");
            System.out.println(tvShow.getYear());
        }
    }

}
