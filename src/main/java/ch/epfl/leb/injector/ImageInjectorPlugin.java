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

import org.micromanager.PropertyMap;
import org.micromanager.Studio;
import org.micromanager.data.ProcessorPlugin;
import org.micromanager.data.ProcessorConfigurator;
import org.micromanager.data.ProcessorFactory;
import org.scijava.plugin.SciJavaPlugin;
import org.scijava.plugin.Plugin;

/**
 * ProcessorPlugin for MicroManager API version 2.0,
 * which replaces images from the camera with a stream of images from a .tif
 * stack.
 *
 * @author Marcel Stefko
 */
@Plugin(type = ProcessorPlugin.class)
public class ImageInjectorPlugin implements 
                org.micromanager.data.ProcessorPlugin,
                org.scijava.plugin.SciJavaPlugin {

    public static final String menuName = "ImageInjector";
    private Studio app;
    private ImageStreamer streamer;
    private InjectorContext context;
    
    public ImageStreamer getImageStreamer() {
        return streamer;
    }
    
    @Override
    public ProcessorConfigurator createConfigurator(PropertyMap pm) {
        return new InjectorConfigurator(pm, context);
    }

    @Override
    public ProcessorFactory createFactory(PropertyMap pm) {
        return new InjectorFactory(pm, context);
    }

    @Override
    public void setContext(Studio studio) {
        app = studio;
        streamer = new ImageStreamer(app);
        context = new InjectorContext(app, this, streamer);
    }
    
    @Override
    public String getName() {
        return menuName;
    }

    @Override
    public String getHelpText() {
        return "Replaces images from camera in processing pipeline with " +
               "ones from chosen .tiff stack.";
    }

    @Override
    public String getVersion() {
        return "0.1";
    }

    @Override
    public String getCopyright() {
        return "MIT License, 2017 Marcel Stefko, LEB EPFL";
    }
}
