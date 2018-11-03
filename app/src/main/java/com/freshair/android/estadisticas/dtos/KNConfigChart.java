package com.freshair.android.estadisticas.dtos;

public class KNConfigChart {
	
	private long id = -1;
	private String point = null;
	private String line = null;
	private String label = null;
	private String grid = null;
	private String background = null;
	private String time = null;
	private boolean showGrid = true;
	private boolean showValue = true;

	public boolean isShowGrid() {
		return showGrid;
	}
	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}
	public boolean isShowValue() {
		return showValue;
	}
	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getPoint() {
		return point;
	}
	public void setPoint(String point) {
		this.point = point;
	}
	public String getLine() {
		return line;
	}
	public void setLine(String line) {
		this.line = line;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public String getGrid() {
		return grid;
	}
	public void setGrid(String grid) {
		this.grid = grid;
	}
	public String getBackground() {
		return background;
	}
	public void setBackground(String background) {
		this.background = background;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}

}
