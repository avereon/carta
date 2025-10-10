# Using Select with Nested Commands

Cartesia is built such that commands can be nested within each other. This allows for a more complex and powerful set of commands to be executed. This is done by allowing many commands to be executed while other commands are in progress.

For example, to draw a line the "draw line" command is started and the command prompts for the first point. While the "draw line" command is in progress, the "select" command can be started to select the point, and pass that back to the line command. The "draw line" command will then continue and prompt for the next point. The "select" command can be started again to select the next point, and the line command will add the line between the two points.

In this case the "select" command is run by pressing the primary mouse key, pressing the Enter key, or by typing the "PD" command.

## Select Commands

There are two general "select" command, "select by point" and "select by window". Used with no other commands started, these commands will select design geometry for use by other commands. When used with other commands, the select can would generally be used to return points to the running command. 

However, this could get more complex when the running command want to select geometry instead of get points. It has been proposed not to allow this behavior. It might be possible to have a flag on the running command to indicate that it wants to select geometry instead of points.

Select with window also has two general patterns, using drag and drop and using two points. Many users will already be familiar with creating a window using drag and drop to select multiple items. This is the most common way to select multiple items. However, using two points to create a window is also common in CAD software. This is done by clicking on the first point, and then clicking on the second point to create a window.

## Scenarios

Here are the known scenarios:

- Point Select with no other commands running - Select single geometry item
- Window Select with no other commands running - Select multiple geometry items
- Point Select with a command running - Return point to running command
- Window Select with a command running - Return two points to running command
- Manually start "select by point" command - Special scenario
- Manually start "select by window" command - Special scenario

Special scenario:

There is an interesting special scenario that needs to be considered. The situation is when the user manually starts the "select by window" command. Normally "select by window" is started from a mouse action, like click and drag. In this case the command was started by the user typing "WS" to start the command. In this case, if we follow the pattern above, a second "select by window" command could be run to define the window, pass the points back to the first command, and the first command would select the geometry. Incidentally, the same situation can happen with the "select by point" command. 

However, I think there might be a check not to allow two commands of the same type in a row. One way to solve this is to have the manual window select run a different command so that the mouse initiated select doesn't collide with the manually started one. -- Turns out there is a Prompt command started which will separate the two select commands.





