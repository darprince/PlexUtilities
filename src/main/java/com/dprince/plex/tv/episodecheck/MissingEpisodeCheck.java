package com.dprince.plex.tv.episodecheck;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_LOGS;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.settings.PlexSettings;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.api.thetvdb.types.episode.EpisodeData;
import com.dprince.plex.tv.api.thetvdb.types.season.SeasonData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowData;
import com.dprince.plex.tv.api.thetvdb.types.show.ShowFolderData;
import com.dprince.plex.tv.utilities.ShowDataFileUtilities;
import com.dprince.plex.tv.utilities.ShowFolderUtilities;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MissingEpisodeCheck {

    private static final Logger LOG = Logging.getLogger(MissingEpisodeCheck.class);
    public static int missingEpisodeCount = 0;
    public static int missingSeasonCount = 0;
    public static int missingSpecialSeasonCount = 0;
    public static int missingSpecialsCount = 0;
    public static int recentEpisodesCount = 0;

    public static Set<String> missingEpisodes = new HashSet<String>();
    public static Set<String> missingSeasons = new HashSet<String>();
    public static Set<String> missingSpecialSeasons = new HashSet<String>();
    public static Set<String> missingSpecials = new HashSet<String>();
    public static Set<String> recentEpisodes = new HashSet<String>();

    public static void main(String[] args) {
        try {
            checkEpisodes();
            printFiles();
        } catch (final IOException e) {
            e.printStackTrace();
        }
        System.out.println("Missing episodes: " + missingEpisodeCount);
        System.out.println("Missing seasons: " + missingSeasonCount);
        System.out.println("Missing special seasons: " + missingSpecialSeasonCount);
        System.out.println("Missing specials: " + missingSpecialsCount);
        System.out.println("Recent episodes: " + recentEpisodesCount);
    }

    public static void getMissingEpisodes() {
        main(null);
    }

    private static void printFiles() {
        try {
            System.out.println("Printing missing episodes");
            final FileWriter episodeFileWriter = CommonUtilities
                    .getFileWriter(PLEX_LOGS + "/missingEpisodes.txt");
            printMissingEpisodesToFile(episodeFileWriter, missingEpisodes);
            episodeFileWriter.close();
            System.out.println();

            System.out.println("Printing recent episodes");
            final FileWriter recentEpisodesFileWriter = CommonUtilities
                    .getFileWriter(PLEX_LOGS + "/missingRecentEpisodes.txt");
            printMissingEpisodesToFile(recentEpisodesFileWriter, recentEpisodes);
            recentEpisodesFileWriter.close();
            System.out.println();

            System.out.println("Printing missing seasons");
            final FileWriter seasonFileWriter = CommonUtilities
                    .getFileWriter(PLEX_LOGS + "/missingSeasons.txt");
            printMissingEpisodesToFile(seasonFileWriter, missingSeasons);
            seasonFileWriter.close();
            System.out.println();

            System.out.println("Printing missing specials");
            final FileWriter specialsFileWriter = CommonUtilities
                    .getFileWriter(PLEX_LOGS + "/missingSpecials.txt");
            printMissingEpisodesToFile(specialsFileWriter, missingSpecials);
            specialsFileWriter.close();

            System.out.println("Printing missing special seasons");
            final FileWriter specialSeasonFileWriter = CommonUtilities
                    .getFileWriter(PLEX_LOGS + "/missingSpecialSeasons.txt");
            printMissingEpisodesToFile(specialSeasonFileWriter, missingSpecialSeasons);
            specialSeasonFileWriter.close();

            System.out.println("episodes: " + missingEpisodes.size());
            System.out.println("seasons: " + missingSeasons.size());
            System.out.println("special Seasons: " + missingSpecialSeasons.size());
            System.out.println("specials: " + missingSpecials.size());
            System.out.println("recentEpisodes: " + recentEpisodes.size());
        } catch (final IOException e) {
            LOG.info("Failed to write missing episode file");
        }
    }

    /**
     * Scans all TvShow folders and compares the existing episodes to the list
     * retrieved from the TvDB, outputs all missing episodes.
     *
     * @throws IOException
     */
    public static void checkEpisodes() throws IOException {
        final LocalDate yesterdaysDate = new LocalDate().minusDays(1);

        for (final String drive : DESKTOP_SHARED_DIRECTORIES) {
            final File driveLocation = new File(PLEX_PREFIX + drive);
            final File[] listOfShowsPerDrive = driveLocation.listFiles();

            for (final File showFolder : listOfShowsPerDrive) {
                if (!CommonUtilities.isSystemFolder(showFolder)) {
                    final ShowFolderData showFolderData = ShowDataFileUtilities
                            .getShowFolderData(showFolder.getName());
                    if (showFolderData == null || showFolderData.getCorrectShowID() == false
                            || showFolderData.getMissingEpisodeCheck() == false) {
                        continue;
                    }

                    for (final SeasonData seasonDataObject : showFolderData.getSeasonData()) {
                        checkSeasonForMissingEpisodes(showFolder, seasonDataObject, yesterdaysDate);
                    }
                }
            }
        }
    }

    /**
     * Checks a seasonFolder for missing episodes.
     *
     * @param showFolder
     * @param missingEpisodes
     * @param seasonDataObject
     */
    private static void checkSeasonForMissingEpisodes(final File showFolder,
            final SeasonData seasonDataObject, final LocalDate yesterdaysDate) {

        final String seasonNumber = CommonUtilities.padInt(seasonDataObject.getSeasonNumber());
        if (seasonNumber.equals("00")) {
            final File specialsFolder = new File(showFolder.toString() + "/Specials");
            if (specialsFolder.exists()) {
                checkSeasonFolderForMissingEpisodes(showFolder, missingSpecials, seasonDataObject,
                        yesterdaysDate, specialsFolder, "Specials");
            } else {
                missingSpecialSeasons.add(showFolder.getName() + " - Season" + seasonNumber);
                missingSpecialSeasonCount++;
            }
        } else {
            final File seasonFolder = new File(showFolder.toString() + "/Season " + seasonNumber);
            if (seasonFolder.exists()) {
                checkSeasonFolderForMissingEpisodes(showFolder, missingEpisodes, seasonDataObject,
                        yesterdaysDate, seasonFolder, "Episodes");
            } else {
                String firstAired = null;
                final List<EpisodeData> episodeList = seasonDataObject.getEpisodeList();
                for (final EpisodeData ep : episodeList) {
                    if (ep.getAiredEpisodeNumber() == 1) {
                        try {
                            firstAired = ep.getFirstAired();
                            break;
                        } catch (final Exception e) {
                        }
                    }
                }
                if (firstAired != null && !firstAired.isEmpty()) {
                    final LocalDate ld = new LocalDate(firstAired);
                    if (ld.isBefore(new LocalDate())) {
                        missingSeasons.add(showFolder.getName() + " - Season" + seasonNumber + " "
                                + firstAired);
                        missingSeasonCount++;
                    }
                }
            }
        }
    }

    private static void checkSeasonFolderForMissingEpisodes(final File showFolder,
            final Set<String> missingEpisodes, SeasonData seasonDataObject,
            final LocalDate yesterdaysDate, File seasonFolder, String counter) {
        final File[] seasonFolderFiles = seasonFolder.listFiles();

        // For each episode listed in showFolderData JSON
        for (final EpisodeData episodeData : seasonDataObject.getEpisodeList()) {
            final String firstAired = episodeData.getFirstAired();
            if (!firstAired.isEmpty()) {
                final LocalDate episodeAiredDate = new LocalDate(firstAired);
                if (episodeAiredDate.isAfter(yesterdaysDate)) {
                    continue;
                }
            }
            final String episodeSeasonAndNumber = CommonUtilities
                    .buildEpisodeSeasonAndNumber(episodeData);

            // For each episode on Plex
            boolean found = false;
            for (final File episodeFile : seasonFolderFiles) {
                if (episodeFile.getName().contains(
                        "E" + CommonUtilities.padInt(episodeData.getAiredEpisodeNumber()))) {
                    found = true;
                }
            }
            if (!found) {
                if (!firstAired.isEmpty() && new LocalDate(firstAired).getYear() == 2016) {
                    recentEpisodes.add(
                            showFolder.getName() + " " + episodeSeasonAndNumber + " " + firstAired);
                    System.out.println("Added " + showFolder.getName() + " "
                            + episodeSeasonAndNumber + " " + firstAired);
                    if (counter.equalsIgnoreCase("Episodes")) {
                        recentEpisodesCount++;
                    } else if (counter.equalsIgnoreCase("Specials")) {
                        missingSpecialsCount++;
                    }
                } else {
                    missingEpisodes.add(
                            showFolder.getName() + " " + episodeSeasonAndNumber + " " + firstAired);
                    if (counter.equalsIgnoreCase("Episodes")) {
                        missingEpisodeCount++;
                    } else if (counter.equalsIgnoreCase("Specials")) {
                        missingSpecialsCount++;
                    }
                }
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
            final Set<String> missingEpisodes) throws IOException {
        final List<String> sortedSet = sortSet(missingEpisodes);
        for (final String missingEpisode : sortedSet) {
            System.out.println("Writing: " + missingEpisode);
            fileWriter.write(missingEpisode);
            fileWriter.write(System.getProperty("line.separator"));
        }
        fileWriter.write("Missing: " + missingEpisodes.size());
        return;
    }

    // TODO: Should be moved to Common, ShowCheckID uses as well
    private static ShowFolderData getShowFolderData(File showFolder) {
        final ObjectMapper mapper = new ObjectMapper();
        String source;
        ShowFolderData showFolderData = null;
        try {
            source = new String(Files.readAllBytes(Paths.get(PlexSettings.PLEX_PREFIX
                    + ShowFolderUtilities.getShowDriveLocation(showFolder.getName()) + "/"
                    + showFolder.getName() + "/showData.json")));
            showFolderData = mapper.readValue(source, ShowFolderData.class);
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return showFolderData;
    }

    // TODO: move to common as above
    private static void writeToShowDataToFile(File showFolder,
            ShowFolderData showFolderDataToWrite) {
        TheTvDbLookup.writeShowDataToFile(showFolder, showFolderDataToWrite);
    }

    public static void setMissingEpisodeCheckToFalse(File showFolder) {
        final ShowFolderData showFolderData = getShowFolderData(showFolder);
        final ShowData showData = showFolderData.getShowData();
        final List<SeasonData> seasonData = showFolderData.getSeasonData();
        final boolean correctShowID = showFolderData.getCorrectShowID();
        final ShowFolderData showFolderDataToWrite = ShowFolderData.builder()
                .setCorrectShowID(correctShowID).setSeasonData(seasonData).setShowData(showData)
                .setMissingEpisodeCheck(false).build();

        writeToShowDataToFile(showFolder, showFolderDataToWrite);
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
