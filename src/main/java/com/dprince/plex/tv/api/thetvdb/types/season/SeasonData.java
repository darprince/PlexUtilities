package com.dprince.plex.tv.api.thetvdb.types.season;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_SeasonData.Builder.class)
@NonNullByDefault
public abstract class SeasonData {
    public static final String FIELD_SEASON_NUMBER = "seasonNumber";
    public static final String FIELD_TOTAL_EPISODES = "totalEpisodes";
    public static final String FIELD_EPISODE_LIST = "episodeList";

    @JsonProperty(FIELD_SEASON_NUMBER)
    public abstract int getSeasonNumber();

    @JsonProperty(FIELD_TOTAL_EPISODES)
    public abstract int getTotalEpisodes();

    @JsonProperty(FIELD_EPISODE_LIST)
    public abstract List<EpisodeData> getEpisodeList();

    public static Builder builder() {
        final Builder builder = new AutoValue_SeasonData.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract SeasonData build();

        @JsonProperty(FIELD_SEASON_NUMBER)
        public abstract Builder setSeasonNumber(final int seasonNumber);

        @JsonProperty(FIELD_TOTAL_EPISODES)
        public abstract Builder setTotalEpisodes(final int totalEpisodes);

        @JsonProperty(FIELD_EPISODE_LIST)
        public abstract Builder setEpisodeList(final List<EpisodeData> episodeDataList);
    }
}
