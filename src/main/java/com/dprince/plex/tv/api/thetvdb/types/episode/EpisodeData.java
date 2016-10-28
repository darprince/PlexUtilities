package com.dprince.plex.tv.api.thetvdb.types.episode;

import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_EpisodeData.Builder.class)
@NonNullByDefault
public abstract class EpisodeData {
    public static final String ABSOLUTE_NUMBER = "absoluteNumber";
    public static final String AIRED_EPISODE_NUMBER = "airedEpisodeNumber";
    public static final String AIRED_SEASON = "airedSeason";
    public static final String AIRED_SEASON_ID = "airedSeasonID";
    public static final String DVD_EPISODE_NUMBER = "dvdEpisodeNumber";
    public static final String DVD_SEASON = "dvdSeason";
    public static final String EPISODE_NAME = "episodeName";
    public static final String FIRST_AIRED = "firstAired";
    public static final String ID = "id";
    public static final String LANGUAGE = "language";
    public static final String OVERVIEW = "overview";

    @Nullable
    @JsonProperty(ABSOLUTE_NUMBER)
    public abstract Integer getAbsoluteNumber();

    @JsonProperty(AIRED_EPISODE_NUMBER)
    public abstract int getAiredEpisodeNumber();

    @JsonProperty(AIRED_SEASON)
    public abstract int getAiredSeason();

    @Nullable
    @JsonProperty(AIRED_SEASON_ID)
    public abstract Integer getAiredSeasonID();

    @Nullable
    @JsonProperty(DVD_EPISODE_NUMBER)
    public abstract Integer getDvdEpisodeNumber();

    @Nullable
    @JsonProperty(DVD_SEASON)
    public abstract Integer getDvdSeason();

    @Nullable
    @JsonProperty(EPISODE_NAME)
    public abstract String getEpisodeName();

    @Nullable
    @JsonProperty(FIRST_AIRED)
    public abstract String getFirstAired();

    @Nullable
    @JsonProperty(ID)
    public abstract Integer getID();

    @Nullable
    @JsonProperty(OVERVIEW)
    public abstract String getOverview();

    public static Builder builder() {
        final Builder builder = new AutoValue_EpisodeData.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract EpisodeData build();

        @JsonProperty(ABSOLUTE_NUMBER)
        public abstract Builder setAbsoluteNumber(@Nullable final Integer absoluteNumber);

        @JsonProperty(AIRED_EPISODE_NUMBER)
        public abstract Builder setAiredEpisodeNumber(final int airedEpisodeNumber);

        @JsonProperty(AIRED_SEASON)
        public abstract Builder setAiredSeason(final int airedSeason);

        @JsonProperty(AIRED_SEASON_ID)
        public abstract Builder setAiredSeasonID(@Nullable final Integer airedSeasonID);

        @JsonProperty(DVD_EPISODE_NUMBER)
        public abstract Builder setDvdEpisodeNumber(@Nullable final Integer dvdEpisodeNumber);

        @JsonProperty(DVD_SEASON)
        public abstract Builder setDvdSeason(@Nullable final Integer dvdSeason);

        @JsonProperty(EPISODE_NAME)
        public abstract Builder setEpisodeName(@Nullable final String episodeName);

        @JsonProperty(FIRST_AIRED)
        public abstract Builder setFirstAired(@Nullable final String firstAired);

        @JsonProperty(ID)
        public abstract Builder setID(@Nullable final Integer id);

        @JsonProperty(OVERVIEW)
        public abstract Builder setOverview(@Nullable final String overview);
    }
}
