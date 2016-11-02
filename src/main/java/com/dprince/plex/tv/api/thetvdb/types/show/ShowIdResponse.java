package com.dprince.plex.tv.api.thetvdb.types.show;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;

/**
 * @author Darren
 */
@AutoValue
@JsonDeserialize(builder = AutoValue_ShowIdResponse.Builder.class)
@NonNullByDefault
public abstract class ShowIdResponse {
    public static final String FIELD_DATA = "data";

    @JsonProperty(FIELD_DATA)
    public abstract ShowData getData();

    public static Builder builder() {
        final Builder builder = new AutoValue_ShowIdResponse.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract ShowIdResponse build();

        @JsonProperty(FIELD_DATA)
        public abstract Builder setData(final ShowData showIdData);
    }
}
