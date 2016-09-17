package com.dprince.plex.tv.utilities;

public enum AwkwardTvShows {
    TOSH("tosh 0", "Tosh.0");

    String match;
    String replacement;

    AwkwardTvShows(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }
}
