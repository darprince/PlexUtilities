package com.dprince.plex.tv.api.thetvdb.preferences;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class TvDBSettings {

    private static final String TOKEN_FILE_NAME = "\\\\Desktop-downloa\\TVShowRenamer\\token.txt";
    private static final String APIKEY = "{\"apikey\":\"B373D959A972D282\",\"username\":\"darprince\",\"userkey\":\"81674296C20B9CA1\"}";

    public String token = "";

    public TvDBSettings() {
        this.token = getToken();
    }

    public String getToken() {
        File file = new File(TOKEN_FILE_NAME);

        StringBuilder result = new StringBuilder("");

        try (Scanner scanner = new Scanner(file)) {

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                result.append(line);
            }

            scanner.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return result.toString();
    }

    public String getApiKey() {
        return APIKEY;
    }

    public void setToken(String tokenToSet) {
        File file = new File(TOKEN_FILE_NAME);

        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(tokenToSet);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
