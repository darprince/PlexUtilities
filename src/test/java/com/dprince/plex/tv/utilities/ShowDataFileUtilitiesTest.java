package com.dprince.plex.tv.utilities;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ShowDataFileUtilitiesTest {

    @Test
    public void getShowIDFromJson() throws Exception {
        String showIDFromJson = ShowDataFileUtilities.getShowIDFromSDF("Fuller House");
        assertEquals("Show ID", "301236", showIDFromJson);

        showIDFromJson = ShowDataFileUtilities.getShowIDFromSDF("Happy Days");
        assertEquals("Show ID", "74475", showIDFromJson);

        showIDFromJson = ShowDataFileUtilities.getShowIDFromSDF("800 Words");
        assertEquals("Show ID", "300667", showIDFromJson);

        showIDFromJson = ShowDataFileUtilities.getShowIDFromSDF("Good Girls Revolt");
        assertEquals("Show ID", "315419", showIDFromJson);
    }
}
