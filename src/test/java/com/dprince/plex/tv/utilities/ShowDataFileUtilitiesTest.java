package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShowDataFileUtilitiesTest {

    @Test
    public void getShowIDFromJson() throws Exception {
        String showIDFromJson = ShowDataFileUtilities.getShowID("Fuller House");
        assertEquals("Show ID", "301236", showIDFromJson);

        showIDFromJson = ShowDataFileUtilities.getShowID("Happy Days");
        assertEquals("Show ID", "74475", showIDFromJson);

        showIDFromJson = ShowDataFileUtilities.getShowID("800 Words");
        assertEquals("Show ID", "300667", showIDFromJson);

        showIDFromJson = ShowDataFileUtilities.getShowID("Good Girls Revolt");
        assertEquals("Show ID", "315419", showIDFromJson);
    }
}
