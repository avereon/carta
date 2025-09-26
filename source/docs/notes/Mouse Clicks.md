# Mouse Clicks

## Problem Statement

Mouse clicks are the primary input mechanism for the user in Cartesia. The user
is particularly sensitive to the mouse-down event and where the user perceives
this event occurs on the screen. Mouse clicks, the combination of a mouse-down
event and a mouse-up event, can be another useful input mechanism. However, they 
should be used with caution due to the delay in time and potential motion 
between the mouse-down and mouse-up events. This can lead to unintended actions 
or misinterpretation of user intent.

## Solution

In Cartesia there will be two mechanisms for handing the challenge of mouse
input. The first will be the ability to accurately configure commands to mouse 
actions. The second will be carefully configured default mouse actions to 
commands such that user intent is not misinterpreted. In particular, care will
be taken to use the mouse-down event to capture important information from the 
user and to minimize the impact of mouse up events on user experience. 

Furthermore, it would be appropriate if all mouse actions could be translated 
into commands. This would allow for a consistent experience between interactive
and non-interactive operations. For example, the mouse-down event could be 
translated into a "pen down" followed by coordinates. This command would be 
interpreted by the tool command processor accordingly. The same could be done 
for mouse-up events. Special interpretation can be done for mouse-up events if
there was a mouse drag event that occurred. Modifiers can also be used to 
configure the mouse-down event. For example, the modifier "shift" could be 
used to select geometry.

Something that Cartesia has not considered yet is "hover" actions. We have not
considered "hover" actions because they are possible with touch gestures. But 
this doesn't necessarily mean that we should not consider them. 

### Default Mouse Actions
Here are the expected default actions and commands for Cartesia:
- Mouse-down, no modifier in an open area. Mouse-up ignored: Unselect any selected geometry
- Mouse-down, no modifier over geometry. Mouse-up ignored: Select geometry
- Mouse-down, ctrl-drag, mouse-up: Add to selected geometry by window contains
- Mouse-down, shift-drag. Mouse-up ignored: Move the viewpoint
- Scroll-up, no modifier: Zoom in
- Scroll down, no modifier: Zoom out

### Draw.io Style Mouse Actions
A different configuration to consider to avoid focusing on the default configuration:
- Mouse-down, no modifier, mouse-up in an open area: Unselect any selected geometry (Note the command is triggered on the mouse-up event)
- Mouse-down, no modifier over geometry. Mouse-up ignored: Select geometry (Note the command is triggered on the mouse-up event
- Mouse-down, shift-drag, mouse-up: Add to selected geometry by window contains
- Mouse-down, ctrl-drag. Mouse-up ignored: Move the viewpoint
- Ctrl-scroll-up: Zoom in
- Ctrl-scroll down: Zoom out
