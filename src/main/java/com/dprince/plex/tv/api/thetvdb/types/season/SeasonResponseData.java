package com.dprince.plex.tv.api.thetvdb.types.season;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_SeasonResponseData.Builder.class)
@NonNullByDefault
public abstract class SeasonResponseData {
    public static final String FIELD_AIRED_SEASONS = "airedSeasons";
    public static final String FIELD_AIRED_EPISODES = "airedEpisodes";

    @JsonProperty(FIELD_AIRED_SEASONS)
    public abstract List<String> getAiredSeasons();

    @JsonProperty(FIELD_AIRED_EPISODES)
    public abstract String getAiredEpisodes();

    public static Builder builder() {
        final Builder builder = new AutoValue_SeasonResponseData.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract SeasonResponseData build();

        @JsonProperty(FIELD_AIRED_SEASONS)
        public abstract Builder setAiredSeasons(final List<String> airedSeasons);

        @JsonProperty(FIELD_AIRED_EPISODES)
        public abstract Builder setAiredEpisodes(String airedEpisodes);
    }
}
