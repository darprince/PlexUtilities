package com.dprince.plex.tv.autoDL.limeTorrents;

public class Torrent {
    String href;
    String size;
    double rating;
    String title;

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Torrent(String href, String size) {
        this.href = href;
        this.size = size;
    }

    public Torrent(String href, double rating, String title) {
        this.href = href;
        this.rating = rating;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Double getSize() {
        if (size.contains("MB")) {
            return Double.parseDouble(size.replace("MB", "").trim());
        } else {
            return Double.parseDouble(size.replace("GB", "").trim()) * 1000;
        }
    }

    public void setSize(String size) {
        this.size = size;
    }

    @Override
    public String toString() {
        return "Torrent [href=" + href + ", size=" + size + "]";
    }
}
