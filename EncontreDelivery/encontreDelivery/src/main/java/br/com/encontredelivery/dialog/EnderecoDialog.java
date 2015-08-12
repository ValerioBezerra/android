package br.com.encontredelivery.dialog;

import java.util.List;

import br.com.encontredelivery.activity.NavigationDrawerActivity;
import br.com.encontredelivery.activity.PedirActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EnderecoAdapter;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.Retorno;
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
import android.widget.TextView;

public class EnderecoDialog extends Dialog {
	
	private Context context;    
	private TextView txtTitulo;    
	private ListView lvEnderecos;
	private Button btnCancelar;
	private View view; 
	
	private List<Endereco> listaEnderecos;
	private EnderecoAdapter enderecoAdapter;
	
	private Cliente cliente;
	
	private android.view.View.OnClickListener clickCancelar = new android.view.View.OnClickListener() {
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
			
			btnSim.setOnClickListener(new android.view.View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					confirmacaoEnderecoDialog.dismiss();
					
					Bundle extras = new Bundle();
					extras.putSerializable("cliente", cliente);
					extras.putSerializable("endereco", endereco);
					Intent intent = new Intent(context, NavigationDrawerActivity.class);
					intent.putExtras(extras);
					context.startActivity(intent);
					
					if (context instanceof PedirActivity) {
						((PedirActivity) context).finish();
					}
					
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
          
        txtTitulo   = (TextView) findViewById(R.id.txtTitulo); 
        lvEnderecos = (ListView) findViewById(R.id.lvEnderecos);
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
    	
        btnCancelar.setOnClickListener(clickCancelar);
        lvEnderecos.setOnItemClickListener(clickLvEnderecos);

        setCancelable(false);
	}
	
	 @Override    
     public void setTitle(CharSequence title) {    
         super.setTitle(title);    
         txtTitulo.setText(title);    
     } 
	 
	 @Override    
     public void setTitle(int titleId) {    
         super.setTitle(titleId);    
         txtTitulo.setText(context.getResources().getString(titleId));    
     }	 
	 
	 public void setCliente(Cliente cliente) {
		 this.cliente = cliente;
	 }
	     
	 public void setListaEnderecos(List<Endereco> listaEnderecos) {
		 this.listaEnderecos  = listaEnderecos;
		 this.enderecoAdapter = new EnderecoAdapter(context, listaEnderecos);
		 
		 lvEnderecos.setAdapter(enderecoAdapter);
	 }

}
