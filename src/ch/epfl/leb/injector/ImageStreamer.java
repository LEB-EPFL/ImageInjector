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

import java.io.File;
import org.micromanager.Studio;
import org.micromanager.data.Datastore;
import org.micromanager.data.Image;

/**
 * 
 * Handles opening and parsing of images and providing them on demand to
 * the InjectorProcessor.
 *
 * @author Marcel Stefko
 */
public class ImageStreamer {
    private final Studio app;
    private File tiff_file;
    private Datastore store;
    private SortedCoordsList coords_list;
    private int img_count = 0;
    private long lastFrameTimeMs = 0l;
    private long frameLengthMs = 100;
    private long now;
    
    public ImageStreamer(Studio studio) {
        // Initiate with no file and default FPS
        app = studio;
        setFile(null, null);
        setFPS(100);
    }
    
    public final void setFile(final File file, InjectorSetupWindow setup_window) {
        // The file pointers can be null.
        String new_path = ""; String old_path = "";
        try {
            new_path = file.getAbsolutePath();
        } catch (NullPointerException ex) {}
        try {
            old_path = tiff_file.getAbsolutePath();
        } catch (NullPointerException ex) {}
        
        // Clean up memory if no file inserted
        if (new_path.equalsIgnoreCase("")) {
            tiff_file = null;
            store = null;
            coords_list = null;
            System.gc();
            return;
        // Do nothing if the file is the same
        } else if (old_path.equals(new_path)) {
            return;
        // Clean up memory and load up new image otherwise
        } else {
            tiff_file = null;
            store = null;
            coords_list = null;
            System.gc();
            
            final TiffParser parser = new TiffParser(app, frameLengthMs, setup_window);
            new Thread(new Runnable(){
                @Override
                public void run() {
                    parser.loadScrubbedData(file);
                    store = parser.getDatastore();
                    coords_list = parser.getCoordsList();
                    tiff_file = file;
                }
            }).start();
        }
    }
    

    
    public final long getFPS() {
        // This rounds down to the nearest integer
        return 1000 / frameLengthMs;
    }
    
    public final void setFPS(long framesPerSecond) {
        // Again, rounds down to nearest integer
        frameLengthMs = 1000 / framesPerSecond;
        // Log the value so we can see the leftover.
        app.logs().logMessage(String.format(
            "Set ImageFactory frameLengthMs to %d from %d FPS. (%d leftover)", 
            frameLengthMs, framesPerSecond, 1000 % framesPerSecond));
    }
    
    public int getNoOfImages() {
        return store.getNumImages();
    }
    
    public Image getNextImage() throws InterruptedException {
        /**
         * Continuously returns references to images from store,
         * (when it comes to the end, it starts over).
         * 
         * This function does not return copies, only references!
         */
        if (store == null) { return null; }
        // Get time for FPS limiter
        now = System.currentTimeMillis();
        // If not enough time has elapsed since last return, sleep for the
        // duration missing.
        long diff = frameLengthMs - (now - lastFrameTimeMs);
        if (diff > 0) {
            Thread.sleep(diff);
        }
        // Increment (and rotate) the counter
        img_count++;
        if (img_count >= coords_list.size()) {
            img_count = 0;
        }
        // Get timestamp for FPS limiter
        lastFrameTimeMs = System.currentTimeMillis();
        // Return image from store
        try {
            return store.getImage(coords_list.get(img_count));
        } catch (Exception e){
            System.out.println("Image IO error");
            throw new InterruptedException();
        }
    }
}