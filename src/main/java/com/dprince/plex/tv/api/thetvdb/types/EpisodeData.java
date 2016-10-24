package com.dprince.plex.tv.api.thetvdb.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_EpisodeData.Builder.class)
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

    @JsonProperty(ABSOLUTE_NUMBER)
    public abstract int getAbsoluteNumber();

    @JsonProperty(AIRED_EPISODE_NUMBER)
    public abstract int getAiredEpisodeNumber();

    @JsonProperty(AIRED_SEASON)
    public abstract int getAiredSeason();

    @JsonProperty(AIRED_SEASON_ID)
    public abstract int getAiredSeasonID();

    @JsonProperty(DVD_EPISODE_NUMBER)
    public abstract int getDvdEpisodeNumber();

    @JsonProperty(DVD_SEASON)
    public abstract int getDvdSeason();

    @JsonProperty(EPISODE_NAME)
    public abstract String getEpisodeName();

    @JsonProperty(FIRST_AIRED)
    public abstract String getFirstAired();

    @JsonProperty(ID)
    public abstract int getID();

    // @JsonProperty(LANGUAGE)
    // public abstract Language getLanguage();

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
        public abstract Builder setAbsoluteNumber(final int absoluteNumber);

        @JsonProperty(AIRED_EPISODE_NUMBER)
        public abstract Builder setAiredEpisodeNumber(final int airedEpisodeNumber);

        @JsonProperty(AIRED_SEASON)
        public abstract Builder setAiredSeason(final int airedSeason);

        @JsonProperty(AIRED_SEASON_ID)
        public abstract Builder setAiredSeasonID(final int airedSeasonID);

        @JsonProperty(DVD_EPISODE_NUMBER)
        public abstract Builder setDvdEpisodeNumber(final int dvdEpisodeNumber);

        @JsonProperty(DVD_SEASON)
        public abstract Builder setDvdSeason(final int dvdSeason);

        @JsonProperty(EPISODE_NAME)
        public abstract Builder setEpisodeName(final String episodeName);

        @JsonProperty(FIRST_AIRED)
        public abstract Builder setFirstAired(final String firstAired);

        @JsonProperty(ID)
        public abstract Builder setID(final int id);

        // @JsonProperty(LANGUAGE)
        // public abstract Builder setLanguage(final Language language);

        @JsonProperty(OVERVIEW)
        public abstract Builder setOverview(final String overview);
    }
}
