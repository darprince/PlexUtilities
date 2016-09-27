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
                year = matcher.group(2).replaceAll("\\(", "").replaceAll("\\)", "");
            }
            return new TvShow(rawTvShowName, year, tvEpisodeNumber, tvSeasonNumber, extension);
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

    public static void getTvEpisodeTitleFromAPI(TvShow tvShow) {
        final String episodeName = TheTvDbLookup.getEpisodeName(tvShow.getFormattedTvShowName(),
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
            queryString = DESKTOP_PLEX + sharedDrive + "/" + tvShow.getFormattedTvShowName();
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
