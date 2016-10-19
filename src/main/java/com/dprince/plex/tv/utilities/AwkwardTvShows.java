package com.dprince.plex.tv.utilities;

public enum AwkwardTvShows {
    TOSH("tosh 0", "Tosh.0"),
    AGENTS_OF_SHIELD("marvels agents of s h i e l d", "Marvel's, Agents of S.H.I.E.L.D");

    String match;
    String replacement;

    AwkwardTvShows(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }
}
