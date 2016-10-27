package com.dprince.plex;

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
                final TvShow tvShow = TvUtilities.parseFileName(args[1]);

                if (TvFileUtilities.episodeExists(tvShow.getOriginalFilepath(),
                        tvShow.getSeasonNumber(), tvShow.getEpisodeNumber())) {
                    // TODO: change to pop up with delete file option
                    JOptionPane.showMessageDialog(new JFrame(),
                            "Episode " + tvShow.getFormattedFileName() + " exists");
                    System.exit(0);
                }

                // while (!TvFileUtilities.seasonFolderExists(tvShow)) {
                // LOG.info("Creating new season folder");
                // TvFileUtilities.createNewSeasonFolder(tvShow.getDestinationFilepath());
                // }

                final boolean success = CommonUtilities.renameFile(tvShow.getOriginalFilepath(),
                        tvShow.getDestinationFilepath());
                LOG.info("File renamed: " + success);

                // tvShow.setOriginalFilepath(tvShow.getNewFilepath());
                // while (!(new File(tvShow.getNewFilepath()).exists())) {
                // LOG.info(tvShow.getNewFilepath() + " doesn't exist");
                // }

                TvUtilities.editMetaData(tvShow.getDestinationFilepath(), tvShow.getEpisodeTitle());
                return;
            case ("refreshTitlesFile"):
                LOG.info("Refresh folders file function called");
                TvFileUtilities.deleteFoldersFile();
                TvFileUtilities.createFoldersFile();
                return;
            case ("TvMetaDataEdit"):
                LOG.info("TvMetaDataEdit function called");
                final TvShow metaDataEditTvShow = TvUtilities.parseFileName(args[1]);
                TvUtilities.editMetaData(metaDataEditTvShow.getOriginalFilepath(),
                        metaDataEditTvShow.getEpisodeTitle());
                CommonUtilities.renameFile(metaDataEditTvShow.getOriginalFilepath(),
                        metaDataEditTvShow.getDestinationFilepath());
                return;
            case ("newSeasonFolder"):
                LOG.info("Create New Season folder called");
                final TvShow tvShowForSeasonFolder = TvUtilities.parseFileName(args[1]);
                TvFileUtilities.createNewSeasonFolder(tvShowForSeasonFolder.getFormattedShowName());
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
