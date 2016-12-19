package com.dprince.plex.tv.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData3;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShowDataFileUtilities {

    private static final Logger LOG = Logging.getLogger(ShowDataFileUtilities.class);

    /**
     * Reads in a shows folder data and returns a ShowFolderData object.
     *
     * @param formattedShowName
     * @return a{@link ShowFolderData3} object.
     */
    public static ShowFolderData getShowFolderData(@NonNull final String formattedShowName) {
        final String showDriveLocation = ShowFolderUtilities
                .getShowDriveLocation(formattedShowName);
        final String showDataFile = PlexSettings.PLEX_PREFIX + "/" + showDriveLocation + "/"
                + formattedShowName + "/showData.json";

        if (new File(showDataFile).exists()) {
            try {
                final String jsonFileData = new String(Files.readAllBytes(Paths.get(showDataFile)));
                final ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(jsonFileData, ShowFolderData.class);
            } catch (final IOException e) {
                LOG.info("Failed to read showFolderData {}", e.getMessage());
                return null;
            }
        } else {
            // TODO: create showFolderData file.
        }
        return null;
    }

    /**
     * Reads the showID from a ShowFolderData object.
     *
     * @param formattedShowName
     * @return The show's TvDB ID.
     */
    public static String getShowID(@NonNull final String formattedShowName) {
        String showID = null;
        LOG.info("FormattedShowName: " + formattedShowName);
        final ShowFolderData showFolderData = getShowFolderData(formattedShowName);
        if (showFolderData == null) {
            LOG.info("Failed to read showID from ShowFolderData");
            return TheTvDbLookup.getShowID(formattedShowName);
        } else {
            showID = showFolderData.getShowData().getId();
        }
        return showID;
    }

}
