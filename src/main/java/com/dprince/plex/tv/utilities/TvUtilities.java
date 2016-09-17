package com.dprince.plex.tv.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import com.dprince.plex.tv.types.TvShow;

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
                year = " " + matcher.group(2);
            }
            return new TvShow(rawTvShowName, tvEpisodeNumber, tvSeasonNumber, extension);
        }

        // TODO: LOG error and exit
        return null;
    }

    public void setOriginalFilepath() {

    }

    public void setNewFilepath() {

    }

    public boolean moveFile() {
        return false;
    }

    public String getEpisodeNameFromAPI() {
        return null;
    }
}
