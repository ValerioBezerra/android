package br.com.encontredelivery.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.FoneAdapter;
import br.com.encontredelivery.util.Retorno;

public class FoneDialog extends Dialog {

	private Context context;
	private ListView lvFones;
	private Button btnCancelar;
	private View view;

	private List<String> listaFones;
	private FoneAdapter foneAdapter;

	private View.OnClickListener clickCancelar = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	private OnItemClickListener clickLvFones = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			String fone = listaFones.get(position);

			Intent callIntent = new Intent(Intent.ACTION_DIAL);
			callIntent.setData(Uri.parse("tel:" + Retorno.getSomenteNumeros(fone)));
			context.startActivity(callIntent);
			dismiss();
		}
	};

	public FoneDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_fone);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
		lvFones     = (ListView) findViewById(R.id.lvFones);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
    	
        btnCancelar.setOnClickListener(clickCancelar);
		lvFones.setOnItemClickListener(clickLvFones);
	}
	
	 public void setListaFones(List<String> listaFones) {
		 this.listaFones  = listaFones;
		 this.foneAdapter = new FoneAdapter(context, listaFones);

		 lvFones.setAdapter(foneAdapter);
	 }

}
