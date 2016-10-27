package com.dprince.plex.tv.api.thetvdb.types;

import java.util.List;

import org.eclipse.jdt.annotation.NonNullByDefault;

import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeLinks;

@NonNullByDefault
public interface TvDbResponse {
    EpisodeLinks getLinks();

    List<EpisodeData> getData();
}
