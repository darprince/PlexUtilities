package com.dprince.plex.shared;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.dprince.plex.shared.jnaMediaMetaDataEditor.domain.MovieMetadata;
import com.dprince.plex.shared.jnaMediaMetaDataEditor.domain.Video;

/**
 * @author Darren
 */
public class JnaMetaFormatter {

    public static String getMetaTitle(final String filepath) {
        final MovieMetadata movieMedataData = new MovieMetadata(filepath);
        final Optional<String> optional = movieMedataData.get(Video.TITLE);
        return optional.get();
    }

    public static void setNewMetaTitle(final String filepath, final String title) {
        try {
            final MovieMetadata movieMedataData = new MovieMetadata(filepath);

            Optional<String> optional = movieMedataData.get(Video.TITLE);
            System.out.println("MyTitle: " + optional.get());

            final Map<Video, String> videoKeys = new HashMap<>();
            videoKeys.put(Video.TITLE, title);
            movieMedataData.setVideoKeys(videoKeys);

            optional = movieMedataData.get(Video.TITLE);
            System.out.println("MyTitle: " + optional.get());
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}
