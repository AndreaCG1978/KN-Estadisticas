package com.freshair.android.knestadisticas.dtos;

import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

import android.graphics.Color;

public class KNChart {

	private long id = -1;
	private String name = null;
	private String description = null;
	private String unit = null;
	private String pointStyle;
	private String lineColor;
	private String labelColor;
	private String gridColor;
	private String backgroundColor;
	private String formatTime;
	private boolean showGrid;
	private boolean showValue;
	
	public boolean isShowGrid() {
		return showGrid;
	}


	public void setShowGrid(boolean showGrid) {
		this.showGrid = showGrid;
	}


	public KNChart() {
		pointStyle = "2";
		lineColor = String.valueOf(Color.GREEN);
		labelColor = String.valueOf(Color.WHITE);
		gridColor = String.valueOf(Color.LTGRAY);
		backgroundColor = String.valueOf(Color.DKGRAY);
		formatTime = ConstantsAdmin.formatTime[0];
		showGrid = false;
		showValue = true;
	}


	public boolean isShowValue() {
		return showValue;
	}


	public void setShowValue(boolean showValue) {
		this.showValue = showValue;
	}


	public String getPointStyle() {
		return pointStyle;
	}


	public void setPointStyle(String pointStyle) {
		this.pointStyle = pointStyle;
	}


	public String getLineColor() {
		return lineColor;
	}


	public void setLineColor(String lineColor) {
		this.lineColor = lineColor;
	}


	public String getLabelColor() {
		return labelColor;
	}


	public void setLabelColor(String labelColor) {
		this.labelColor = labelColor;
	}


	public String getGridColor() {
		return gridColor;
	}


	public void setGridColor(String gridColor) {
		this.gridColor = gridColor;
	}


	public String getBackgroundColor() {
		return backgroundColor;
	}


	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}


	public String getFormatTime() {
		return formatTime;
	}


	public void setFormatTime(String formatTime) {
		this.formatTime = formatTime;
	}


	public String toString(){
		return name;
	}

	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}

}
