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
import org.micromanager.data.ProcessorConfigurator;

/**
 * 
 * Opens up a GUI, modifies the ImageStreamer singleton according to settings, 
 * and is used to set up the PropertyMap for InjectorFactory.
 *
 * @author Marcel Stefko
 */
public class InjectorConfigurator implements ProcessorConfigurator {
    public InjectorContext context;
    private PropertyMap property_map;
    private InjectorSetupWindow frame;
    
    public InjectorConfigurator(PropertyMap pm, InjectorContext context) {
        this.context = context;
        PropertyMap.PropertyMapBuilder builder = pm.copy();
        builder.putInt("framesPerSecond", 15);
        setPropertyMap(builder.build());
        frame = new InjectorSetupWindow(new javax.swing.JFrame(), false, this);
    }
    
    public final void setPropertyMap(PropertyMap pm) {
        property_map = pm;
    }
    
    @Override
    public void showGUI() {
        frame.setVisible(true);
    }

    @Override
    public void cleanup() {
         
    }

    @Override
    public PropertyMap getSettings() {
        return property_map;
    }
}
