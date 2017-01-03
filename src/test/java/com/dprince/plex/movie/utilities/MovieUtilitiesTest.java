package com.dprince.plex.movie.utilities;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class MovieUtilitiesTest {

    @Test
    public void getMovieDriveLocation() throws Exception {
        assertTrue("Movies A-I" == MovieUtilities.getMovieDriveLocation("The Accountant (2016)"));
        assertTrue("Movies J-S" == MovieUtilities.getMovieDriveLocation("John Wick (2014)"));
        assertTrue("Movies T-Z" == MovieUtilities.getMovieDriveLocation("World War Z (2013)"));
        assertTrue("Kids Movies" == MovieUtilities.getMovieDriveLocation("Toy Story (1995)"));
    }
}
