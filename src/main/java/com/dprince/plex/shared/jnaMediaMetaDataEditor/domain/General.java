package com.dprince.plex.shared.jnaMediaMetaDataEditor.domain;

public enum General {

    DURATION("Duration"),
    DURATION_STRING("Duration/String"),
    DURATION_START("Duration_Start"),
    DURATION_END("Duration_End"),
    FORMAT("Format"),
    FORMAT_INFO("Format/Info"),
    NAME("CompleteName"),
    FILESIZE("FileSize"),
    FILESIZE_ASSTRING("FileSize/String"),
    OVERALLBITRATE("OverallBitRate"),
    OVERALLBITRATE_STRING("OverallBitRate/String"),
    OVERALLBITRATE_MODE("OverallBitRate_Mode"),
    OVERALLBITRATE_MODE_STRING("OverallBitRate_Mode/String"),
    OVERALLBITRATE_MINIMUM("OverallBitRate_Minimum"),
    OVERALLBITRATE_MINIMUM_STRING("OverallBitRate_Minimum/String"),
    OVERALLBITRATE_NOMINAL("OverallBitRate_Nominal"),
    OVERALLBITRATE_NOMINAL_STRING("OverallBitRate_Nominal/String"),
    OVERALLBITRATE_MAXIMUM("OverallBitRate_Maximum"),
    OVERALLBITRATE_MAXIMUM_STRING("OverallBitRate_Maximum/String"),
    ALBUM_REPLAY_GAIN_GAIN("Album_ReplayGain_Peak"),
    ALBUM_REPLAY_GAIN_PEAK("Album_ReplayGain_Gain"),
    ALBUM_REPLAY_GAIN_GAIN_STRING("Album_ReplayGain_Gain/String"),
    ENCRYPTION("Encryption"),
    DOMAIN("Domain"),
    COLLECTION("Collection"),
    SEASON("Season"),
    SEASON_POSITION("Season_Position"),
    SEASON_POSITION_TOTAL("Season_Position_Total"),
    MOVIE("Movie"),
    MOVIE_MORE("Movie/More"),
    MOVIE_COUNTRY("Movie/Country"),
    MOVIE_URL("Movie/Url"),
    ALBUM("Album"),
    ALBUM_MORE("Album/More"),
    ALBUM_SORT("Album/Sort"),
    ALBUM_PERFORMER("Album/Performer"),
    ALBUM_PERFORMER_SORT("Album/Performer/Sort"),
    ALBUM_PERFORMER_URL("Album/Performer/Url"),
    COMIC("Comic"),
    COMIC_MORE("Comic/More"),
    COMIC_POSITION_TOTAL("Comic/Position_Total"),
    PART("Part"),
    PART_POSITION("Part/Position"),
    PART_POSITION_TOTAL("Part/Position_Total"),
    TRACK("Track"),
    TRACK_MORE("Track/More"),
    TRACK_URL("Track/Url"),
    TRACK_SORT("Track/Sort"),
    TRACK_POSITION("Track/Position"),
    TRACK_POSITION_TOTAL("Track/Position_Total"),
    GROUPING("Grouping"),
    CHAPTER("Chapter"),
    SUBTRACK("SubTrack"),
    ORIGINAL_ALBUM("Original/Album"),
    ORIGINAL_MOVIE("Original/Movie"),
    ORIGINAL_PART("Original/Part"),
    ORIGINAL_TRACK("Original/Track"),
    COMPILATION("Compilation"),
    COMPILATION_STRING("Compilation/String"),
    PERFORMER("Performer"),
    PERFORMER_SORT("Performer/Sort"),
    PERFORMER_URL("Performer/Url"),
    ORIGINAL_PERFORMER("Original/Performer"),
    ACCOMPANIMENT("Accompaniment"),
    COMPOSER("Composer"),
    COMPOSER_NATIONALITY("Composer/Nationality"),
    ARRANGER("Arranger"),
    LYRICIST("Lyricist"),
    ORIGINAL_LYRICIST("Original/Lyricist"),
    CONDUCTOR("Conductor"),
    DIRECTOR("Director"),
    CODIRECTOR("CoDirector"),
    ASSISTANTDIRECTOR("AssistantDirector"),
    DIRECTOROFPHOTOGRAPHY("DirectorOfPhotography"),
    SOUNDENGINEER("SoundEngineer"),
    ARTDIRECTOR("ArtDirector"),
    PRODUCTIONDESIGNER("ProductionDesigner"),
    CHOREGRAPHER("Choregrapher"),
    COSTUMEDESIGNER("CostumeDesigner"),
    ACTOR("Actor"),
    ACTOR_CHARACTER("Actor_Character"),
    WRITTENBY("WrittenBy"),
    SCREENPLAYBY("ScreenplayBy"),
    EDITEDBY("EditedBy"),
    COMMISSIONEDBY("CommissionedBy"),
    PRODUCER("Producer"),
    COPRODUCER("CoProducer"),
    EXECUTIVEPRODUCER("ExecutiveProducer"),
    MUSICBYDISTRIBUTEDBY("MusicByDistributedBy"),
    ORIGINALSOURCEFORM_DISTRIBUTEDBY("OriginalSourceForm/DistributedBy"),
    MASTEREDBY("MasteredBy"),
    ENCODEDBY("EncodedBy"),
    REMIXEDBY("RemixedBy"),
    PRODUCTIONSTUDIO("ProductionStudio"),
    THANKSTO("ThanksTo"),
    PUBLISHER("Publisher"),
    PUBLISHER_URL("Publisher/URL"),
    LABEL("Label"),
    GENRE("Genre"),
    MOOD("Mood"),
    CONTENTTYPE("ContentType"),
    SUBJECT("Subject"),
    DESCRIPTION("Description"),
    KEYWORDS("Keywords"),
    SUMMARY("Summary"),
    SYNOPSIS("Synopsis"),
    PERIOD("Period"),
    LAWRATING("LawRating"),
    LAWRATING_REASON("LawRating_Reason"),
    ICRA("ICRA"),
    RELEASED_DATE("Released_Date"),
    ORIGINAL_RELEASED_DATE("Original/Released_Date"),
    RECORDED_DATE("Recorded_Date"),
    ENCODED_DATE("Encoded_Date"),
    TAGGED_DATE("Tagged_Date"),
    WRITTEN_DATE("Written_Date"),
    MASTERED_DATE("Mastered_Date"),
    RECORDED_LOCATION("Recorded_Location"),
    WRITTEN_LOCATION("Written_Location"),
    ARCHIVAL_LOCATION("Archival_Location"),
    ENCODED_APPLICATION_STRING("Encoded_Application/String"),
    ENCODED_LIBRARY_STRING("Encoded_Library/String"),
    ENCODED_LIBRARY_SETTINGS("Encoded_Library_Settings"),
    CROPPEDDIMENSIONS("CroppedDimensions"),
    DOTSPERINCHLIGHTNESS("DotsPerInchLightness"),
    ORIGINALSOURCEMEDIUM("OriginalSourceMedium"),
    ORIGINALSOURCEFORM("OriginalSourceForm"),
    ORIGINALSOURCEFORM_NUMCOLORS("OriginalSourceForm/NumColors"),
    ORIGINALSOURCEFORM_NAME("OriginalSourceForm/Name"),
    ORIGINALSOURCEFORM_CROPPED("OriginalSourceForm/Cropped"),
    ORIGINALSOURCEFORM_SHARPNESS("OriginalSourceForm/Sharpness"),
    TAGGED_APPLICATION("Tagged_Application"),
    BPMISRC("BPMISRC"),
    ISBN("ISBN"),
    BARCODE("BarCode"),
    CATALOGNUMBER("CatalogNumber"),
    LABELCODE("LabelCode"),
    OWNER("Owner"),
    COPYRIGHT("Copyright"),
    COPYRIGHT_URL("Copyright/Url"),
    PRODUCER_COPYRIGHT("Producer_Copyright"),
    TERMSOFUSE("TermsOfUse"),
    SERVICENAME("ServiceName"),
    SERVICECHANNEL("ServiceChannel"),
    SERVICE_URL("Service/Url"),
    SERVICEPROVIDER("ServiceProvider"),
    SERVICEPROVIDERR_URL("ServiceProviderr/Url"),
    SERVICETYPE("ServiceType"),
    NETWORKNAME("NetworkName"),
    ORIGINALNETWORKNAME("OriginalNetworkName"),
    COUNTRY("Country"),
    TIMEZONE("TimeZone"),
    COVER("Cover"),
    COVER_DESCRIPTION("Cover_Description"),
    COVER_TYPE("Cover_Type"),
    COVER_MIME("Cover_Mime"),
    LYRICS("Lyrics"),
    COMMENT("Comment"),
    RATING("Rating"),
    ADDED_DATE("Added_Date"),
    PLAYED_FIRST_DATE("Played_First_Date"),
    PLAYED_LAST_DATE("Played_Last_Date"),
    PLAYED_COUNT("Played_Count");

    private String key;

    private General(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
