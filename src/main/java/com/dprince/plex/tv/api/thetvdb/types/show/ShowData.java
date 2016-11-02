package com.dprince.plex.tv.api.thetvdb.types.show;

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
@JsonDeserialize(builder = AutoValue_ShowData.Builder.class)
@NonNullByDefault
public abstract class ShowData {
    public static final String FIELD_FIRST_AIRED = "firstAired";
    public static final String FIELD_ID = "id";
    public static final String FIELD_OVERVIEW = "overview";
    public static final String FIELD_SERIES_NAME = "seriesName";
    public static final String FIELD_STATUS = "status";

    @JsonProperty(FIELD_FIRST_AIRED)
    public abstract String getFirstAired();

    @JsonProperty(FIELD_ID)
    public abstract String getId();

    @Nullable
    @JsonProperty(FIELD_OVERVIEW)
    public abstract String getOverview();

    @Nullable
    @JsonProperty(FIELD_SERIES_NAME)
    public abstract String getSeriesName();

    @JsonProperty(FIELD_STATUS)
    public abstract String getStatus();

    public static Builder builder() {
        final Builder builder = new AutoValue_ShowData.Builder();
        return builder;
    }

    @AutoValue.Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static abstract class Builder {

        public abstract ShowData build();

        @JsonProperty(FIELD_FIRST_AIRED)
        public abstract Builder setFirstAired(final String firstAired);

        @JsonProperty(FIELD_ID)
        public abstract Builder setId(final String id);

        @JsonProperty(FIELD_OVERVIEW)
        public abstract Builder setOverview(@Nullable final String overview);

        @JsonProperty(FIELD_SERIES_NAME)
        public abstract Builder setSeriesName(@Nullable final String seriesName);

        @JsonProperty(FIELD_STATUS)
        public abstract Builder setStatus(final String status);
    }
}
