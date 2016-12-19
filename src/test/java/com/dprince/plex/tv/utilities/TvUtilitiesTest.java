package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.DOWNLOADS_DIRECTORY;
import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;
import static com.dprince.plex.settings.PlexSettings.FILES_WE_WANT;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.dprince.plex.common.CommonUtilities;
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

    private static final String SINGLE_WORD_SHOW = "C://Documents and Settings/Users/Darren/atlanta.110-yestv.mkv";
    private static final String US_IN_SHOW_NAME = "C://Documents and Settings/Users/Darren/Secrets.and.Lies.US.S02E06.HDTV.x264-FLEET.mkv";

    @Test
    public void parseFileName_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES) {
            final TvShow tvShow = TvUtilities.parseFileName(filepath);
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
            final TvShow tvShow = TvUtilities.parseFileName(filepath);
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
        final TvShow tvShow = TvUtilities.parseFileName(SINGLE_WORD_SHOW);
        assertNotNull(tvShow);
        assertEquals("Tv Raw Showname ", "atlanta", tvShow.getRawShowName());
        assertEquals("Episode Season ", "01", tvShow.getSeasonNumber());
        assertEquals("Episode Number ", "10", tvShow.getEpisodeNumber());
        assertEquals("TvEpisodeTitle", "The Jacket", tvShow.getEpisodeTitle());
        assertEquals("Extension ", "mkv", tvShow.getExtension());
    }

    @Test
    public void buildNewFileName_Test() throws Exception {
        final String buildFileName = TvUtilities.buildFileName("Doctor Who", "02", "03",
                "Episode Title", "avi");
        assertEquals("Build fileName", "Doctor Who - S02E03 - Episode Title.avi", buildFileName);
    }

    @Test
    @Ignore
    public void formatRawShowName() throws Exception {

    }

    @Test
    public void getEpisodeTitleFromTvDB() throws Exception {
        final String episodeTitle = TvUtilities.getEpisodeTitleFromTvDB("Orange Is The New Black",
                "02", "03");
        assertEquals("Episode Title", "Hugs Can Be Deceiving", episodeTitle);
    }

    @Test
    public void buildDestinationFilepath_Test() throws Exception {
        final String destinationFilepath = TvUtilities.buildDestinationFilepath("Doctor Who (2005)",
                "Doctor Who (2005) - S02E01 - An Unearthly Child 1.avi", "02");
        assertEquals("Build destinationFilepath",
                "//Desktop-plex/tv a-e/Doctor Who (2005)/Season 02/Doctor Who (2005) - S02E01 - An Unearthly Child 1.avi",
                destinationFilepath);
    }

    @Test
    public void getShowDriveLocation_Test() throws Exception {
        final String[] formattedFileNames = {
                "Breaking Bad", "Grandfathered", "Manhattan Love Story", "The Walking Dead"
        };

        for (int i = 0; i < formattedFileNames.length; i++) {
            assertEquals(formattedFileNames[i] + " Drive Location", DESKTOP_SHARED_DIRECTORIES[i],
                    TvUtilities.getShowDriveLocation(formattedFileNames[i]));
        }
    }

    @Test
    public void getShowIDFromJson() throws Exception {
        String showIDFromJson = TvUtilities.getShowIDFromJson("Fuller House");
        assertEquals("Show ID", "301236", showIDFromJson);

        showIDFromJson = TvUtilities.getShowIDFromJson("Happy Days");
        assertEquals("Show ID", "74475", showIDFromJson);

        showIDFromJson = TvUtilities.getShowIDFromJson("800 Words");
        assertEquals("Show ID", "300667", showIDFromJson);

        showIDFromJson = TvUtilities.getShowIDFromJson("Good Girls Revolt");
        assertEquals("Show ID", "315419", showIDFromJson);
    }

    @Test
    public void editMetaData_Test() throws Exception {

    }

    @Test
    public void moveEpisodeFile_Test() throws Exception {

    }

    @Test
    @Ignore
    public void batchMoveEpisodes_Test() throws Exception {
        final Set<File> batchMoveEpisodes = TvUtilities.batchMoveEpisodes(DOWNLOADS_DIRECTORY);
        // TvUtilities.deleteEmptyShowFolders(batchMoveEpisodes);
    }

    // TODO: move to other test folder
    @Test
    @Ignore
    public void episodeExists_Test() throws Exception {
        final String episodeExists = TvFileUtilities.episodeExists(
                PLEX_PREFIX
                        + "/tv m-s/Orange Is The New Black/Season 02/Orange Is The New Black - S02E03.avi",
                "02", "03");
        assertNotNull(episodeExists);
    }

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

    @Test
    public void formatRawShowName_Test() throws Exception {
        String formatRawShowName = TvUtilities.formatRawShowName("Girls");
        System.out.println(formatRawShowName);

        formatRawShowName = TvUtilities.formatRawShowName("Notorious");
        System.out.println(formatRawShowName);
    }
}
