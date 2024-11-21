/*
 * Copyright (C) 2017 Laboratory of Experimental Biophysics
 * Ecole Polytechnique Federale de Lausanne
 *
 * Author: Marcel Stefko
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ch.epfl.leb.injector;

import ij.ImagePlus;
import ij.ImageStack;
import ij.io.Opener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.micromanager.Studio;
import org.micromanager.data.Coords;
import org.micromanager.data.Datastore;
import org.micromanager.data.DatastoreFrozenException;
import org.micromanager.data.DatastoreRewriteException;
import org.micromanager.data.Image;
import org.micromanager.data.Metadata;

/**
 *
 * @author stefko
 */
public class TiffParser {
    private BusyIndicatorWindow window = new BusyIndicatorWindow(null, true);
    private final InjectorSetupWindow setup_window;
    private final Studio app;
    private File tiff_file;
    private Datastore store;
    private SortedCoordsList coords_list;
    private long frameLengthMs;
    private Coords.CoordsBuilder c_builder;
    private Metadata.MetadataBuilder m_builder;
    
    public TiffParser(Studio studio, long frameLengthMs, InjectorSetupWindow setup_window) {
        this.frameLengthMs = frameLengthMs;
        app = studio;
        this.setup_window = setup_window;
    }
    
    public final void loadGeneralTiff(File file) throws FileNotFoundException, IOException {
        // Set up progressbar
        window.setProgress(0);
        SwingUtilities.invokeLater( new Runnable() {
            @Override
            public void run() {
                window.setVisible(true);
            }
        });
        // Open the tiff via ImageJ
        app.logs().logMessage("Trying to open general tiff.");
        InputStream input_stream = new FileInputStream(file);
        ImagePlus win;
        try {
            Opener o = new Opener();
            win = o.openTiff(input_stream,"InjectorStack");
        } finally {
            input_stream.close();
        }

        ImageStack stack = win.getImageStack();
        // Build up metadata from scratch
        m_builder = app.data().getMetadataBuilder();
        m_builder.imageNumber(0l).camera("ImageInjector").exposureMs(1.0d)
                .xPositionUm(0.0).yPositionUm(0.0).zPositionUm(0.0)
                .positionName("Injection");
        
        // 
        int width = stack.getWidth();
        int height = stack.getHeight();
        int bytesPerPixel;
        if(stack.getPixels(win.getSlice()) instanceof short[]) {
            bytesPerPixel = 2;
        } else {
            throw new ArrayStoreException("Wrong image bit depth.");
        }
        
        
        window.setMaximum(stack.getSize());
        Image ram_image;
        Coords coords; Metadata metadata;
        for (int i=1; i<=stack.getSize(); i++) {
            // get immutable types from builders
            coords = c_builder.build();
            metadata = m_builder.build();
            
            ram_image = app.data().createImage(stack.getPixels(i), width, height, bytesPerPixel, 1, coords, metadata);
            // put the image into new datastore
            try {
                store.putImage(ram_image);
            } catch (DatastoreFrozenException ex) {
                Logger.getLogger(ImageStreamer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (DatastoreRewriteException ex) {
                Logger.getLogger(ImageStreamer.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(ImageStreamer.class.getName()).log(Level.SEVERE, null, ex);
            }
            // put the relevant coords into an ordered array
            coords_list.add(coords);
            
            // Prepare builders for next image by incrementing values
            c_builder.offset("time", (int) frameLengthMs);
            m_builder.imageNumber((long) i).elapsedTimeMs((double) i*frameLengthMs);
            // Log the process
            if (i%100==0) {
                app.logs().logDebugMessage(String.format("Loaded %d images from general tiff stack.",i));
            }
            // Increment progressbar
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    window.increment();
                }
            });
        }
        if (setup_window != null) {
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    setup_window.setTiffLoaded(true);
                }
            });
        }
    }
    
    
    public final void loadScrubbedData(File file) {
        /**
         * Loads data from a .tiff file from disk, cleans  the Coords and
         * Metadata information, and loads it up into a RAM datastore.
         */
        
        app.logs().logDebugMessage(String.format(
            "Parsing .tiff file: %s", file.getAbsolutePath()));
        
        // Containers for cleaned up final data
        store = app.data().createRAMDatastore();
        coords_list = new SortedCoordsList();
        
        // Get a coords builder, but scrap all coords info.
        c_builder = app.data().coordsBuilder(); // getCoordsBuilder() has been deprecated
        c_builder.channel(0).stagePosition(0).time(0).z(0);
        
        // Get metadata from .tiff file, but scrub the position and other information
        // which doesn't pertain to actual image.
        
        
        Datastore disk_store = null;
        try {
            loadGeneralTiff(file);
        } catch (Exception ex2) {
            Logger.getLogger(ImageStreamer.class.getName()).log(Level.SEVERE, null, ex2);
            app.logs().showError("Loading of .tiff file failed.");
            store = null;
            coords_list = null;
            SwingUtilities.invokeLater( new Runnable() {
                public void run() {
                    window.dispose();
                    setup_window.setTiffLoaded(false);
                }
            });

            return;
        }
        tiff_file = file;

        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                window.dispose();                
            }
        });
    }
    
    public Datastore getDatastore() {
        return store;
    } 
    
    public SortedCoordsList getCoordsList() {
        return coords_list;
    }
}
