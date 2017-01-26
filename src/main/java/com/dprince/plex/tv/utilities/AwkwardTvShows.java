package com.dprince.plex.tv.utilities;

public enum AwkwardTvShows {
    CONTINENT7("continent 7 antarctica", "Continent 7, Antarctica"),
    CHICAGO_PD("chicago p d", "Chicago PD"),
    DREAMWORKS_TROLLHUNTERS("dreamworks trollhunters", "Trollhunters"),
    DR_KEN("dr ken", "Dr. Ken"),
    TABOO("taboo uk", "Taboo (2017)"),
    TOSH("tosh 0", "Tosh.0"),
    MACGYVER("macgyver", "MacGyver (2016)"),
    SECRETS_AND_LIES("secrets and lies us", "Secrets And Lies (2015)"),
    AGENTS_OF_SHIELD("marvels agents of s h i e l d", "Marvel's, Agents of S.H.I.E.L.D"),
    DESCENDANTS("descendants wicked world", "Descendants, Wicked World"),
    EVE_UK("eve uk", "Eve (2015)"),
    ALASKA_LAST_FRONTIER("alaska-the last frontier", "Alaska, The Last Frontier"),
    EYEWITNESS("eyewitness us", "Eyewitness (US)"),
    BROOKLYN_NINE_NINE("brooklyn nine nine", "Brooklyn Nine-Nine"),
    MAGNUM_PI("magnum p i", "Magnum P.I."),
    HEAVY_RESCUE("heavy rescue", "Heavy Rescue 401"),
    HUNTED_US("hunted us", "Hunted (US)"),
    THE_SELECTION(
            "the selection-special operations experiment",
            "The Selection, Special Operations Experiment");

    String match;
    String replacement;

    AwkwardTvShows(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }
}
