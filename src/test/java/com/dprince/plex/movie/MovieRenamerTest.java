package com.dprince.plex.movie;

import org.junit.Test;

public class MovieRenamerTest {

    @Test
    public void getMovieName_Test() throws Exception {
        final String foldername = "C:\\Users\\Darren\\War.Dogs.2016.1080p.BluRay.x264-DRONES[rarbg]";
        final String movieName = MovieRenamer.getMovieNameFromFolder(foldername);
        // assertThat("Movie name is formatted correctly", "War Dogs",
        // equalTo(movieName));
    }
}
