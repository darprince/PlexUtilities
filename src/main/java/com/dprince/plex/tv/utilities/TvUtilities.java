package com.dprince.plex.tv.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.text.WordUtils;

import com.dprince.plex.tv.types.TvShow;
import com.dprince.tv.source.TheTvDbLookup;

public class TvUtilities {
    // private static final String REGEX = "(.*?)(\\({0,1}[\\d]{4}\\){0,1})" /*
    // * Title
    // * plus
    // * 0or1
    // * year
    // * with
    // * or
    // * without
    // * brackets
    // */
    // + "(?:[ -]{0,3}" /* 0or3 space/dash */
    // + "|[\\d])" //
    // + "(?:[sS]{1}|\\.|\\.[\\d]{4}\\.)" /* s/S or a period */
    // + "(?:(?=" /* beginning of grab */
    // + "[\\d]{4}" /* 4#'s */
    // + "|" /* OR */
    // + "[\\d]{2}\\D{1}[\\d]{2}" /* 2#'s, 1 letter, 2#'s */
    // + "|" /* OR */
    // + "(?i)part[\\.]{0,1}[\\d]{1,2})" /* part, 0or1 period, 1or2 #'s */
    // + "([\\d]{2})[\\D]{0,1}([\\d]{2})" /* 2#'s, 0or1 letter, 2#'s */
    // + "|" /* OR */
    // + "([\\d]{1})[ofx]{0,2}([\\d]{1,2})" /* 1#, 0or2 ofx, 1or2 #'s */
    // + "|" // /* OR */
    // + "part[\\.]{0,1}([\\d]{1,2}))" /* part, 0or1 period, 1or2 #'s */
    // + ".*(?i)" /* rest of filename before extension */
    // + "(.mkv|.mp4|.avi|.mpg)";

    private static final String REGEX = "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})\\.).*(.mkv|.mp4|.avi|.mpg){1}";

    /**
     * Takes the show's filename and determines the rawTvShowName, year,
     * episodeNumber, seasonNumber and extension.
     *
     * @param fileName
     *            The filename without path
     * @return Partially filled out TvShow
     */
    public static TvShow parseFileName(String originalFilepath) {
        String filename = null;
        try {
            filename = originalFilepath.substring(originalFilepath.lastIndexOf("/") + 1,
                    originalFilepath.length());
        } catch (final StringIndexOutOfBoundsException e) {
            filename = originalFilepath.substring(originalFilepath.lastIndexOf("\\") + 1,
                    originalFilepath.length());
        }

        if (filename == null) {
            System.out.println("Filename is null from filepath.");
        }

        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(filename);

        // System.out.println("\nFS: " + filename + " " + matcher.find());
        // matcher.reset();

        while (matcher.find()) {
            final String rawTvShowname = WordUtils
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

            String year = "";
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

        // TODO: LOG error and exit
        return null;
    }

    public static void setOriginalFilepath(TvShow tvShow, final String originalFilepath) {
        tvShow.setOriginalFilepath(originalFilepath);
    }

    public static void setFormattedTvShowname(TvShow tvShow) {
        String[][] titles = null;
        File tvShowFoldersFile = new File("C:\\FileManipulation\\TVShowRenamer\\folders.txt");
        if (!tvShowFoldersFile.exists()) {
            tvShowFoldersFile = new File("\\\\DESKTOP-DOWNLOA\\TVShowRenamer\\folders.txt");
            if (!tvShowFoldersFile.exists()) {
                System.out.println("Creating folders.txt file at: " + tvShowFoldersFile);
                createFoldersFile(tvShowFoldersFile, titles);
            }
        }

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
        // else {
        // TVShowRenamer.tvShow.setName(toReturn);
        // TVShowRenamer.tvShow.setEpisode(episode);
        // }
    }

    public static void getTvEpisodeTitleFromAPI(TvShow tvShow) {
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
                "tv a-i", "tv t-z", "tv j-s"
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

    // TODO: This is from the old version, should be updated
    private static void createFoldersFile(final File foldersFile, String[][] titles) {
        try {
            foldersFile.createNewFile();
        } catch (final IOException e) {
            System.out.println("Creating folders.txt file failed");
            System.out.println(e.toString());
        }
        titles = getTitles();

        BufferedWriter outputWriter = null;
        try {
            outputWriter = new BufferedWriter(new FileWriter(foldersFile));

            outputWriter.write(String.valueOf(titles.length));
            outputWriter.newLine();

            for (final String[] folderLine : titles) {
                outputWriter.write(folderLine[0] + "^^^" + folderLine[1]);
                outputWriter.newLine();
            }
            outputWriter.flush();
            outputWriter.close();
        } catch (final IOException e) {
            System.out.println("Writing to folders file failed.");
            System.out.println(e.toString());
        }
    }

    // TODO: also from old version
    public static String[][] getTitles() {
        final String downloadsDirectories[] = {
                "M:\\", "N:\\", "O:\\", "R:\\"
        };

        File file = null;
        File[] files = null;
        final List<File> finalFileList = new ArrayList<File>();

        for (final String dir : downloadsDirectories) {
            file = new File(dir);
            files = file.listFiles();
            for (final File tempFile : files) {
                finalFileList.add(tempFile);
            }
        }

        final String[][] allFiles = new String[finalFileList.size()][2];
        String tempString = null;
        String replacement = null;

        for (int i = 0; i < finalFileList.size(); i++) {
            tempString = finalFileList.get(i).toString();
            replacement = tempString.substring(tempString.lastIndexOf("\\") + 1,
                    tempString.length());
            allFiles[i][0] = replacement;

            allFiles[i][1] = replacement.toLowerCase().replaceAll("'", "").replaceAll("\\.", "")
                    .replaceAll(",", "").replaceAll("marvels ", " ").replaceAll(" the ", " ")
                    .replaceAll("the ", "").replaceAll(" on ", " ").replaceAll(" a ", " ")
                    .replaceAll(" of ", " ").trim();
        }

        return allFiles;
    }

    public static boolean moveFile() {
        return false;
    }
}
