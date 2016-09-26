package com.dprince.plex.tv.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import com.dprince.plex.tv.types.TvShow;
import com.dprince.tv.source.TheTvDbLookup;

public class TvUtilities {
    private static final String REGEX = "(.*?)(\\([\\d]{4}\\))*(?:[ -]{0,3}|[\\d])" //
            + "(?:[sS]{1}|\\.)"
            + "(?:(?=[\\d]{4}|[\\d]{2}\\D{1}[\\d]{2}|(?i)part[\\.]{0,1}[\\d]{1,2})" //
            + "([\\d]{2})[\\D]{0,1}([\\d]{2})" //
            + "|" //
            + "([\\d]{1})[ofx]{0,2}([\\d]{1,2})" //
            + "|" //
            + "part[\\.]{0,1}([\\d]{1,2}))" //
            + ".*(?i)(.mkv|.mp4|.avi|.mpg)";

    /**
     * Takes the show's filename and determines the rawTvShowName, year,
     * episodeNumber, seasonNumber and extension.
     *
     * @param fileName
     *            The filename without path
     * @return Partially filled out TvShow
     */
    public static TvShow parseFileName(String fileName) {
        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(fileName);

        while (matcher.find()) {
            final String rawTvShowName = WordUtils
                    .capitalize(matcher.group(1).replaceAll("\\.", " ").toLowerCase()).trim();

            // TODO: move to show matcher

            // for (final AwkwardTvShows awkwardTvShows :
            // AwkwardTvShows.values()) {
            // if (show.toLowerCase().contains(awkwardTvShows.match)) {
            // show = show.replaceAll(awkwardTvShows.match,
            // awkwardTvShows.replacement);
            // break;
            // }
            // }

            String tvSeasonNumber = "XX";
            String tvEpisodeNumber = "XX";
            if (matcher.group(3) != null) {
                tvSeasonNumber = String.format("%02d", Integer.parseInt(matcher.group(3)));
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(4)));
            } else if (matcher.group(5) != null) {
                tvSeasonNumber = String.format("%02d", Integer.parseInt(matcher.group(5)));
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(6)));
            } else {
                tvSeasonNumber = "01";
                tvEpisodeNumber = String.format("%02d", Integer.parseInt(matcher.group(7)));
            }
            final String extension = matcher.group(8);

            String year = "";
            if (matcher.group(2) != null) {
                year = matcher.group(2);
            }
            return new TvShow(rawTvShowName, year, tvEpisodeNumber, tvSeasonNumber, extension);
        }

        // TODO: LOG error and exit
        return null;
    }

    public static void setOriginalFilepath(TvShow tvShow, final String file) {
        tvShow.setOriginalFilepath(file);
    }

    public static void setProperFilename(TvShow tvShow) {
        final File tvShowFoldersFile = new File("C:\\FileManipulation\\TVShowRenamer\\folders.txt");
        String[][] titles = null;

        // get show titles from file
        try {
            final BufferedReader br = Files
                    .newBufferedReader(Paths.get(tvShowFoldersFile.getCanonicalPath()));
            final int fileLength = Integer.parseInt(br.readLine());
            titles = new String[fileLength][2];
            for (int i = 0; i < fileLength; i++) {
                final String line = br.readLine();
                final String[] splitLine = line.split("\\^\\^\\^");
                titles[i][0] = splitLine[0];
                titles[i][1] = splitLine[1];
            }
        } catch (final IOException e) {
            System.out.println("Reading folders.txt failed");
            System.out.println(e.toString());
        }

        // match show to titles array
        String[] titleFromFileArray = null;
        int stringMatches = 0;
        int toReturnMatches = 0;
        String toReturn = null;
        for (final String title[] : titles) {
            stringMatches = 0;
            final String titleFromFoldersFile = title[1].toLowerCase();
            titleFromFileArray = titleFromFoldersFile.split(" ");
            for (int i = 0; i < titleFromFileArray.length; i++) {
                if (!tvShow.getRawTvShowName().contains(titleFromFileArray[i].toLowerCase())) {
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
                    if (!tvShow.getRawTvShowName().contains(titleFromFileArray[i].toLowerCase())) {
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
        // else {
        // TVShowRenamer.tvShow.setName(toReturn);
        // TVShowRenamer.tvShow.setEpisode(episode);
        // }
    }

    public static void getEpisodeNameFromAPI(TvShow tvShow) {
        final String episodeName = TheTvDbLookup.getEpisodeName(tvShow.getProperTvShowName(),
                tvShow.getTvEpisodeNumber(), tvShow.getTvSeasonNumber());
        tvShow.setTvEpisodeTitle(episodeName);
    }

    public static void setNewFilepath(TvShow tvShow) {
        final String DESKTOP_PLEX = "//DESKTOP-PLEX/";
        final String[] DesktopPlexLocation = {
                "tv a-i", "tv t-z", "tv j-s"
        };

        File queriedDrive = null;
        String queryString = null;
        for (final String sharedDrive : DesktopPlexLocation) {
            queryString = DESKTOP_PLEX + sharedDrive + "/" + tvShow.getProperTvShowName();
            queriedDrive = new File(queryString);
            if (queriedDrive.exists()) {
                System.out.println("Destination: " + queryString);
                break;
            }
        }

        // build destination
        final String destinationFolder = queryString + "\\" + tvShow.getTvSeasonNumber() + "\\";
        final String destinationFilename = destinationFolder + "\\" + tvShow.getNewFilename();
    }

    public static boolean moveFile() {
        return false;
    }
}
