package com.dprince.plex.tv.utilities;

public enum AwkwardTvShows {
    CONTINENT7("continent 7 antarctica", "Continent 7, Antarctica"),
    TOSH("tosh 0", "Tosh.0"),
    MACGYVER("macgyver", "MacGyver (2016)"),
    SECRETS_AND_LIES("secrets and lies us", "Secrets And Lies (2015)"),
    AGENTS_OF_SHIELD("marvels agents of s h i e l d", "Marvel's, Agents of S.H.I.E.L.D"),
    DESCENDANTS("descendants wicked world", "Descendants, Wicked World"),
    EVE_UK("eve uk", "Eve (2015)");

    String match;
    String replacement;

    AwkwardTvShows(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }
}
