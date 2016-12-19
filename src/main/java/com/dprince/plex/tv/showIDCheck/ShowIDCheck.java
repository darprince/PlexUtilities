package com.dprince.plex.tv.showIDCheck;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.utilities.ShowDataFileUtilities;
import com.dprince.plex.tv.utilities.ShowFolderUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ShowIDCheck {
    // http://thetvdb.com/banners/posters/81189-10.jpg

    private static final Logger LOG = Logging.getLogger(ShowIDCheck.class);

    final static int YES = 0;
    final static int NO = 1;
    final static int REFRESH = 2;

    public static void main(String[] args) {
        processShowIDs();
    }

    private static void createOptionPane(String showID, File showFolder, int iteration) {
        LOG.info("ShowID {}, iteration {}", showID, iteration);
        final String path = "http://thetvdb.com/banners/posters/" + showID + "-" + iteration
                + ".jpg";
        URL url;
        BufferedImage image = null;
        try {
            url = new URL(path);
            image = ImageIO.read(url);
        } catch (final IOException e) {
            LOG.info("No more images");
            return;
        }

        final String[] buttons = {
                "Yes", "No", "Refresh"
        };
        final Image scaledInstance = image.getScaledInstance(400, -1, 0);
        final JLabel label = new JLabel(new ImageIcon(scaledInstance));
        final int showConfirmDialog = JOptionPane.showOptionDialog(null, label,
                showFolder.getName(), JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                buttons, buttons[0]);
        // Yes - 0
        // No - 1
        // Cancel - 2

        switch (showConfirmDialog) {
            case (YES):
                writeCorrectID(showID, showFolder);
                return;
            case (NO):

                return;

            case (REFRESH):
                createOptionPane(showID, showFolder, ++iteration);
                return;
        }
    }

    public static void writeCorrectID(String showID, File showFolder) {
        final ShowFolderData showFolderData = getShowFolderData(showFolder);
        final ShowData showData = showFolderData.getShowData();
        final List<SeasonData> seasonData = showFolderData.getSeasonData();
        final boolean missingEpisodeCheck = showFolderData.getMissingEpisodeCheck();
        final ShowFolderData showFolderDataToWrite = ShowFolderData.builder().setCorrectShowID(true)
                .setSeasonData(seasonData).setShowData(showData)
                .setMissingEpisodeCheck(missingEpisodeCheck).build();

        writeToShowDataToFile(showFolder, showFolderDataToWrite);
    }

    public static void setCorrectIDtoTrue(File showFolder) {
        final ShowFolderData showFolderData = getShowFolderData(showFolder);
        final ShowData showData = showFolderData.getShowData();
        final List<SeasonData> seasonData = showFolderData.getSeasonData();
        final boolean missingEpisodeCheck = showFolderData.getMissingEpisodeCheck();
        final ShowFolderData showFolderDataToWrite = ShowFolderData.builder().setCorrectShowID(true)
                .setSeasonData(seasonData).setShowData(showData)
                .setMissingEpisodeCheck(missingEpisodeCheck).build();

        writeToShowDataToFile(showFolder, showFolderDataToWrite);
    }

    private static ShowFolderData getShowFolderData(File showFolder) {
        final ObjectMapper mapper = new ObjectMapper();
        String source;
        ShowFolderData showFolderData = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(PlexSettings.PLEX_PREFIX
                    + ShowFolderUtilities.getShowDriveLocation(showFolder.getName()) + "/"
                    + showFolder.getName() + "/showData.json")));
            showFolderData = mapper.readValue(source, ShowFolderData.class);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return showFolderData;
    }

    private static void writeToShowDataToFile(File showFolder,
            ShowFolderData showFolderDataToWrite) {
        TheTvDbLookup.writeShowDataToFile(showFolder, showFolderDataToWrite);
    }

    public static void processShowIDs() {
        for (final String drive : DESKTOP_SHARED_DIRECTORIES) {
            final File driveLocation = new File(PLEX_PREFIX + drive);
            final File[] listOfShows = driveLocation.listFiles();

            for (final File showFolder : listOfShows) {
                if (!CommonUtilities.isSystemFolder(showFolder)) {
                    final ShowFolderData showFolderData = getShowFolderData(showFolder);
                    if (showFolderData != null) {
                        if (showFolderData.getCorrectShowID() == false) {
                            final String showIDFromJson = showFolderData.getShowData().getId();
                            if (showIDFromJson != null) {
                                LOG.info("ShowID is null {}", showIDFromJson);
                                createOptionPane(showIDFromJson, showFolder, 1);
                            } else {
                                LOG.info("ShowID is null for {}", showIDFromJson);
                            }
                        }
                    } else {
                        // TODO: create showFolderData.json???
                    }
                }
            }
        }
    }

    public static void refreshData(String folderPath) {
        final File folder = new File(folderPath);
        final String formattedShowName = folder.getName();

        final String showID = ShowDataFileUtilities.getShowID(formattedShowName);
        TheTvDbLookup.createShowDataJSONForShow(folder, showID);
    }
}
