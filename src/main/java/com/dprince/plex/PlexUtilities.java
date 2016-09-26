package com.dprince.plex;

import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.TvUtilities;

public class PlexUtilities {
    public static void main(String[] args) {
        final String function = args[0];
        final String argument = args[1];

        switch (function) {
            case ("tvShowRename"):
                final String filename = argument.substring(argument.lastIndexOf("/"),
                        argument.length());
                final String file = argument;

                final TvShow tvShow = TvUtilities.parseFileName(filename);
                TvUtilities.setProperFilename(tvShow);
        }
    }
}
