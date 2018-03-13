package com.dprince.plex.tv.utilities;

public enum AwkwardTvShows {
    CONTINENT7("continent 7 antarctica", "Continent 7, Antarctica"),
    CHICAGO_PD("chicago p d", "Chicago PD"),
    CRIMINAL_MINDS("criminal minds beyond borders", "Criminal Minds, Beyond Borders"),
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
    ALASKA_LAST_FRONTIER2("alaska the last frontier", "Alaska, The Last Frontier"),
    TWENTY_TWENTY("20 20 in an instant", "In An Instant"),
    EYEWITNESS("eyewitness us", "Eyewitness (US)"),
    BROOKLYN_NINE_NINE("brooklyn nine nine", "Brooklyn Nine-Nine"),
    MAGNUM_PI("magnum p i", "Magnum P.I."),
    HEAVY_RESCUE("heavy rescue", "Heavy Rescue 401"),
    ROOM_104("room", "Room 104"),
    HUNTED_US("hunted us", "Hunted (US)"),
    DIMENSION("dimension", "Dimension 404"),
    PRIME_SUSPECT("prime suspect", "Prime Suspect 1973"),
    MANHUNT("manhunt unabomber", "Manhunt, Unabomber"),
    MARVELS_JESSICA_JONES("marvels jessica jones", "Marvel's, Jessica Jones"),
    THE_100("the", "The 100"),
    YOURE_THE_WORST("youre the worst", "You're the Worst"),
    GREAT_FIRE("the great fire", "The Great Fire, In Real Time"),
    AMERICAN_MASTERS("pbs american masters", "American Masters"),
    THE_SELECTION(
            "the selection-special operations experiment",
            "The Selection, Special Operations Experiment"),
    VICE("vice world of sports", "Vice, World of Sports"),
    VICE_TONIGHT("vice news tonight", "Vice News, Tonight"),
    LAST_MAN("last man standing us", "Last Man Standing"),
    A_SEASON("a season with navy football", "A Season With"),
    X_FILES("the x-files", "X-Files"),
    A_SEASON_WITH("a season with notre dame football", "A Season With"),
    MCMAFIA("mcmafia", "McMafia"),
    HUGH_HEFNER(
            "american playboy the hugh hefner story",
            "American Playboy, The Hugh Hefner Story"),
    ESCOBAR("surviving escobar alias jj", "Surviving Escobar");

    String match;
    String replacement;

    AwkwardTvShows(String match, String replacement) {
        this.match = match;
        this.replacement = replacement;
    }
}
