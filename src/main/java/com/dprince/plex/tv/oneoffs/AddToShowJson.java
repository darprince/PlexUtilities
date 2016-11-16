package com.dprince.plex.tv.oneoffs;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;

public class AddToShowJson {

    public static void main(String[] args) {
        addEpisodeCheck();
    }

    /**
     * Adds the episodeCheck boolean value to showDataJson
     */
    public static void addEpisodeCheck() {
        for (final String sharedDirectory : DESKTOP_SHARED_DIRECTORIES) {
            final File[] listFiles = new File(PLEX_PREFIX + sharedDirectory).listFiles();

            // for (final File showFolder : listFiles) {
            // if (!CommonUtilities.isSystemFolder(showFolder)) {
            // System.out.println("ShowFolder: " + showFolder.getName());
            // final String showName = showFolder.getName();
            // try {
            // final ShowFolderData3 showFolderData = TvUtilities
            // .getShowFolderData(showName);
            // final boolean correctShowID = showFolderData.getCorrectShowID();
            // final List<SeasonData> seasonData =
            // showFolderData.getSeasonData();
            // final ShowData showData = showFolderData.getShowData();
            //
            // final boolean missingEpisodeCheck = true;
            //
            // final ShowFolderData updatedShowFolderData =
            // ShowFolderData.builder()
            // .setCorrectShowID(correctShowID).setSeasonData(seasonData)
            // .setShowData(showData).setMissingEpisodeCheck(missingEpisodeCheck)
            // .build();
            //
            // TheTvDbLookup.writeShowDataToFile(showFolder,
            // updatedShowFolderData);
            // } catch (final Exception e) {
            // System.out.println("No data for " + showName);
            // }
            // }
            // }
        }
    }
}
