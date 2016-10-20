package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.BeforeClass;
import org.junit.Ignore;
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
            null, "02", "02", ".avi");

    @BeforeClass
    public static void createFoldersFileIfNotThere() {
        TvFileUtilities.createFoldersFile();
    }

    @Test
    public void parseFileName_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES) {
            final TvShow tvShow = TvUtilities.parseFileName(filepath);
            assertNotNull(tvShow);
            assertEquals("Tv Raw Showname ", "Orange Is The New Black", tvShow.getRawTvShowName());
            assertEquals("Episode Number ", "09", tvShow.getTvEpisodeNumber());
            assertEquals("Episode Season ", "01", tvShow.getTvSeasonNumber());
            assertNotNull(tvShow.getExtension());
        }
    }

    @Test
    public void parseFileNameWithYear_Test() throws Exception {
        for (final String filepath : RAW_FILENAMES_WITH_YEAR) {
            final TvShow tvShow = TvUtilities.parseFileName(filepath);
            assertNotNull(tvShow);
            TvUtilities.setFormattedTvShowname(tvShow);
            TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
            assertEquals("Tv Raw Showname ", "American Gothic", tvShow.getRawTvShowName());
            assertEquals("Episode Number ", "09", tvShow.getTvEpisodeNumber());
            assertEquals("Episode Season ", "01", tvShow.getTvSeasonNumber());
            assertEquals("Year ", "2016", tvShow.getYear());
            assertEquals("TvEpisodeTitle", "The Oxbow", tvShow.getTvEpisodeTitle());
            assertNotNull(tvShow.getExtension());
        }
    }

    @Test
    public void parseFormattedFilename_Test() throws Exception {
        final String filepath = "C:\\directory\\The Big Bang Theory - S03E04.mkv";
        final TvShow tvShow = TvUtilities.parseFileName(filepath);

        assertEquals("TvShowName", "The Big Bang Theory", tvShow.getFormattedTvShowName());
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
        TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
        assertEquals("TvEpisodeTitle", "Looks Blue, Tastes Red", tvShow.getTvEpisodeTitle());
    }

    @Test
    public void setNewFilename_Test() throws Exception {
        final TvShow tvShow = TVSHOW_AHS;
        TvUtilities.setFormattedTvShowname(tvShow);
        tvShow.setFormattedTvShowName("Orange Is The New Black");
        TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
        TvUtilities.setNewFilename(tvShow);
        assertEquals("New Filename",
                "Orange Is The New Black - S02E02 - Looks Blue, Tastes Red.avi",
                tvShow.getNewFilename());
    }

    @Test
    public void setNewFilepath_Test() throws Exception {
        final TvShow tvShow = new TvShow("orange.is.the.new.black", FILEPATH_OITNB, null, "06",
                "01", ".avi");
        tvShow.setFormattedTvShowName("Orange Is The New Black");
        TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
        TvUtilities.setNewFilename(tvShow);
        TvUtilities.setNewFilepath(tvShow);
        assertEquals("New Filepath",
                "//DESKTOP-PLEX/tv m-s/Orange Is The New Black/Season 01/Orange Is The New Black - S01E06 - WAC Pack.avi",
                tvShow.getNewFilepath());
    }

    @Test
    public void testTvDirectories() throws Exception {
        final String[] directories = TvFileUtilities.DESKTOP_SHARED_DIRECTORIES;
        final String plexPrefix = "\\\\Desktop-plex\\";

        for (final String dir : directories) {
            final File file = new File(plexPrefix + dir);
            assertTrue(file.exists());
        }
    }

    @Test
    public void createNewSeasonFolder_Test() throws Exception {
        final String folderLocation = "\\\\Desktop-downloa\\TVShowRenamer\\TestFolder.204.kljf.avi";
        TvFileUtilities.createNewSeasonFolder(folderLocation);
        final File file = new File("\\\\Desktop-plex\\Tv a-e\\TestFolder\\Season 02");
        assertTrue(file.exists());
        file.delete();
        assertFalse(file.exists());
    }

    @Test
    @Ignore
    public void runMKVEditorForTvShow_Test() throws Exception {
        final String filepath = "\\\\Desktop-plex\\Tv a-e\\Deadbeat\\Season 03\\Deadbeat - S03E01.mp4";
        File file = new File(filepath);
        // assertTrue(file.exists());
        final TvShow tvShow = TvUtilities.parseFileName(filepath);
        file = new File("\\\\Desktop-downloa\\TVShowRenamer\\mkvpropedit.exe");
        assertTrue(file.exists());

        TvUtilities.editMetaData(tvShow);
        // TODO: need to assert that the title was something before and after
        // change
    }

    @Test
    public void seasonFolderExists_Test() throws Exception {
        assertTrue(TVSHOW_AHS.getTvSeasonNumber().equals("02"));
        assertTrue(TVSHOW_AHS.getTvEpisodeNumber().equals("02"));

        TVSHOW_AHS.setNewFilepath(
                "\\\\Desktop-plex\\Tv m-s\\Orange Is The New Black\\Season 02\\Orange Is The New Black - S02E02.mp4");

        final boolean exists = TvFileUtilities.seasonFolderExists(TVSHOW_AHS);
        assertTrue(exists);
    }

    @Test
    public void episodeExistsTrue_Test() throws Exception {
        assertTrue(TVSHOW_AHS.getTvSeasonNumber().equals("02"));
        assertTrue(TVSHOW_AHS.getTvEpisodeNumber().equals("02"));

        TVSHOW_AHS.setNewFilepath(
                "\\\\Desktop-plex\\Tv m-s\\Orange Is The New Black\\Season 02\\Orange Is The New Black - S02E02.mp4");

        assertTrue(TvFileUtilities.episodeExists(TVSHOW_AHS));
    }

    @Test
    public void episodeExistsFalse_Test() throws Exception {
        TVSHOW_AHS.setTvEpisodeNumber("22");

        assertTrue(TVSHOW_AHS.getTvSeasonNumber().equals("02"));
        assertTrue(TVSHOW_AHS.getTvEpisodeNumber().equals("22"));

        TVSHOW_AHS.setNewFilepath(
                "\\\\Desktop-plex\\Tv m-s\\Orange Is The New Black\\Season 02\\Orange is the New Black - S02E22.mp4");

        assertFalse(TvFileUtilities.episodeExists(TVSHOW_AHS));
    }

    @Test
    @Ignore
    public void showFolderExists_Test() throws Exception {
        TVSHOW_AHS.setNewFilepath(
                "\\\\Desktop-plex\\Tv m-s\\new show name\\Season 02\\Orange is the New Black - S02E22.mp4");
        TvFileUtilities.showFolderExists(TVSHOW_AHS);
    }

    @Test
    @Ignore
    public void createShowFolder_Test() throws Exception {
        TVSHOW_AHS.setRawTvShowName("billy show");
        TvFileUtilities.createShowFolder(TVSHOW_AHS);
    }

    @Test
    public void extractTvFiles_Test() throws Exception {
        TvFileUtilities.extractTvFiles();
    }

    @Test
    @Ignore
    public void runMKVEditorForMovie_Test() throws Exception {
        TvFileUtilities.runMKVEditorForMovie(
                "\\\\Desktop-plex\\Tv m-s\\new show name\\Season 02\\Orange is the New Black - S02E22.mp4");

    }
}
