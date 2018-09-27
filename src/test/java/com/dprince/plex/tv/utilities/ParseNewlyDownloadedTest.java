package com.dprince.plex.tv.utilities;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

import com.dprince.plex.tv.types.TvShow;

public class ParseNewlyDownloadedTest {

    @Test
    public void getSubtitleFiles_test() {
        final File folder = new File("//Desktop-downloa/TVShowRenamer/SubtitlesTest");
        final List<File> subtitleFiles = ParseNewlyDownloaded.getSubtitleFiles(folder);

        assertThat(subtitleFiles).containsExactly(new File(
                "\\\\Desktop-downloa\\TVShowRenamer\\SubtitlesTest\\Subs\\SubtitlesChild.srt"),
                new File("\\\\Desktop-downloa\\TVShowRenamer\\SubtitlesTest\\SubtitlesRoot.srt"));
    }

    @Test
    public void renameFileWithSubExtensionAndNumber_test() throws Exception {
        final String destinationFilepath = "\\\\This\\is\\the filename.mkv";
        final Integer versionNumber = 2;
        String replaceExtensionWithSubExtension = ParseNewlyDownloaded
                .renameFileWithSubExtensionAndNumber(destinationFilepath, versionNumber);
        assertThat(replaceExtensionWithSubExtension)
                .isEqualTo("\\\\This\\is\\the filename.en.srt");

        replaceExtensionWithSubExtension = ParseNewlyDownloaded
                .renameFileWithSubExtensionAndNumber(destinationFilepath, null);
        assertThat(replaceExtensionWithSubExtension)
                .isEqualTo("\\\\This\\is\\the filename.eng.srt");
    }

    @Test
    public void getTvShowFile_test() throws Exception {
        final String testFolderPath = "\\\\Desktop-downloa\\TVShowRenamer\\Mayans.M.C.S01E04.WEBRip.x264-ION10";
        final List<TvShow> tvShowList = ParseNewlyDownloaded.getTvShowFile(testFolderPath);

        final List<File> subtitlesFilepaths = tvShowList.get(0).getSubtitlesFilepaths();
        for (final File file : subtitlesFilepaths) {
            System.out.println(file.getPath());
        }
        assertThat(subtitlesFilepaths.size()).isEqualTo(2);
    }

}
