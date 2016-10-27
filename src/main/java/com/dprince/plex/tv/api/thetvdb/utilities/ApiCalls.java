package com.dprince.plex.tv.api.thetvdb.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.api.thetvdb.auth.Authorization;

/**
 * Utility class for the TvDB API calls.
 *
 * @author Darren
 */
public class ApiCalls {

    private static final Logger LOG = Logging.getLogger(ApiCalls.class);

    private final static String HOST = "https://api.thetvdb.com";

    /**
     * Takes a URL to theTvDb with parameters and attempts to receive a response
     * from theTvDb
     *
     * @param queryString
     *            a URL with parameters
     * @return results from theTvDb.
     */
    public static String hitTvDbAPI(@NonNull final String queryString) {
        final String url = HOST + queryString;

        final HttpsURLConnection con = createConnection(url);
        if (con == null) {
            LOG.error("Connection to TvDb failed.");
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
            con = setHttpHeaders(con, obj, Authorization.getTokenFromFile());

            if (con.getResponseCode() == 401) {
                LOG.info("Refreshing Token from server");
                con = setHttpHeaders(con, obj, Authorization.getRequestTokenFromTVDB());
            }
        } catch (final IOException e) {
            LOG.error("Failed to create a connection to the TvDB", e);
            return null;
        }

        return con;
    }

    /**
     * Sets the headers for the http connection.
     *
     * @param con
     *            the HttpsURLConnection
     * @param url
     * @param token
     * @return
     */
    private static HttpsURLConnection setHttpHeaders(HttpsURLConnection con, @NonNull URL url,
            @NonNull String token) {
        try {
            con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Authorization", "Bearer " + token);
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
            LOG.error("Failed to receive response from TvDB", e);
        }

        return response.toString();
    }
}
