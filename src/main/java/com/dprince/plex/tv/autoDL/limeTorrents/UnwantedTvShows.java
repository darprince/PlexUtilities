package com.dprince.plex.tv.autoDL.limeTorrents;

public enum UnwantedTvShows {
    AMERICAN_NINJA_WARRIOR("american ninja warrior"),
    THE_BACHELOR("the bachelor"),
    BIG_BROTHER("big brother"),
    BONES("bones"),
    CZECH("czechcouples"),
    DC_WEEK("dc week"),
    FATHER_BROWN("father brown"),
    FRESH_OFF_THE_BOAT("fresh off the boat"),
    GHOST_ADVENTURES("ghost adventures"),
    GOLD_RUSH("gold rush"),
    GROWING_UP_HIP_HOP("growing up hip hop"),
    HELLS_KITCHEN("hells kitchen us"),
    HOLBY_CITY("holby city"),
    HOMES_UNDER_THE_HAMMER("homes under the hammer"),
    JEFF_SOME_ALIENS("jeff some aliens"),
    JIMMY_KIMMEL("jimmy kimmel"),
    KATE_PLUS_8("kate plus 8"),
    LEAH_REMINY("leah remini scientology and the aftermath"),
    THE_LIBRARIANS("the librarians us"),
    MARVEL_WEEK("marvel week"),
    PAULA_ZAHN("paula zahn"),
    REALWIFESTORIES("realwifestories"),
    REAL_HOUSEWIVES("the real housewives of"),
    THE_ROYALS("the royals"),
    SHADOW_HUNTERS("shadowhunters"),
    SILENT_WITNESS("silent witness"),
    STRANDED("strandedteens"),
    TOP_CHEF("top chef"),
    TWO_BROKE_GIRLS("2 broke girls"),
    UFC("ufc"),
    UNDERCOVER_BOSS("undercover boss us"),
    THE_WITNESS_FOR_THE_PROSECUTION("the witness for the prosecution"),
    WWE("wwe"),
    XXX("xxx");

    public String rawShowName;

    UnwantedTvShows(String rawShowName) {
        this.rawShowName = rawShowName;
    }
}
