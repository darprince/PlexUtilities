package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.dprince.plex.tv.types.TvShow;

public class TvUtilitiesTest {
    private static final String[] RAW_FILENAMES = {
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.S01E09.WEB-DL.XviD-FUM[ettv].mkv",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.109.WEB-DL.XviD-FUM[ettv].mp4",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.1x09.WEB-DL.XviD-FUM[ettv].avi",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.1of9.WEB-DL.XviD-FUM[ettv].mpg",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.part.9.WEB-DL.XviD-FUM[ettv].mpg",
    };

    private static final String[] RAW_FILENAMES_WITH_YEAR = {
            "C://Documents and Settings/Users/Darren/american.gothic.2016.S01E09.WEB-DL.XviD-FUM[ettv].mkv",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.109.WEB-DL.XviD-FUM[ettv].mp4",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.1x09.WEB-DL.XviD-FUM[ettv].avi",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.1of9.WEB-DL.XviD-FUM[ettv].mpg",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.part.9.WEB-DL.XviD-FUM[ettv].mpg",
    };

    private static final String FILEPATH_AG = "C://Documents and Settings/Users/Darren/american.gothic.2016.1x06.WEB-DL.XviD-FUM[ettv].avi";
    private static final String FILEPATH_OITNB = "C://Documents and Settings/Users/Darren/orange.is.the.new.black.1x06.WEB-DL.XviD-FUM[ettv].avi";

    private static final TvShow TVSHOW_AHS = new TvShow("orange is the new black", FILEPATH_OITNB,
            null, "02", "02", "avi");

    @Test
    public void parseFileName_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES) {
            final TvShow tvShow = TvUtilities.parseFileName(filepath);
            assertNotNull(tvShow);
            // assertEquals("Tv Raw Showname ", "Orange Is The New Black",
            // tvShow.getRawTvShowName());
            // assertEquals("Episode Number ", "09",
            // tvShow.getTvEpisodeNumber());
            // assertEquals("Episode Season ", "01",
            // tvShow.getTvSeasonNumber());
            // assertNotNull(tvShow.getExtension());
        }
    }

    @Test
    public void parseFileNameWithYear_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES_WITH_YEAR) {
            final TvShow tvShow = TvUtilities.parseFileName(filepath);
            assertNotNull(tvShow);
            TvUtilities.setFormattedTvShowname(tvShow);
            TvUtilities.getTvEpisodeTitleFromAPI(tvShow);
            assertEquals("Tv Raw Showname ", "American Gothic", tvShow.getRawTvShowName());
            assertEquals("Episode Number ", "09", tvShow.getTvEpisodeNumber());
            assertEquals("Episode Season ", "01", tvShow.getTvSeasonNumber());
            assertEquals("Year ", "2016", tvShow.getYear());
            assertEquals("TvEpisodeTitle", "The Oxbow", tvShow.getTvEpisodeTitle());
            assertNotNull(tvShow.getExtension());
        }
    }

    @Test
    public void setOriginalFilepath_Test() throws Exception {
        final TvShow tvShow = TVSHOW_AHS;
        tvShow.setOriginalFilepath(FILEPATH_OITNB);
        assertEquals("Original filepath", tvShow.getOriginalFilePath(), FILEPATH_OITNB);
    }

    @Test
    public void setFormattedTvShowName_Test() throws Exception {
        final TvShow tvShow = TVSHOW_AHS;
        TvUtilities.setFormattedTvShowname(tvShow);
        assertEquals("Formatted TvShowname", tvShow.getFormattedTvShowName(),
                "Orange Is The New Black");
    }

    @Test
    public void getTvEpisodeTitleFromAPI_Test() throws Exception {
        final TvShow tvShow = TVSHOW_AHS;
        TvUtilities.setFormattedTvShowname(tvShow);
        tvShow.setFormattedTvShowName("Orange Is The New Black");
        TvUtilities.getTvEpisodeTitleFromAPI(tvShow);
        assertEquals("TvEpisodeTitle", "Looks Blue, Tastes Red", tvShow.getTvEpisodeTitle());
    }

    @Test
    public void setNewFilename_Test() throws Exception {
        final TvShow tvShow = TVSHOW_AHS;
        TvUtilities.setFormattedTvShowname(tvShow);
        tvShow.setFormattedTvShowName("Orange Is The New Black");
        TvUtilities.getTvEpisodeTitleFromAPI(tvShow);
        TvUtilities.setNewFilename(tvShow);
        assertEquals("New Filename",
                "Orange Is The New Black - S02E02 - Looks Blue, Tastes Red.avi",
                tvShow.getNewFilename());
    }

    @Test
    public void setNewFilepath_Test() throws Exception {
        final TvShow tvShow = new TvShow("orange.is.the.new.black", FILEPATH_OITNB, null, "06",
                "01", "avi");
        tvShow.setFormattedTvShowName("Orange Is The New Black");
        TvUtilities.getTvEpisodeTitleFromAPI(tvShow);
        TvUtilities.setNewFilename(tvShow);
        TvUtilities.setNewFilepath(tvShow);
        assertEquals("New Filepath",
                "//DESKTOP-PLEX/tv j-s/Orange Is The New Black/Season 01/Orange Is The New Black - S01E06 - WAC Pack.avi",
                tvShow.getNewFilepath());
    }
}
