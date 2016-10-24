package com.dprince.plex.tv.api.thetvdb.types;

import com.google.auto.value.AutoValue;

/**
 * Episode Data object.
 * 
 * @author Darren
 */
@AutoValue
public abstract class Episode {
    public static int season;
    public static int episode;
    public static String title;

    public abstract int getSeason();

    public abstract int getEpisode();

    public abstract String getTitle();

    public static Builder builder() {
        final Builder builder = new AutoValue_Episode.Builder();
        return builder;
    }

    public static Builder builder(final Episode source) {
        return new AutoValue_Episode.Builder(source);
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Episode build();

        public abstract Builder setSeason(final int season);

        public abstract Builder setEpisode(final int episode);

        public abstract Builder setTitle(final String title);
    }
}
