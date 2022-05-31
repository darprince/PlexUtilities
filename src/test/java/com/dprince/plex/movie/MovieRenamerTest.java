package com.dprince.plex.movie;

import org.junit.Ignore;
import org.junit.Test;

public class MovieRenamerTest {

    @Test
    public void getMovieName_Test() throws Exception {
        final String foldername = "C:\\Users\\Darren\\War.Dogs.2016.1080p.BluRay.x264-DRONES[rarbg]";
        final String movieName = MovieRenamerBackup.getMovieNameFromFolder(foldername);
        // assertThat("Movie name is formatted correctly", "War Dogs",
        // equalTo(movieName));
    }

    @Test
    @Ignore
    public void test() throws Exception {
        MovieRenamerBackup.renameMovieFromFolder(
                "C:\\FileManipulation\\PlexUtilsTest\\MovieName.fjkj.2000.ddd",
                false, false);
    }
}
