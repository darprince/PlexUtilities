package com.dprince.plex.tv.api.thetvdb.utilities;

import static com.dprince.plex.tv.api.thetvdb.TheTvDbLookup.AIRED_EPISODE;
import static com.dprince.plex.tv.api.thetvdb.TheTvDbLookup.EPISODES_SEARCH_PREFIX;
import static com.dprince.plex.tv.api.thetvdb.TheTvDbLookup.SERIES_CONTEXT;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeNameResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiCallsTest {

    @Test
    public void hitTvDbAPI_Test() throws Exception {
        // data for Breaking Bad
        final String showID = "81189";
        final String seasonNumber = "2";
        final String episodeNumber = "4";

        final String queryString = SERIES_CONTEXT + showID + EPISODES_SEARCH_PREFIX + seasonNumber
                + AIRED_EPISODE + episodeNumber;
        final String subject = "ShowID: " + showID;
        final String tvDBresponse = ApiCalls.hitTvDbAPI(queryString, subject);

        assertNotNull(tvDBresponse);

        final ObjectMapper mapper = new ObjectMapper();
        final EpisodeNameResponse episodeNameResponse = mapper.readValue(tvDBresponse,
                EpisodeNameResponse.class);

        assertTrue(episodeNameResponse instanceof EpisodeNameResponse);
    }
}
