package com.dprince.plex.tv.api.thetvdb.types.episode;

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
@NonNullByDefault
@JsonDeserialize(builder = AutoValue_EpisodeNameResponse.Builder.class)
public abstract class EpisodeNameResponse {
    public static final String LINKS = "links";
    public static final String DATA = "data";

    @JsonProperty(LINKS)
    public abstract EpisodeLinks getLinks();

    @JsonProperty(DATA)
    public abstract List<EpisodeData> getData();

    public static Builder builder() {
        final Builder builder = new AutoValue_EpisodeNameResponse.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {
        public abstract EpisodeNameResponse build();

        @JsonProperty(LINKS)
        public abstract Builder setLinks(final EpisodeLinks episodeLinks);

        @JsonProperty(DATA)
        public abstract Builder setData(final List<EpisodeData> episodeData);
    }
}
