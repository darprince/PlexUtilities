package com.dprince.plex.tv.api.thetvdb.types;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

@NonNullByDefault
public interface TvDbResponse {
    EpisodeLinks getLinks();

    List<EpisodeData> getData();
}
