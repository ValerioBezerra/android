package br.com.kingsoft.procureaki.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.activity.EncontreMeuEnderecoActivity;
import br.com.kingsoft.procureaki.activity.SegmentosActivity;
import br.com.kingsoft.procureaki.adapter.EnderecoAdapter;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.util.Retorno;


public class EnderecoDialog extends Dialog {
	
	private Context context;    
	private ListView lvEnderecos;
	private Button btnCancelar;
	private View view; 
	
	private List<Endereco> listaEnderecos;
	private EnderecoAdapter enderecoAdapter;
	
	private View.OnClickListener clickCancelar = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	private OnItemClickListener clickLvEnderecos = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			final Endereco endereco = listaEnderecos.get(position);

			final ConfirmacaoEnderecoDialog confirmacaoEnderecoDialog = new ConfirmacaoEnderecoDialog(context);
			confirmacaoEnderecoDialog.setTitle(context.getResources().getString(R.string.confirmar_endereco));
			confirmacaoEnderecoDialog.setCEPLogradouro("(" + Retorno.getMascaraCep(endereco.getCep()) + ") " + endereco.getLogradouro());
			confirmacaoEnderecoDialog.setBairro(endereco.getBairro().getNome());
			confirmacaoEnderecoDialog.setCidadeUF(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getNome());
			dismiss();
			confirmacaoEnderecoDialog.show();

			Button btnSim = (Button) confirmacaoEnderecoDialog.findViewById(R.id.btnSim);

			btnSim.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
				confirmacaoEnderecoDialog.dismiss();

				Bundle extras = new Bundle();
				extras.putString("latitude", endereco.getLatitude());
				extras.putString("longitude", endereco.getLongitude());
				Intent intent = new Intent(context, SegmentosActivity.class);
				intent.putExtras(extras);
				context.startActivity(intent);
				((EncontreMeuEnderecoActivity) context).iniciarComponentes();
				}
			});
			
		}
	}; 

	public EnderecoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_endereco);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
        lvEnderecos = (ListView) findViewById(R.id.lvEnderecos);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
    	
        btnCancelar.setOnClickListener(clickCancelar);
        lvEnderecos.setOnItemClickListener(clickLvEnderecos);

        setCancelable(false);
	}
	

	 public void setListaEnderecos(List<Endereco> listaEnderecos) {
		 this.listaEnderecos  = listaEnderecos;
		 this.enderecoAdapter = new EnderecoAdapter(context, listaEnderecos);
		 
		 lvEnderecos.setAdapter(enderecoAdapter);
	 }

}
