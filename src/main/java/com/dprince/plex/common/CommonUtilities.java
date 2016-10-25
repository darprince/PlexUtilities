package com.dprince.plex.common;

import java.io.File;

import org.slf4j.Logger;

import com.dprince.logger.Logging;

public class CommonUtilities {

    private static final Logger LOG = Logging.getLogger(CommonUtilities.class);

    /**
     * Moves file
     *
     * @param originalFilename
     *            original location of file with filename
     * @param newFileName
     *            destination location of file with filename
     * @return boolean for success
     */
    public static boolean renameFile(String originalFilename, String newFileName) {
        final File oldName = new File(originalFilename);
        final File newName = new File(newFileName);
        if (oldName.renameTo(newName)) {
            return true;
        } else {
            return false;
        }
    }
}
