package com.dprince.plex.tv.limeTorrents;

public enum UnwantedTvShows {
    THE_BACHELOR("the bachelor"),
    CZECH("czechcouples"),
    DC_WEEK("dc week"),
    GHOST_ADVENTURES("ghost adventures"),
    GOLD_RUSH("gold rush"),
    HELLS_KITCHEN("hells kitchen us"),
    JEFF_SOME_ALIENS("jeff some aliens"),
    LEAH_REMINY("leah remini scientology and the aftermath"),
    THE_LIBRARIANS("the librarians us"),
    MARVEL_WEEK("marvel week"),
    REALWIFESTORIES("realwifestories"),
    REAL_HOUSEWIVES("the real housewives of"),
    THE_ROYALS("the royals"),
    STRANDED("strandedteens"),
    TOP_CHEF("top chef"),
    TWO_BROKE_GIRLS("2 broke girls"),
    UFC("ufc"),
    UNDERCOVER_BOSS("undercover boss us"),
    THE_WITNESS_FOR_THE_PROSECUTION("the witness for the prosecution"),
    WWE("wwe"),
    XXX("xxx");

    String rawShowName;

    UnwantedTvShows(String rawShowName) {
        this.rawShowName = rawShowName;
    }
}
