package com.dprince.plex;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.movie.MovieRenamer;
import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.TvFileUtilities;
import com.dprince.plex.tv.utilities.TvUtilities;

public class PlexUtilities {

    private static final Logger LOG = Logging.getLogger(PlexUtilities.class);

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length <= 0) {
            System.out.println("No arguments provided.");
            System.exit(0);
        }

        for (final String string : args) {
            System.out.println("Arg: " + string);
        }
        final String function = args[0];

        switch (function) {
            case ("tvShowRename"):
                LOG.info("Renaming function called");
                final String renameOriginalFilepath = args[1];

                final TvShow tvShow = TvUtilities.parseFileName(renameOriginalFilepath);
                TvUtilities.setFormattedTvShowname(tvShow);
                TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
                TvUtilities.setNewFilename(tvShow);
                TvUtilities.setNewFilepath(tvShow);

                if (TvFileUtilities.episodeExists(tvShow)) {
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Episode " + tvShow.getNewFilename() + " exists");
                    System.exit(0);
                }

                while (!TvFileUtilities.seasonFolderExists(tvShow)) {
                    LOG.info("Creating new season folder");
                    TvFileUtilities.createNewSeasonFolder(tvShow.getNewFilepath());
                }

                final boolean success = CommonUtilities.renameFile(tvShow.getOriginalFilePath(),
                        tvShow.getNewFilepath());
                LOG.info("File renamed: " + success);

                tvShow.setOriginalFilepath(tvShow.getNewFilepath());
                while (!(new File(tvShow.getNewFilepath()).exists())) {
                    LOG.info(tvShow.getNewFilepath() + " doesn't exist");
                }

                TvUtilities.editMetaData(tvShow);
                return;
            case ("refreshTitlesFile"):
                LOG.info("Refresh folders file function called");
                TvFileUtilities.deleteFoldersFile();
                TvFileUtilities.createFoldersFile();
                return;
            case ("TvMetaDataEdit"):
                LOG.info("MetaDataEdit function called");
                final TvShow metaDataEditTvShow = TvUtilities.parseFileName(args[1]);
                TvUtilities.editMetaData(metaDataEditTvShow);
                return;
            case ("newSeasonFolder"):
                LOG.info("Create New Season folder called");
                final String seasonOriginalFilepath = args[1];
                TvFileUtilities.createNewSeasonFolder(seasonOriginalFilepath);
                return;
            case ("newFolderFromDir"):
                LOG.info("Create New Season folder from directory called");
                final String dir = args[1];
                TvFileUtilities.createNewSeasonFolderFromDir(dir);
                return;
            case ("extractTvFiles"):
                LOG.info("Extract TV Shows function called");
                TvFileUtilities.extractTvFiles();
                return;
            case ("renameMovie"):
                LOG.info("Rename movie function called");
                final String movieFilename = args[1];
                MovieRenamer.renameMovie(movieFilename);
                return;
            default:
                System.exit(0);
        }
    }
}
