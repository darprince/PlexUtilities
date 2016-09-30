package com.dprince.plex;

import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.TvFileUtilities;
import com.dprince.plex.tv.utilities.TvUtilities;

public class PlexUtilities {
    public static void main(String[] args) {
        final String function = args[0];
        final String argument = args[1];

        switch (function) {
            case ("tvShowRename"):
                final String originalFilepath = argument;

                final TvShow tvShow = TvUtilities.parseFileName(originalFilepath);
                TvUtilities.setFormattedTvShowname(tvShow);
                TvUtilities.setTvEpisodeTitleFromAPI(tvShow);
                TvUtilities.setNewFilename(tvShow);
                TvUtilities.setNewFilepath(tvShow);

            case ("refreshTitlesFile"):
                TvFileUtilities.deleteFoldersFile();
                TvFileUtilities.createFoldersFile();
        }
    }
}
