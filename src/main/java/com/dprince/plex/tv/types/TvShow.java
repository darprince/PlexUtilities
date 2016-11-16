package com.dprince.plex.tv.types;

import javax.inject.Singleton;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * Data object for TvShow
 *
 * @author Darren
 */
@Singleton
@AutoValue
@JsonDeserialize(builder = AutoValue_TvShow.Builder.class)
public abstract class TvShow {
    public static final String RAW_SHOW_NAME = "rawShowName";
    public static final String ORIGINAL_FILEPATH = "originalFilepath";
    public static final String EPISODE_NUMBER = "episodeNumber";
    public static final String SEASON_NUMBER = "seasonNumber";
    public static final String EXTENSION = "extension";

    public static final String FORMATTED_SHOW_NAME = "formattedShowName";
    public static final String EPISODE_TITLE = "episodeTitle";
    public static final String FORMATTED_FILE_NAME = "formattedFileName";
    public static final String DESTINATION_FILEPATH = "destinationFilepath";

    @JsonProperty(RAW_SHOW_NAME)
    public abstract String getRawShowName();

    @JsonProperty(ORIGINAL_FILEPATH)
    public abstract String getOriginalFilepath();

    @JsonProperty(SEASON_NUMBER)
    public abstract String getSeasonNumber();

    @JsonProperty(EPISODE_NUMBER)
    public abstract String getEpisodeNumber();

    @JsonProperty(EXTENSION)
    public abstract String getExtension();

    @JsonProperty(FORMATTED_SHOW_NAME)
    public abstract String getFormattedShowName();

    @Nullable
    @JsonProperty(EPISODE_TITLE)
    public abstract String getEpisodeTitle();

    @JsonProperty(FORMATTED_FILE_NAME)
    public abstract String getFormattedFileName();

    @JsonProperty(DESTINATION_FILEPATH)
    public abstract String getDestinationFilepath();

    public static Builder builder() {
        return new AutoValue_TvShow.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract TvShow build();

        @JsonProperty(RAW_SHOW_NAME)
        public abstract Builder setRawShowName(final String rawShowName);

        @JsonProperty(ORIGINAL_FILEPATH)
        public abstract Builder setOriginalFilepath(final String originalFilepath);

        @JsonProperty(SEASON_NUMBER)
        public abstract Builder setSeasonNumber(final String seasonNumber);

        @JsonProperty(EPISODE_NUMBER)
        public abstract Builder setEpisodeNumber(final String episodeNumber);

        @JsonProperty(EXTENSION)
        public abstract Builder setExtension(final String extension);

        @JsonProperty(FORMATTED_SHOW_NAME)
        public abstract Builder setFormattedShowName(final String formattedShowName);

        @Nullable
        @JsonProperty(EPISODE_TITLE)
        public abstract Builder setEpisodeTitle(final String episodeTitle);

        @JsonProperty(FORMATTED_FILE_NAME)
        public abstract Builder setFormattedFileName(final String formattedFileName);

        @JsonProperty(DESTINATION_FILEPATH)
        public abstract Builder setDestinationFilepath(final String destinationFilepath);
    }
}
