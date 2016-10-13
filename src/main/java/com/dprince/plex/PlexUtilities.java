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

        for (final String string : args) {
            System.out.println("Arg: " + string);
        }
        try {
            Thread.sleep(5000);
        } catch (final InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
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
                final TvShow editFilepathTvShow = TvUtilities.parseFileName(editFilepath);
                final String extension = editFilepathTvShow.getExtension();

                if (extension.toLowerCase().matches(".mp4|.avi")) {
                    try {
                        MetaDataFormatter.writeRandomMetadata(editFilepath, "");
                    } catch (final IOException e) {
                        e.printStackTrace();
                    }
                } else if (extension.toLowerCase().matches(".mkv")) {
                    TvFileUtilities.runMKVEditorForTvShow(editFilepathTvShow);
                }
                try {
                    Thread.sleep(5000);
                } catch (final InterruptedException e) {
                    e.printStackTrace();
                }
                return;
            case ("newSeasonFolder"):
                LOG.info("Create New Season folder called");
                final String seasonOriginalFilepath = args[1];
                TvFileUtilities.createNewSeasonFolder(seasonOriginalFilepath);
                return;
            default:
                System.exit(0);
        }
    }
}
