package com.dprince.plex;

import java.io.File;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.movie.MovieRenamer;
import com.dprince.plex.tv.api.thetvdb.TheTvDbLookup;
import com.dprince.plex.tv.autoDL.limeTorrents.LimeTorrents;
import com.dprince.plex.tv.autoDL.rarbg.RarBG;
import com.dprince.plex.tv.episodecheck.MissingEpisodeCheck;
import com.dprince.plex.tv.metadata.MetaData;
import com.dprince.plex.tv.showIDCheck.ShowIDCheck;
import com.dprince.plex.tv.types.TvShow;
import com.dprince.plex.tv.utilities.Downloads;
import com.dprince.plex.tv.utilities.ParseFileName;
import com.dprince.plex.tv.utilities.ParseNewlyDownloaded;

public class PlexUtilities {

    public static void main(String[] args) throws InterruptedException, IOException {
        if (args.length <= 0) {
            System.out.println("No arguments provided.");
            System.exit(0);
        }

        for (final String string : args) {
            System.out.println("Arg: " + string);
        }

        if (args.length > 2) {
            final int timeToWait = Integer.parseInt(args[2]);
            System.out.println("SLEEPING for " + timeToWait);
            Thread.sleep(timeToWait);
        }

        final String function = args[0];

        switch (function) {
            case ("tvShowRename"):
                System.out.println("Renaming function called");
                final TvShow tvShow = ParseFileName.parseFileName(args[1], true, true);
                Downloads.moveEpisodeFile(tvShow);
                MetaData.editMetaData(tvShow.getDestinationFilepath(), tvShow.getEpisodeTitle());
                System.exit(0);
                break;
            case ("TvMetaDataEdit"):
                System.out.println("TvMetaDataEdit function called");
                final TvShow metaDataEditTvShow = ParseFileName.parseFileName(args[1], false,
                        false);
                MetaData.editMetaData(metaDataEditTvShow.getOriginalFilepath(),
                        metaDataEditTvShow.getEpisodeTitle());
                CommonUtilities.renameFile(metaDataEditTvShow.getOriginalFilepath(),
                        metaDataEditTvShow.getDestinationFilepath());
                break;
            case ("parseFolder"):
                ParseNewlyDownloaded.parseFolder(args[1]);
                break;
            case ("extractTvFiles"):
                System.out.println("Extract TV Shows function called");
                Downloads.extractTvFiles();
                break;
            case ("renameMovie"):
                System.out.println("Rename movie function called");
                MovieRenamer.renameMovieFromFolder(args[1]);
                break;
            case ("showDataFile"):
                TheTvDbLookup.createShowDataJSONForShow(new File(args[1]), null);
                return;
            case ("setToTrue"):
                ShowIDCheck.setCorrectIDtoTrue(new File(args[1]));
                break;
            case ("writeCorrectShowData"):
                final Object result = JOptionPane.showInputDialog(new JFrame(),
                        "What is the showID?", "");
                TheTvDbLookup.createShowDataJSONForShow(new File(args[1]), result.toString());
                break;
            case ("setMissingEpisodeCheck"):
                final File file = new File(args[1]);
                MissingEpisodeCheck.setMissingEpisodeCheckToFalse(file);
                break;
            case ("refreshData"):
                ShowIDCheck.refreshData(args[1]);
                break;
            case ("getMissingEpisodes"):
                MissingEpisodeCheck.getMissingEpisodes();
                break;
            case ("autoDL"):
                LimeTorrents.begin();
                break;
            case ("rarBG"):
                RarBG.begin();
                return;
            default:
                System.exit(0);
        }
    }
}
