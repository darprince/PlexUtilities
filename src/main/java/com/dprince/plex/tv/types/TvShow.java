package com.dprince.plex.tv.types;

import javax.inject.Singleton;

@Singleton
public class TvShow {
    String rawTvShowName;
    String year;
    String tvEpisodeNumber;
    String tvSeasonNumber;
    String extension;

    String filename;
    String newFilename;
    String originalFilepath;
    String newFilepath;
    String properTvShowName;

    String tvEpisodeTitle;

    public TvShow(String rawTvShowName, String year, String tvEpisodeNumber, String tvSeasonNumber,
            String extension) {
        this.rawTvShowName = rawTvShowName;
        this.year = year;
        this.tvEpisodeNumber = tvEpisodeNumber;
        this.tvSeasonNumber = tvSeasonNumber;
        this.extension = extension;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return this.year;
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

    public void setFormattedTvShowName(String properTvShowName) {
        this.properTvShowName = properTvShowName;
    }

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    public void setTvEpisodeTitle(String tvEpisodeTitle) {
        this.tvEpisodeTitle = tvEpisodeTitle;
    }

    public String getRawTvShowName() {
        return this.rawTvShowName;
    }

    public String getProperTvShowName() {
        return this.properTvShowName;
    }

    public String getTvEpisodeNumber() {
        return this.tvEpisodeNumber;
    }

    public String getTvSeasonNumber() {
        return this.tvSeasonNumber;
    }

    public String getNewFilename() {
        return this.newFilename;
    }
}
