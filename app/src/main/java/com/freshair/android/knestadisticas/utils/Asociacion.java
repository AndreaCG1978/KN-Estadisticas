package com.freshair.android.knestadisticas.utils;

class Asociacion {
	
	private Object key;
	private Object value;
	public Object getKey() {
		return key;
	}
	public void setKey(Object key) {
		this.key = key;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Asociacion(Object key, Object value) {
		super();
		this.key = key;
		this.value = value;
	}
	
	

}
