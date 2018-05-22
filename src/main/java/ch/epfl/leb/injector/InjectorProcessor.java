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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.micromanager.PropertyMap;
import org.micromanager.data.Image;
import org.micromanager.data.Processor;
import org.micromanager.data.ProcessorContext;

/**
 * Replaces images from camera in the pipeline with images from ImageStreamer.
 *
 * @author Marcel Stefko
 */
public class InjectorProcessor extends Processor {
    private final InjectorContext context;
    private final PropertyMap property_map;
    private final ImageStreamer image_streamer;
    
    int counter = 0;
    private Image current_image;
    private long last_time;
    
    public InjectorProcessor(PropertyMap pm, InjectorContext context) {
        super();
        this.context = context;
        property_map = pm;
        image_streamer = context.streamer;
        // Used for real FPS calculation
        last_time = System.currentTimeMillis();
}

    @Override
    public void processImage(Image image, ProcessorContext pc) {
        /**
         * Recieves image from pipeline, asks for an image from ImageStreamer,
         * and puts this new image into the pipeline. The original image is
         * discarded.
         */
        counter++;
        // Output real FPS information into log
        if (counter % 100 == 0) {
            long time = System.currentTimeMillis() - last_time;
            double FPS = 100000.0 / (double) time;
            context.app.logs().logDebugMessage(String.format("100 images processed in %d milliseconds. (%4.2f FPS)", time, FPS));
            last_time = System.currentTimeMillis();
        }
        try {
            // Get image from streamer
            current_image = image_streamer.getNextImage();
            // If null, put in original image
            if (current_image == null) {
                pc.outputImage(image);
                return;
            // Otherwise, replace the image
            } else {
                pc.outputImage(current_image);
                return;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(InjectorProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(InjectorProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
}