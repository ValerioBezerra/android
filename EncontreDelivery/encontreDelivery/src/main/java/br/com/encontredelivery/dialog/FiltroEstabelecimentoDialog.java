package br.com.encontredelivery.dialog;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.SegmentoAdapter;
import br.com.encontredelivery.model.Segmento;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridView;



public class FiltroEstabelecimentoDialog extends Dialog {
	private View view;   
	private GridView gdSegmentos;
	private Button btnCancelar;
	
	private SegmentoAdapter segmentoAdapter;
	
	private android.view.View.OnClickListener clickCancelar = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dismiss();
		}
	};
	
	public FiltroEstabelecimentoDialog(Context context, List<Segmento> listaSegmentos) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_filtro_estabelecimento);
        
        view        = getWindow().getDecorView();    
        gdSegmentos = (GridView) view.findViewById(R.id.gdSegmentos);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        
        view.setBackgroundResource(android.R.color.transparent);    
        
        segmentoAdapter = new SegmentoAdapter(context, listaSegmentos);
        gdSegmentos.setAdapter(segmentoAdapter);
        
        btnCancelar.setOnClickListener(clickCancelar);
        setCancelable(false);
	}

	public List<Segmento> getListaSegmentos() {
		return segmentoAdapter.getListaSegmentosEscolhidos();
	}

}
