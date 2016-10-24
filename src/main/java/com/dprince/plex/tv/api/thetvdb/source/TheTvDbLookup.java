package com.dprince.plex.tv.api.thetvdb.source;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.api.thetvdb.preferences.TvDBSettings;
import com.dprince.plex.tv.api.thetvdb.types.Episode;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class TheTvDbLookup {

    private static final String LOGIN = "https://api.thetvdb.com/login";
    private static final String SEARCH_SERIES_NAME = "/search/series?name=";
    private final static String HOST = "https://api.thetvdb.com";
    private final static String SERIES_CONTEXT = "/series/";
    private final static String EPISODES_CONTEXT = "/episodes";
    private final static String EPISODES_SEARCH_PREFIX = "/episodes/query?airedSeason=";

    private static String apiKey;
    private static String token;
    private static TvDBSettings settings;

    private static final Logger LOG = Logging.getLogger(TheTvDbLookup.class);

    public static void main(String[] args) {
        init();
        System.out.println(getShowID("30 for 30"));
    }

    public static void init() {
        settings = new TvDBSettings();
        token = settings.getToken();
        apiKey = settings.getApiKey();
    }

    public static String getEpisodeName(String showName, String episodeNumber,
            String seasonNumber) {
        init();
        final String showID = getShowID(showName);
        if (showID == null) {
            return null;
        }

        final String queryString = SERIES_CONTEXT + showID + EPISODES_CONTEXT
                + "/query?airedSeason=" + seasonNumber + "&airedEpisode=" + episodeNumber;

        final JsonObject response = lookupAPI(queryString);

        if (response == null) {
            return null;
        }

        final JsonParser parser = new JsonParser();
        final JsonObject result = parser.parse(response.toString()).getAsJsonObject();

        // get episode
        final JsonArray jsonArrayData = result.getAsJsonArray("data");
        final JsonObject jsonObject = jsonArrayData.get(0).getAsJsonObject();
        final String episodeName = jsonObject.get("episodeName").getAsString();

        return episodeName;
    }

    public static List<Episode> getAllEpisodesForShow(String showName) {
        final String showID = getShowID(showName);
        final String queryString = SERIES_CONTEXT + showID + EPISODES_CONTEXT;

        JsonObject response = lookupAPI(queryString);

        LOG.info("Parsing response");
        JsonParser parser = new JsonParser();
        JsonObject result = parser.parse(response.toString()).getAsJsonObject();

        // get total number of pages
        int currentPage = 1;
        final int lastPage = Integer
                .parseInt(result.getAsJsonObject("links").get("last").getAsString());

        // get episodes
        JsonArray jsonArrayData = result.getAsJsonArray("data");

        final List<Episode> episodeList = new ArrayList<>();
        int arraySize = jsonArrayData.size();
        LOG.info("Array size: " + arraySize);
        while (arraySize > 0) {
            for (int i = 0; i < jsonArrayData.size(); i++) {
                final JsonObject jsonObject = jsonArrayData.get(i).getAsJsonObject();
                final int airedSeason = jsonObject.get("airedSeason").getAsInt();
                final int airedEpisodeNumber = jsonObject.get("airedEpisodeNumber").getAsInt();
                // final String firstAired =
                // jsonObject.get("firstAired").getAsString();
                final String episodeName = jsonObject.get("episodeName").getAsString();

                if (airedSeason > 0 && airedEpisodeNumber > 0) {
                    final Episode episode = Episode.builder().setEpisode(airedEpisodeNumber)
                            .setSeason(airedSeason).setTitle(episodeName).build();
                    episodeList.add(episode);
                }
            }

            currentPage++;
            if (currentPage <= lastPage) {
                final String newQueryString = queryString + "?page=" + currentPage;

                response = lookupAPI(newQueryString);

                if (response == null) {
                    return null;
                }

                parser = new JsonParser();
                result = parser.parse(response.toString()).getAsJsonObject();

                // get episodes
                jsonArrayData = result.getAsJsonArray("data");
                arraySize = jsonArrayData.size();
            } else {
                arraySize = 0;
            }

        }

        LOG.info("Returning episode list");

        return episodeList;
    }

    private static String getShowID(String showTitle) {
        final String queryString = SEARCH_SERIES_NAME + showTitle.replaceAll(" ", "%20");

        final JsonObject response = lookupAPI(queryString);
        if (response == null) {
            LOG.info("Show ID is null, returning, null...");
            return null;
        }

        final JsonParser parser = new JsonParser();
        final JsonObject result = parser.parse(response.toString()).getAsJsonObject();

        final JsonArray jsonArrayData = result.getAsJsonArray("data");

        final JsonObject jsonFirstElement = jsonArrayData.get(0).getAsJsonObject();
        final String showID = jsonFirstElement.get("id").getAsString();

        return showID;
    }

    private static JsonObject lookupAPI(final String queryString) {
        final String url = HOST + queryString;
        LOG.info(url);
        HttpsURLConnection con = null;

        try {
            final URL obj = new URL(url);
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Authorization", "Bearer " + token);
            final int responseCode = con.getResponseCode();

            if (responseCode == 401) {
                LOG.info("Getting Token from server");
                con = (HttpsURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
                final String newToken = getRequestToken();
                con.setRequestProperty("Authorization", "Bearer " + newToken);
            } else if (responseCode == 404) {
                JOptionPane.showMessageDialog(new JFrame(), "Response code 404 from theTvDb");
                return null;
            }
            LOG.info("Response code: " + responseCode);
        } catch (final IOException e) {
            e.printStackTrace();
        }

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

        LOG.info("Response received");

        try {
            final JsonParser parser = new JsonParser();
            final JsonObject result = parser.parse(response.toString()).getAsJsonObject();
            LOG.info("Returning result");
            return result;
        } catch (final Exception e) {
            LOG.info("Error parsing response");
        }
        LOG.info("Returning null");
        return null;
    }

    /**
     * @return a request token from thetvdb.
     */
    private static String getRequestToken() {
        final String url = LOGIN;
        URL obj;
        HttpsURLConnection con = null;
        try {
            obj = new URL(url);
            con = (HttpsURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");
        } catch (final IOException e2) {
            e2.printStackTrace();
        }

        final String urlParameters = apiKey;

        // Send post request
        con.setDoOutput(true);

        try {
            final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.flush();
            wr.close();
            final int responseCode = con.getResponseCode();
            System.out.println("\nSending 'POST' request to URL : " + url);
            System.out.println("Post parameters : " + urlParameters);
            System.out.println("Response Code : " + responseCode);
        } catch (final IOException e) {
            e.printStackTrace();
        }

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        } catch (final IOException e) {
            e.printStackTrace();
        }
        String inputLine;
        final StringBuffer response = new StringBuffer();

        try {
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }

        // print result
        System.out.println(response.toString());

        final JsonParser parser = new JsonParser();
        final JsonObject result = parser.parse(response.toString()).getAsJsonObject();

        final String token = result.get("token").getAsString();
        LOG.info("Setting new token");
        settings.setToken(token);
        return token;
    }
}
