package com.dprince.plex.tv.api.thetvdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.api.thetvdb.auth.Authorization;
import com.dprince.plex.tv.api.thetvdb.types.Episode;
import com.dprince.plex.tv.api.thetvdb.types.EpisodeNameResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TheTvDbLookup {

    private static final String FIELD_JSON_LAST = "last";
    private static final String FIELD_JSON_LINKS = "links";
    private static final String FIELD_JSON_DATA = "data";

    private static final String FIELD_EPISODE_NAME = "episodeName";
    private static final String FIELD_EPISODE_NUMBER = "airedEpisodeNumber";
    private static final String FIELD_SEASON_NUMBER = "airedSeason";

    private final static String HOST = "https://api.thetvdb.com";
    private final static String SERIES_CONTEXT = "/series/";
    private final static String EPISODES_CONTEXT = "/episodes";
    private static final String SEARCH_SERIES_NAME = "/search/series?name=";
    private final static String EPISODES_SEARCH_PREFIX = "/episodes/query?airedSeason=";

    private static final Logger LOG = Logging.getLogger(TheTvDbLookup.class);

    public static void main(String[] args) {
        System.out.println(getEpisodeName("breaking bad", "02", "04"));
    }

    /**
     * Top level method to return a tv shows episode name
     *
     * @param showTitle
     *            The name of the show being queried.
     * @param episodeNumber
     * @param seasonNumber
     * @return the name of the episode being queried, null otherwise.
     */
    @Nullable
    public static String getEpisodeName(@NonNull String showTitle, @NonNull String episodeNumber,
            @NonNull String seasonNumber) {

        final String showID = getShowID(showTitle);
        if (showID == null) {
            return null;
        }

        final String queryString = SERIES_CONTEXT + showID + EPISODES_SEARCH_PREFIX + seasonNumber
                + "&airedEpisode=" + episodeNumber;

        final String response = hitTvDbAPI(queryString);

        final ObjectMapper mapper = new ObjectMapper();
        EpisodeNameResponse episodeNameResponse = null;
        try {
            episodeNameResponse = mapper.readValue(response, EpisodeNameResponse.class);
        } catch (final IOException e) {
            LOG.error("Failed to create EpisodeNameResponse object,", e);
            return null;
        }

        return episodeNameResponse.getData().get(0).getEpisodeName();
    }

    public static List<Episode> getAllEpisodesForShow(String showName) {
        final String showID = getShowID(showName);
        final String queryString = SERIES_CONTEXT + showID + EPISODES_CONTEXT;

        String response = hitTvDbAPI(queryString);

        LOG.info("Parsing response");
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(response.toString()).getAsJsonObject();

        // get total number of pages
        int currentPage = 1;
        final int lastPage = Integer.parseInt(
                result.getAsJsonObject(FIELD_JSON_LINKS).get(FIELD_JSON_LAST).getAsString());

        // get episodes
        JsonArray jsonArrayData = result.getAsJsonArray(FIELD_JSON_DATA);

        final List<Episode> episodeList = new ArrayList<>();
        int arraySize = jsonArrayData.size();
        LOG.info("Array size: " + arraySize);
        while (arraySize > 0) {
            for (int i = 0; i < jsonArrayData.size(); i++) {
                final JsonObject jsonObject = jsonArrayData.get(i).getAsJsonObject();
                final int airedSeason = jsonObject.get(FIELD_SEASON_NUMBER).getAsInt();
                final int airedEpisodeNumber = jsonObject.get(FIELD_EPISODE_NUMBER).getAsInt();
                // final String firstAired =
                // jsonObject.get("firstAired").getAsString();
                final String episodeName = jsonObject.get(FIELD_EPISODE_NAME).getAsString();

                if (airedSeason > 0 && airedEpisodeNumber > 0) {
                    final Episode episode = Episode.builder().setEpisode(airedEpisodeNumber)
                            .setSeason(airedSeason).setTitle(episodeName).build();
                    episodeList.add(episode);
                }
            }

            currentPage++;
            if (currentPage <= lastPage) {
                final String newQueryString = queryString + "?page=" + currentPage;

                response = hitTvDbAPI(newQueryString);

                if (response == null) {
                    return null;
                }

                parser = new JsonParser();
                result = parser.parse(response.toString()).getAsJsonObject();

                // get episodes
                jsonArrayData = result.getAsJsonArray(FIELD_JSON_DATA);
                arraySize = jsonArrayData.size();
            } else {
                arraySize = 0;
            }

        }

        LOG.info("Returning episode list");

        return episodeList;
    }

    /**
     * attempts to get theTvDb showID corresponding to the showTitle given
     *
     * @param showTitle
     *            The title of the show being queried
     * @return theTvDb showId of the show being queried, null otherwise
     */
    private static String getShowID(@NonNull String showTitle) {
        final String queryString = SEARCH_SERIES_NAME + showTitle.replaceAll(" ", "%20");

        final String response = hitTvDbAPI(queryString);
        if (response == null) {
            LOG.info("Show ID is null, returning null...");
            return null;
        }

        final JsonParser parser = new JsonParser();
        final JsonObject result = parser.parse(response).getAsJsonObject();

        final JsonArray jsonArrayData = result.getAsJsonArray(FIELD_JSON_DATA);

        final JsonObject jsonFirstElement = jsonArrayData.get(0).getAsJsonObject();
        final String showID = jsonFirstElement.get("id").getAsString();

        return showID;
    }

    /**
     * Takes a url to theTvDb with parameters and attempts to receive a response
     * from theTvDb
     *
     * @param queryString
     *            a url with parameters
     * @return results from theTvDb.
     */
    private static String hitTvDbAPI(@NonNull final String queryString) {
        final String url = HOST + queryString;
        LOG.info("URL: {}", url);

        final HttpsURLConnection con = createConnection(url);
        if (con == null) {
            LOG.info("Connection to TvDb failed.");
            return null;
        }

        return getResponseFromTvDb(con);
    }

    /**
     * Creates a connection to the TVDB.
     *
     * @param url
     *            the url including parameters to theTvDb
     * @return a {@link HttpsURLConnection} to theTvDb, null if connection fails
     */
    private static HttpsURLConnection createConnection(@NonNull final String url) {
        HttpsURLConnection con = null;

        try {
            final URL obj = new URL(url);
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Authorization", "Bearer " + Authorization.getTokenFromFile());
            final int responseCode = con.getResponseCode();

            if (responseCode == 401) {
                LOG.info("Getting Token from server");
                con = (HttpsURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                final String newToken = Authorization.getRequestTokenFromTVDB();
                con.setRequestProperty("Authorization", "Bearer " + newToken);
            } else if (responseCode == 404) {
                JOptionPane.showMessageDialog(new JFrame(), "Response code 404 from theTvDb");
                return null;
            }
            LOG.info("Response code: " + con.getResponseCode());
        } catch (final IOException e) {
            e.printStackTrace();
        }

        return con;
    }

    /**
     * Takes the connection to theTvDb and tries to get a response from theTvDb
     *
     * @param con
     *            a preconfigured {@link HttpsURLConnection} to theTvDb.
     * @return the response given by theTvDb.
     */
    private static String getResponseFromTvDb(@NonNull HttpsURLConnection con) {
        StringBuffer response = null;

        try {
            final BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (final IOException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Show not found at theTvDb");
            return null;
        }

        return response.toString();
    }
}
