
# NBody

N-Body is a project to make a simple-to-use, two-dimensional Newtonian N-Body simulation with features like zoom, pan, track, select, remove, edit, and save. Users often experience frustration with online, free-to-use N-body simulations because many of these features are incomplete or lacking altogether. The goal of this project is to improve the user experience by providing features that are not just interactive but also enjoyable. The editor is still work-in-progress, but the simulator is fully functional. The simulation runs on Java 1.8 with JavaFX as the primary graphics library. 

## Getting Started

These instructions will tell you how to download and play around with the simulation or get a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

This project requires Java 1.8 to run. If you are only running the executable `jar`, you will only need JRE 1.8, but if you plan on making changes to the project, you will need JDK 1.8 and a Java IDE.

### Installing

If you don't plan on making changes, just download the executable `jar` located in the `bin/` folder. It will give you a menacing virus warning, but there's no viruses, I promise.
I would also recommend downloading the `samples/` folder, which contains many sample simulation files for you to open and play around with.

## How to use the program

To begin, double-click the executable `jar` you downloaded. At startup, you will see an otherwise blank window with a UI bar at the top. The buttons, text boxes, and check boxes in the UI bar control the simulation, while space underneath displays the objects.
#### UI Elements
All text fields in this project are instances of the `SettableNumberField` class. That is, they follow a certain behavior:
1. When clicked on, all text in the field will be selected by default.
2. To change the value, type in your new value and press `enter` or simply click away from the text field.
3. If you typed in the wrong value by mistake, press `esc` to cancel and replace the incorrect value with the original value. This only works while the focus is on the text field; once the value has been set, the change cannot be undone.
4. Only integer and decimal values can be successfully entered in these fields. If the field contains an illegal value (a value containing any spaces, characters other than numbers, `-`, or `.`, or is not in proper number format) when a change is confirmed by the user, the field will revert to its original value.

**Control Panel:** This is where you can change how your simulation is viewed/run while it is running. There are several components to this panel.

_Mass:_ This field determines the mass of a manually created object. The units here have no relation to true mass.

_Clear:_ This button clears all objects and trails from the scene. Be careful when clicking this button!

_Clear Trails:_ This button clears all trails but not objects from the scene. When trails are turned on, it is advised to periodically click this button, especially when many objects are in the screen. Displaying too many trails can severely decrease frame rate.

_Enable Trails:_ Pretty self-explanatory. When this box is checked, trails will appear; otherwise they will not. Unchecking this box will not remove existing trails, however. Only the _Clear Trails_ button can do that.

_Enable Collisions:_ When this box is checked, objects will collide. All collision is perfectly inelastic, and mass is summed.

_Clipping Mode:_ This is a setting for slightly more advanced users. It defines how intersecting objects are treated when when collisions are off; thus, it is disabled when collisions are enabled. The button toggles between two modes `Full` and `Soft`. In `Full` mode, intersecting objects continue to experience gravitational forces with the object they are intersecting with, while in `Soft` mode, the objects do not experience these forces. The user should remain on `Soft` mode for the majority of the time; `Full` mode is rarely used.

_Simulation Speed:_ This field determines the speed of the simulation. The initial default for this field is 1. The speed of the simulation will always reflect the number in the field multiplied by the initial default speed.

_Play/Pause Button:_ This plays/pauses the simulation. On startup, the simulation is paused by default.

_Back Button:_ This reverts the simulation back to its initial state. The initial state does not change while a file is loaded, and does not keep any changes the user makes directly in the simulation window. If not already paused, the simulation automatically pauses when this button is clicked.

_Step Frame:_ This button increments time by one frame. It is only practical when the simulation is paused. It is mostly for debugging purposes, but can be useful when detail is needed at a fixed simulation speed.

_Track Object_: This button switches the visual coordinate system to remain centered on a selected object. While the universal coordinate system does not change, this button allows the user to view the simulation from the "point of view" of one of the objects in the scene: It appears stationary while all other objects assume their positions relative to the location of the tracked object. When an object is being tracked, trails also draw relative to that object. To track an object, simply click the `Track Object` button when an object is selected (See _Object Selection_ under **Simulation View**).  You will see a purple selector reticle appear around the tracked object. You may select other objects while an object is being tracked; the purple reticle will remain until the button is clicked again for a different object. To cancel the tracking and return to universal coordinates, click the button while no object is selected.

_Open File:_ This is used to open `.nbd` simulation files. To open a file, click on the button, choose the desired file in the file explorer, and press `open`. This will pause and replace any already loaded simulation. Sample `.nbd` files are provided in the `samples/` folder in the main project directory. `.nbd` files store object data but not simulation settings like speed, collision enabling, or clipping type.

**Simulation View:** This is where you will see your simulation running. You can make simple changes to the simulation directly in the simulation view while it is running, but these changes will not be saved.

_Navigation:_ To pan the view, right click and drag. Scroll to zoom.

_Object Creation:_ To create an object, click somewhere in the display region and drag. You will see a black line segment appear between the point you first clicked on and the current location of your cursor. This line is the velocity vector of the object: the new object will travel in the direction of the line with a speed directly related to the length of the line.

_Object Deletion:_ To delete an object, click on it while holding `shift`. It is advised to pause the simulation while doing this, as it is very difficult to accurately click on an object as it is moving.

_Object Selection:_ To select an object, click on it while holding `ctrl`. It is advised to pause the simulation while doing this. You will see a green selector reticle appear around the object. Only one object can be selected at a time. To deselect an object, `ctrl + click` on any empty space.

**Notes for more technical users:**

_Units_
Time: Frame
Distance: JavaFX pixel
* The simulation is locked at 60 frames per second, with a default gravitational constant of 2. This value was chosen entirely for viewing convenience for the given units of distance and time. 
* Simulation speed does not change computation speed. Instead, it scales speed linearly and scales the gravitational constant by a factor of a square, fundamentally changing the simulation in a way which appears to increase the speed. Thus, **changing this value will cause inaccuracies in sensitive simulations such as** `triple gravity assist.nbd`**.** For these, it is advised to keep the value at its default, even if the simulation is frustratingly slow.
* Simulation scale and object mass are of type `double`, which has diminishing precision for increasing values, so setting masses ridiculously high or zooming out ridiculously far will cause glitching, even after clearing the simulation.
* Exercise caution when using the `Full` clipping type. Objects getting very close to each other produce unnaturally large forces, so turning clipping type to `Full` in more complex simulations may cause explosions.
* `.nbd` files hard-code object locations, masses, and velocities, so randomizing simulations is not possible when loading a file. To vary results in randomized simulations like galaxy collisions or planet formation, a little code-digging is required. User-end randomization has not yet been implemented.
* x-axis Sweep and Prune was used for broad-phase collision detection, while a simple brute-force `distance < r1 + r2` test was used for narrow-phase collision detection.

## Author

* **Colin Li**

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Inspiration given by Gravity Toy - [http://www.nowykurier.com/toys/gravity/gravity.html](http://www.nowykurier.com/toys/gravity/gravity.html)
* And NBody, a long-forgotten mobile three-dimensional N-Body simulator on iOS. RIP.
