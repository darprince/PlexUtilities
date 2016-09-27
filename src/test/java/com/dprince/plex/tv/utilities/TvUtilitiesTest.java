package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
    private static final String FILEPATH = "C:\\Documents and Settings\\Users\\Darren\\Outcast.thing.1x09.WEB-DL.XviD-FUM[ettv].avi";
    private static final TvShow TVSHOW_AHS = new TvShow("american horror story", "2016", "02", "02",
            "avi");

    @Test
    public void parseFileName_Test() throws Exception {
        for (final String filename : RAW_FILENAMES) {
            final TvShow tvShow = TvUtilities.parseFileName(filename);
            assertEquals("Tv Raw Showname ", tvShow.getRawTvShowName(), "Outcast Thing");
            assertEquals("Episode Number ", tvShow.getTvEpisodeNumber(), "09");
            assertEquals("Episode Season ", tvShow.getTvSeasonNumber(), "01");
            assertNotNull(tvShow.getExtension());
        }
    }

    @Test
    public void setOriginalFilepath_Test() throws Exception {
        TVSHOW_AHS.setOriginalFilepath(FILEPATH);
        assertEquals("Original filepath", TVSHOW_AHS.getOriginalFilePath(), FILEPATH);
    }

    @Test
    public void setFormattedTvShowName_Test() throws Exception {
        TvUtilities.setFormattedTvShowname(TVSHOW_AHS);
        assertEquals("Formatted TvShowname", TVSHOW_AHS.getFormattedTvShowName(),
                "American Horror Story");
    }

    @Test
    public void getTvEpisodeTitleFromAPI_Test() throws Exception {
        TvUtilities.setFormattedTvShowname(TVSHOW_AHS);
        TVSHOW_AHS.setFormattedTvShowName("American Horror Story");
        TvUtilities.getTvEpisodeTitleFromAPI(TVSHOW_AHS);
        assertEquals("TvEpisodeTitle", TVSHOW_AHS.getTvEpisodeTitle(), "Tricks and Treats");

    }
}
