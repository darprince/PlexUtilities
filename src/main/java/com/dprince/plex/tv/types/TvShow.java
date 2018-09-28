package com.dprince.plex.tv.types;

import java.io.File;
import java.util.List;

import javax.inject.Singleton;

import org.eclipse.jdt.annotation.Nullable;

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

    public abstract String getRawShowName();

    public abstract String getOriginalFilepath();

    public abstract String getSeasonNumber();

    public abstract String getEpisodeNumber();

    public abstract String getExtension();

    public abstract String getFormattedShowName();

    @Nullable
    public abstract String getEpisodeTitle();

    public abstract String getFormattedFileName();

    public abstract String getDestinationFilepath();

    @Nullable
    public abstract List<File> getSubtitlesFilepaths();

    public static Builder builder() {
        return new AutoValue_TvShow.Builder();
    }

    public TvShow addSubtitles(final List<File> subtitlesList) {
        return TvShow.builder()
                .setDestinationFilepath(getDestinationFilepath())
                .setEpisodeNumber(getEpisodeNumber())
                .setEpisodeTitle(getEpisodeTitle())
                .setExtension(getExtension())
                .setFormattedFileName(getFormattedFileName())
                .setFormattedShowName(getFormattedShowName())
                .setOriginalFilepath(getOriginalFilepath())
                .setRawShowName(getRawShowName())
                .setSeasonNumber(getSeasonNumber())
                .setSubtitlesFilepaths(subtitlesList)
                .build();
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract TvShow build();

        public abstract Builder setRawShowName(final String rawShowName);

        public abstract Builder setOriginalFilepath(final String originalFilepath);

        public abstract Builder setSeasonNumber(final String seasonNumber);

        public abstract Builder setEpisodeNumber(final String episodeNumber);

        public abstract Builder setExtension(final String extension);

        public abstract Builder setFormattedShowName(final String formattedShowName);

        @Nullable
        public abstract Builder setEpisodeTitle(final String episodeTitle);

        public abstract Builder setFormattedFileName(final String formattedFileName);

        public abstract Builder setDestinationFilepath(final String destinationFilepath);

        public abstract Builder setSubtitlesFilepaths(
                @Nullable final List<File> subtitlesFilepaths);
    }
}
