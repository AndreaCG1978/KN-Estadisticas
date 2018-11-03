package com.freshair.android.estadisticas;

import java.util.Calendar;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.freshair.android.estadisticas.dtos.KNItemChart;
import com.freshair.android.estadisticas.utils.ConstantsAdmin;

public class AltaItemChartActivity extends Activity {
	
	Dialog dialog = null;
	KNItemChart mItemChartSeleccionado = null;
	TextView mDate = null;
	TextView mTime = null;
	EditText mValue = null;
	private Button mPickDate;
	private Button mPickTime;
	Button btnEditar = null;
	private int mYear;
	private String mMonth;
	private String mDay;
	String mHour = null;
	String mMin = null;
	private String mChartId = null; 
	
	static final int DATE_DIALOG_ID = 0;
	static final int TIME_DIALOG_ID = 1;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.configurarDialog();
        this.registrarWidgets();
        this.guardarItemChartSeleccionado(this.getIntent());
        this.configurarBotonAgregar();
        this.configurarDatePicker();
        this.configurarTimePicker();
        ConstantsAdmin.guardoItem = false;
        dialog.show();
    }
	
	private void configurarDialog(){
		dialog = new Dialog(this);
        dialog.setContentView(R.layout.alta_item);
        dialog.setCancelable(true);
        dialog.setTitle(this.getString(R.string.title_nuevo_item));
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	private void registrarWidgets(){
		mDate = (TextView) dialog.findViewById(R.id.fechaEntrada);
		mValue = (EditText) dialog.findViewById(R.id.valorEntrada);	
		mPickDate = (Button) dialog.findViewById(R.id.buttonPickDate);
		mPickTime = (Button) dialog.findViewById(R.id.buttonPickTime);
		mTime = (TextView) dialog.findViewById(R.id.horaEntrada);
		btnEditar = (Button) dialog.findViewById(R.id.buttonGuardarItem);
	}
	
	private void configurarDatePicker(){
		 int month = 0;
		 int day = 0;

         mPickDate.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(DATE_DIALOG_ID);
	         }
	     });
	
	     final Calendar c = Calendar.getInstance();
	     if(mItemChartSeleccionado == null || mItemChartSeleccionado.getDay() == null){
	    	    if(ConstantsAdmin.mDay != null){
	    	    	month = Integer.valueOf(ConstantsAdmin.mMonth);
		    	    day = Integer.valueOf(ConstantsAdmin.mDay);
		    	    mYear = ConstantsAdmin.mYear;
	    	    }else{
	    	    	month = c.get(Calendar.MONTH) + 1;
			    	day = c.get(Calendar.DAY_OF_MONTH);
			     	mYear = c.get(Calendar.YEAR);
	    	    }
	    	    
		     	if(month < 10){
		     		mMonth = "0" + String.valueOf(month);
		     	}else{
		     		mMonth = String.valueOf(month);
		     	}
		     	if(day < 10){
		     		mDay = "0" +  String.valueOf(day);
		     	}else{
		     		mDay = String.valueOf(day);
		     	}
	     }else{
	     	this.cargarVariablesDeFecha();
	     }
	     this.actualizarFecha();
	}
     
	private void configurarTimePicker(){
	     mPickTime.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(TIME_DIALOG_ID);
	         }
	     });
	     if(mItemChartSeleccionado == null || mItemChartSeleccionado.getHour() == null){
	     	 if(ConstantsAdmin.mHour != null){
	    		 mHour = ConstantsAdmin.mHour;
			     mMin = ConstantsAdmin.mMin;	    		 
	    	 }else{
	    		 mHour = "00";
			     mMin = "00";
	    	 }
	    	
	     }else{
	     	this.cargarVariablesDeHora();
	     }
	     this.actualizarHora();
	}

	
	private DatePickerDialog.OnDateSetListener mDateSetListener =
        new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, 
                                  int monthOfYear, int dayOfMonth) {
            	mYear = year;
            	if(monthOfYear < 9){
            		mMonth = "0" + String.valueOf(monthOfYear + 1);
            	}else{
            		mMonth = String.valueOf(monthOfYear + 1);
            	}
                if(dayOfMonth < 10){
                	mDay = "0" + String.valueOf(dayOfMonth);
                }else{
                	mDay = String.valueOf(dayOfMonth);
                }
                
                actualizarFecha();
            }
    };
    
    private TimePickerDialog.OnTimeSetListener mTimeSetListener =
    	new TimePickerDialog.OnTimeSetListener() {
			
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				// TODO Auto-generated method stub
				if(hourOfDay < 10){
					mHour = "0" + String.valueOf(hourOfDay);
				}else{
					mHour = String.valueOf(hourOfDay);
				}
				if(minute < 10){
					mMin = "0" + String.valueOf(minute);
				}else{
					mMin = String.valueOf(minute);
				}
                actualizarHora();
			}
		};
    
    private void actualizarFecha(){
    	String fecha = null; 
    	fecha = obtenerFecha();
		mDate.setText(fecha);
    }
    
    private void actualizarHora(){
    	String hora = null; 
    	hora = obtenerHora();
    	mTime.setText(hora);
    }
       
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, Integer.valueOf(mMonth) - 1, Integer.valueOf(mDay));

        case TIME_DIALOG_ID:
        	return new TimePickerDialog(this, mTimeSetListener, Integer.valueOf(mHour), Integer.valueOf(mMin), true);
        }
        return null;
    }
	
 	private void cargarVariablesDeFecha(){
		mDay = mItemChartSeleccionado.getDay();
		mMonth = mItemChartSeleccionado.getMonth();
		mYear = new Integer(mItemChartSeleccionado.getYear());
	}

 	
 	private void cargarVariablesDeHora(){
		mHour = mItemChartSeleccionado.getHour();
		mMin = mItemChartSeleccionado.getMin();
	}
 	
	private void guardarItemChartSeleccionado(Intent intent){
		String idItemString = null;
		mChartId = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
		if(intent.hasExtra(ConstantsAdmin.ITEM_CHART_SELECCIONADO)){
			idItemString = (String)intent.getExtras().get(ConstantsAdmin.ITEM_CHART_SELECCIONADO);
			int idItem = new Integer(idItemString);
			mItemChartSeleccionado = ConstantsAdmin.obtenerItemId(this, idItem);
			this.cargarEntriesConItemDto();
		}else{
			mItemChartSeleccionado = new KNItemChart();
		}
 	}
	
	private void cargarEntriesConItemDto(){
		mValue.setText(mItemChartSeleccionado.getValue());
	}
	
	private String obtenerFecha(){
		return String.valueOf(mYear) + "-" + mMonth + "-" + mDay;
	}
	
	private String obtenerHora(){
		return mHour + ":" + mMin;
	}
	
	private boolean validarEntradaDeDatos(){
		boolean estaOk = false;
		String value = mValue.getText().toString();
		String date = mDate.getText().toString();
		estaOk = !value.equals("") && !date.equals("");
		return estaOk;
	}
	
    private void configurarBotonAgregar(){
    	btnEditar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
            	if(validarEntradaDeDatos()){
                    registrarItem();
                    setResult(RESULT_OK);
                    finish();
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_item_incompleto, 4);
            		t.show();
            		
            	}
			}
		});
    	
    }
    
	private void registrarItem(){
		try {

			if(mItemChartSeleccionado == null){
				mItemChartSeleccionado = new KNItemChart();
			}
			mItemChartSeleccionado.setValue(mValue.getText().toString());
			mItemChartSeleccionado.setDay(String.valueOf(mDay));
			mItemChartSeleccionado.setMonth(String.valueOf(mMonth));
			mItemChartSeleccionado.setYear(String.valueOf(mYear));
			mItemChartSeleccionado.setHour(String.valueOf(mHour));
			mItemChartSeleccionado.setMin(String.valueOf(mMin));
			mItemChartSeleccionado.setChartId(mChartId);
			ConstantsAdmin.agregarItem(mItemChartSeleccionado, this);
			ConstantsAdmin.mDay = mDay;
			ConstantsAdmin.mMonth = mMonth;
			ConstantsAdmin.mYear = mYear;
			ConstantsAdmin.mHour = mHour;
			ConstantsAdmin.mMin = mMin;
			ConstantsAdmin.guardoItem = true;
		} catch (Exception e) {
			if(mItemChartSeleccionado.getId() == 0){
				ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorAltaItemChart));
			}else{
				ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorActualizacionItemChart));
			}
		}

	}
	

}
