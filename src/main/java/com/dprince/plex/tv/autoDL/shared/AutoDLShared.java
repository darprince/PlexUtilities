package com.dprince.plex.tv.autoDL.shared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import com.dprince.plex.tv.autoDL.limeTorrents.LimeTorrents;
import com.dprince.plex.tv.autoDL.limeTorrents.UnwantedTvShows;
import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.Downloads;

public class AutoDLShared {

    public static boolean isUnwanted(final String href) {
        for (final UnwantedTvShows unwantedTvShow : UnwantedTvShows.values()) {
            if (href.toLowerCase().replaceAll("\\.", " ").replaceAll("-", " ")
                    .contains(unwantedTvShow.rawShowName)) {
                LimeTorrents.unwantedTvCount++;
                return true;
            }
        }
        return false;
    }

    public static boolean episodeExists(final TvShow tvShow) {
        final String episodeExists = Downloads.episodeExists(tvShow.getDestinationFilepath(),
                tvShow.getSeasonNumber(), tvShow.getEpisodeNumber());
        if (episodeExists != null) {
            return true;
        } else {
            return false;
        }
    }

    public static void saveFile(String baseUrl, String file) throws IOException {
        final URL url = new URL(baseUrl);
        System.out.println("opening connection");
        final InputStream in = url.openStream();
        final FileOutputStream fos = new FileOutputStream(new File(file));

        System.out.println("reading file...");
        int length = -1;
        final byte[] buffer = new byte[1024];// buffer for portion of data from
        // connection
        while ((length = in.read(buffer)) > -1) {
            fos.write(buffer, 0, length);
        }

        fos.close();
        in.close();
        System.out.println("file was downloaded");
        LimeTorrents.downloadedCount++;
    }

    public static String getPageSource(final String url) throws Exception {
        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Referer", "http://rarbg.to");
        con.setRequestProperty("Connection", "keep-alive");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.8");
        con.setRequestProperty("Accept",
                "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        con.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML,like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        final StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        final int responseCode = con.getResponseCode();

        if (responseCode == 200) {
            return response.toString();
        } else {
            return null;
        }
    }

}
