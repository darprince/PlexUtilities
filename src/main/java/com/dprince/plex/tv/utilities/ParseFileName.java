package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.showIDCheck.ShowIDCheck;
import com.dprince.plex.tv.types.TvShow;

/**
 * Takes a raw filename and builds a {@link TvShow}.
 *
 * @author Darren
 */
public class ParseFileName {

    private static final Logger LOG = Logging.getLogger(ParseFileName.class);

    private static final String REGEX = "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})[\\.-]{1}).*(mkv|mp4|avi|mpg){1}";
    private static final String REGEX_FORMATTED_FILENAME = "(^[^-]*)[ -]{3}[sS]{1}([0-9]{2})[eE]{1}([0-9]{2}).*(mkv|mp4|avi|mpg|m4v){1}";

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

        final Pattern pattern = Pattern.compile(REGEX);
        final Matcher matcher = pattern.matcher(filename);

        final Pattern patternFormatted = Pattern.compile(REGEX_FORMATTED_FILENAME);
        final Matcher matcherFormatted = patternFormatted.matcher(filename);

        TvShow tvShow = null;
        if (matcher.find()) {
            tvShow = matcherOneFound(matcher, filename, originalFilepath);
        } else if (matcherFormatted.find()) {
            tvShow = matcherTwoFound(matcherFormatted, filename, originalFilepath);
        } else {
            LOG.error("Failed to parse filename {}", originalFilepath);
            return null;
        }

        return tvShow;
    }

    private static TvShow matcherOneFound(Matcher matcher, String filename,
            String orginalFilepath) {
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        LOG.info("Matcher[1] found for {}", filename);
        rawShowName = matcher.group(1).replaceAll("\\.", " ").toLowerCase().trim();
        LOG.info(rawShowName);
        if (matcher.group(3) != null) {
            seasonNumber = CommonUtilities.padString(matcher.group(3));
            episodeNumber = CommonUtilities.padString(matcher.group(4));
        } else if (matcher.group(5) != null) {
            seasonNumber = CommonUtilities.padString(matcher.group(5));
            episodeNumber = CommonUtilities.padString(matcher.group(6));
        } else if (matcher.group(8) != null) {
            seasonNumber = CommonUtilities.padString(matcher.group(8));
            episodeNumber = CommonUtilities.padString(matcher.group(9));
        } else {
            seasonNumber = "01";
            episodeNumber = CommonUtilities.padString(matcher.group(7));
        }
        extension = matcher.group(10);
        LOG.info("RawShowName: {}", rawShowName);

        final TvShow tvShow = createTvShow(orginalFilepath, filename, rawShowName, seasonNumber,
                episodeNumber, extension);

        return tvShow;
    }

    private static TvShow matcherTwoFound(Matcher matcherFormatted, String filename,
            String orginalFilepath) {
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        LOG.info("Matcher[2] found for {}", filename);
        rawShowName = matcherFormatted.group(1);
        seasonNumber = CommonUtilities.padString(matcherFormatted.group(2));
        episodeNumber = CommonUtilities.padString(matcherFormatted.group(3));
        extension = matcherFormatted.group(4);
        LOG.info("RawShowName: {}", rawShowName);

        final TvShow tvShow = createTvShow(orginalFilepath, filename, rawShowName, seasonNumber,
                episodeNumber, extension);

        return tvShow;
    }

    /**
     * Creates a tvShow object from data retrieved from the parser.
     *
     * @param originalFilepath
     * @param filename
     * @param rawShowName
     * @param seasonNumber
     * @param episodeNumber
     * @param extension
     * @return a {@link TvShow}
     */
    private static TvShow createTvShow(String originalFilepath, String filename, String rawShowName,
            String seasonNumber, String episodeNumber, String extension) {
        TvShow tvShow = null;
        try {
            final String formattedShowName = formatRawShowName(rawShowName);
            LOG.info("Formatted filename from parser: " + formattedShowName);

            String episodeTitle = getEpisodeTitleFromShowDataFile(formattedShowName, seasonNumber, episodeNumber);

            if (episodeTitle == null) {
                LOG.info("Refreshing showData file.");
                final String showDriveLocation = ShowFolderUtilities
                        .getShowDriveLocation(formattedShowName);
                final String showFolderPath = PlexSettings.PLEX_PREFIX + "/" + showDriveLocation
                        + "/" + formattedShowName;
                ShowIDCheck.refreshData(showFolderPath);
                episodeTitle = getEpisodeTitleFromShowDataFile(formattedShowName, seasonNumber, episodeNumber);
                if (episodeTitle == null) {
                    LOG.info("Failed to get TV episode title");
                }
            }
            final String formattedFileName = buildFileName(formattedShowName, seasonNumber,
                    episodeNumber, episodeTitle, extension);
            if (formattedFileName == null) {
                LOG.error("Failed to get formatted file name in parser");
            }
            final String destinationFilepath = buildDestinationFilepath(formattedShowName,
                    formattedFileName, seasonNumber);
            if (destinationFilepath == null) {
                LOG.error("Failed to get destination filepath in parser");
            }

            tvShow = TvShow.builder().setDestinationFilepath(destinationFilepath)
                    .setEpisodeNumber(episodeNumber).setEpisodeTitle(episodeTitle)
                    .setExtension(extension).setFormattedFileName(formattedFileName)
                    .setFormattedShowName(formattedShowName).setOriginalFilepath(originalFilepath)
                    .setRawShowName(rawShowName).setSeasonNumber(seasonNumber).build();

            LOG.info(tvShow.toString());
        } catch (final Exception e) {
            LOG.error("Failed to build TvShow", e);
            return null;
        }

        return tvShow;
    }

    /**
     * Queries the shows folder data for the show title. If showCorrectID is
     * false, the TvDB will be queried.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @return the episode title.
     */
    private static String getEpisodeTitleFromShowDataFile(String formattedShowName, String seasonNumber,
            String episodeNumber) {
        try {
            final ShowFolderData showFolderData = ShowDataFileUtilities
                    .getShowFolderData(formattedShowName);
            if (showFolderData.getCorrectShowID()) {
                for (final SeasonData seasonData : showFolderData.getSeasonData()) {
                    if (seasonData.getSeasonNumber() == Integer.parseInt(seasonNumber)) {
                        for (final EpisodeData episodeData : seasonData.getEpisodeList()) {
                            if (episodeData.getAiredEpisodeNumber() == Integer
                                    .parseInt(episodeNumber)) {
                                LOG.info("Returning episode title from showFolderData");
                                return episodeData.getEpisodeName();
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            return getEpisodeTitleFromTvDB(formattedShowName, seasonNumber, episodeNumber);
        }
        return null;
    }

    /**
     * Builds a properly formatted FileName.
     *
     * @param formattedShowName
     * @param paddedSeasonNumber
     *            assumed to be zero padded
     * @param paddedEpisodeNumber
     *            assumed to be zero padded
     * @param episodeTitle
     * @param extension
     *            without period
     * @return A properly formatted FileName.
     */
    @NonNullByDefault
    public static String buildFileName(@NonNull final String formattedShowName,
            @NonNull final String paddedSeasonNumber, @NonNull final String paddedEpisodeNumber,
            @NonNull final String episodeTitle, @NonNull final String extension) {

        return formattedShowName + " - S" + paddedSeasonNumber + "E" + paddedEpisodeNumber + " - "
                + CommonUtilities.filenameEncode(episodeTitle) + "." + extension;
    }

    /**
     * Attempts to match the rawTvShowName to either an awkwardShowName,
     * PlexFolderName or a title from the titles file.
     *
     * @param rawTvShowName
     * @return The formatted show name
     */
    static String formatRawShowName(String rawTvShowName) {
        String toReturn = null;

        toReturn = matchAwkwardTvShow(rawTvShowName);
        if (toReturn != null) {
            return toReturn;
        }

        toReturn = matchFromFolderName(rawTvShowName);
        if (toReturn != null) {
            return toReturn;
        }

        LOG.error("Failed to formatRawShowName() ({})", rawTvShowName);
        System.exit(0);
        return null;
        // final String[][] titles = TvFileUtilities.getTitlesArray();
        //
        // String[] titleFromFileArray = null;
        // int stringMatches = 0;
        // int toReturnMatches = 0;
        //
        // if (toReturn == null) {
        // for (final String title[] : titles) {
        // stringMatches = 0;
        // final String titleFromFoldersFile = title[1].toLowerCase();
        // titleFromFileArray = titleFromFoldersFile.split(" ");
        // for (int i = 0; i < titleFromFileArray.length; i++) {
        // if (!rawTvShowName.contains(titleFromFileArray[i].toLowerCase())) {
        // break;
        // }
        // if (i == titleFromFileArray.length - 1) {
        // stringMatches++;
        // if (stringMatches > toReturnMatches) {
        // toReturnMatches = stringMatches;
        // toReturn = title[0].toString();
        // }
        // }
        // stringMatches++;
        // }
        // }
        // }
        //
        // if (toReturn == null) {
        // String formattedShowName = null;
        // try {
        // formattedShowName =
        // ShowFolderUtilities.createShowFolder(rawTvShowName);
        // } catch (final IOException e) {
        // LOG.error("Failed to get formated show name", e);
        // }
        // return formattedShowName;
        // }
        // return toReturn;
    }

    /**
     * Compares the rawFilename to the list of folder names and returns the
     * formatted showname if there is a match.
     *
     * @param rawTvShowName
     * @return The formatted tv show name if there is a match, null otherwise.
     */
    private static String matchFromFolderName(String rawTvShowName) {
        for (final String sharedDirectory : DESKTOP_SHARED_DIRECTORIES) {
            final File[] listFiles = new File(PLEX_PREFIX + sharedDirectory).listFiles();

            for (final File showFolder : listFiles) {
                String folderSubstring = showFolder.getName();
                if (showFolder.getName().contains("(")) {
                    folderSubstring = showFolder.getName()
                            .substring(0, showFolder.getName().indexOf("(")).trim();
                }
                if (rawTvShowName.contains("(")) {
                    rawTvShowName = rawTvShowName.substring(0, rawTvShowName.indexOf("(")).trim();
                }
                if (rawTvShowName.equalsIgnoreCase(folderSubstring)) {
                    LOG.info("Matched folderName \"{}\", {}", showFolder.getName(), rawTvShowName);
                    return showFolder.getName();
                }
            }
        }
        return null;
    }

    /**
     * Compares the rawFilename to the awkwardTvShow and returns the formatted
     * showname if there is a match.
     *
     * @param rawTvShowName
     * @return The formatted tv show name if there is a match, null otherwise.
     */
    public static String matchAwkwardTvShow(@NonNull final String rawTvShowName) {
        for (final AwkwardTvShows awkwardTvShows : AwkwardTvShows.values()) {
            if (rawTvShowName.equalsIgnoreCase(awkwardTvShows.match)) {
                return awkwardTvShows.replacement;
            }
        }
        return null;
    }

    /**
     * Queries the TvDB API for the specified episodes title.
     *
     * @param formattedShowName
     * @param seasonNumber
     * @param episodeNumber
     * @return The episodes title.
     */
    public static String getEpisodeTitleFromTvDB(String formattedShowName, String seasonNumber,
            String episodeNumber) {
        String showID = ShowDataFileUtilities.getShowID(formattedShowName);
        if (showID == null) {
            showID = TheTvDbLookup.getShowID(formattedShowName);
            LOG.info("Failed to receive showID from JSON, received from the TvDB instead");
        }
        final String episodeTitle = TheTvDbLookup.getEpisodeTitle(showID, seasonNumber,
                episodeNumber);
        return episodeTitle;
    }

    /**
     * Builds the new filepath on the plex server
     *
     * @param formattedFileName
     * @param paddedSeasonNumber
     * @return a properly built filepath on the plex server.
     */
    @NonNullByDefault
    public static String buildDestinationFilepath(@NonNull final String formattedShowName,
            @NonNull final String formattedFileName, @NonNull final String paddedSeasonNumber) {

        final String showDriveLocation = ShowFolderUtilities
                .getShowDriveLocation(formattedShowName);
        if (paddedSeasonNumber.equals("00")) {
            return PLEX_PREFIX + showDriveLocation + "/" + formattedShowName + "/Specials/"
                    + formattedFileName;
        } else {
            return PLEX_PREFIX + showDriveLocation + "/" + formattedShowName + "/Season "
                    + paddedSeasonNumber + "/" + formattedFileName;
        }
    }

}
