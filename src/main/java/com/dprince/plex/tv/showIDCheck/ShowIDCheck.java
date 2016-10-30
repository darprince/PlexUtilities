package com.dprince.plex.tv.showIDCheck;

import static com.dprince.plex.settings.PlexSettings.DESKTOP_SHARED_DIRECTORIES;
import static com.dprince.plex.settings.PlexSettings.PLEX_PREFIX;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.lang3.text.WordUtils;

import com.dprince.plex.tv.utilities.TvUtilities;

public class ShowIDCheck {
    // http://thetvdb.com/banners/posters/81189-10.jpg

    public static void main(String[] args) {
        TestURLImage();
    }

    public static void getImage() {
        for (final String drive : DESKTOP_SHARED_DIRECTORIES) {
            final File driveLocation = new File(PLEX_PREFIX + drive);
            final File[] listOfShows = driveLocation.listFiles();

            for (final File showFolder : listOfShows) {
                final String showIDFromJson = TvUtilities.getShowIDFromJson(showFolder.getName());

            }
        }
    }

    public static void getPopUp(String showID) throws IOException {

        final URL url = new URL("http://thetvdb.com/banners/posters/81189-10.jpg");
        final BufferedImage bufferedImage = ImageIO.read(url);
        final JLabel label = new JLabel(new ImageIcon(bufferedImage));
        final JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.getContentPane().add(label);
        f.pack();
        f.setLocation(200, 200);
        f.setVisible(true);

        final Object result = JOptionPane.showInputDialog(f, "Add this show to Plex?",
                WordUtils.capitalize("Show Name"));

    }

    public static void TestURLImage() {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                        | UnsupportedLookAndFeelException ex) {
                }

                try {
                    final Dimension d = new Dimension();
                    d.setSize(100.0, 200.0);

                    final String path = "http://thetvdb.com/banners/posters/81189-10.jpg";
                    System.out.println("Get Image from " + path);
                    final URL url = new URL(path);
                    final BufferedImage image = ImageIO.read(url);
                    System.out.println("Load image into frame...");
                    final JLabel label = new JLabel(new ImageIcon(image));
                    label.setSize(d);
                    final JFrame f = new JFrame();
                    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    final Container contentPane = f.getContentPane();
                    contentPane.setSize(d);
                    contentPane.add(label);
                    f.pack();
                    final Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
                    f.setLocation(dim.width / 2 - f.getSize().width / 2,
                            dim.height / 2 - f.getSize().height / 2);
                    f.setVisible(true);

                    // final Object result = JOptionPane.showInputDialog(f, "Add
                    // this show to Plex?",
                    // WordUtils.capitalize("Show Name"));
                } catch (final Exception exp) {
                    exp.printStackTrace();
                }

            }

            private Object getSize() {
                // TODO Auto-generated method stub
                return null;
            }
        });
    }

}
