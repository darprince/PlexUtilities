package com.dprince.plex.tv.api.thetvdb.auth;

import static com.dprince.plex.settings.PlexSettings.TOKEN_FILE_NAME;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Authorization {

    private static final Logger LOG = Logging.getLogger(Authorization.class);

    private static final String APIKEY = "{\"apikey\":\"B373D959A972D282\",\"username\":\"darprince\",\"userkey\":\"81674296C20B9CA1\"}";

    private static final String LOGIN = "https://api.thetvdb.com/login";

    /**
     * Uses the apiKey to request a refreshed token
     *
     * @return a new request token from thetvdb using apikey.
     */
    public static String getRequestTokenFromTVDB() {
        LOG.info("Getting Token from server");
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

        final String urlParameters = APIKEY;

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
            LOG.info("Failed to receive token from TheTvDb");
            return null;
        }

        // print result
        System.out.println(response.toString());

        final JsonParser parser = new JsonParser();
        final JsonObject result = parser.parse(response.toString()).getAsJsonObject();

        final String token = result.get("token").getAsString();

        setToken(token);

        LOG.info("Returning new token: {}", token);
        return token;
    }

    /**
     * Retrieves the token from the text file.
     *
     * @return The token for theTvDb api
     */
    public static String getTokenFromFile() {
        final File file = new File(TOKEN_FILE_NAME);

        final StringBuilder result = new StringBuilder("");

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                result.append(line);
            }

            scanner.close();

        } catch (final IOException e) {
            LOG.error("Failed to read token file.");
        }

        return result.toString();
    }

    public String getApiKey() {
        return APIKEY;
    }

    /**
     * Overwrites expired token with current one
     *
     * @param tokenToSet
     *            The newly received token
     */
    public static void setToken(String tokenToSet) {
        LOG.info("Setting new token");
        final File file = new File(TOKEN_FILE_NAME);

        try {
            final FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(tokenToSet);
            fileWriter.close();
        } catch (final IOException e) {
            LOG.info("New token not written to file.");
        }
    }

}
