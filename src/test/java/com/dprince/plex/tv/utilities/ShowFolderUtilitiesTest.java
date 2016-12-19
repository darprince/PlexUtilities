package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShowFolderUtilitiesTest {

    @Test
    public void getShowDriveLocation_Test() throws Exception {
        final String[] formattedFileNames = {
                "Breaking Bad", "Grandfathered", "Manhattan Love Story", "The Walking Dead"
        };

        for (int i = 0; i < formattedFileNames.length; i++) {
            assertEquals(formattedFileNames[i] + " Drive Location", DESKTOP_SHARED_DIRECTORIES[i],
                    ShowFolderUtilities.getShowDriveLocation(formattedFileNames[i]));
        }
    }
}
