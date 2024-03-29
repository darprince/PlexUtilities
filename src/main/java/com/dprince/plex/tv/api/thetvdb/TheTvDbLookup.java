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

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData3;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowIdResponse;
import com.dprince.plex.tv.api.thetvdb.utilities.ApiCalls;
import com.dprince.plex.tv.utilities.ShowDataFileUtilities;
import com.dprince.plex.tv.utilities.ShowFolderUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Methods for retrieving data from the TvDB.
 *
 * @author Darren
 */
public class TheTvDbLookup {

    private static final Logger LOG = Logging.getLogger(TheTvDbLookup.class);

    public static final List<String> failedShowList = new ArrayList<>();

    public static final String FILE_OUTPUT_NAME = "/showData.json";
    public static final String PAGE = "?page=";
    public static final String AIRED_EPISODE = "&airedEpisode=";
    public final static String SERIES_CONTEXT = "/series/";
    public final static String EPISODES_CONTEXT = "/episodes";
    public final static String EPISODES_SUMMARY_CONTEXT = "/episodes/summary";
    public static final String SEARCH_SERIES_NAME = "/search/series?name=";
    public final static String EPISODES_SEARCH_PREFIX = "/episodes/query?airedSeason=";

    public TheTvDbLookup() {
    }

    public static void main(String[] args) {
        // final String showID = getShowID("breaking bad");
        // getShowData();
        getShowIdResponseFromShowTitle("Breaking Bad");
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
            final JsonParser parser = new JsonParser();
            final JsonElement jsonElement = parser.parse(response);
            final JsonElement dataElement = jsonElement.getAsJsonObject().get("data")
                    .getAsJsonArray().get(0);

            final JsonObject jsonObject = new JsonObject();
            jsonObject.add("data", dataElement);

            String showID = null;
            try {
                final ShowIdResponse showIdResponse = mapper.readValue(jsonObject.toString(),
                        ShowIdResponse.class);
                showID = showIdResponse.getData().getId();
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
    @Deprecated
    public static ShowIdResponse getShowIdResponseFromShowTitle(@NonNull final String showTitle) {
        final ObjectMapper mapper = new ObjectMapper();
        String queryString = null;
        try {
            queryString = SEARCH_SERIES_NAME + URLEncoder.encode(showTitle, "UTF-8");
        } catch (final UnsupportedEncodingException e1) {
            LOG.error("Failed to encode URL", e1);
        }

        // TODO: create object, this is ugly
        final String response = ApiCalls.hitTvDbAPI(queryString, "ShowTitle: " + showTitle);
        if (response == null) {
            LOG.info("Response is null, returning null");
            return null;
        }
        final JsonParser parser = new JsonParser();
        final JsonElement element = parser.parse(response);
        final JsonElement data = element.getAsJsonObject().get("data").getAsJsonArray().get(0);
        data.getAsJsonObject().addProperty("correctShowID", false);
        final JsonObject jsonObject = new JsonObject();
        jsonObject.add("data", data);

        try {
            return mapper.readValue(jsonObject.toString(), ShowIdResponse.class);
        } catch (final IOException e) {
            LOG.error("Failed to map ShowIdResponse for {} {}", showTitle, e);
            return null;
        }
    }

    /**
     * Queries the TvDB for the specified Tv ShowID and returns all data
     *
     * @param showID
     *            The TvDB showID of the show being queried
     * @return theTvDb {@link ShowIdResponse} of the showID being queried, null
     *         otherwise
     */
    public static ShowIdResponse getShowIdResponseFromShowID(@NonNull final String showID) {
        final ObjectMapper mapper = new ObjectMapper();
        final String queryString = SERIES_CONTEXT + showID;

        final String response = ApiCalls.hitTvDbAPI(queryString, "ShowID: " + showID);
        System.out.println("Response " + response);

        if (response != null) {
            try {
                return mapper.readValue(response, ShowIdResponse.class);
            } catch (final IOException e) {
                LOG.error("Failed to map ShowIdResponse for {} {}", showID, e);
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
     * Creates a {@link ShowFolderData3} for all shows.
     */
    public static void createShowDataJSONForAllDirectories() {
        for (final String rootDirectory : DESKTOP_SHARED_DIRECTORIES) {
            final File directory = new File(PLEX_PREFIX + rootDirectory);

            for (final File showFolder : directory.listFiles()) {
                final ShowFolderData showFolderData = ShowDataFileUtilities
                        .getSDF(showFolder.getName());

                if (showFolderData == null) { // No JSON
                    createShowDataJSONForShow(showFolder);
                } else { // Refresh JSON files
                    try {
                        final boolean correctShowID = showFolderData.getCorrectShowID();
                        if (correctShowID) {
                            final String showID = showFolderData.getShowData().getId(); // correctID
                            createShowDataJSONForShow(showFolder, showID);
                        } else {
                            createShowDataJSONForShow(showFolder); // incorrectID
                        }
                    } catch (final Exception e) {
                        LOG.info("Failed to get ShowFolderData");
                    }
                }
            }
        }

        // outputs a list of all shows that failed to have a json file created.
        CommonUtilities.writeListToFile(failedShowList,
                "//Desktop-downloa/Completed/FailedShowRetrieval.txt");

        return;
    }

    public static void createShowDataJSONForShow(final File showFolder, final String showID) {
        parentCreateShowDataJSONForShow(showFolder, showID);
        return;
    }

    public static void createShowDataJSONForShow(final File showFolder) {
        parentCreateShowDataJSONForShow(showFolder, null);
        return;
    }

    /**
     * Queries the TvDB for data about the show being queried. Writes a
     * {@link ShowFolderData3} to the shows root folder.
     *
     * @param showFolder
     */
    static void parentCreateShowDataJSONForShow(final File showFolder,
            final String showIDIn) {
        LOG.info("ShowFolder: " + showFolder);
        LOG.info("ShowID: {}", showIDIn);
        if (CommonUtilities.isSystemFolder(showFolder)) {
            LOG.info("Folder is system folder, returnin");
            return;
        }

        final List<SeasonData> seasonDataList = new ArrayList<>();
        LOG.info("Creating showDataJson for " + showFolder.getName());

        ShowIdResponse showIdResponse = null;
        String showID = null;
        ShowData showData = null;
        if (showIDIn == null) {
            showIdResponse = getShowIdResponseFromShowTitle(showFolder.getName());
            if (showIdResponse == null) {
                LOG.error("ShowIDResponse is null when showIDIn is null, skipping");
                return;
            }
            showData = showIdResponse.getData();
            showID = showData.getId();
            LOG.info("ShowID: {}", showID);
        } else {
            showIdResponse = getShowIdResponseFromShowID(showIDIn);
            if (showIdResponse == null) {
                LOG.error("ShowIDResponse is null when showIDIn is NOT null, skipping");
                return;
            }
            showData = showIdResponse.getData();
            showID = showIDIn;
        }

        LOG.info("ShowIdResponse is not null");
        final List<String> seasons = getSeasonResponseData(showID).getAiredSeasons();
        final List<EpisodeData> allEpisodesForShow = getAllEpisodesForShow(showID);

        if (allEpisodesForShow == null) {
            LOG.error("Failed to get episode list");
            failedShowList.add(showFolder.getName());
            return;
        }

        for (final String season : seasons) {
            LOG.info("Buildin season {}", season);
            final List<EpisodeData> episodeDataList = new ArrayList<>();
            int totalEpisodesCount = 0;
            for (final EpisodeData episodeData : allEpisodesForShow) {
                if (season.equals(String.valueOf(episodeData.getAiredSeason()))) {
                    episodeDataList.add(episodeData);
                    totalEpisodesCount++;
                }
            }

            final SeasonData seasonData = SeasonData.builder().setEpisodeList(episodeDataList)
                    .setSeasonNumber(Integer.parseInt(season))
                    .setTotalEpisodes(totalEpisodesCount).build();
            seasonDataList.add(seasonData);
        }

        final boolean correctID = true;
        final boolean missingEpisodeCheck = true;
        // if (showFolder.exists()) {
        // LOG.info("Show folder exists");
        // final ShowFolderData currentFolderData = ShowDataFileUtilities
        // .getSDF(showFolder.getName());
        // if (currentFolderData != null) {
        // correctID = currentFolderData.getCorrectShowID();
        // missingEpisodeCheck = currentFolderData.getMissingEpisodeCheck();
        // }
        // }

        LOG.info("Buildin ShowFolderData");
        final ShowFolderData showFolderData = ShowFolderData.builder()
                .setCorrectShowID(true)
                .setSeasonData(seasonDataList)
                .setShowData(showData)
                .setCorrectShowID(true)
                .setMissingEpisodeCheck(missingEpisodeCheck)
                .build();

        // try {
        // System.out.println(showFolderData.toJsonString());
        // } catch (final JsonProcessingException e) {
        // e.printStackTrace();
        // }

        if (correctID == false) {
            String status = "";
            if (!showFolderData.getShowData().getStatus().equals("")) {
                status = "Status: " + showFolderData.getShowData().getStatus() + "<br>";
            }
            final String firstAired = getFirstAired(showFolderData);

            JDialog.setDefaultLookAndFeelDecorated(true);
            final int response = JOptionPane.showConfirmDialog(null,
                    "<html><body><div width=500px><p style='max-width: 250px;'>"
                            + showFolderData.getShowData().getSeriesName() + "<br>"
                            + firstAired
                            + status
                            + "<br>"
                            + showFolderData.getShowData().getOverview().replaceAll("‘", "'")
                                    .replaceAll("’", "'")
                    // + "</p></div></body></html>"
                    ,
                    "Correct for " + showFolder.getName() + "?", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.NO_OPTION) {
                System.out.println("Option close");
                final Object result = JOptionPane.showInputDialog(new JFrame(),
                        "Add series Id?", "");
                // TODO: fix this.
                if (result == null) {
                    System.out.println("Closed option");
                    System.exit(0);
                }
                parentCreateShowDataJSONForShow(showFolder, String.valueOf(result));
            } else if (response == JOptionPane.YES_OPTION) {
                System.out.println("Yes option");

                LOG.info("Attempting to create show folder - {}", showFolder.getPath());
                try {
                    final String createdShowFolder = ShowFolderUtilities
                            .createShowFolder(showFolder.getName());
                    if (!writeShowDataToFile(new File(createdShowFolder), showFolderData)) {
                        LOG.info("Failed to write to ShowDataFolder for {}",
                                showFolder.getName());
                    } else {
                        LOG.info("File written");
                        return;
                    }
                } catch (final IOException e) {
                    e.printStackTrace();
                }

            }
        } else {
            if (!writeShowDataToFile(showFolder, showFolderData)) {
                LOG.info("Failed to write to ShowDataFolder for {}", showFolder.getName());
            } else {
                LOG.info("File written");
                return;
            }
        }

        return;
    }

    private static String getFirstAired(ShowFolderData showFolderData) {
        String firstAired = "";
        if (!(firstAired = showFolderData.getShowData().getFirstAired()).equals("")) {
            return "FirstAired: " + firstAired + "<br>";
        } else if ((firstAired = getFirstEpisodeAiredDate(showFolderData)) != null) {
            return "FirstAired: " + firstAired + "<br>";
        }
        return "";
    }

    private static String getFirstEpisodeAiredDate(final ShowFolderData showFolderData) {
        for (final SeasonData sd : showFolderData.getSeasonData()) {
            if (sd.getSeasonNumber() == 1) {
                for (final EpisodeData ed : sd.getEpisodeList()) {
                    if (ed.getAiredEpisodeNumber() == 1) {
                        final String firstAired = ed.getFirstAired();
                        if (firstAired != null && !firstAired.equals("")) {
                            return firstAired;
                        } else {
                            return null;
                        }
                    }
                }
            }
        }

        return null;
    }

    /**
     * Converts a {@link ShowFolderData3} to json and writes to the show's root
     * folder
     *
     * @param showFolder
     * @param showFolderData
     */
    public static boolean writeShowDataToFile(final File showFolder,
            final ShowFolderData showFolderData) {
        final Gson g = new Gson();
        final String output = g.toJson(showFolderData);

        final File showFolderDataFile = new File(showFolder.toString() + FILE_OUTPUT_NAME);

        if (showFolderDataFile.exists()) {
            showFolderDataFile.delete();
        }

        try (FileWriter file = new FileWriter(showFolder.toString() + FILE_OUTPUT_NAME)) {
            file.write(output);
        } catch (final IOException e) {
            LOG.error("Failed to write show folder data for {}", showFolder.toString(), e);
            return false;
        }
        return true;
    }
}
