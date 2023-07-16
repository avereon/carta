package com.avereon.cartesia.tool.view;

import javafx.geometry.Bounds;
import javafx.scene.text.Text;

public class TextMetrics {

	private final Text text;

	private final double lead;

	private final double ascent;

	private final double descent;

	private final double spacing;

	public TextMetrics( Text text ) {
		this.text = text;

		Bounds bounds = text.getLayoutBounds();

		// Lead will be negative
		this.lead = -(bounds.getMinX() - text.getX());
		// Ascent will be negative
		this.ascent = -(bounds.getMinY() - text.getY());
		// Descent will be negative
		this.descent = -(bounds.getMaxY() - text.getY());
		// Spacing will be positive
		this.spacing = bounds.getHeight();
	}

	public Text getText() {
		return text;
	}

	public double getLead() {
		return lead;
	}

	public double getAscent() {
		return ascent;
	}

	public double getDescent() {
		return descent;
	}

	public double getSpacing() {
		return spacing;
	}

	@Override
	public String toString() {
		return "TextMetrics [lead=" + lead + " ascent=" + ascent + ", decent=" + descent + ", spacing=" + spacing + "]";
	}
}
