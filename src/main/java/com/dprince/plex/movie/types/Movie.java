package com.dprince.plex.movie.types;

import javax.inject.Singleton;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@Singleton
@AutoValue
@JsonDeserialize(builder = AutoValue_Movie.Builder.class)
public abstract class Movie {
    private static final String MOVIE_NAME = "movieName";
    private static final String YEAR = "year";
    private static final String EXTENSION = "extension";

    @JsonProperty(MOVIE_NAME)
    public abstract String getMovieName();

    @JsonProperty(YEAR)
    public abstract String getYear();

    @JsonProperty(EXTENSION)
    public abstract String getExtension();

    public static Builder builder() {
        final Builder builder = new AutoValue_Movie.Builder();
        return builder;
    }

    @AutoValue.Builder
    public static abstract class Builder {
        public abstract Movie build();

        @JsonProperty(MOVIE_NAME)
        public abstract Builder setMovieName(final String movieName);

        @JsonProperty(YEAR)
        public abstract Builder setYear(final String year);

        @JsonProperty(EXTENSION)
        public abstract Builder setExtension(final String extension);
    }
}
