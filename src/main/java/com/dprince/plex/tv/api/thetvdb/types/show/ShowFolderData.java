package com.dprince.plex.tv.api.thetvdb.types.show;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_ShowFolderData.Builder.class)
@NonNullByDefault
public abstract class ShowFolderData {
    public static final String FIELD_SHOW_DATA = "showData";
    public static final String FIELD_SEASON_DATA = "seasonData";
    public static final String FIELD_CORRECT_SHOWID = "correctShowID";
    // public static final String FIELD_CHECK_MISSING_EPISODES =
    // "missingEpisodeCheck";

    @JsonProperty(FIELD_CORRECT_SHOWID)
    public abstract boolean getCorrectShowID();

    // @JsonProperty(FIELD_CHECK_MISSING_EPISODES)
    // public abstract boolean getMissingEpisodeCheck();

    @JsonProperty(FIELD_SHOW_DATA)
    public abstract ShowData getShowData();

    @JsonProperty(FIELD_SEASON_DATA)
    public abstract List<SeasonData> getSeasonData();

    public static Builder builder() {
        final Builder builder = new AutoValue_ShowFolderData.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {
        public abstract ShowFolderData build();

        @JsonProperty(FIELD_CORRECT_SHOWID)
        public abstract Builder setCorrectShowID(final boolean correctShowID);

        // @JsonProperty(FIELD_CHECK_MISSING_EPISODES)
        // public abstract Builder setMissingEpisodeCheck(final boolean
        // missingEpisodeCheck);

        @JsonProperty(FIELD_SHOW_DATA)
        public abstract Builder setShowData(final ShowData showIdData);

        @JsonProperty(FIELD_SEASON_DATA)
        public abstract Builder setSeasonData(final List<SeasonData> seasonData);
    }
}
