package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.types.TvShow;

public class ParseFileNameTest {

    private static final String[] RAW_FILENAMES = {
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.S01E09.WEB-DL.XviD-FUM[ettv].mkv",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.109.WEB-DL.XviD-FUM[ettv].mp4",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.9x09.WEB-DL.XviD-FUM[ettv].avi",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.9of9.WEB-DL.XviD-FUM[ettv].mpg",
            "C://Documents and Settings/Users/Darren/orange.is.the.new.black.part.9.WEB-DL.XviD-FUM[ettv].mpg",
    };

    private static final String[] RAW_FILENAMES_WITH_YEAR = {
            "C://Documents and Settings/Users/Darren/american.gothic.2016.S01E09.WEB-DL.XviD-FUM[ettv].mkv",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.109.WEB-DL.XviD-FUM[ettv].mp4",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.9x09.WEB-DL.XviD-FUM[ettv].avi",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.9of9.WEB-DL.XviD-FUM[ettv].mpg",
            "C://Documents and Settings/Users/Darren/american.gothic.2016.part.9.WEB-DL.XviD-FUM[ettv].mpg",
    };

    private static final String SINGLE_WORD_SHOW = "C://Documents and Settings/Users/Darren/atlanta.110-yestv.mkv";
    private static final String US_IN_SHOW_NAME = "C://Documents and Settings/Users/Darren/Secrets.and.Lies.US.S02E06.HDTV.x264-FLEET.mkv";

    @Test
    public void parseFileName_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES) {
            final TvShow tvShow = ParseFileName.parseFileName(filepath, false, true);
            assertNotNull(tvShow);
            assertEquals("Tv Raw Showname ", "orange is the new black", tvShow.getRawShowName());
            assertEquals("Episode Number ", "09", tvShow.getEpisodeNumber());
            assertEquals("Episode Season ", "01", tvShow.getSeasonNumber());
            assertEquals("Extension ", CommonUtilities.getExtension(filepath),
                    tvShow.getExtension());
        }
    }

    @Test
    @Ignore
    public void parseFileNameWithYear_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES_WITH_YEAR) {
            final TvShow tvShow = ParseFileName.parseFileName(filepath, false, true);
            assertNotNull(tvShow);
            assertEquals("Tv Raw Showname ", "american gothic", tvShow.getRawShowName());
            assertEquals("Episode Number ", "09", tvShow.getEpisodeNumber());
            assertEquals("Episode Season ", "01", tvShow.getSeasonNumber());
            assertEquals("TvEpisodeTitle", "The Oxbow", tvShow.getEpisodeTitle());
            assertEquals("Extension ", CommonUtilities.getExtension(filepath),
                    tvShow.getExtension());
        }
    }

    @Test
    public void parseFileNameWithTrailingDash_Test() throws Exception {
        final TvShow tvShow = ParseFileName.parseFileName(SINGLE_WORD_SHOW, false, true);
        assertNotNull(tvShow);
        assertEquals("Tv Raw Showname ", "atlanta", tvShow.getRawShowName());
        assertEquals("Episode Season ", "01", tvShow.getSeasonNumber());
        assertEquals("Episode Number ", "10", tvShow.getEpisodeNumber());
        assertEquals("TvEpisodeTitle", "The Jacket", tvShow.getEpisodeTitle());
        assertEquals("Extension ", "mkv", tvShow.getExtension());
    }

    @Test
    public void buildNewFileName_Test() throws Exception {
        final String buildFileName = ParseFileName.buildFileName("Doctor Who", "02", "03",
                "Episode Title", "avi");
        assertEquals("Build fileName", "Doctor Who - S02E03 - Episode Title.avi", buildFileName);
    }

    @Test
    public void getEpisodeTitleFromTvDB() throws Exception {
        final String episodeTitle = ParseFileName.getEpisodeTitleFromTvDB("Orange Is The New Black",
                "02", "03");
        assertEquals("Episode Title", "Hugs Can Be Deceiving", episodeTitle);
    }

    @Test
    public void buildDestinationFilepath_Test() throws Exception {
        final String destinationFilepath = ParseFileName.buildDestinationFilepath(
                "Doctor Who (2005)", "Doctor Who (2005) - S02E01 - An Unearthly Child 1.avi", "02");
        assertEquals("Build destinationFilepath",
                "//Desktop-plex/tv a-e/Doctor Who (2005)/Season 02/Doctor Who (2005) - S02E01 - An Unearthly Child 1.avi",
                destinationFilepath);
    }

    @Test
    public void formatRawShowName_Test() throws Exception {
        String formatRawShowName = ParseFileName.formatRawShowName("Girls", false);
        System.out.println(formatRawShowName);

        formatRawShowName = ParseFileName.formatRawShowName("Notorious", false);
        System.out.println(formatRawShowName);
    }
}
