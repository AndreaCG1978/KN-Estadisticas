package com.freshair.android.knestadisticas;


import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.freshair.android.knestadisticas.database.DataBaseManager;
import com.freshair.android.knestadisticas.dtos.KNChart;
import com.freshair.android.knestadisticas.utils.ConstantsAdmin;

public class AltaChartActivity extends Activity {
	
	private EditText mNombreChart = null;
	private EditText mDescripcionChart = null;
	private EditText mUnidadChart = null;
	private Button btnEditar = null;
	private Dialog dialog = null;
	private KNChart mChartSeleccionado = null;
	
    
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.configurarDialog();
        this.registrarWidgets();
        this.guardarChartSeleccionado(this.getIntent());
        this.configurarBotonAgregar();
        if(mChartSeleccionado.getId()== -1){
        	dialog.setTitle(R.string.title_nuevo_chart);
        }else{
        	dialog.setTitle(mChartSeleccionado.getName());
        }
        
        dialog.show();
    }
	
    private void configurarBotonAgregar(){
    	btnEditar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
            	if(validarEntradaDeDatos()){
                    registrarChart();
                    setResult(RESULT_OK);
                    finish();
            	}else{
            		Toast t = Toast.makeText(getApplicationContext(), R.string.error_alta_chart_incompleto, Toast.LENGTH_LONG);
            		t.show();
           		
            	}
			}
		});
    	
    }
    
	private void registrarChart(){
		try {
			DataBaseManager mDBManager = DataBaseManager.getInstance(this);
			if(mChartSeleccionado == null){
				mChartSeleccionado = new KNChart();
			}
			mChartSeleccionado.setName(mNombreChart.getText().toString());
			mChartSeleccionado.setDescription(mDescripcionChart.getText().toString());
			mChartSeleccionado.setUnit(mUnidadChart.getText().toString());
			ConstantsAdmin.agregarChart(mChartSeleccionado, mDBManager);

		} catch (Exception e) {
			if(mChartSeleccionado.getId() == 0){
				ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorAltaChart));
			}else{
				ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.errorActualizacionChart));
			}
		}

	}
    
    
	private boolean validarEntradaDeDatos(){
		boolean estaOk;
		String name = mNombreChart.getText().toString();
		String unit = mUnidadChart.getText().toString();
		estaOk = !name.equals("") && !unit.equals("");
		return estaOk;
	}
	
	private void guardarChartSeleccionado(Intent intent){
		String idChartString;
		DataBaseManager mDBManager = DataBaseManager.getInstance(this);
		if(intent.hasExtra(ConstantsAdmin.CHART_SELECCIONADO)){
			idChartString = (String)intent.getExtras().get(ConstantsAdmin.CHART_SELECCIONADO);
			int idChart = Integer.valueOf(idChartString);
			mChartSeleccionado = ConstantsAdmin.obtenerChartId(idChart, mDBManager);
			this.cargarEntriesConChartDto();
		}else{
			mChartSeleccionado = new KNChart();
		}
 	}
	
	private void cargarEntriesConChartDto(){
		mNombreChart.setText(mChartSeleccionado.getName());
		mDescripcionChart.setText(mChartSeleccionado.getDescription());
		mUnidadChart.setText(mChartSeleccionado.getUnit());

	}
	
	private void configurarDialog(){
		dialog = new Dialog(this);
        dialog.setContentView(R.layout.alta_chart);
        dialog.setCancelable(true);
        dialog.setOnCancelListener(new Dialog.OnCancelListener() {
        	public void onCancel(DialogInterface dialog) {
        		finish();
        	}
        });
	}
	
	
	
	private void registrarWidgets(){
		mNombreChart = dialog.findViewById(R.id.entryNombreChart);
		mDescripcionChart = dialog.findViewById(R.id.entryDescripcionChart);
		mUnidadChart = dialog.findViewById(R.id.entryUnidadChart);
		btnEditar = dialog.findViewById(R.id.buttonGuardarChart);
	}
	
 

}
