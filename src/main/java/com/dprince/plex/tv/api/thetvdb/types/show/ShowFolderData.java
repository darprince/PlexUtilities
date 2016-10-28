package com.dprince.plex.tv.api.thetvdb.types.show;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
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

    @NonNull
    @JsonProperty(FIELD_SHOW_DATA)
    public abstract ShowData getShowData();

    @NonNull
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

        @JsonProperty(FIELD_SHOW_DATA)
        public abstract Builder setShowData(@NonNull final ShowData showIdData);

        @JsonProperty(FIELD_SEASON_DATA)
        public abstract Builder setSeasonData(@NonNull final List<SeasonData> seasonData);
    }
}
