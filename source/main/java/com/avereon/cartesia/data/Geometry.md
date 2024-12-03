# Geometry Tasks

## Bounds

Updated: 2024-12-02

|         | Simple     | Visual | Select |
|---------|------------|--------|--------|
| Line    | 2024-12-02 |        | ✓      |
| Box     | 2024-12-02 |        | ✓      |
| Arc     |            |        | ✓      |
| Ellipse |            |        | ✓      |
| Cubic   |            |        | ✓      |
| Quad    |            |        | ✓      |
| Path    |            |        |        |
| Marker  |            |        | ✓      |
| Text    |            |        | ✓      |

The bounds of the geometry without including stroke parameters.

## Select Bounds

The bounds of the geometry for purposes of screen selection.

## Visual Bounds

The bounds of the geometry including stroke parameters.

## Intersection

Updated: 2024-12-02

|         | Line | Box | Arc | Ellipse | Cubic | Quad | Path | Marker | Text |
|---------|------|-----|-----|---------|-------|------|------|--------|------|
| Line    | ✓    | -   | -   | -       | -     | -    | -    | -      | -    |
| Box     |      |     | -   | -       | -     | -    | -    | -      | -    |
| Arc     | ✓    |     | ✓   | -       | -     | -    | -    | -      | -    |
| Ellipse | ✓    |     | ✓   | ✓       | -     | -    | -    | -      | -    |
| Cubic   | ✓    |     | ✓   | ✓       | ✓     | -    | -    | -      | -    |
| Quad    |      |     |     |         |       |      | -    | -      | -    |
| Path    |      |     |     |         |       |      |      | -      | -    |
| Marker  |      |     |     |         |       |      |      |        | -    |
| Text    |      |     |     |         |       |      |      |        |      |

## Transforms

