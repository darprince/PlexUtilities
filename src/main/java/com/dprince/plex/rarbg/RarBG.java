package com.dprince.plex.rarbg;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.Downloads;
import com.dprince.plex.tv.utilities.ParseFileName;

public class RarBG {
    private static final String DOWNLOADS_DIRECTORY = "//DESKTOP-DOWNLOA/Downloads/";
    private static final String INDEX_MARKER = "-torrent-";
    private static final String USER_AGENT = "Mozilla/5.0";

    private static final String BASE_URL = "http://limetorrents.bypassed.top/index.php?page=top100";
    private static final String TORRENT_PAGE_PREFIX = "http://limetorrents.bypassed.top/";
    private static final String HASH_PREFIX = "http://itorrents.org/torrent/";

    private static final List<String> fileList = new ArrayList<String>();

    static int count = 0;
    static int falseCount = 0;
    // http://limetorrents.bypassed.top/Vikings-S04E14-PROPER-HDTV-x264-KILLERS[ettv]-torrent-8517567.html
    // http://itorrents.org/torrent/AECDEDA4BC562A9A52FD25E5036FDFB8B5ECCB44.torrent

    public static void main(String[] args) {
        try {
            final String html = getPageSource(BASE_URL);
            final List<Torrent> torrentList = getTorrents(html);

            for (final Torrent torrent : torrentList) {
                final String href = torrent.getHref().substring(0,
                        torrent.getHref().lastIndexOf(INDEX_MARKER));
                final String fileName = "c:/path/" + href + ".mp4";
                final TvShow tvShow = ParseFileName.parseFileName(fileName.replaceAll("-", "."),
                        false);
                // TODO: need to stop show creation popup for this script.

                if (tvShow != null) {
                    // check if episode exists
                    if (!episodeExists(tvShow) && torrent.getSize() < 500) {
                        count++;
                        // if it does not exist, get it
                        getTorrentFile(torrent, tvShow);
                    } else {
                        falseCount++;
                    }
                } else {
                    falseCount++;
                }
            }
            System.out.println("Count: " + count);
            System.out.println("False Count: " + falseCount);

            for (final String file : fileList) {
                System.out.println(file);
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static void getTorrentFile(Torrent torrent, TvShow tvShow) {
        final String hash = getHash(torrent);
        final String torrentFile = HASH_PREFIX + hash + ".torrent";
        try {
            saveFile(torrentFile, DOWNLOADS_DIRECTORY + tvShow.getFormattedFileName() + ".torrent");
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static String getHash(Torrent torrent) {
        final String link = TORRENT_PAGE_PREFIX + torrent.getHref();
        String pageSource = null;
        try {
            pageSource = getPageSource(link);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        final String regex = "infohash=(.+?)'";
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(pageSource);

        while (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static boolean episodeExists(final TvShow tvShow) {
        System.out.println(tvShow.getDestinationFilepath());
        final String episodeExists = Downloads.episodeExists(tvShow.getDestinationFilepath(),
                tvShow.getSeasonNumber(), tvShow.getEpisodeNumber());
        if (episodeExists != null) {
            System.out.println("\n\nEpisode Exists: " + episodeExists + "\n\n");
            return true;
        } else {
            System.out.println("*****************************************Episode Doesnt Exist: "
                    + tvShow.getFormattedFileName());
            return false;
        }
    }

    public static List<Torrent> getTorrents(final String html) {
        final String regexTR = "<tr(.+?)</tr>";
        final String regexTD = "<td class=\"tdnormal\">(.+?)</td>";
        final String regexHREF = "<a href=\"(.+?)\"";

        final List<Torrent> torrentList = new ArrayList<Torrent>();

        final Pattern patternTR = Pattern.compile(regexTR);
        final Pattern patternTD = Pattern.compile(regexTD);
        final Pattern patternHREF = Pattern.compile(regexHREF);

        final Matcher matcherTR = patternTR.matcher(html);

        while (matcherTR.find()) {
            for (int i = 0; i < matcherTR.groupCount(); i++) {
                final String group = matcherTR.group(0);
                final Matcher matcherTD = patternTD.matcher(group);
                String torrentSize = null;
                while (matcherTD.find()) {
                    torrentSize = matcherTD.group(1);
                }

                final Matcher matcherHREF = patternHREF.matcher(group);
                String href = null;
                while (matcherHREF.find()) {
                    href = matcherHREF.group(1);
                    href = href.substring(1, href.length());
                }

                if (href != null && torrentSize != null) {
                    final Torrent torrent = new Torrent(href, torrentSize);
                    torrentList.add(torrent);
                }
            }
        }

        return torrentList;
    }

    private static String getPageSource(final String url) throws Exception {
        final URL obj = new URL(url);
        final HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("User-Agent", USER_AGENT);

        final int responseCode = con.getResponseCode();

        final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        final StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        if (responseCode == 200) {
            return response.toString();
        } else {
            return null;
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
        fileList.add(file);
        count++;
    }
}
