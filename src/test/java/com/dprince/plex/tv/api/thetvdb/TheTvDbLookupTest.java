package com.dprince.plex.tv.api.thetvdb;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonResponseData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowIdResponse;

public class TheTvDbLookupTest {
    // showID for Orange is the new black
    private static final String SHOW_ID = "264586";
    private static final String SEASON_NUMBER = "2";
    private static final String EPISODE_NUMBER = "5";

    @Test
    public void getEpisodeTitle_Test() throws Exception {
        final String episodeTitle = TheTvDbLookup.getEpisodeTitle(SHOW_ID, SEASON_NUMBER,
                EPISODE_NUMBER);
        assertThat(episodeTitle)
                .as("Episode title should be 'Low Self Esteem City', but was " + episodeTitle)
                .isEqualTo("Low Self Esteem City");
    }

    @Test
    public void getAllEpisodesForShow() throws Exception {
        final List<EpisodeData> episodeList = TheTvDbLookup.getAllEpisodesForShow(SHOW_ID);
        assertThat(episodeList.size())
                .as("Episode list size should be greater than 50, but was " + episodeList.size())
                .isGreaterThan(50);
    }

    @Test
    public void getShowID_Test() throws Exception {
        final String showID = TheTvDbLookup.getShowID("Orange Is The New Black");
        assertThat(showID).as("ShowId should be " + SHOW_ID + ", but was " + showID)
                .isEqualTo(SHOW_ID);
    }

    @Test
    public void getShowIdResponseFromShowID_Test() throws Exception {
        final ShowIdResponse showIdResponseFromShowTitle = TheTvDbLookup
                .getShowIdResponseFromShowID(SHOW_ID);
        assertNotNull(showIdResponseFromShowTitle);
        assertThat(showIdResponseFromShowTitle).as("Should be instance of ShowIdResponse.")
                .isInstanceOf(ShowIdResponse.class);

        assertThat(showIdResponseFromShowTitle.getData().getId()).as("ShowId should be " + SHOW_ID
                + ", but was " + showIdResponseFromShowTitle.getData().getId()).isEqualTo(SHOW_ID);
    }

    @Test
    public void getSeasonResponseData_Test() throws Exception {
        final SeasonResponseData seasonResponseData = TheTvDbLookup.getSeasonResponseData(SHOW_ID);
        assertThat(seasonResponseData).as("SeasonResponseData is null").isNotNull();
        assertThat(seasonResponseData).as("Should be instance of SeasonResponseData.")
                .isInstanceOf(SeasonResponseData.class);

        assertThat(seasonResponseData.getAiredSeasons().size())
                .as("Number of seasons should be 6, but was "
                        + seasonResponseData.getAiredSeasons().size())
                .isEqualTo(6);
    }

    @Test
    @Ignore
    public void parentCreateShowDataJSONForShow_Test() throws Exception {
        // TheTvDbLookup.parentCreateShowDataJSONForShow(
        // new File("\\\\Desktop-downloa\\TVShowRenamer\\Sick Of It"), null);

        TheTvDbLookup.parentCreateShowDataJSONForShow(
                new File("\\\\Desktop-downloa\\TVShowRenamer\\Madam Secretary"), null);

        // TheTvDbLookup.parentCreateShowDataJSONForShow(new File("N:\\Sick Of
        // It"), null);

    }
}
