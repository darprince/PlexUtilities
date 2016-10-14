package com.dprince.plex;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
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

                final boolean success = TvFileUtilities.renameFile(tvShow.getOriginalFilePath(),
                        tvShow.getNewFilepath());
                LOG.info("File renamed: " + success);

                tvShow.setOriginalFilepath(tvShow.getNewFilepath());
                while (!(new File(tvShow.getNewFilepath()).exists())) {
                    LOG.info("File doesn't exist");
                }

                TvUtilities.editMetaData(tvShow);
                return;
            case ("moveFile"):
                LOG.info("Move File function called");
                return;
            case ("refreshTitlesFile"):
                LOG.info("Refresh folders file function called");
                TvFileUtilities.deleteFoldersFile();
                TvFileUtilities.createFoldersFile();
                return;
            case ("metaDataEdit"):
                LOG.info("MetaDataEdit function called");
                final String editFilepath = args[1];
                final TvShow metaDataEditTvShow = TvUtilities.parseFileName(editFilepath);
                TvUtilities.editMetaData(metaDataEditTvShow);
                return;
            case ("newSeasonFolder"):
                LOG.info("Create New Season folder called");
                final String seasonOriginalFilepath = args[1];
                TvFileUtilities.createNewSeasonFolder(seasonOriginalFilepath);
                return;
            default:
                System.exit(0);
        }

        Thread.sleep(10000);
    }
}
