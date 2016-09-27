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
    String formattedTvShowName;

    String tvEpisodeTitle;

    public TvShow(String rawTvShowName, String year, String tvEpisodeNumber, String tvSeasonNumber,
            String extension) {
        this.rawTvShowName = rawTvShowName;
        this.year = year;
        this.tvEpisodeNumber = tvEpisodeNumber;
        this.tvSeasonNumber = tvSeasonNumber;
        this.extension = extension;
    }

    @Override
    public String toString() {
        return "TvShow [rawTvShowName=" + rawTvShowName + ", year=" + year + ", tvEpisodeNumber="
                + tvEpisodeNumber + ", tvSeasonNumber=" + tvSeasonNumber + ", extension="
                + extension + ", filename=" + filename + ", newFilename=" + newFilename
                + ", originalFilepath=" + originalFilepath + ", newFilepath=" + newFilepath
                + ", formattedTvShowName=" + formattedTvShowName + ", tvEpisodeTitle="
                + tvEpisodeTitle + "]";
    }

    public String getRawTvShowName() {
        return this.rawTvShowName;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getYear() {
        return this.year;
    }

    public String getTvEpisodeNumber() {
        return this.tvEpisodeNumber;
    }

    public String getTvSeasonNumber() {
        return this.tvSeasonNumber;
    }

    public String getExtension() {
        return this.extension;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return this.filename;
    }

    public void setOriginalFilepath(String originalFilepath) {
        this.originalFilepath = originalFilepath;
    }

    public String getOriginalFilePath() {
        return this.originalFilepath;
    }

    public void setNewFilepath(String newFilepath) {
        this.newFilepath = newFilepath;
    }

    public String getNewFilepath() {
        return this.newFilepath;
    }

    public void setFormattedTvShowName(String formattedTvShowName) {
        this.formattedTvShowName = formattedTvShowName;
    }

    public String getFormattedTvShowName() {
        return this.formattedTvShowName;
    }

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    public String getNewFilename() {
        return this.newFilename;
    }

    public void setTvEpisodeTitle(String tvEpisodeTitle) {
        this.tvEpisodeTitle = tvEpisodeTitle;
    }

    public String getTvEpisodeTitle() {
        return this.tvEpisodeTitle;
    }

}
