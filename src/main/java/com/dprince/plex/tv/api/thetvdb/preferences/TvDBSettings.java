package com.dprince.plex.tv.api.thetvdb.preferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

/**
 * Class holding ApiKey and current location to current Token.
 *
 * @author Darren
 */
public class TvDBSettings {

    private static final String TOKEN_FILE_NAME = "\\\\Desktop-downloa\\TVShowRenamer\\token.txt";
    private static final String APIKEY = "{\"apikey\":\"B373D959A972D282\",\"username\":\"darprince\",\"userkey\":\"81674296C20B9CA1\"}";

    public String token = "";

    public TvDBSettings() {
        this.token = getToken();
    }

    /**
     * Retrieves the token from the text file.
     *
     * @return The token for theTvDb api
     */
    public String getToken() {
        final File file = new File(TOKEN_FILE_NAME);

        final StringBuilder result = new StringBuilder("");

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                final String line = scanner.nextLine();
                result.append(line);
            }

            scanner.close();

        } catch (final IOException e) {
            e.printStackTrace();
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
    public void setToken(String tokenToSet) {
        final File file = new File(TOKEN_FILE_NAME);

        try {
            final FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(tokenToSet);
            fileWriter.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

}
