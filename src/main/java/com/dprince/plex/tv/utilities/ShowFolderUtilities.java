package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.io.File;
import java.io.IOException;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.apache.commons.lang3.text.WordUtils;
import org.eclipse.jdt.annotation.NonNull;
import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.types.TvShow;

public class ShowFolderUtilities {

    private static final Logger LOG = Logging.getLogger(ShowFolderUtilities.class);

    /**
     * Determines the drive that the show is located on.
     *
     * @param formattedShowName
     * @return The name of the drive that the show is located on.
     */
    public static String getShowDriveLocation(@NonNull final String formattedShowName) {
        File queriedDrive = null;
        String queryString = null;
        for (final String sharedDrive : DESKTOP_SHARED_DIRECTORIES) {
            queryString = PLEX_PREFIX + sharedDrive + "/" + formattedShowName;
            queriedDrive = new File(queryString);
            if (queriedDrive.exists()) {
                return sharedDrive;
            }
        }
        return null;
    }

    /**
     * Creates the next season folder that does not exist for a show.
     *
     * @param formattedShowName
     * @return true if folder is created, false otherwise.
     */
    public static boolean createNewSeasonFolder(@NonNull final TvShow tvShow) {
        final String formattedShowName = tvShow.getFormattedShowName();
        final String showDriveLocation = ShowFolderUtilities
                .getShowDriveLocation(formattedShowName);

        final File file = new File(PLEX_PREFIX + showDriveLocation + "/" + formattedShowName);

        if (file.exists()) {
            if (tvShow.getSeasonNumber().equals("00")) {
                final File specialsFolder = new File(file.getPath() + "\\Specials");
                System.out.println("CREATING FOLDER: " + specialsFolder.getName());
                System.out.println();
                return specialsFolder.mkdir();
            } else {
                int season = 1;
                final String seasonFolderPrefix = file.getPath() + "\\Season ";
                File seasonFolder = new File(seasonFolderPrefix + CommonUtilities.padInt(season));
                while (seasonFolder.exists()) {
                    season++;
                    seasonFolder = new File(seasonFolderPrefix + CommonUtilities.padInt(season));
                }
                System.out.println("CREATING FOLDER: " + seasonFolder.getName());
                System.out.println();
                return seasonFolder.mkdir();
            }
        }
        return false;
    }

    /**
     * Creates a new season folder in the current folder.
     *
     * @param filepath
     * @return true if folder is created, false otherwise.
     */
    public static boolean createNewSeasonFolderFromDir(String filepath) {
        final File file = new File(filepath);
        if (file.exists()) {
            int season = 1;
            final String seasonFolderPrefix = file.getPath() + "\\Season 0";
            File seasonFolder = new File(seasonFolderPrefix + season);
            while (seasonFolder.exists()) {
                season++;
                seasonFolder = new File(seasonFolderPrefix + season);
            }
            System.out.println("CREATING FOLDER: " + seasonFolder.getName());
            System.out.println();
            return seasonFolder.mkdir();
        }
        return false;
    }

    public static String createShowFolder(String rawTvShowName) throws IOException {
        final JCheckBox checkbox = new JCheckBox("Kids show?");
        final String message = "Add this show to Plex?";
        final Object[] params = {
                message, checkbox
        };
        final Object result = JOptionPane.showInputDialog(new JFrame(), params,
                WordUtils.capitalize(rawTvShowName));
        if (result == null) {
            LOG.error("Result/Checkbox is null.");
        }
        final boolean kidsShow = checkbox.isSelected();
        String newSubFolderName = null;

        if (kidsShow) {
            System.out.println("THIS IS A KIDS SHOW");
            newSubFolderName = "Kids TV\\";
        } else {

            // TODO: add numerical to regex
            String resultWithoutPre = result.toString();
            if (result.toString().startsWith("The ")) {
                resultWithoutPre = resultWithoutPre.substring(4, resultWithoutPre.length()).trim();
            }
            final String firstLetter = resultWithoutPre.toLowerCase().substring(0, 1);
            if (firstLetter.matches("[a-e]")) {
                newSubFolderName = "tv a-e\\";
            } else if (firstLetter.matches("[f-l]")) {
                newSubFolderName = "tv f-l\\";
            } else if (firstLetter.matches("[m-s]")) {
                newSubFolderName = "tv m-s\\";
            } else if (firstLetter.matches("[t-z]")) {
                newSubFolderName = "tv t-z\\";
            }
        }

        final String resultString = result.toString();
        final File folderToCreate = new File(PLEX_PREFIX + newSubFolderName + resultString);
        final File seasonFolderToCreate = new File(folderToCreate.toString() + "\\Season 01");

        // test if folder already exists
        if (folderToCreate.exists()) {
            return resultString;
        } else {
            folderToCreate.mkdir();
            seasonFolderToCreate.mkdir();
        }

        // TheTvDbLookup.createShowDataJSONForShow(folderToCreate);
        return folderToCreate.getPath();
    }
}
