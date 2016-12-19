package com.dprince.plex.settings;

public class PlexSettings {
    public static final String MKVPROPEDIT_LOCATION = "//Desktop-downloa/TVShowRenamer/mkvpropedit.exe";
    public static final String FOLDERS_FILE_LOCATION = "//DESKTOP-DOWNLOA/TVShowRenamer/folders.txt";
    public static final String PARSER_LOCATION = "//DESKTOP-DOWNLOA/TVShowRenamer/parser.jar";

    public static final String DESKTOP_SHARED_DIRECTORIES[] = {
            "tv a-e", "tv f-l", "tv m-s", "tv t-z", "Kids Tv"
    };

    public static final String PLEX_PREFIX = "//Desktop-plex/";
    public static final String DOWNLOADS_PREFIX = "//Desktop-downloa/";
    public static final String DOWNLOADS_DIRECTORY = "//Desktop-Downloa/Completed";
    public static final String PLEX_LOGS = "//Desktop-downloa/PlexLogs";

    public static final String FILES_TO_IGNORE = "rarbg.com.mp4|rarbg.com.avi|rarbg.mp4";

    public static final String FILES_WE_WANT = "mkv|mp4|avi|mpg|m4v|mp3|flac|epub";
    public static final String VIDEO_FILES = "mkv|mp4|avi|mpg|m4v";

    public static final String TWO_DECIMALS = "%02d";

    public static final String PLEX_TEST_FILES = DOWNLOADS_PREFIX + "PlexTestFiles/";
    public static final String PLEX_RECYCLE = DOWNLOADS_PREFIX + "PlexRecycle/";

    public static final String TOKEN_FILE_NAME = DOWNLOADS_PREFIX + "TVShowRenamer/token.txt";
}
