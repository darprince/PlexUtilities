package com.dprince.plex;

import java.io.IOException;

import org.slf4j.Logger;

import com.dprince.logger.Logging;
import com.dprince.plex.shared.MetaDataFormatter;
import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.TvFileUtilities;
import com.dprince.plex.tv.utilities.TvUtilities;

public class PlexUtilities {

    private static final Logger LOG = Logging.getLogger(PlexUtilities.class);

    public static void main(String[] args) {
        if (args.length <= 0) {
            System.out.println("No arguments provided.");
            System.exit(0);
        }

        final String function = args[0];

        switch (function) {
            case ("tvShowRename"):
                LOG.info("Renaming function called");
                final String originalFilepath = args[1];

                final TvShow tvShow = TvUtilities.parseFileName(originalFilepath);
                TvUtilities.setFormattedTvShowname(tvShow);
                TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
                TvUtilities.setNewFilename(tvShow);
                TvUtilities.setNewFilepath(tvShow);
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
                final String filepath = args[1];
                try {
                    MetaDataFormatter.writeRandomMetadata(filepath, "");
                } catch (final IOException e) {
                    e.printStackTrace();
                }
                return;
            case ("newSeasonFolder"):
                LOG.info("Create New Season folder called");
                return;
            default:
                System.exit(0);
        }
    }
}
