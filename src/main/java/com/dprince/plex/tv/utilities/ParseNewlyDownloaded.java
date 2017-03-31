package com.dprince.plex.tv.utilities;

import static com.dprince.plex.settings.PlexSettings.FILES_TO_IGNORE;
import static com.dprince.plex.settings.PlexSettings.VIDEO_FILES;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.annotation.NonNull;

import com.dprince.plex.common.CommonUtilities;
import com.dprince.plex.tv.metadata.MetaData;
import com.dprince.plex.tv.types.TvShow;

public class ParseNewlyDownloaded {

    public static void parseFolder(@NonNull final String folderPath) {
        System.out.println("FolderPath: " + folderPath);
        if (new File(folderPath).isDirectory()) {

            final List<TvShow> tvShowList = getTvShowFile(folderPath);
            if (tvShowList.size() == 1) {
                final TvShow tvShow = tvShowList.get(0);
                if (moveTvShow(tvShow, folderPath)) {
                    MetaData.editMetaData(tvShow.getDestinationFilepath(),
                            tvShow.getEpisodeTitle());
                }
            }
        } else {
            final TvShow metaDataEditTvShow = ParseFileName.parseFileName(folderPath, true, true);
            MetaData.editMetaData(metaDataEditTvShow.getOriginalFilepath(),
                    metaDataEditTvShow.getEpisodeTitle());
            CommonUtilities.renameFile(metaDataEditTvShow.getOriginalFilepath(),
                    metaDataEditTvShow.getDestinationFilepath());
        }
    }

    private static List<TvShow> getTvShowFile(@NonNull final String folderPath) {
        final File folder = new File(folderPath);
        final List<TvShow> tvShowList = new ArrayList<TvShow>();

        for (final File file : folder.listFiles()) {
            if (CommonUtilities.getExtension(file.getName()).matches(VIDEO_FILES)
                    && !file.getName().toLowerCase().matches(FILES_TO_IGNORE)) {

                final TvShow tvShow = ParseFileName.parseFileName(file.toString(), true, true);
                if (tvShow != null) {
                    tvShowList.add(tvShow);
                }
            }
        }
        return tvShowList;
    }

    private static boolean moveTvShow(@NonNull final TvShow tvShow,
            @NonNull final String folderPath) {
        final String episodeExists = Downloads.episodeExists(tvShow.getDestinationFilepath(),
                tvShow.getSeasonNumber(), tvShow.getEpisodeNumber());

        if (episodeExists != null) {
            System.out.println("\n************ Episode Exists Deleting folder *************");
            final File folderFile = new File(folderPath);

            while (folderFile.exists()) {
                try {
                    deleteFolder(folderPath);
                } catch (final Exception e) {
                    System.out.println(e.getMessage());
                }
            }

            System.exit(0);
        }

        while (!Downloads.seasonFolderExists(tvShow.getDestinationFilepath())) {
            ShowFolderUtilities.createNewSeasonFolder(tvShow);
        }

        if (CommonUtilities.renameFile(tvShow.getOriginalFilepath(),
                tvShow.getDestinationFilepath())) {
            return deleteFolder(folderPath);
        } else {
            System.out.println("Retrying move file.");
            try {
                Thread.sleep(10000);
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
            if (CommonUtilities.renameFile(tvShow.getOriginalFilepath(),
                    tvShow.getDestinationFilepath())) {
                return deleteFolder(folderPath);
            }
        }
        System.out.println("File not moved.");
        return false;
    }

    private static boolean deleteFolder(final String folder) {
        final File showFolder = new File(folder);

        for (final File file : showFolder.listFiles()) {
            if (file.isDirectory()) {
                for (final File file2 : file.listFiles()) {
                    CommonUtilities.recycle(file2.toString());
                }
            }
            CommonUtilities.recycle(file.toString());
        }
        CommonUtilities.recycle(showFolder.toString());
        return !showFolder.exists();
    }
}
