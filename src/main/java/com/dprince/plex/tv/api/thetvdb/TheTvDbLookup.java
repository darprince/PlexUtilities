package com.dprince.plex.tv.api.thetvdb;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeNameResponse;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonResponse;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonResponseData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowIdResponse;
import com.dprince.plex.tv.api.thetvdb.utilities.ApiCalls;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * Methods for retrieving data from the TvDB.
 *
 * @author Darren
 */
public class TheTvDbLookup {

    private static final Logger LOG = Logging.getLogger(TheTvDbLookup.class);

    public static final List<String> failedShowList = new ArrayList<String>();

    private static final String FILE_OUTPUT_NAME = "/showData.json";
    private static final String PAGE = "?page=";
    private static final String AIRED_EPISODE = "&airedEpisode=";
    private final static String SERIES_CONTEXT = "/series/";
    private final static String EPISODES_CONTEXT = "/episodes";
    private final static String EPISODES_SUMMARY_CONTEXT = "/episodes/summary";
    private static final String SEARCH_SERIES_NAME = "/search/series?name=";
    private final static String EPISODES_SEARCH_PREFIX = "/episodes/query?airedSeason=";

    public static void main(String[] args) {
        // final String showID = getShowID("breaking bad");
        getShowData();
        // System.out.println(getEpisodeName("breaking bad", "02", "04"));
    }

    /**
     * Queries the TvDB for the show corresponding to the showID, episodeNumber
     * and seasonNumber and returns the episodeName
     *
     * @param showID
     *            The TvDB ID corresponding to the show being queried.
     * @param episodeNumber
     * @param seasonNumber
     * @return the name of the episode being queried, null otherwise.
     */
    @Nullable
    public static String getEpisodeTitle(@NonNull String showID, @NonNull String seasonNumber,
            @NonNull String episodeNumber) {
        final ObjectMapper mapper = new ObjectMapper();

        final String queryString = SERIES_CONTEXT + showID + EPISODES_SEARCH_PREFIX + seasonNumber
                + AIRED_EPISODE + episodeNumber;

        final String response = ApiCalls.hitTvDbAPI(queryString, "ShowID: " + showID);
        if (response != null) {
            String episodeName = null;
            try {
                final EpisodeNameResponse episodeNameResponse = mapper.readValue(response,
                        EpisodeNameResponse.class);
                episodeName = episodeNameResponse.getData().get(0).getEpisodeName();
            } catch (final IOException e) {
                LOG.error("Failed to create EpisodeNameResponse object,", e);
                return null;
            }
            return episodeName;
        }
        return null;
    }

    /**
     * Queries the TvDB for the show corresponding to the showID and returns a
     * List<{@link EpisodeData}>
     *
     * @param showID
     *            The TvDB ID for a show.
     * @return a List<{@link EpisodeData}> of all episodes for the showID.
     */
    public static List<EpisodeData> getAllEpisodesForShow(@NonNull String showID) {
        final String initialQueryString = SERIES_CONTEXT + showID + EPISODES_CONTEXT;

        int currentPage = 1;
        int lastPage = 2;
        final ObjectMapper mapper = new ObjectMapper();
        String newQueryString = initialQueryString;
        final List<EpisodeData> episodeList = new ArrayList<>();

        while (currentPage <= lastPage) {
            final String response = ApiCalls.hitTvDbAPI(newQueryString, "ShowID: " + showID);
            if (response != null) {
                try {
                    final EpisodeNameResponse episodeNameResponse = mapper.readValue(response,
                            EpisodeNameResponse.class);
                    episodeList.addAll(episodeNameResponse.getData());

                    currentPage++;
                    lastPage = episodeNameResponse.getLinks().getLast();
                    newQueryString = initialQueryString + PAGE + currentPage;
                } catch (final IOException e) {
                    LOG.error("Failed to map EpisodeNameResponse", e);
                }
            } else {
                return null;
            }
        }

        return episodeList;
    }

    /**
     * Queries the TvDB for the specified Tv Show and returns its showID
     *
     * @param showTitle
     *            The title of the show being queried
     * @return theTvDb showID of the show being queried, null otherwise
     */
    public static String getShowID(@NonNull String showTitle) {
        final ObjectMapper mapper = new ObjectMapper();
        String queryString = null;
        try {
            queryString = SEARCH_SERIES_NAME + URLEncoder.encode(showTitle, "UTF-8");
        } catch (final UnsupportedEncodingException e1) {
            LOG.error("Failed to encode URL", e1);
        }

        final String response = ApiCalls.hitTvDbAPI(queryString, "ShowTitle: " + showTitle);
        if (response != null) {
            String showID = null;
            try {
                final ShowIdResponse showIdResponse = mapper.readValue(response,
                        ShowIdResponse.class);
                showID = showIdResponse.getData().get(0).getId();
            } catch (final IOException e) {
                LOG.error("Failed to map ShowIdResponse", e);
            }

            return showID;
        }
        return null;
    }

    /**
     * Queries the TvDB for the specified Tv Show and returns all data
     *
     * @param showTitle
     *            The title of the show being queried
     * @return theTvDb {@link ShowIdResponse} of the show being queried, null
     *         otherwise
     */
    public static ShowIdResponse getShowIdResponse(@NonNull final String showTitle) {
        final ObjectMapper mapper = new ObjectMapper();
        String queryString = null;
        try {
            queryString = SEARCH_SERIES_NAME + URLEncoder.encode(showTitle, "UTF-8");
        } catch (final UnsupportedEncodingException e1) {
            LOG.error("Failed to encode URL", e1);
        }

        final String response = ApiCalls.hitTvDbAPI(queryString, "ShowTitle: " + showTitle);
        if (response != null) {
            try {
                return mapper.readValue(response, ShowIdResponse.class);
            } catch (final IOException e) {
                LOG.error("Failed to map ShowIdResponse for {} {}", showTitle, e);
                return null;
            }
        }
        return null;
    }

    /**
     * Queries the TvDB for the specified Tv Show and returns the number of
     * seasons and episodes
     *
     * @param showID
     * @return a {@link SeasonResponseData}
     */
    public static SeasonResponseData getSeasonResponseData(String showID) {
        final ObjectMapper mapper = new ObjectMapper();
        final String queryString = SERIES_CONTEXT + showID + EPISODES_SUMMARY_CONTEXT;
        final String response = ApiCalls.hitTvDbAPI(queryString, "ShowID: " + showID);

        if (response != null) {
            try {
                final SeasonResponse seasonResponse = mapper.readValue(response,
                        SeasonResponse.class);
                return seasonResponse.getSeasonResponseData();
            } catch (final IOException e) {
                LOG.error("Failed to map SeasonResponse", e);
                return null;
            }
        }
        return null;
    }

    /**
     * Creates a {@link ShowFolderData} object and writes to the shows root
     * folder
     */
    public static void getShowData() {
        for (final String rootDirectory : DESKTOP_SHARED_DIRECTORIES) {
            final File directory = new File(PLEX_PREFIX + rootDirectory);
            final List<SeasonData> seasonDataList = new ArrayList<>();
            for (final File showFolder : directory.listFiles()) {
                if (!showFolder.getName().startsWith("$")
                        && !showFolder.getName().equals("System Volume Information")) {
                    final File showFolderFile = new File(showFolder + "/" + FILE_OUTPUT_NAME);
                    if (!showFolderFile.exists()) {
                        LOG.info("Processing {}", showFolder.getName());
                        final ShowIdResponse showIdResponse = getShowIdResponse(
                                showFolder.getName());
                        if (showIdResponse != null) {
                            final ShowData showData = showIdResponse.getData().get(0);
                            LOG.info("Show data: {}", showData.toString());
                            final String showID = showData.getId();

                            final List<String> seasons = getSeasonResponseData(showID)
                                    .getAiredSeasons();
                            final List<EpisodeData> allEpisodesForShow = getAllEpisodesForShow(
                                    showID);

                            if (allEpisodesForShow == null) {
                                LOG.error("Failed to get episode list");
                                failedShowList.add(showFolder.getName());
                                continue;
                            }

                            for (final String season : seasons) {
                                final List<EpisodeData> episodeDataList = new ArrayList<>();
                                int totalEpisodesCount = 0;
                                for (final EpisodeData episodeData : allEpisodesForShow) {
                                    if (season
                                            .equals(String.valueOf(episodeData.getAiredSeason()))) {
                                        episodeDataList.add(episodeData);
                                        totalEpisodesCount++;
                                    }
                                }
                                final SeasonData seasonData = SeasonData.builder()
                                        .setEpisodeList(episodeDataList)
                                        .setSeasonNumber(Integer.parseInt(season))
                                        .setTotalEpisodes(totalEpisodesCount).build();
                                seasonDataList.add(seasonData);
                            }
                            final ShowFolderData showFolderData = ShowFolderData.builder()
                                    .setSeasonData(seasonDataList).setShowData(showData).build();
                            writeShowDataToFile(showFolder, showFolderData);
                        }
                    }
                }
            }
        }
        CommonUtilities.writeListToFile(failedShowList,
                "//Desktop-downloa/Completed/FailedShowRetrieval.txt");
    }

    /**
     * Converts a {@link ShowFolderData} to json and writes to the show's root
     * folder
     *
     * @param showFolder
     * @param showFolderData
     */
    private static void writeShowDataToFile(final File showFolder,
            final ShowFolderData showFolderData) {
        final Gson g = new Gson();
        final String output = g.toJson(showFolderData);
        try (FileWriter file = new FileWriter(showFolder.toString() + FILE_OUTPUT_NAME)) {
            file.write(output);
        } catch (final IOException e) {
            LOG.error("Failed to write show folder data");
            e.printStackTrace();
        }
    }
}
