package com.dprince.plex.tv.api.thetvdb;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;

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
        assertThat("Orange is the new black S02E05 title name", "Low Self Esteem City",
                equalTo(episodeTitle));
    }

    @Test
    public void getAllEpisodesForShow() throws Exception {
        final List<EpisodeData> episodeList = TheTvDbLookup.getAllEpisodesForShow(SHOW_ID);
        // assertThat("Orange is the new black episode list",
        // episodeList.size(), greaterThan(10));
        assertTrue(episodeList.size() > 50);
    }

    @Test
    public void getShowID_Test() throws Exception {
        final String showID = TheTvDbLookup.getShowID("Orange Is The New Black");
        assertThat("Get Show Id", SHOW_ID, equalTo(showID));
    }

    @Test
    public void getShowIdResponseFromShowTitle_Test() throws Exception {
        final ShowIdResponse showIdResponseFromShowTitle = TheTvDbLookup
                .getShowIdResponseFromShowTitle("Orange Is The New Black");
        assertNotNull(showIdResponseFromShowTitle);

        assertTrue(showIdResponseFromShowTitle instanceof ShowIdResponse);

        assertThat("ShowIdReponse", SHOW_ID,
                equalTo(showIdResponseFromShowTitle.getData().getId()));
    }

    @Test
    public void getShowIdResponseFromShowID_Test() throws Exception {
        final ShowIdResponse showIdResponseFromShowTitle = TheTvDbLookup
                .getShowIdResponseFromShowID(SHOW_ID);
        assertNotNull(showIdResponseFromShowTitle);

        assertTrue(showIdResponseFromShowTitle instanceof ShowIdResponse);

        assertThat("ShowIdReponse", SHOW_ID,
                equalTo(showIdResponseFromShowTitle.getData().getId()));
    }

    @Test
    public void getSeasonResponseData_Test() throws Exception {
        final SeasonResponseData seasonResponseData = TheTvDbLookup.getSeasonResponseData(SHOW_ID);
        assertNotNull(seasonResponseData);

        assertTrue(seasonResponseData instanceof SeasonResponseData);

        assertThat("SeasonResponseData", 4, equalTo(seasonResponseData.getAiredSeasons().size()));
    }
}
