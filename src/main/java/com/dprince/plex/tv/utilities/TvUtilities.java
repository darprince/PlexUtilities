package com.dprince.plex.tv.utilities;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.tv.types.TvShow;
import com.dprince.tv.source.TheTvDbLookup;

public class TvUtilities {

    private static final Logger LOG = Logging.getLogger(TvUtilities.class);

    private static final String REGEX = "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})\\.).*(.mkv|.mp4|.avi|.mpg){1}";
    private static final String REGEX_FORMATTED_FILENAME = "(^[^-]*)[ -]{3}[sS]{1}([0-9]{2})[eE]{1}([0-9]{2}).*(.mkv|.mp4|.avi|.mpg){1}";

    /**
     * Takes the show's filename and determines the rawTvShowName,
     * originalFilePath, year, episodeNumber, seasonNumber and extension.
     *
     * @param filepath
     *            The filename with path
     * @return Partially filled out TvShow
     */
    public static TvShow parseFileName(String originalFilepath) {
        String filename = null;
        filename = TvFileUtilities.getFilenameFromPath(originalFilepath);

        if (filename == null) {
            System.out.println("Filename is null from filepath.");
        } else {
            System.out.println("Filename from parser: " + filename);
        }

        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(filename);

        while (matcher.find()) {
            System.out.println("Parser: matcher found");
            LOG.info("Parser: matcher found");
            final String rawTvShowname = WordUtils
                    .capitalize(matcher.group(1).replaceAll("\\.", " ").toLowerCase()).trim();
            // TODO: move to show matcher

            System.out.println("Parser: " + rawTvShowname);
            LOG.info("Parser: rawTvShowname = " + rawTvShowname);
            // for (final AwkwardTvShows awkwardTvShows :
            // AwkwardTvShows.values()) {
            // if (show.toLowerCase().contains(awkwardTvShows.match)) {
            // show = show.replaceAll(awkwardTvShows.match,
            // awkwardTvShows.replacement);
            // break;
            // }
            // }

            // for (int i = 0; i <= matcher.groupCount(); i++) {
            // System.out.println("GP" + i + ": " + matcher.group(i));
            // }

            String tvSeasonNumber = "XX";
            String tvEpisodeNumber = "XX";
            if (matcher.group(3) != null) {
                tvSeasonNumber = String.format("%02d", Integer.parseInt(matcher.group(3)));
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(4)));
            } else if (matcher.group(5) != null) {
                tvSeasonNumber = String.format("%02d", Integer.parseInt(matcher.group(5)));
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(6)));
            } else if (matcher.group(8) != null) {
                tvSeasonNumber = String.format("%02d", Integer.parseInt(matcher.group(8)));
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(9)));
            } else {
                tvSeasonNumber = "01";
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(7)));
            }
            final String extension = matcher.group(10);

            // System.out.println(filename + ": " + matcher.group(3) + " " +
            // matcher.group(4) + " "
            // + matcher.group(5) + " " + matcher.group(6));

            String year = null;
            if (matcher.group(2) != null) {
                year = matcher.group(2).replaceAll("\\(", "").replaceAll("\\)", "");
            }

            try {
                return new TvShow(rawTvShowname, originalFilepath, year, tvEpisodeNumber,
                        tvSeasonNumber, extension);
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }

        final Pattern patternFormatted = Pattern.compile(REGEX_FORMATTED_FILENAME);
        final Matcher matcherFormatted = patternFormatted.matcher(filename);
        System.out.println("FN: " + filename);
        while (matcherFormatted.find()) {
            final String showName = matcherFormatted.group(1);
            final String seasonNumber = matcherFormatted.group(2);
            final String episodeNumber = matcherFormatted.group(3);
            final String extension = matcherFormatted.group(4);

            final TvShow tvShow = new TvShow(showName, originalFilepath, null, episodeNumber,
                    seasonNumber, extension);
            tvShow.setFormattedTvShowName(showName);
            return tvShow;
        }

        LOG.info("Error in parseFileName()");
        return null;
    }

    public static void setOriginalFilepath(TvShow tvShow, final String originalFilepath) {
        tvShow.setOriginalFilepath(originalFilepath);
    }

    public static void setFormattedTvShowname(TvShow tvShow) {
        final String[][] titles = TvFileUtilities.getTitlesArray();

        // match show to titles array
        String[] titleFromFileArray = null;
        int stringMatches = 0;
        int toReturnMatches = 0;
        String toReturn = null;
        final String rawTvShowName = tvShow.getRawTvShowName().toLowerCase() + " ("
                + tvShow.getYear() + ")";
        for (final String title[] : titles) {
            stringMatches = 0;
            final String titleFromFoldersFile = title[1].toLowerCase();
            titleFromFileArray = titleFromFoldersFile.split(" ");
            for (int i = 0; i < titleFromFileArray.length; i++) {
                if (!rawTvShowName.contains(titleFromFileArray[i].toLowerCase())) {
                    break;
                }
                if (i == titleFromFileArray.length - 1) {
                    stringMatches++;
                    if (stringMatches > toReturnMatches) {
                        toReturnMatches = stringMatches;
                        toReturn = title[0].toString();
                    }
                }
                stringMatches++;
            }
        }

        if (toReturn == null) {
            for (final AwkwardTvShows awkwardTvShows : AwkwardTvShows.values()) {
                stringMatches = 0;
                titleFromFileArray = awkwardTvShows.match.toLowerCase().split(" ");
                for (int i = 0; i < titleFromFileArray.length; i++) {
                    if (!rawTvShowName.contains(titleFromFileArray[i].toLowerCase())) {
                        break;
                    }
                    if (i == titleFromFileArray.length - 1) {
                        stringMatches++;
                        if (stringMatches > toReturnMatches) {
                            toReturnMatches = stringMatches;
                            toReturn = awkwardTvShows.replacement;
                        }
                    }
                    stringMatches++;
                }
            }
        }

        if (toReturn == null) {
            System.out.println("TVShow name is null.");
            System.exit(0);
        } else {
            tvShow.setFormattedTvShowName(toReturn);
        }
    }

    public static void setTvEpisodeTitleFromAPI(TvShow tvShow) {
        if (tvShow.getYear() == null) {
            final String episodeName = TheTvDbLookup.getEpisodeName(tvShow.getFormattedTvShowName(),
                    tvShow.getTvEpisodeNumber(), tvShow.getTvSeasonNumber());
            tvShow.setTvEpisodeTitle(episodeName);
        } else {
            final String episodeName = TheTvDbLookup.getEpisodeName(
                    tvShow.getFormattedTvShowName() + " (" + tvShow.getYear() + ")",
                    tvShow.getTvEpisodeNumber(), tvShow.getTvSeasonNumber());
            tvShow.setTvEpisodeTitle(episodeName);
        }
    }

    public static void setNewFilename(TvShow tvShow) {
        if (tvShow.getYear() == null) {
            final String newFilename = tvShow.getFormattedTvShowName() + " - S"
                    + tvShow.getTvSeasonNumber() + "E" + tvShow.getTvEpisodeNumber() + " - "
                    + tvShow.getTvEpisodeTitle() + "." + tvShow.getExtension();
            tvShow.setNewFilename(newFilename);
        } else {
            final String newFilename = tvShow.getFormattedTvShowName() + " (" + tvShow.getYear()
                    + ") - S" + tvShow.getTvSeasonNumber() + "E" + tvShow.getTvEpisodeNumber()
                    + " - " + tvShow.getTvEpisodeTitle() + "." + tvShow.getExtension();
            tvShow.setNewFilename(newFilename);
        }
    }

    public static void setNewFilepath(TvShow tvShow) {
        final String DESKTOP_PLEX = "//DESKTOP-PLEX/";
        final String[] DesktopPlexLocation = {
                "tv a-e", "tv f-l", "tv m-s", "tv t-z"
        };

        File queriedDrive = null;
        String queryString = null;
        for (final String sharedDrive : DesktopPlexLocation) {
            queryString = DESKTOP_PLEX + sharedDrive + "/" + tvShow.getFormattedTvShowName();
            queriedDrive = new File(queryString);
            if (queriedDrive.exists()) {
                break;
            }
        }

        tvShow.setNewFilepath(queryString + "/Season " + tvShow.getTvSeasonNumber() + "/"
                + tvShow.getNewFilename());
    }

    public static boolean moveFile() {
        return false;
    }
}
