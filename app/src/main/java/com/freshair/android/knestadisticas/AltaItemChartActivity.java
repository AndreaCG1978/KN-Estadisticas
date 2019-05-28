package com.freshair.android.knestadisticas;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNItemChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

public class AltaItemChartActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private Dialog dialog = null;
	private KNItemChart mItemChartSeleccionado = null;
	private TextView mDate = null;
	private TextView mTime = null;
	private EditText mValue = null;
	private Button mPickDate;
	private Button mPickTime;
	private Button btnEditar = null;
	private int mYear;
	private String mMonth;
	private String mDay;
	private String mHour = null;
	private String mMin = null;
	private String mChartId = null; 
	
	// --Commented out by Inspection (27/5/2019 08:13):private static final int DATE_DIALOG_ID = 0;
	// --Commented out by Inspection (27/5/2019 08:13):private static final int TIME_DIALOG_ID = 1;
	private final int ITEM_CHART_CURSOR = 1;
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		this.cargarLoaders();
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
		mDate = dialog.findViewById(R.id.fechaEntrada);
		mValue = dialog.findViewById(R.id.valorEntrada);
		mPickDate = dialog.findViewById(R.id.buttonPickDate);
		mPickTime = dialog.findViewById(R.id.buttonPickTime);
		mTime = dialog.findViewById(R.id.horaEntrada);
		btnEditar = dialog.findViewById(R.id.buttonGuardarItem);
	}
	
	private void configurarDatePicker(){
		 final int month;
		 final int day;
/*
         mPickDate.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(DATE_DIALOG_ID);
	         }
	     });
	*/

		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = String.valueOf(c.get(Calendar.MONTH));
		mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));

		month = c.get(Calendar.MONTH);
		day = c.get(Calendar.DAY_OF_MONTH);

		mPickDate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DatePickerDialog dpd = new DatePickerDialog(AltaItemChartActivity.this,
						new DatePickerDialog.OnDateSetListener() {
							@Override
							public void onDateSet(DatePicker view, int year, int month, int day) {
								c.set(year, month , day);
								//		String date = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
								//mDateDisplay.setText(date);
								mYear = c.get(Calendar.YEAR);
								mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);
								mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));
								actualizarFecha();
							}
						}, mYear, month - 1, day);
				/*dpd.getDatePicker().setMinDate(System.currentTimeMillis());

				Calendar d = Calendar.getInstance();
				d.add(Calendar.MONTH,1);

				dpd.getDatePicker().setMaxDate(d.getTimeInMillis());*/
				dpd.show();


			}

		});


		int monthTemp = month;
		int dayTemp = day;


	 //    final Calendar c = Calendar.getInstance();
	     if(mItemChartSeleccionado == null || mItemChartSeleccionado.getDay() == null){
	    	    if(ConstantsAdmin.mDay != null){
					monthTemp = Integer.valueOf(ConstantsAdmin.mMonth);
					dayTemp = Integer.valueOf(ConstantsAdmin.mDay);
		    	    mYear = ConstantsAdmin.mYear;
	    	    }else{
					monthTemp = c.get(Calendar.MONTH) + 1;
					dayTemp = c.get(Calendar.DAY_OF_MONTH);
			     	mYear = c.get(Calendar.YEAR);
	    	    }
	    	    
		     	if(monthTemp < 10){
		     		mMonth = "0" + monthTemp;
		     	}else{
		     		mMonth = String.valueOf(monthTemp);
		     	}
		     	if(dayTemp < 10){
		     		mDay = "0" + dayTemp;
		     	}else{
		     		mDay = String.valueOf(dayTemp);
		     	}
	     }else{
	     	this.cargarVariablesDeFecha();
	     }
	     this.actualizarFecha();
	}
     
	private void configurarTimePicker(){
	   /*  mPickTime.setOnClickListener(new View.OnClickListener() {
	         public void onClick(View v) {
	             showDialog(TIME_DIALOG_ID);
	         }
	     });
*/

	     mPickTime.setOnClickListener(new View.OnClickListener() {

			 @Override
			 public void onClick(View v) {
				 // TODO Auto-generated method stub
				 Calendar mcurrentTime = Calendar.getInstance();
				 int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
				 int minute = mcurrentTime.get(Calendar.MINUTE);
				 TimePickerDialog mTimePicker;
				 mTimePicker = new TimePickerDialog(AltaItemChartActivity.this, new TimePickerDialog.OnTimeSetListener() {
					 @Override
					 public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
						 //eReminderTime.setText( selectedHour + ":" + selectedMinute);
						 mHour = String.valueOf(selectedHour);
						 mMin = String.valueOf(selectedMinute);
						 actualizarHora();
					 }
				 }, hour, minute, true);//Yes 24 hour time
				 mTimePicker.show();

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

	
// --Commented out by Inspection START (28/5/2019 07:26):
//	private final DatePickerDialog.OnDateSetListener mDateSetListener =
//        new DatePickerDialog.OnDateSetListener() {
//
//            public void onDateSet(DatePicker view, int year,
//                                  int monthOfYear, int dayOfMonth) {
//            	mYear = year;
//            	if(monthOfYear < 9){
//            		mMonth = "0" + (monthOfYear + 1);
//            	}else{
//            		mMonth = String.valueOf(monthOfYear + 1);
//            	}
//                if(dayOfMonth < 10){
//                	mDay = "0" + dayOfMonth;
//                }else{
//                	mDay = String.valueOf(dayOfMonth);
//                }
//
//                actualizarFecha();
//            }
//    };
// --Commented out by Inspection STOP (28/5/2019 07:26)

// --Commented out by Inspection START (28/5/2019 07:26):
//    private final TimePickerDialog.OnTimeSetListener mTimeSetListener =
//    	new TimePickerDialog.OnTimeSetListener() {
//
//			@Override
//			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//				// TODO Auto-generated method stub
//				if(hourOfDay < 10){
//					mHour = "0" + hourOfDay;
//				}else{
//					mHour = String.valueOf(hourOfDay);
//				}
//				if(minute < 10){
//					mMin = "0" + minute;
//				}else{
//					mMin = String.valueOf(minute);
//				}
//                actualizarHora();
//			}
//		};
// --Commented out by Inspection STOP (28/5/2019 07:26)

    private void actualizarFecha(){
    	String fecha;
    	fecha = obtenerFecha();
		mDate.setText(fecha);
    }
    
    private void actualizarHora(){
    	String hora;
    	hora = obtenerHora();
    	mTime.setText(hora);
    }
       
   /* protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DATE_DIALOG_ID:
            return new DatePickerDialog(this,
                        mDateSetListener,
                        mYear, Integer.valueOf(mMonth) - 1, Integer.valueOf(mDay));

        case TIME_DIALOG_ID:
        	return new TimePickerDialog(this, mTimeSetListener, Integer.valueOf(mHour), Integer.valueOf(mMin), true);
        }
        return null;
    }*/
	
 	private void cargarVariablesDeFecha(){
		mDay = mItemChartSeleccionado.getDay();
		mMonth = mItemChartSeleccionado.getMonth();
		mYear = Integer.valueOf(mItemChartSeleccionado.getYear());
	}

 	
 	private void cargarVariablesDeHora(){
		mHour = mItemChartSeleccionado.getHour();
		mMin = mItemChartSeleccionado.getMin();
	}
 	
	private void guardarItemChartSeleccionado(Intent intent){
		String idItemString;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		mChartId = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
		if(intent.hasExtra(ConstantsAdmin.ITEM_CHART_SELECCIONADO)){
			idItemString = (String)intent.getExtras().get(ConstantsAdmin.ITEM_CHART_SELECCIONADO);
			int idItem = Integer.valueOf(idItemString);
			mItemChartSeleccionado = ConstantsAdmin.obtenerItemId(this, idItem, mDBManager);
			this.cargarEntriesConItemDto();
		}else{
			mItemChartSeleccionado = new KNItemChart();
		}
 	}
	
	private void cargarEntriesConItemDto(){
		mValue.setText(mItemChartSeleccionado.getValue());
	}
	
	private String obtenerFecha(){
		return mYear + "-" + mMonth + "-" + mDay;
	}
	
	private String obtenerHora(){
		return mHour + ":" + mMin;
	}
	
	private boolean validarEntradaDeDatos(){
		boolean estaOk;
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
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_item_incompleto, Toast.LENGTH_LONG);
            		t.show();
            		
            	}
			}
		});
    	
    }
    
	private void registrarItem(){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
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
			ConstantsAdmin.agregarItem(mItemChartSeleccionado, mDBManager);
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


	@NonNull
	@Override
	public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		CursorLoader cl = null;
		switch(id) {
			case ITEM_CHART_CURSOR:
				cl = mDBManager.cursorLoaderItemChart(this, -1);
				ConstantsAdmin.cursorItemChart = cl;
				break; // optional
			default : // Optional
				// Statements
		}

		return cl;
	}

	private void cargarLoaders() {
		this.getSupportLoaderManager().initLoader(ITEM_CHART_CURSOR, null, this);

	}

	@Override
	public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

	}

	@Override
	public void onLoaderReset(@NonNull Loader<Cursor> loader) {

	}
}
