/*
 * The MIT License
 *
 * Copyright 2017 Marcel Stefko, LEB EPFL.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package ch.epfl.leb.injector;

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
        }
        
    }
}