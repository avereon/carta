# Mouse Event Action Triggers

It has been challenging to find a way to coordinate command triggers based on mouse events. This is due to the fact that triggers are not directly related to commands already on the command stack and that mouse events can trigger commands at any time. This behavior is somewhat desired, but it can be difficult to coordinate the behavior of the mouse with the behavior of the commands.

## Example Scenarios

1. Select a single object
2. Toggle select a single object
3. Window select multiple objects
4. Window intersect select multiple objects
5. Move design viewpoint
6. Zoom window
7. Copy

## Brainstorming

Some of the scenarios are pretty simple on the surface but can have complex interactions with other commands. For example, selecting a single object can be triggered on the MOUSE RELEASED event, but this will cause it to be triggered on any MOUSE RELEASE event, which, when following a window select command, can cause all the selected objects to be unselected. This can partly be solved by determining if the mouse has moved since the MOUSE PRESSED event, but this does not solve all side effects.

The closest solution we have come up with is to separate the commands linked to MOUSE PRESSED and MOUSE RELEASED events. This way, the MOUSE PRESSED event can simply set an anchor point for use by other commands, and the MOUSE RELEASED event can trigger the select command. However, this got complicated when we introduced the window zoom command which is started before the mouse events are fired.

So the challenge is to make commands that are triggered before the mouse events, and commands that are triggered because of the mouse events, work together in a meaningful way to the user. So, what is the "meaningful way" that we are trying to achieve? Here are the expectations for each scenario:

### Select a single object

- No commands have been started and the command stack is empty.
- The user clicks on an object and it is selected.
- Other commands can be started after this command that can utilize the selected object.
- Clicking on another object will deselect the first object and select the second object.
- Clicking on empty space will deselect all objects.

### Toggle select a single object

- No commands have been started and the command stack is empty.
- The user clicks on an object, holding the CTRL key, and its selected state is toggled.
- Other commands can be started after this command that can utilize the selected object.
- Clicking on another object will toggle the selected state of the second object.
- Clicking on empty space will deselect all objects.

### Window select multiple objects

- No commands have been started and the command stack is empty.
- The user clicks and drags to create a window.
- All objects that are completely inside the window are selected.
- Other commands can be started after this command that can utilize the selected objects.
- Window selecting again will deselect all objects and start a new window.
- Clicking on empty space will deselect all objects.

### Window intersect select multiple objects

- No commands have been started and the command stack is empty.
- The user clicks and drags to create a window, holding the SHIFT key.
- All objects that intersect the window are selected.
- Other commands can be started after this command that can utilize the selected objects.
- Window selecting again will deselect all objects and start a new window.
- Clicking on empty space will deselect all objects.

### Move design viewpoint

- Commands may, or may not, have been started. This command is not dependent on the state of the command stack and should work the same regardless of  prior commands.
- The user clicks and drags, holding the CTRL key, to move the design viewpoint.
- The design viewpoint moves with the mouse to show the user the movement.
- Releasing the mouse will stop the movement.
- Existing commands can be continued after this command.

### Zoom window
- This command is not started by mouse events, but other user action.
- Commands may, or may not, have been started. This command is not dependent on the state of the command stack and should work the same regardless of  prior commands.
- The user starts the Zoom Window command by typing "ZW" and pressing ENTER, or by clicking the Zoom Window action button.
- The user clicks and drags to create a window.
- The design view is zoomed to the window.
- Existing commands can be continued after this command.

### Copy
- This command is not started by mouse events, but other user action.
- Commands may, or may not, have been started. This command is not dependent on the state of the command stack and should work the same regardless of  prior commands.
- Optional: The user may, or may not have selected objects to copy, before starting the copy command.
- The user starts the Copy command by typing "CP" and pressing ENTER, or by clicking the Copy action button.
- Optional: If no objects are selected, the user is prompted to select objects. 
- Optional: The user selects one or multiple objects. This may also include toggling object selection.
- Optional: Not sure how the user would identify that they are done selecting objects. Maybe the user presses ENTER to indicate they are done selecting objects.
- The user clicks on a point to indicate the anchor of the copy.
- A copy of the objects follows the mouse to show the user the copy.
- The user clicks on a point to indicate the destination of the copy.
- The objects are copied to the destination.
- Existing commands can be continued after this command.

Back to brainstorming, note that scenarios that depend on mouse events to trigger the command are limited and have some common attributes. Scenarios where the user starts a command, and then uses the mouse to interact with the command, are not limited and much more common. 

So, let's tackle the problem from the common scenarios first. How should the mouse interact with exiting commands. I think that two general solutions are possible: Use mouse events to trigger commands that help the existing command, or have the existing command consume the mouse events so that further commands are not triggered. The second solution will probably not work since consuming the mouse events, and preventing further commands, breaks the desire to run other commands like moving the viewpoint, snapping to objects, etc. So, the first solution seems to be the only viable solution.

Since commands will not consume the mouse events, we need to find a way to trigger commands that help the existing command. This could be done by having special commands that are triggered by mouse events, but require the command stack not be empty. It may also be possible for the select commands to behave differently depending on the state of the command stack. For example, if the command stack is empty, the select command will select a single object, but if the command stack is not empty, the select command will simply return a point.

The copy command with "select after start" would really complicate the matter, since the command would be in different "modes" when using the mouse events. Maybe that is a reasonable solution, to have the "select" commands operate in to modes, "select" and "point". The "select" mode would be used when the command stack is empty, or the current command requests it, and the "point" mode would be used otherwise.

## Possible Solutions

1. Special Mouse Event Commands
2. Special Mouse Event Handling in Select Commands
3. Special Mouse Event Handling in Command Stack
4. Special Mouse Event Handling with empty command stack

