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
/**
 * 
 * Encapsulates often-accessed singletons of this plugin so we don't have to 
 * pass them to all classes one-by-one.
 *
 * @author Marcel Stefko
 */
public class InjectorContext {
    public final Studio app;
    public final ImageInjectorPlugin plugin;
    public final ImageStreamer streamer;
    public InjectorContext(Studio app, 
            ImageInjectorPlugin plugin, ImageStreamer streamer) {
        this.app = app;
        this.plugin = plugin;
        this.streamer = streamer;
    }
}
