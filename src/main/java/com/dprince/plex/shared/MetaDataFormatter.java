package com.dprince.plex.shared;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.List;

import org.slf4j.Logger;

import com.coremedia.iso.IsoFile;
import com.coremedia.iso.boxes.Box;
import com.coremedia.iso.boxes.ChunkOffsetBox;
import com.coremedia.iso.boxes.Container;
import com.coremedia.iso.boxes.FreeBox;
import com.coremedia.iso.boxes.HandlerBox;
import com.coremedia.iso.boxes.MetaBox;
import com.coremedia.iso.boxes.MovieBox;
import com.coremedia.iso.boxes.UserDataBox;
import com.coremedia.iso.boxes.apple.AppleItemListBox;
import com.dprince.logger.Logging;
import com.googlecode.mp4parser.boxes.apple.AppleCommentBox;
import com.googlecode.mp4parser.boxes.apple.AppleNameBox;
import com.googlecode.mp4parser.util.Path;

/**
 * @author Darren
 */
public class MetaDataFormatter {

    private static final Logger LOG = Logging.getLogger(MetaDataFormatter.class);

    public static void main(String[] args) throws IOException {
        final String filePath = args[0];

        writeRandomMetadata(filePath, "");
    }

    public static void writeRandomMetadata(String videoFilePath, String title) throws IOException {
        LOG.info("Writing metaData to file ({})", title);
        final File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            throw new FileNotFoundException("File " + videoFilePath + " not exists");
        }

        if (!videoFile.canWrite()) {
            throw new IllegalStateException("No write permissions to file " + videoFilePath);
        }

        final IsoFile isoFile = new IsoFile(videoFilePath);

        MovieBox moov = null;
        try {
            final List<MovieBox> boxes = isoFile.getBoxes(MovieBox.class);
            moov = boxes.get(0);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        FreeBox freeBox = findFreeBox(moov);

        final boolean correctOffset = needsOffsetCorrection(isoFile);

        final long sizeBefore = moov.getSize();
        long offset = 0;
        for (final Box box : isoFile.getBoxes()) {
            if ("moov".equals(box.getType())) {
                break;
            }
            offset += box.getSize();
        }

        // Create structure or just navigate to Apple List Box.
        UserDataBox userDataBox;
        if ((userDataBox = (UserDataBox) Path.getPath(moov, "udta")) == null) {
            userDataBox = new UserDataBox();
            moov.addBox(userDataBox);
        }
        MetaBox metaBox;
        if ((metaBox = (MetaBox) Path.getPath(userDataBox, "meta")) == null) {
            metaBox = new MetaBox();
            final HandlerBox hdlr = new HandlerBox();
            hdlr.setHandlerType("mdir");
            metaBox.addBox(hdlr);
            userDataBox.addBox(metaBox);
        }
        AppleItemListBox ilst;
        if ((ilst = (AppleItemListBox) Path.getPath(metaBox, "ilst")) == null) {
            ilst = new AppleItemListBox();
            metaBox.addBox(ilst);

        }
        if (freeBox == null) {
            freeBox = new FreeBox(128 * 1024);
            metaBox.addBox(freeBox);
        }

        /**************************************/
        /* This sets the title box */
        /*************************************/
        setTitleMetaData(title, ilst);

        /**************************************/
        /* This sets the comment box */
        /*************************************/
        setCommentMetaData(title, ilst);

        long sizeAfter = moov.getSize();
        long diff = sizeAfter - sizeBefore;
        // This is the difference of before/after

        // can we compensate by resizing a Free Box we have found?
        if (freeBox.getData().limit() > diff) {
            // either shrink or grow!
            freeBox.setData(ByteBuffer.allocate((int) (freeBox.getData().limit() - diff)));
            sizeAfter = moov.getSize();
            diff = sizeAfter - sizeBefore;
        }
        if (correctOffset && diff != 0) {
            System.out.println("Commented out section");
            correctChunkOffsets(moov, diff);
        }
        final BetterByteArrayOutputStream baos = new BetterByteArrayOutputStream();
        moov.getBox(Channels.newChannel(baos));
        isoFile.close();
        FileChannel fc;
        if (diff != 0) {
            // this is not good: We have to insert bytes in the middle of the
            // file
            // and this costs time as it requires re-writing most of the file's
            // data
            fc = splitFileAndInsert(videoFile, offset, sizeAfter - sizeBefore);
        } else {
            // simple overwrite of something with the file
            fc = new RandomAccessFile(videoFile, "rw").getChannel();
        }
        fc.position(offset);
        fc.write(ByteBuffer.wrap(baos.getBuffer(), 0, baos.size()));
        fc.close();
        baos.close();
    }

    public static String getTitleFromMetaData(String videoFilePath) throws IOException {
        final File videoFile = new File(videoFilePath);
        if (!videoFile.exists()) {
            throw new FileNotFoundException("File " + videoFilePath + " not exists");
        }

        if (!videoFile.canWrite()) {
            throw new IllegalStateException("No write permissions to file " + videoFilePath);
        }

        final IsoFile isoFile = new IsoFile(videoFilePath);

        MovieBox moov = null;
        try {
            final List<MovieBox> boxes = isoFile.getBoxes(MovieBox.class);
            moov = boxes.get(0);
        } catch (final Exception e) {
            e.printStackTrace();
        }

        FreeBox freeBox = findFreeBox(moov);

        final boolean correctOffset = needsOffsetCorrection(isoFile);

        for (final Box box : isoFile.getBoxes()) {
            if ("moov".equals(box.getType())) {
                break;
            }
        }

        // Create structure or just navigate to Apple List Box.
        UserDataBox userDataBox;
        if ((userDataBox = (UserDataBox) Path.getPath(moov, "udta")) == null) {
            userDataBox = new UserDataBox();
            moov.addBox(userDataBox);
        }
        MetaBox metaBox;
        if ((metaBox = (MetaBox) Path.getPath(userDataBox, "meta")) == null) {
            metaBox = new MetaBox();
            final HandlerBox hdlr = new HandlerBox();
            hdlr.setHandlerType("mdir");
            metaBox.addBox(hdlr);
            userDataBox.addBox(metaBox);
        }
        AppleItemListBox ilst;
        if ((ilst = (AppleItemListBox) Path.getPath(metaBox, "ilst")) == null) {
            ilst = new AppleItemListBox();
            metaBox.addBox(ilst);

        }
        if (freeBox == null) {
            freeBox = new FreeBox(128 * 1024);
            metaBox.addBox(freeBox);
        }

        return getTitleMetaData(ilst);
    }

    private static void setCommentMetaData(String title, AppleItemListBox ilst) {
        AppleCommentBox cmt;
        if ((cmt = (AppleCommentBox) Path.getPath(ilst, "©cmt")) == null) {
            cmt = new AppleCommentBox();
            cmt.setDataCountry(0);
            cmt.setDataLanguage(0);
            cmt.setValue(title);
            ilst.addBox(cmt);
            // System.out.println("created new cmt box");
        } else {
            cmt.setValue(title);
            // System.out.println("set value of cmt box");
        }
    }

    public static String getTitleMetaData(AppleItemListBox ilst) {
        AppleNameBox nam;
        String value = null;
        if ((nam = (AppleNameBox) Path.getPath(ilst, "©nam")) == null) {
            nam = new AppleNameBox();
            nam.setDataCountry(0);
            nam.setDataLanguage(0);
            value = nam.getValue();
        } else {
            value = nam.getValue();
        }

        return value;
    }

    public static String setTitleMetaData(String title, AppleItemListBox ilst) {
        AppleNameBox nam;
        if ((nam = (AppleNameBox) Path.getPath(ilst, "©nam")) == null) {
            nam = new AppleNameBox();
            nam.setDataCountry(0);
            nam.setDataLanguage(0);
            nam.setValue(title);
            ilst.addBox(nam);
            // System.out.println("created new nam box");
        } else {
            nam.setValue(title);
            // System.out.println("set value of nam box");
        }

        return nam.getValue();
    }

    static FreeBox findFreeBox(Container c) {
        for (final Box box : c.getBoxes()) {
            if (box instanceof FreeBox) {
                return (FreeBox) box;
            }
            if (box instanceof Container) {
                final FreeBox freeBox = findFreeBox((Container) box);
                if (freeBox != null) {
                    return freeBox;
                }
            }
        }
        return null;
    }

    private static void correctChunkOffsets(MovieBox movieBox, long correction) {
        List<Box> chunkOffsetBoxes = Path.getPaths((Box) movieBox,
                "trak/mdia[0]/minf[0]/stbl[0]/stco[0]");
        if (chunkOffsetBoxes.size() == 0) {
            chunkOffsetBoxes = Path.getPaths((Box) movieBox,
                    "trak/mdia[0]/minf[0]/stbl[0]/st64[0]");
        }
        for (final Box Box : chunkOffsetBoxes) {
            final long[] cOffsets = ((ChunkOffsetBox) Box).getChunkOffsets();
            for (int i = 0; i < cOffsets.length; i++) {
                cOffsets[i] += correction;
            }
        }
    }

    private static class BetterByteArrayOutputStream extends ByteArrayOutputStream {
        byte[] getBuffer() {
            return buf;
        }
    }

    @SuppressWarnings("resource")
    public static FileChannel splitFileAndInsert(File f, long pos, long length) throws IOException {
        final FileChannel read = new RandomAccessFile(f, "r").getChannel();
        final File tmp = File.createTempFile("ChangeMetaData", "splitFileAndInsert");
        final FileChannel tmpWrite = new RandomAccessFile(tmp, "rw").getChannel();
        read.position(pos);
        tmpWrite.transferFrom(read, 0, read.size() - pos);
        read.close();
        final FileChannel write = new RandomAccessFile(f, "rw").getChannel();
        write.position(pos + length);
        tmpWrite.position(0);
        long transferred = 0;
        while ((transferred += tmpWrite.transferTo(0, tmpWrite.size() - transferred,
                write)) != tmpWrite.size()) {
            System.out.println("Transferred: " + transferred);
        }
        System.out.println("Transferred: " + transferred);
        tmpWrite.close();
        tmp.delete();
        return write;
    }

    private static boolean needsOffsetCorrection(IsoFile isoFile) {
        if (Path.getPath(isoFile, "moov[0]/mvex[0]") != null) {
            // Fragmented files don't need a correction
            return false;
        } else {
            // no correction needed if mdat is before moov as insert into moov
            // want change the offsets of mdat
            for (final Box box : isoFile.getBoxes()) {
                if ("moov".equals(box.getType())) {
                    return true;
                }
                if ("mdat".equals(box.getType())) {
                    return false;
                }
            }
            try {
                isoFile.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }

            throw new RuntimeException(
                    "I need moov or mdat. Otherwise all this doesn't make sense");
        }
    }
}
