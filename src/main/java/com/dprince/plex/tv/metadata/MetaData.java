package com.dprince.plex.tv.metadata;

import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;

public class MetaData {

    private static final Logger LOG = Logging.getLogger(MetaData.class);

    /**
     * Determines which metadata editor to use by grabbing the files extension,
     * then calls the appropriate method.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void editMetaData(@NonNull final String filepath, String episodeTitle) {
        final String extension = CommonUtilities.getExtension(filepath);

        final String utf8episodeTitle = new String(episodeTitle.getBytes(), StandardCharsets.UTF_8);
        if (extension.equalsIgnoreCase("mp4")) {
            LOG.info("Setting mp4 metadata with title: {}", utf8episodeTitle);
            System.out.println();
            runMP4EditorForTvShow(filepath, utf8episodeTitle);
        } else if (extension.equalsIgnoreCase("mkv")) {
            LOG.info("Setting mkv metadata with title: {}", utf8episodeTitle);
            System.out.println();
            runMKVEditorForTvShow(filepath, utf8episodeTitle);
        }
    }

    /**
     * Edits the metadata of a MP4 file.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void runMP4EditorForTvShow(String filepath, String episodeTitle) {
        try {
            if (episodeTitle != null) {
                MetaDataFormatter.writeRandomMetadata(filepath, episodeTitle);
            } else {
                MetaDataFormatter.writeRandomMetadata(filepath, "");
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Edits the meta data of a mkv file.
     *
     * @param filepath
     * @param episodeTitle
     */
    public static void runMKVEditorForTvShow(@NonNull final String filepath, String episodeTitle) {

        if (episodeTitle == null || episodeTitle.equals("null")) {
            episodeTitle = "";
        }

        final String command = MKVPROPEDIT_LOCATION + " \"" + filepath + "\" --set title=\""
                + episodeTitle
                + "\" --edit track:a1 --set name=\"\" --edit track:v1 --set name=\"\"";

        try {
            final Process exec = Runtime.getRuntime().exec(command);
            while (exec.isAlive()) {

            }
            LOG.info("Metadata has been set!!!, returning.");
        } catch (final Exception e) {
            LOG.error("Failed to edit mkv metadata", e);
        }
    }

}
