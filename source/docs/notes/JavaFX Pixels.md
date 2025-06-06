# JavaFX Pixels

# Problem Statement

When using JavaFX, the normal unit for JavaFX is pixels, presumably because it
is optimized for rendering on pixel based devices. An initial implementation of
Cartesia naively used design units (cm, inch, etc.) as the normal unit for the
rendering engine. This caused issues with several types of geometry,
particularly lines and text.

The problem with rendering lines in design units is that when the line width is
less than one design unit, using JavaFX logic to calculate the line visual
bounds it ends up being incorrect because JavaFX always rounds up to one, again
presumably one pixel. Because of this, anything that relies on the line's visual
bounds, such as zoom, selection, and hit testing, will not work consistently.

Text is similar in that it is rendered in pixels, and when the text size is
small, the JavaFX text rendering engine will not render the text properly. This
usually happens when requesting font sizes that are less than four. Again, the
assumption is that JavaFX is optimized for rendering text in pixels, and when
the text size is that small, rounding errors occur, and it will not render
correctly.

# Solution

The solution is to use pixels as the normal unit for the rendering engine. This
means that all geometry will have to be rendered in pixels, and the design
geometry will have to be scaled to and from pixels possibly without the help of
JavaFX. Here are some possible options for how to do this:

- Internal design scaling transform: Create a transform that scales the design
  geometry units to pixels, and all geometry will be rendered in pixels based
  on that scale. This means that the pane will not be scaled, but all geometry
  will be rendered in pixels based on the scale.
- Nested design scaling pane: Create a pane that is scaled based on the design
  scale, and all geometry will be rendered in a pane inside that pane in that
  pane. This means that the pane will be scaled to the design scale, and all
  geometry will be rendered in pixels based on that scale.

