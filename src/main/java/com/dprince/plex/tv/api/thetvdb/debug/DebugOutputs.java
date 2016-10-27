package com.dprince.plex.tv.api.thetvdb.debug;

import java.util.List;

import com.dprince.plex.tv.api.thetvdb.types.episode.Episode;

public class DebugOutputs {

    public static void printAllEpisodesForShow(List<Episode> episodeList) {
        int count = 0;

        for (int i = 1; i < 100; i++) {
            for (final Episode episode : episodeList) {
                if (episode.getSeason() == i) {
                    if (count == 0) {
                        System.out.println("\nSeason: " + i);
                    }
                    count++;
                    System.out.println(episode.getEpisode() + " " + episode.getTitle());
                }
            }
            if (count == 0) {
                System.exit(0);
            }
            count = 0;
        }

        System.out.println("Episode Count: " + episodeList.size());
    }
}
