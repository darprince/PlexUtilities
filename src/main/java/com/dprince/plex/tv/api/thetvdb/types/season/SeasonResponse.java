package com.dprince.plex.tv.api.thetvdb.types.season;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_SeasonResponse.Builder.class)
@NonNullByDefault
public abstract class SeasonResponse {
    public static final String FIELD_DATA = "data";

    @JsonProperty(FIELD_DATA)
    public abstract SeasonResponseData getSeasonResponseData();

    public static Builder builder() {
        final Builder builder = new AutoValue_SeasonResponse.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract SeasonResponse build();

        @JsonProperty(FIELD_DATA)
        public abstract Builder setSeasonResponseData(final SeasonResponseData seasonResponseData);
    }
}
