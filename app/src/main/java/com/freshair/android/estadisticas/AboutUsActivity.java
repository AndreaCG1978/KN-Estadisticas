package com.freshair.android.estadisticas;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.freshair.android.estadisticas.utils.ConstantsAdmin;

public class AboutUsActivity extends Activity {
	
	private  TextView mail = null;
	private TextView publicaciones = null;
	private ImageView logo = null;

	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.about_us);
       	this.setTitle(R.string.title_acerca_de);
       	this.configurarWidgets();
    }

    
    private void configurarWidgets(){
    	mail = (TextView) this.findViewById(R.id.textAcercaDeMail);
    	mail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enviarMail(mail.getText().toString());
			}
		});
    	publicaciones = (TextView) this.findViewById(R.id.textTodasLasPublicaciones);
    	publicaciones.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				irPublicaciones();
			}
		});
    	/*logo = (ImageView) this.findViewById(R.id.imagenLogo);
    	logo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				irBoxico();
			}
		});*/

    	
    }
    
    
    private void irPublicaciones(){
    	try {
    		Intent intent = new Intent(Intent.ACTION_VIEW);
    		intent.setData(Uri.parse("market://search?q=pub:" + getString(R.string.boxico)));
    		startActivity(intent);
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.error_ver_publicaciones));
		}
    	 	
    }
    
    private void irBoxico(){
    	try {
    		Intent intent = new Intent(Intent.ACTION_VIEW);
    		intent.setData(Uri.parse(ConstantsAdmin.UrlBoxico));
    		startActivity(intent);
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.error_ver_publicaciones));
		}
    	 	
    }

    private void enviarMail(String text){
    	try {
    		Intent email_intent =  new Intent(android.content.Intent.ACTION_SEND);
        	email_intent.setType("plain/text");
        	email_intent.putExtra(android.content.Intent.EXTRA_EMAIL, new
        	String[]{text});
        	email_intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Subject");
        	email_intent.putExtra(android.content.Intent.EXTRA_TEXT, " ");
        	this.startActivity(Intent.createChooser(email_intent,"Send mail..."));   
			
		} catch (Exception e) {
			ConstantsAdmin.mostrarMensajeAplicacion(this, getString(R.string.error_enviar_mail));
		}
    	 	
    }
    
}
