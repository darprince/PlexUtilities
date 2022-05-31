package com.dprince.plex.tv.missingepisodecheck.types;

import java.util.List;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_ShowData.Builder.class)
@NonNullByDefault
public abstract class ShowData {

    public static final String FIELD_SHOWID = "ShowID";
    public static final String FIELD_SHOW_TITLE = "ShowTitle";
    public static final String FIELD_SEASONS = "Seasons";

    @JsonProperty(FIELD_SHOWID)
    public abstract String getShowID();

    @JsonProperty(FIELD_SHOW_TITLE)
    public abstract String getShowTitle();

    @JsonProperty(FIELD_SEASONS)
    public abstract List<Season> getSeasons();

    public abstract Builder toBuilder();

    /**
     * Used to build {@link ShowData} instances.
     */
    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {
        public abstract ShowData build();

        @JsonProperty(FIELD_SHOWID)
        public abstract Builder setShowID(@NonNull final String showID);

        @JsonProperty(FIELD_SHOW_TITLE)
        public abstract Builder setShowTitle(@NonNull final String showTitle);

        @JsonProperty(FIELD_SEASONS)
        public abstract Builder setSeasons(@NonNull final List<Season> seasons);
    }
}
