package com.dprince.plex.tv.autoDL.limeTorrents;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dprince.plex.movie.MovieRenamer;
import com.dprince.plex.movie.utilities.MovieUtilities;
import com.dprince.plex.tv.autoDL.limeTorrents.types.OMDBapi;
import com.dprince.plex.tv.autoDL.shared.AutoDLShared;
import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.ParseFileName;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LimeTorrents {
    private static final String DOWNLOADS_DIRECTORY = "//DESKTOP-DOWNLOA/Downloads/";
    private static final String INDEX_MARKER = "-torrent-";
    public static final String USER_AGENT = "Mozilla/5.0";

    private static final String BASE_URL = "http://limetorrents.bypassed.top/index.php?page=top100";
    private static final String TORRENT_PAGE_PREFIX = "http://limetorrents.bypassed.top/";
    private static final String HASH_PREFIX = "http://itorrents.org/torrent/";

    public static final String FILES_TO_SKIP = ".*korsub.*|.* hc .*|.*[hd]{0,2}cam .*";

    private static final List<String> fileList = new ArrayList<String>();
    private static final Set<String> movieList = new HashSet<String>();
    private static final Set<String> unknownTv = new HashSet<String>();

    public static int downloadedCount = 0;
    static int tvCount = 0;
    public static int unwantedTvCount = 0;
    // http://limetorrents.bypassed.top/Vikings-S04E14-PROPER-HDTV-x264-KILLERS[ettv]-torrent-8517567.html
    // http://itorrents.org/torrent/AECDEDA4BC562A9A52FD25E5036FDFB8B5ECCB44.torrent

    public static void main(String[] args) {
        begin();
    }

    public static void begin() {
        try {
            final String html = AutoDLShared.getPageSource(BASE_URL);
            final List<Torrent> torrentList = getTorrents(html);

            for (final Torrent torrent : torrentList) {
                final String href = torrent.getHref().substring(0,
                        torrent.getHref().lastIndexOf(INDEX_MARKER));

                // Check unwantedTvShows and skip if found
                if (AutoDLShared.isUnwanted(href)) {
                    continue;
                }

                final String fileName = "c:/path/" + href + ".mp4";
                if (href.matches(".*[sS]{1}[0-9]{2}[Ee]{1}[0-9]{2}.*")) {
                    tvCount++;
                    final TvShow tvShow = ParseFileName.parseFileName(fileName.replaceAll("-", "."),
                            false, false);

                    if (tvShow != null) {
                        if (!AutoDLShared.episodeExists(tvShow) && torrent.getSize() < 500) {
                            downloadedCount++;
                            getTorrentFile(torrent, tvShow.getFormattedFileName());
                        }
                        continue;
                    } else {
                        // skipping existing show or too large of a file
                        unknownTv.add(href + " " + torrent.getSize());
                        continue;
                    }
                } else {
                    if (isWantedMovie(torrent, href)) {
                        continue;
                    } else {
                    }
                }
            }

            System.out.println("\n\n##################### MOVIE LIST ######################");
            for (final String movieHref : movieList) {
                System.out.println(movieHref);
            }

            System.out.println("\n\n###################### UNKNOWN TV ####################");
            for (final String file : unknownTv) {
                System.out.println(file);
            }

            System.out.println("\n\n###################### DOWNLOADED ####################");
            for (final String file : fileList) {
                System.out.println(file);
            }

            System.out.println("\nDownloaded Count: " + downloadedCount);
            System.out.println("Matched TV Count: " + tvCount);
            System.out.println("Unwanted TV Count: " + unwantedTvCount);
        } catch (

        final Exception e)

        {
            e.printStackTrace();
        }
    }

    private static boolean isWantedMovie(Torrent torrent, String href) {
        href = href.replaceAll("\\.", " ").replaceAll("-", " ").replaceAll("[ ]{2,}", " ");
        final String formattedMovieName = MovieRenamer.getMovieNameFromFolder(href);
        final String year = MovieRenamer.getYear(href);

        final String formattedFileName = formattedMovieName + " (" + year + ")";

        if (MovieUtilities.getMovieDriveLocation(formattedFileName) == null) {
            // check for 1080p or subtitled
            if (href.matches(".*1080p.*") && !href.toLowerCase().matches(FILES_TO_SKIP)) {
                // check for rating on imdb
                final String url = "http://www.omdbapi.com/?t="
                        + formattedMovieName.replaceAll("[ ]{1,}", "%20") + "&y=" + year;
                double rating = 0.0;
                try {
                    final String html = AutoDLShared.getPageSource(url);
                    final ObjectMapper mapper = new ObjectMapper()
                            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    final OMDBapi omdb = mapper.readValue(html, OMDBapi.class);
                    rating = omdb.getImdbRating();
                    if (rating > 6.0 && torrent.getSize() < 5000.0) {
                        getTorrentFile(torrent, href);
                    }
                } catch (final Exception e) {
                    System.out.println("Failed to read in from mapper");
                    e.printStackTrace();
                }
                // TODO: check for size

                movieList.add(formattedFileName + " " + torrent.getSize());
                return true;
            }
        }
        return false;
    }

    private static void getTorrentFile(Torrent torrent, String formattedFileName) {
        final String hash = getHash(torrent);
        final String torrentFile = HASH_PREFIX + hash + ".torrent";
        try {
            AutoDLShared.saveFile(torrentFile,
                    DOWNLOADS_DIRECTORY + formattedFileName + " " + torrent.getSize() + ".torrent");
            fileList.add(formattedFileName + " " + torrent.getSize());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    private static String getHash(Torrent torrent) {
        final String link = TORRENT_PAGE_PREFIX + torrent.getHref();
        String pageSource = null;
        try {
            pageSource = AutoDLShared.getPageSource(link);
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
}
