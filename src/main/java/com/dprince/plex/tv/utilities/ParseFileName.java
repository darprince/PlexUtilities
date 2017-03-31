package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.BASIC_REGEX;
import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;
import static com.dprince.plex.settings.PlexSettings.REGEX_FORMATTED_FILENAME;

import java.io.File;
import java.io.IOException;
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

    // private static final String BASIC_REGEX =
    // "(.*?)([\\d]{4})?[\\.](?:(?:[sS]{1}([\\d]{2})[eE]{1}([\\d]{2}))|([\\d]{1})[ofx]{1,2}([\\d]{1,2})|[pP]{1}art[\\.]?([\\d]{1,2})|([\\d]{1})([\\d]{2})[\\.-]{1}).*(mkv|mp4|avi|mpg){1}";
    // private static final String REGEX_FORMATTED_FILENAME = "(^[^-]*)[
    // -]{3}[sS]{1}([0-9]{2})[eE]{1}([0-9]{2}).*(mkv|mp4|avi|mpg|m4v){1}";

    private static boolean debug;

    /**
     * Takes the show's filepath and determines the rawTvShowName, seasonNumber,
     * episodeNumber, extension, formattedShowName, episodeTitle,
     * formattedFileName, destinationFilepath.
     *
     * @param originalFilepath
     * @return {@link TvShow}
     */
    public static TvShow parseFileName(@NonNull final String originalFilepath,
            @NonNull final boolean createShowFolder, @NonNull final boolean debugIn) {
        ParseFileName.debug = debugIn;
        String filename = new File(originalFilepath).getName();
        System.out.println(filename);
        filename = filename.toLowerCase().replaceAll("heavy.rescue.401", "heavy.rescue");
        System.out.println(filename);

        final Pattern pattern = Pattern.compile(BASIC_REGEX);
        final Matcher matcher = pattern.matcher(filename);

        final Pattern patternFormatted = Pattern.compile(REGEX_FORMATTED_FILENAME);
        final Matcher matcherFormatted = patternFormatted.matcher(filename);

        TvShow tvShow = null;
        if (matcher.find()) {
            tvShow = matcherOneFound(matcher, filename, originalFilepath, createShowFolder);
        } else if (matcherFormatted.find()) {
            tvShow = matcherTwoFound(matcherFormatted, filename, originalFilepath,
                    createShowFolder);
        } else {
            LOG.error("Failed to parse filename {}", originalFilepath);
            return null;
        }

        return tvShow;
    }

    private static TvShow matcherOneFound(Matcher matcher, String filename, String orginalFilepath,
            boolean createShowFolder) {
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        rawShowName = matcher.group(1).replaceAll("\\.", " ").toLowerCase().trim();
        if (debug) {
            System.out.println("M1 RawShowName: " + rawShowName);
        }
        if (matcher.group(3) != null) {
            System.out.println("Matcher g3");
            seasonNumber = CommonUtilities.padString(matcher.group(3));
            episodeNumber = CommonUtilities.padString(matcher.group(4));
        } else if (matcher.group(5) != null) {
            System.out.println("Matcher g5");
            seasonNumber = "01";
            episodeNumber = CommonUtilities.padString(matcher.group(5));
        } else if (matcher.group(8) != null) {
            System.out.println("Matcher g8");
            seasonNumber = CommonUtilities.padString(matcher.group(8));
            episodeNumber = CommonUtilities.padString(matcher.group(9));
        } else {
            seasonNumber = "01";
            episodeNumber = CommonUtilities.padString(matcher.group(7));
        }
        extension = matcher.group(10);

        return createTvShow(orginalFilepath, filename, rawShowName, seasonNumber, episodeNumber,
                extension, createShowFolder);
    }

    private static TvShow matcherTwoFound(Matcher matcherFormatted, String filename,
            String orginalFilepath, boolean createShowFolder) {
        String rawShowName = null;
        String seasonNumber = null;
        String episodeNumber = null;
        String extension = null;

        rawShowName = matcherFormatted.group(1);
        seasonNumber = CommonUtilities.padString(matcherFormatted.group(2));
        episodeNumber = CommonUtilities.padString(matcherFormatted.group(3));
        extension = matcherFormatted.group(4);
        if (debug) {
            System.out.println("M2 RawShowName: " + rawShowName);
        }

        return createTvShow(orginalFilepath, filename, rawShowName, seasonNumber, episodeNumber,
                extension, createShowFolder);
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
            String seasonNumber, String episodeNumber, String extension, boolean createShowFolder) {
        TvShow tvShow = null;
        try {
            final String formattedShowName = formatRawShowName(rawShowName, createShowFolder);
            if (debug) {
                System.out.println("Formatted showName: " + formattedShowName);
            }

            if (formattedShowName == null) {
                return null;
            }
            String episodeTitle = getEpisodeTitleFromSDF(formattedShowName, seasonNumber,
                    episodeNumber);

            if (debug) {
                System.out.println("EpTitle for S" + seasonNumber + "E" + episodeNumber
                        + " from SDF: " + episodeTitle + " ParseFileName 152");
            }

            if (episodeTitle == null) {
                final String showDriveLocation = ShowFolderUtilities
                        .getShowDriveLocation(formattedShowName);
                final String showFolderPath = PlexSettings.PLEX_PREFIX + "/" + showDriveLocation
                        + "/" + formattedShowName;
                ShowIDCheck.refreshData(showFolderPath);
                episodeTitle = getEpisodeTitleFromSDF(formattedShowName, seasonNumber,
                        episodeNumber);
                if (episodeTitle == null) {
                    LOG.error("Failed to get TV episode title");
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
    private static String getEpisodeTitleFromSDF(String formattedShowName, String seasonNumber,
            String episodeNumber) {

        try {
            final ShowFolderData showFolderData = ShowDataFileUtilities.getSDF(formattedShowName);
            if (showFolderData.getCorrectShowID()) {
                for (final SeasonData seasonData : showFolderData.getSeasonData()) {
                    if (seasonData.getSeasonNumber() == Integer.parseInt(seasonNumber)) {
                        for (final EpisodeData episodeData : seasonData.getEpisodeList()) {
                            if (episodeData.getAiredEpisodeNumber() == Integer
                                    .parseInt(episodeNumber)) {
                                if (debug) {
                                    System.out.println("Returning " + episodeData.getEpisodeName()
                                            + " from SDF.");
                                }
                                return episodeData.getEpisodeName();
                            }
                        }
                    }
                }
            }
        } catch (final Exception e) {
            if (debug) {
                System.out.println("EpTitle from SDF is null, trying theTVDB. ParseFileName 200");
            }
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
    static String formatRawShowName(String rawTvShowName, boolean createShowFolder) {
        String toReturn = null;

        toReturn = matchAwkwardTvShow(rawTvShowName);
        if (toReturn != null) {
            return toReturn;
        }

        toReturn = matchFromFolderName(rawTvShowName);
        if (toReturn != null) {
            return toReturn;
        } else {
            toReturn = matchFromFolderName(rawTvShowName.replaceAll(".us", ""));
            if (toReturn != null) {
                return toReturn;
            }
        }

        if (createShowFolder) {
            try {
                toReturn = ShowFolderUtilities.createShowFolder(rawTvShowName);
            } catch (final IOException e) {
                LOG.error("Failed to get formatted show name", e);
            }
        }
        return toReturn;
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
        String showID = ShowDataFileUtilities.getShowIDFromSDF(formattedShowName);
        if (showID == null) {
            showID = TheTvDbLookup.getShowID(formattedShowName);
            LOG.error("Failed to receive showID from JSON, received from the TvDB instead");
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
