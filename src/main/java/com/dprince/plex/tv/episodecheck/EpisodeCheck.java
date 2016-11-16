package com.dprince.plex.tv.episodecheck;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.DOWNLOADS_DIRECTORY;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.utilities.TvUtilities;

public class EpisodeCheck {

    private static final Logger LOG = Logging.getLogger(EpisodeCheck.class);

    public static void main(String[] args) {
        try {
            checkEpisodes();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scans all TvShow folders and compares the existing episodes to the list
     * retrieved from the TvDB, outputs all missing episodes.
     *
     * @throws IOException
     */
    public static void checkEpisodes() throws IOException {
        final FileWriter fileWriter = CommonUtilities
                .getFileWriter(DOWNLOADS_DIRECTORY + "/missingEpisodes.txt");

        for (final String drive : DESKTOP_SHARED_DIRECTORIES) {
            final File driveLocation = new File(PLEX_PREFIX + drive);
            final File[] listOfShowsPerDrive = driveLocation.listFiles();

            for (final File showFolder : listOfShowsPerDrive) {
                Set<String> missingEpisodes = new HashSet<String>();
                if (!CommonUtilities.isSystemFolder(showFolder)) {
                    final ShowFolderData showFolderData = TvUtilities
                            .getShowFolderData(showFolder.getName());
                    if (showFolderData == null) {
                        continue;
                    }

                    for (final SeasonData seasonDataObject : showFolderData.getSeasonData()) {
                        missingEpisodes = checkSeasonForMissingEpisodes(showFolder, missingEpisodes,
                                seasonDataObject);
                    }
                }
                printMissingEpisodesToFile(fileWriter, showFolder, missingEpisodes);
            }
        }
    }

    /**
     * Prints missing episodes of a single show to file.
     *
     * @param fileWriter
     * @param showFolder
     * @param missingEpisodes
     * @throws IOException
     */
    private static void printMissingEpisodesToFile(final FileWriter fileWriter,
            final File showFolder, final Set<String> missingEpisodes) throws IOException {
        LOG.info("Scanned {}, missing {} episodes", showFolder.getName(), missingEpisodes.size());
        final List<String> sortedSet = sortSet(missingEpisodes);
        for (final String missingEpisode : sortedSet) {
            System.out.println(missingEpisode);
            fileWriter.write(missingEpisode);
            fileWriter.write(System.getProperty("line.separator"));
        }
        return;
    }

    /**
     * Checks a seasonFolder for missing episodes.
     *
     * @param showFolder
     * @param missingEpisodes
     * @param seasonDataObject
     */
    private static Set<String> checkSeasonForMissingEpisodes(final File showFolder,
            final Set<String> missingEpisodes, final SeasonData seasonDataObject) {
        final String seasonNumber = CommonUtilities.padInt(seasonDataObject.getSeasonNumber());
        if (seasonNumber.equals("00")) {
            return missingEpisodes;
        }

        final File seasonFolder = new File(showFolder.toString() + "/Season " + seasonNumber);
        if (seasonFolder.exists()) {
            final File[] seasonFolderFiles = seasonFolder.listFiles();

            // For each episode listed in showFolderData JSON
            for (final EpisodeData episodeData : seasonDataObject.getEpisodeList()) {
                final String episodeSeasonAndNumber = CommonUtilities
                        .buildEpisodeSeasonAndNumber(episodeData);

                // For each episode on Plex
                boolean found = false;
                for (final File episodeFile : seasonFolderFiles) {
                    if (episodeFile.getName().contains(episodeSeasonAndNumber)) {
                        found = true;
                    }
                }
                if (!found) {
                    missingEpisodes.add(showFolder.getName() + " - " + episodeSeasonAndNumber);
                }
            }
        } else {
            for (final EpisodeData episodeData : seasonDataObject.getEpisodeList()) {
                missingEpisodes.add(showFolder.getName() + " - "
                        + CommonUtilities.buildEpisodeSeasonAndNumber(episodeData));
            }
        }
        return missingEpisodes;
    }

    /**
     * Sorts a set
     *
     * @param c
     * @return a sortedList
     */
    public static <T extends Comparable<? super T>> List<T> sortSet(Collection<T> c) {
        final List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
