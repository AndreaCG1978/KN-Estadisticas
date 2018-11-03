package com.freshair.android.estadisticas.dtos;

import java.text.SimpleDateFormat;

import com.freshair.android.estadisticas.utils.ConstantsAdmin;

public class KNItemChart {
	
	private long id = -1;
	private String day = null;
	private String month = null;
	private String year = null;
	private String value = null;
	private String chartId = null;
	private String hour = "00";
	private String min = "00";
	
	public String getHour() {
		return hour;
	}
	public void setHour(String hour) {
		if(hour!= null && hour.length() == 1){
			this.hour = "0" + hour;	
		}else{
			this.hour = hour;
		}
	}
	public String getMin() {
		return min;
	}
	public void setMin(String min) {
		if(min!= null && min.length() == 1){
			this.min = "0" + min;	
		}else{
			this.min = min;
		}
		
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getChartId() {
		return chartId;
	}
	public void setChartId(String chartId) {
		this.chartId = chartId;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		if(day!= null && day.length() == 1){
			this.day = "0" + day;	
		}else{
			this.day = day;
		}
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		if(month!= null && month.length() == 1){
			this.month = "0" + month;	
		}else{
			this.month = month;
		}
			
		
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	
	// DESDE ACA
	
	public String getDate(){
		return year + ConstantsAdmin.SEPARADOR_FECHA + month + ConstantsAdmin.SEPARADOR_FECHA + day; 
	}
	
	public String getHourMin(){
		return hour + ConstantsAdmin.SEPARADOR_HORA + min; 
	}
	
	
    public void setDate(String date){
        String[] temp = date.split(ConstantsAdmin.SEPARADOR_FECHA);
        if(temp != null && temp.length == 3){
	        this.setYear(temp[0]);
	        this.setMonth(temp[1]);
	        this.setDay(temp[2]);
        }
    }

    public void setTime(String time){
        String[] temp = time.split(ConstantsAdmin.SEPARADOR_HORA);
        if(temp != null && temp.length == 2){
            this.setHour(temp[0]);
            this.setMin(temp[1]);
        }
    }

    public void setDateTime(String dateTime){
        String[] temp = dateTime.split(ConstantsAdmin.SEPARADOR_FECHA_HORA);
        if(temp != null && temp.length == 2){
	        this.setDate(temp[0]);
	        this.setTime(temp[1]);
        }
    }

	public boolean validoDatos(){
		boolean result = true;
		result = validoValue() && validaFecha() && validaHora();
		return result;
	}
	
	private boolean validoValue(){
		boolean result = true;
		try {
			Double.valueOf(this.getValue());
			
		} catch (Exception e) {
			result = false;
		}
		
		return result;
	}
	
	private boolean validaFecha(){
		boolean result = true;
		SimpleDateFormat df = null;
		try {
		    df = new SimpleDateFormat("dd/MM/yyyy");
		    df.setLenient(false);
		    df.parse(this.getDay() + ConstantsAdmin.SEPARADOR_FECHA + this.getMonth() + ConstantsAdmin.SEPARADOR_FECHA + Integer.parseInt(this.getYear()));
		} catch (Exception e) {
		    result = false;
		}
		return result;
	}
	
	private boolean validaHora(){
		int i = 0;
		boolean result = true;
		try {
			i = Integer.valueOf(this.getHour());
			result = i <= 23 && i >= 0 && this.getHour().length() <= 2;
		} catch (Exception e) {
			result = false;
		}

		if(result){
			try {
				i = Integer.valueOf(this.getMin());
				result = i <= 59 && i >= 0 && this.getMin().length() <= 2;
			} catch (Exception e) {
				result = false;
			}
		}
		return result;
	}
/*	try {
	    df = new java.text.SimpleDateFormat('MM/dd/yyyy')
	    df.setLenient(false)
	    df.parse('40/40/4353')
	    assert false
	} catch (java.text.ParseException e) {
	    assert e.message =~ 'Unparseable'
	}
	*/

}
