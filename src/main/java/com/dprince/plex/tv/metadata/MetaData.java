package com.dprince.plex.tv.metadata;

import static com.dprince.plex.settings.PlexSettings.MKVPROPEDIT_LOCATION;

import java.io.IOException;

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
    public static void editMetaData(@NonNull final String filepath, final String episodeTitle) {
        final String extension = CommonUtilities.getExtension(filepath);

        if (extension.equalsIgnoreCase("mp4")) {
            LOG.info("Setting mp4 metadata with title: {}", episodeTitle);
            runMP4EditorForTvShow(filepath, episodeTitle);
        } else if (extension.equalsIgnoreCase("mkv")) {
            LOG.info("Setting mkv metadata with title: {}", episodeTitle);
            runMKVEditorForTvShow(filepath, episodeTitle);
        }
        return;
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
            LOG.error("Failed to call MetaDataFormatter", e);
        }

        try {
            LOG.info("ReRunning METADATA FORMATTER for mp4");
            if (!MetaDataFormatter.getTitleFromMetaData(filepath).equals(episodeTitle)) {
                runMP4EditorForTvShow(filepath, episodeTitle);
            }
        } catch (final IOException e) {
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

        LOG.info("Mkv metadata edit command: " + command);

        try {
            final Process exec = Runtime.getRuntime().exec(command);
            while (exec.isAlive()) {

            }
            LOG.info("Metadata has been set!!!, returning.");
            return;
        } catch (final Exception e) {
            LOG.error("Failed to edit mkv metadata", e);
        }
    }

}
