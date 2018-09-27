package com.dprince.plex.tv.types;

public enum AwkwardTvShows {
    A_SEASON("a season with navy football", "A Season With"),
    A_SEASON_WITH("a season with notre dame football", "A Season With"),
    AGENTS_OF_SHIELD("marvels agents of s h i e l d", "Marvel's, Agents of S.H.I.E.L.D"),
    ALASKA_LAST_FRONTIER("alaska-the last frontier", "Alaska, The Last Frontier"),
    ALASKA_LAST_FRONTIER2("alaska the last frontier", "Alaska, The Last Frontier"),
    AMERICAN_MASTERS("pbs american masters", "American Masters"),
    ANNE("anne", "Anne With an E"),
    BIG_HERO_SIX("big hero 6 the series", "Big Hero 6"),
    BROOKLYN_NINE_NINE("brooklyn nine nine", "Brooklyn Nine-Nine"),
    CONTINENT7("continent 7 antarctica", "Continent 7, Antarctica"),
    CHICAGO_PD("chicago p d", "Chicago PD"),
    CRIMINAL_MINDS("criminal minds beyond borders", "Criminal Minds, Beyond Borders"),
    CULTS_AND_EXTREME_BELIEF("cults and extreme beliefs", "Cults And Extreme Belief"),
    DESCENDANTS("descendants wicked world", "Descendants, Wicked World"),
    DIMENSION("dimension", "Dimension 404"),
    DREAMWORKS_TROLLHUNTERS("dreamworks trollhunters", "Trollhunters"),
    DR_KEN("dr ken", "Dr. Ken"),
    ESCOBAR("surviving escobar alias jj", "Surviving Escobar"),
    EVE_UK("eve uk", "Eve (2015)"),
    EYEWITNESS("eyewitness us", "Eyewitness (US)"),
    FOREVER_2018("forever", "Forever (2018)"),
    GREAT_FIRE("the great fire", "The Great Fire, In Real Time"),
    HEAVY_RESCUE("heavy rescue", "Heavy Rescue 401"),
    HUGH_HEFNER("american playboy the hugh hefner story", "American Playboy, The Hugh Hefner Story"),
    HUNTED_US("hunted us", "Hunted (US)"),
    LAST_MAN("last man standing us", "Last Man Standing"),
    MACGYVER("macgyver", "MacGyver (2016)"),
    MAGNUM_PI("magnum p i", "Magnum P.I."),
    MANHUNT("manhunt unabomber", "Manhunt, Unabomber"),
    MARVELS_JESSICA_JONES("marvels jessica jones", "Marvel's, Jessica Jones"),
    MARVELS_LUKE_CAGE("marvels luke cage", "Marvel's, Luke Cage"),
    MAYANS_MC("mayans m c", "Mayans MC"),
    MCMAFIA("mcmafia", "McMafia"),
    PRIME_SUSPECT("prime suspect", "Prime Suspect 1973"),
    ROOM_104("room", "Room 104"),
    SECRETS_AND_LIES("secrets and lies us", "Secrets And Lies (2015)"),
    STAN_LEES("stan lees lucky man", "Stan Lee's Lucky Man"),
    TABOO("taboo uk", "Taboo (2017)"),
    TANGLED("tangled the series", "Rapunzel's Tangled Adventure"),
    THE_LAST_OG("the last o g", "The Last OG"),
    THE_SELECTION("the selection-special operations experiment", "The Selection, Special Operations Experiment"),
    TOSH("tosh 0", "Tosh.0"),
    TWENTY_TWENTY("20 20 in an instant", "In An Instant"),
    THE_100("the", "The 100"),
    VICE("vice world of sports", "Vice, World of Sports"),
    VICE_TONIGHT("vice news tonight", "Vice News, Tonight"),
    X_FILES("the x-files", "X-Files"),
    YOURE_THE_WORST("youre the worst", "You're the Worst");

    public String match;
    public String replacement;

    AwkwardTvShows(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }
}
