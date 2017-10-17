# ImageInjector

ProcessorPlugin for MicroManager 2.0 which replaces images in the
processing pipeline with ones from a user-selected .tiff image stack.
This can be useful for debugging or performing demos.

Developed by Marcel Stefko at Laboratory of Experimental Biophysics
EPFL, under supervision of Kyle M. Douglass, PhD. The original
repository may be found at
[https://github.com/MStefko/ImageInjectorPlugin](https://github.com/MStefko/ImageInjectorPlugin).



## Installation

Copy `ImageInjector.jar` from one of the
[releases](https://github.com/MStefko/ImageInjectorPlugin/releases)
into the `Micro-Manager2.0/mmplugins` folder.

## Usage

ImageInjector uses the MicroManager2.0
[ProcessorPlugin](https://micro-manager.org/wiki/Version_2.0_Plugins#ProcessorPlugin)
interface.  Just select a `.tif` file you wish to inject and press
`OK` button to load. Make sure to reopen the MicroManager Live view
after the `.tif` file is loaded.
