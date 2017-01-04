package com.dprince.plex.tv.limeTorrents.types;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OMDBapi {
    String imdbRating;

    public OMDBapi() {
    }

    public OMDBapi(String imdbRating) {
        this.imdbRating = imdbRating;
    }

    @JsonProperty("imdbRating")
    public double getImdbRating() {
        return Double.parseDouble(imdbRating);
    }

    @JsonProperty("imdbRating")
    public void setImdbRating(String imdbRating) {
        this.imdbRating = imdbRating;
    }

}
