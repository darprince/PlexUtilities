package com.dprince.plex.tv.types;

import javax.inject.Singleton;

@Singleton
public class TvShow {
    String rawTvShowName;
    String tvEpisodeNumber;
    String tvSeasonNumber;
    String extension;

    String filename;
    String originalFilepath;
    String newFilepath;
    String formattedTvShowName;

    String tvEpisodeTitle;

    public TvShow(String rawTvShowName, String tvEpisodeNumber, String tvSeasonNumber,
            String extension) {
        this.rawTvShowName = rawTvShowName;
        this.tvEpisodeNumber = tvEpisodeNumber;
        this.tvSeasonNumber = tvSeasonNumber;
        this.extension = extension;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setOriginalFilepath(String originalFilepath) {
        this.originalFilepath = originalFilepath;
    }

    public void setNewFilepath(String newFilepath) {
        this.newFilepath = newFilepath;
    }

    public void setFormattedTvShowName(String formattedTvShowName) {
        this.formattedTvShowName = formattedTvShowName;
    }

    public void setTvEpisodeTitle(String tvEpisodeTitle) {
        this.tvEpisodeTitle = tvEpisodeTitle;
    }
}
