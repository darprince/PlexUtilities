package com.dprince.plex.tv.types;

import com.google.auto.value.AutoValue;

@AutoValue
// @Singleton
public abstract class TvShowAuto {

    String originalFilepath;
    String tvShowName;
    String rawTvShowName;
    String tvEpisodeNumber;
    String tvSeasonNumber;
    // String tvEpisodeTitle;
    String extension;
    String newFilepath;
    String filename;

    public abstract String getOriginalFilepath();

    public abstract String getTvShowName();

    public abstract String getRawTvShowName();

    public abstract String getTvEpisodeNumber();

    public abstract String getTvSeasonNumber();

    public abstract String getExtension();

    public abstract String getNewFilepath();

    public abstract String getFilename();

    public static Builder builder() {
        // return new AutoValue_TvShow.Builder();
        return null;
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setOriginalFilepath(String originalFilepath);

        public abstract Builder setTvShowName(String tvShowName);

        public abstract Builder setRawTvShowName(String rawTvShowName);

        public abstract Builder setTvEpisodeNumber(String tvEpisodeNumber);

        public abstract Builder setTvSeasonNumber(String tvSeasonNumber);

        public abstract Builder setExtension(String extension);

        public abstract Builder setNewFilepath(String newFilepath);

        public abstract Builder setFilename(String filename);

        public abstract TvShowAuto build();
    }
}
