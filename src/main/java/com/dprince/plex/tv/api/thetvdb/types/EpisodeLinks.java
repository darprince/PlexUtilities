package com.dprince.plex.tv.api.thetvdb.types;

import org.eclipse.jdt.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

@AutoValue
@JsonDeserialize(builder = AutoValue_EpisodeLinks.Builder.class)
public abstract class EpisodeLinks {
    public static final String FIRST = "first";
    public static final String LAST = "last";
    public static final String NEXT = "next";
    public static final String PREV = "prev";

    @JsonProperty(FIRST)
    public abstract int getFirst();

    @JsonProperty(LAST)
    public abstract int getLast();

    @Nullable
    @JsonProperty(NEXT)
    public abstract String getNext();

    @Nullable
    @JsonProperty(PREV)
    public abstract String getPrev();

    public static Builder builder() {
        final Builder builder = new AutoValue_EpisodeLinks.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract EpisodeLinks build();

        @JsonProperty(FIRST)
        public abstract Builder setFirst(final int first);

        @JsonProperty(LAST)
        public abstract Builder setLast(final int last);

        @JsonProperty(NEXT)
        public abstract Builder setNext(final String next);

        @JsonProperty(PREV)
        public abstract Builder setPrev(final String prev);

    }
}
