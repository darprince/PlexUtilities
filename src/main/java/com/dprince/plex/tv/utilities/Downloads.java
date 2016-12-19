package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DOWNLOADS_DIRECTORY;
import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.FILES_WE_WANT;
import static com.dprince.plex.settings.PlexSettings.VIDEO_FILES;

import java.io.File;
import java.io.IOException;

import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.types.TvShow;

public class Downloads {

    private static final Logger LOG = Logging.getLogger(Downloads.class);

    /**
     * Extracts all video files from Completed sub-directories to Completed that
     * are smaller than 700 MB and not called rarbg.com.mp4 or rarbg.com.avi
     *
     * @throws IOException
     */
    public static void extractTvFiles() throws IOException {
        final File folder = new File(DOWNLOADS_DIRECTORY);

        for (final File showFolder : folder.listFiles()) {
            if (showFolder.isDirectory()) {
                for (final File showFile : showFolder.listFiles()) {
                    if (CommonUtilities.getExtension(showFile.getName()).matches(VIDEO_FILES)
                            && !showFile.getName().toLowerCase().matches(FILES_TO_IGNORE)
                            && showFile.length() < 700000000) {

                        if (!CommonUtilities.renameFile(showFile.toString(),
                                folder.toString() + "\\" + showFile.getName())) {
                            LOG.error("Failed to move file {}", showFile.getName());
                            continue;
                        }
                    }
                }

                if (!showFolderContainsVideoFile(showFolder)) {
                    for (final File file : showFolder.listFiles()) {
                        if (file.isDirectory()) {
                            for (final File file2 : file.listFiles()) {
                                CommonUtilities.recycle(file2.toString());
                            }
                        }
                        CommonUtilities.recycle(file.toString());
                    }
                    CommonUtilities.recycle(showFolder.toString());
                }
            }
        }
    }

    /**
     * Determines if a folder contains a video file that we care about. ie. not
     * a rarbg.mkv
     *
     * @param showFolder
     * @return true if folder contains file, false otherwise
     */
    private static boolean showFolderContainsVideoFile(File showFolder) {
        for (final File showFile : showFolder.listFiles()) {
            if (CommonUtilities.getExtension(showFile.getName()).matches(FILES_WE_WANT)
                    && !showFile.getName().toLowerCase().matches(FILES_TO_IGNORE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Takes a filepath, seasonNumber, episodeNumber and determines if that
     * episode already exisits.
     *
     * @param filepath
     * @param seasonNumber
     * @param episodeNumber
     * @return true if episode exisits, false otherwise.
     */
    public static String episodeExists(@NonNull final String filepath,
            @NonNull final String seasonNumber, @NonNull final String episodeNumber) {
        final File file = new File(filepath);
        final File folder = new File(file.getParent());

        if (folder.exists()) {
            final String seasonEpisode = "S" + seasonNumber + "E" + episodeNumber;

            for (final File episode : folder.listFiles()) {
                if (episode.getName().contains(seasonEpisode)) {
                    return episode.getName();
                }
            }
        }
        return null;
    }

    /**
     * Determines if the season folder for the file exists.
     *
     * @param filepath
     * @return true if season folder exists, false otherwise.
     */
    public static boolean seasonFolderExists(String filepath) {
        final File file = new File(filepath);
        final File seasonFolder = new File(file.getParent());

        if (seasonFolder.exists()) {
            return true;
        }
        return false;
    }

    /**
     * Moves TvEpisode file to appropriate folder, if season folder does not
     * exist, the season folder and all season folders below it will be created.
     *
     * @param tvShow
     * @return true if file is moved, false otherwise.
     */
    public static boolean moveEpisodeFile(@NonNull final TvShow tvShow) {
        final String episodeExists = episodeExists(tvShow.getDestinationFilepath(),
                tvShow.getSeasonNumber(), tvShow.getEpisodeNumber());

        if (episodeExists != null) {
            System.out.println("\n************Deleting file: " + tvShow.getOriginalFilepath()
                    + "*************");
            CommonUtilities.recycle(tvShow.getOriginalFilepath());
            System.exit(0);
        }

        while (!seasonFolderExists(tvShow.getDestinationFilepath())) {
            ShowFolderUtilities.createNewSeasonFolder(tvShow);
        }

        return CommonUtilities.renameFile(tvShow.getOriginalFilepath(),
                tvShow.getDestinationFilepath());
    }
}
