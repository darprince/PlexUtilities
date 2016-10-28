package com.dprince.plex.tv.utilities;

import org.junit.BeforeClass;

public class OldTvUtilitiesTest {
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

    @BeforeClass
    public static void createFoldersFileIfNotThere() {
        TvFileUtilities.createFoldersFile();
    }

    // @Test
    // public void setOriginalFilepath_Test() throws Exception {
    // final TvShow tvShow = TVSHOW_AHS;
    // tvShow.setOriginalFilepath(FILEPATH_OITNB);
    // assertEquals("Original filepath", tvShow.getOriginalFilePath(),
    // FILEPATH_OITNB);
    // }
    //
    // @Test
    // public void setFormattedTvShowName_Test() throws Exception {
    // final TvShow tvShow = TVSHOW_AHS;
    // TvUtilities.formatRawTvShowName(tvShow.getRawTvShowName());
    // assertEquals("Formatted TvShowname", tvShow.getFormattedTvShowName(),
    // "Orange Is The New Black");
    // }
    //
    // @Test
    // public void getTvEpisodeTitleFromAPI_Test() throws Exception {
    // final TvShow tvShow = TVSHOW_AHS;
    // TvUtilities.formatRawTvShowName(tvShow.getRawTvShowName());
    // tvShow.setFormattedTvShowName("Orange Is The New Black");
    // TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
    // assertEquals("TvEpisodeTitle", "Looks Blue, Tastes Red",
    // tvShow.getTvEpisodeTitle());
    // }
    //
    // @Test
    // public void setNewFilename_Test() throws Exception {
    // final TvShow tvShow = TVSHOW_AHS;
    // TvUtilities.formatRawTvShowName(tvShow.getRawTvShowName());
    // tvShow.setFormattedTvShowName("Orange Is The New Black");
    // TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
    // TvUtilities.setNewFilename(tvShow);
    // assertEquals("New Filename",
    // "Orange Is The New Black - S02E02 - Looks Blue, Tastes Red.avi",
    // tvShow.getNewFilename());
    // }
    //
    // @Test
    // public void testTvDirectories() throws Exception {
    // for (final String dir : DESKTOP_SHARED_DIRECTORIES) {
    // final File file = new File(PLEX_PREFIX + dir);
    // assertTrue(file.exists());
    // }
    // }
    //
    // @Test
    // public void createNewSeasonFolder_Test() throws Exception {
    // final String folderLocation = DOWNLOADS_PREFIX +
    // "TVShowRenamer/TestFolder.204.kljf.avi";
    // TvFileUtilities.createNewSeasonFolder(folderLocation);
    // final File file = new File(PLEX_PREFIX + "Tv a-e/TestFolder/Season 02");
    // assertTrue(file.exists());
    // file.delete();
    // assertFalse(file.exists());
    // }
    //
    // @Test
    // @Ignore
    // public void runMKVEditorForTvShow_Test() throws Exception {
    // final String filepath = PLEX_PREFIX + "Tv a-e/Deadbeat/Season 03/Deadbeat
    // - S03E01.mp4";
    // File file = new File(filepath);
    // // assertTrue(file.exists());
    // final TvShow tvShow = TvUtilities.parseFileName(filepath);
    // file = new File(MKVPROPEDIT_LOCATION);
    // assertTrue(file.exists());
    //
    // TvUtilities.editMetaData(tvShow);
    // // TODO: need to assert that the title was something before and after
    // // change
    // }
    //
    // @Test
    // public void seasonFolderExists_Test() throws Exception {
    // assertTrue(TVSHOW_AHS.getTvSeasonNumber().equals("02"));
    // assertTrue(TVSHOW_AHS.getTvEpisodeNumber().equals("02"));
    //
    // TVSHOW_AHS.setNewFilepath(
    // "\\\\Desktop-plex\\Tv m-s\\Orange Is The New Black\\Season 02\\Orange Is
    // The New Black - S02E02.mp4");
    //
    // final boolean exists = TvFileUtilities.seasonFolderExists(TVSHOW_AHS);
    // assertTrue(exists);
    // }
    //
    // @Test
    // public void episodeExistsTrue_Test() throws Exception {
    // assertTrue(TVSHOW_AHS.getTvSeasonNumber().equals("02"));
    // assertTrue(TVSHOW_AHS.getTvEpisodeNumber().equals("02"));
    //
    // TVSHOW_AHS.setNewFilepath(
    // "\\\\Desktop-plex\\Tv m-s\\Orange Is The New Black\\Season 02\\Orange Is
    // The New Black - S02E02.mp4");
    //
    // assertTrue(TvFileUtilities.episodeExists(TVSHOW_AHS));
    // }
    //
    // @Test
    // public void episodeExistsFalse_Test() throws Exception {
    // TVSHOW_AHS.setTvEpisodeNumber("22");
    //
    // assertTrue(TVSHOW_AHS.getTvSeasonNumber().equals("02"));
    // assertTrue(TVSHOW_AHS.getTvEpisodeNumber().equals("22"));
    //
    // TVSHOW_AHS.setNewFilepath(
    // "\\\\Desktop-plex\\Tv m-s\\Orange Is The New Black\\Season 02\\Orange is
    // the New Black - S02E22.mp4");
    //
    // assertFalse(TvFileUtilities.episodeExists(TVSHOW_AHS));
    // }
    //
    // @Test
    // @Ignore
    // public void createShowFolder_Test() throws Exception {
    // TVSHOW_AHS.setRawTvShowName("billy show");
    // // TvFileUtilities.createShowFolder(TVSHOW_AHS);
    // }
    //
    // @Test
    // @Ignore
    // public void extractTvFiles_Test() throws Exception {
    // TvFileUtilities.extractTvFiles();
    // }
    //
    // @Test
    // @Ignore
    // public void runMKVEditorForMovie_Test() throws Exception {
    // MovieUtilities.runMKVEditorForMovie(
    // "\\\\Desktop-plex\\Tv m-s\\new show name\\Season 02\\Orange is the New
    // Black - S02E22.mp4");
    //
    // }
}
