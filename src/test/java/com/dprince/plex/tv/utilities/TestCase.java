package com.dprince.plex.tv.utilities;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class TestCase {

    @Test
    @Ignore
    public void testName() throws Exception {
        final String originalFilepath = "C:/junbk/more/Bones.s01e06.dfsa.avi";
        ParseFileName.parseFileName(originalFilepath, true, true);
    }

}
