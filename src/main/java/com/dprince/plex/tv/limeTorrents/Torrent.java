package com.dprince.plex.tv.limeTorrents;

public class Torrent {
    String href;
    String size;

    public Torrent(String href, String size) {
        this.href = href;
        this.size = size;
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
