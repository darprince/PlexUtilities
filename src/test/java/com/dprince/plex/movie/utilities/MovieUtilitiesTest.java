package com.dprince.plex.movie.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_MOVIE_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.KIDS_MOVIES;
import static com.dprince.plex.settings.PlexSettings.MOVIES_AI;
import static com.dprince.plex.settings.PlexSettings.MOVIES_JO;
import static com.dprince.plex.settings.PlexSettings.MOVIES_PS;
import static com.dprince.plex.settings.PlexSettings.MOVIES_TZ;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.dprince.plex.movie.MovieRenamer;
import com.dprince.plex.settings.PlexSettings;

public class MovieUtilitiesTest {

    private static final String THE_ACCOUNTANT_2016 = "The Accountant (2016)";
    private static final String JOHN_WICK_2014 = "John Wick (2014)";
    private static final String RESEVOIR_DOGS_1992 = "Resevoir Dogs (1992)";
    private static final String WORLD_WAR_Z_2013 = "World War Z (2013)";

    @Test
    public void getMovieDriveLocation() throws Exception {

        assertThat(MOVIES_AI).as("Mapped directories are not the same for Movies A-I")
                .isEqualTo(DESKTOP_SHARED_MOVIE_DIRECTORIES[0]);
        assertThat(MOVIES_JO).as("Mapped directories are not the same for Movies J-O")
                .isEqualTo(DESKTOP_SHARED_MOVIE_DIRECTORIES[1]);
        assertThat(MOVIES_PS).as("Mapped directories are not the same for Movies P-S")
                .isEqualTo(DESKTOP_SHARED_MOVIE_DIRECTORIES[2]);
        assertThat(MOVIES_TZ).as("Mapped directories are not the same for Movies T-Z")
                .isEqualTo(DESKTOP_SHARED_MOVIE_DIRECTORIES[3]);
        assertThat(KIDS_MOVIES).as("Mapped directories are not the same for Kids Movies")
                .isEqualTo(DESKTOP_SHARED_MOVIE_DIRECTORIES[4]);

        final String theAccountant = MovieRenamer.deriveDestinationFolder(THE_ACCOUNTANT_2016);
        assertThat(theAccountant)
                .as("Shared Directory for %s should be: %s, but was: %s", THE_ACCOUNTANT_2016,
                        PlexSettings.MOVIES_AI, theAccountant)
                .contains(DESKTOP_SHARED_MOVIE_DIRECTORIES[0]);

        final String johnWick = MovieRenamer.deriveDestinationFolder(JOHN_WICK_2014);
        assertThat(johnWick)
                .as("Shared Directory for %s should be: %s, but was: %s", JOHN_WICK_2014,
                        PlexSettings.MOVIES_JO, johnWick)
                .contains(DESKTOP_SHARED_MOVIE_DIRECTORIES[1]);

        final String resevoirDogs = MovieRenamer.deriveDestinationFolder(RESEVOIR_DOGS_1992);
        assertThat(resevoirDogs).as("Shared Directory for %s should be: %s, but was: %s",
                RESEVOIR_DOGS_1992, MOVIES_JO, resevoirDogs)
                .contains(DESKTOP_SHARED_MOVIE_DIRECTORIES[2]);

        final String worldWarZ = MovieRenamer.deriveDestinationFolder(WORLD_WAR_Z_2013);
        assertThat(worldWarZ)
                .as("Shared Directory for %s should be: %s, but was: %s", WORLD_WAR_Z_2013,
                        PlexSettings.MOVIES_JO, worldWarZ)
                .contains(DESKTOP_SHARED_MOVIE_DIRECTORIES[3]);
    }
}
