package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.types.TvShow;

public class TvUtilities {

    private static final Logger LOG = Logging.getLogger(TvUtilities.class);

    private static final String TWO_DECIMALS = "%02d";
    private static final String REGEX = "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})\\.).*(.mkv|.mp4|.avi|.mpg){1}";
    private static final String REGEX_FORMATTED_FILENAME = "(^[^-]*)[ -]{3}[sS]{1}([0-9]{2})[eE]{1}([0-9]{2}).*(.mkv|.mp4|.avi|.mpg){1}";

    /**
     * Takes the show's filepath and determines the rawTvShowName, seasonNumber,
     * episodeNumber, extension, formattedShowName, episodeTitle,
     * formattedFileName, destinationFilepath.
     *
     * @param originalFilepath
     * @return {@link TvShow}
     */
    public static TvShow parseFileName(@NonNull final String originalFilepath) {
        final String filename = new File(originalFilepath).getName();
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(filename);

        final Pattern patternFormatted = Pattern.compile(REGEX_FORMATTED_FILENAME);
        final Matcher matcherFormatted = patternFormatted.matcher(filename);

        if (matcher.find()) {
            LOG.info("Matcher found for {}", filename);
            rawShowName = matcher.group(1).replaceAll("\\.", " ").toLowerCase().trim();

            if (matcher.group(3) != null) {
                seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(3)));
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(4)));
            } else if (matcher.group(5) != null) {
                seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(5)));
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(6)));
            } else if (matcher.group(8) != null) {
                seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(8)));
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(9)));
            } else {
                seasonNumber = "01";
                episodeNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcher.group(7)));
            }
            extension = matcher.group(10);

        } else if (matcherFormatted.find()) {
            rawShowName = matcherFormatted.group(1);
            seasonNumber = String.format(TWO_DECIMALS, Integer.parseInt(matcherFormatted.group(2)));
            episodeNumber = String.format(TWO_DECIMALS,
                    Integer.parseInt(matcherFormatted.group(3)));
            extension = matcherFormatted.group(4);
        } else {
            LOG.error("Failed to parse filename");
            System.exit(0);
        }

        final String formattedShowName = formatRawShowName(rawShowName);
        final String episodeTitle = getTvEpisodeTitleFromAPI(formattedShowName, seasonNumber,
                episodeNumber);
        final String formattedFileName = buildFileName(formattedShowName, seasonNumber,
                episodeNumber, episodeTitle, extension);
        final String destinationFilepath = buildNewFilepath(formattedFileName, seasonNumber);

        final TvShow tvShow = TvShow.builder().setDestinationFilepath(destinationFilepath)
                .setEpisodeNumber(episodeNumber).setEpisodeTitle(episodeTitle)
                .setExtension(extension).setFormattedFileName(formattedFileName)
                .setFormattedShowName(formattedShowName).setOriginalFilepath(originalFilepath)
                .setRawShowName(rawShowName).setSeasonNumber(seasonNumber).build();

        return tvShow;
    }

    /**
     * Builds a properly formatted FileName.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @param episodeTitle
     * @param extension
     * @return A properly formatted FileName.
     */
    @NonNullByDefault
    private static String buildFileName(@NonNull final String formattedShowName,
            @NonNull final String seasonNumber, @NonNull final String episodeNumber,
            @NonNull final String episodeTitle, @NonNull final String extension) {

        return formattedShowName + " - S" + seasonNumber + "E" + episodeNumber + " - "
                + CommonUtilities.filenameEncode(episodeTitle) + "." + extension;
    }

    private static String formatRawShowName(String rawTvShowName) {
        final String[][] titles = TvFileUtilities.getTitlesArray();

        String[] titleFromFileArray = null;
        int stringMatches = 0;
        int toReturnMatches = 0;
        String toReturn = null;

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
            String formattedShowName = null;
            try {
                formattedShowName = TvFileUtilities.createShowFolder(rawTvShowName);
            } catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return formattedShowName;
        }
        return toReturn;
    }

    /**
     * Queries the TvDB API for the specified episodes title.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @return The episodes title.
     */
    public static String getTvEpisodeTitleFromAPI(String formattedShowName, String seasonNumber,
            String episodeNumber) {
        final String episodeTitle = TheTvDbLookup.getEpisodeTitle(formattedShowName, seasonNumber,
                episodeNumber);
        return episodeTitle;
    }

    /**
     * Builds the new filepath on the plex server
     *
     * @param formattedFileName
     * @param seasonNumber
     * @return a properly built filepath on the plex server.
     */
    @NonNullByDefault
    public static String buildNewFilepath(@NonNull final String formattedFileName,
            @NonNull final String seasonNumber) {

        File queriedDrive = null;
        String queryString = null;
        for (final String sharedDrive : DESKTOP_SHARED_DIRECTORIES) {
            queryString = PLEX_PREFIX + sharedDrive + "/" + formattedFileName;
            queriedDrive = new File(queryString);
            if (queriedDrive.exists()) {
                break;
            }
        }

        return queryString + "/Season " + seasonNumber + "/" + formattedFileName;
    }

    /**
     * Determines which metadata editor to use by grabbing the files extension,
     * then calls the appropriate method.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void editMetaData(@NonNull final String filepath, final String episodeTitle) {
        final String extension = CommonUtilities.getExtension(filepath);

        if (extension.equalsIgnoreCase("mp4")) {
            LOG.info("Setting mp4 metadata with title: {}", episodeTitle);
            TvFileUtilities.runMP4EditorForTvShow(filepath, episodeTitle);
        } else if (extension.equalsIgnoreCase("mkv")) {
            LOG.info("Setting mkv metadata with title: {}", episodeTitle);
            TvFileUtilities.runMKVEditorForTvShow(filepath, episodeTitle);
        }
        return;
    }

    public static boolean moveFile() {
        return false;
    }
}
