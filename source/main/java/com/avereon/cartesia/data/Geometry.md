# Geometry Tasks

## Bounds

|         | Simple     | Visual | Select     |
|---------|------------|--------|------------|
| Line    | 2024-12-02 |        | 2024-07-30 |
| Box     | 2024-12-02 |        | 2024-07-30 |
| Arc     |            |        | 2024-07-30 |
| Ellipse |            |        | 2024-07-30 |
| Cubic   |            |        | 2024-07-30 |
| Quad    |            |        | 2024-07-30 |
| Path    |            |        | 2024-07-30 |
| Marker  |            |        | 2024-07-30 |
| Text    |            |        | 2024-07-30 |

- Simple - The bounds of the geometry without including stroke parameters.
- Visual - The bounds of the geometry including stroke parameters.
- Select - The bounds of the geometry for purposes of screen selection.

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

