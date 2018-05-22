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
import org.micromanager.data.Processor;
import org.micromanager.data.ProcessorFactory;

/**
 * 
 * Generates InjectorProcessors for every stream. However, all processors
 * draw images from the same ImageStreamer (a single .tiff file in first-come
 * first-serve fashion).
 *
 * @author Marcel Stefko
 */
public class InjectorFactory implements ProcessorFactory {
    private final InjectorContext context;
    private PropertyMap property_map;
    public InjectorFactory(PropertyMap pm, InjectorContext context) {
        super();
        property_map = pm;
        this.context = context;
    }
    
    @Override
    public Processor createProcessor() {
        return new InjectorProcessor(property_map, context);
    }
}