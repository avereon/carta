package com.avereon.cartesia.settings;

class PaintEntry {

	private final String key;

	private final String label;

	private String paint;

	public PaintEntry( String key, String label, String paint ) {
		this.key = key;
		this.label = label;
		this.paint = paint;
	}

	public String getKey() {
		return key;
	}

	public String getLabel() {
		return label;
	}

	public String getPaint() {
		return paint;
	}

	public void setPaint( String paint ) {
		this.paint = paint;
	}

}
