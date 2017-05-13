package com.dprince.plex.movie.utilities;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dprince.plex.settings.PlexSettings;

public class MovieUtilitiesTest {

    @Test
    public void getMovieDriveLocation() throws Exception {
        System.out.println(MovieUtilities.getMovieDriveLocation("Toy Story (1995)"));
        assertTrue(PlexSettings.DESKTOP_SHARED_MOVIE_DIRECTORIES[0] == MovieUtilities
                .getMovieDriveLocation("The Accountant (2016)"));
        assertTrue(PlexSettings.DESKTOP_SHARED_MOVIE_DIRECTORIES[1] == MovieUtilities
                .getMovieDriveLocation("John Wick (2014)"));
        assertTrue(PlexSettings.DESKTOP_SHARED_MOVIE_DIRECTORIES[2] == MovieUtilities
                .getMovieDriveLocation("World War Z (2013)"));
        assertTrue(PlexSettings.DESKTOP_SHARED_MOVIE_DIRECTORIES[3] == MovieUtilities
                .getMovieDriveLocation("Toy Story (1995)"));

    }
}
