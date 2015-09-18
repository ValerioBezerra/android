package br.com.encontredelivery.dialog;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.TamanhoAdapter;
import br.com.encontredelivery.model.Tamanho;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class TamanhoDialog extends Dialog {
	
	private Context context;    
	private TextView txtTitulo;    
	private ListView lvTamanhos;
	private View view; 
	
	private TamanhoAdapter tamanhoAdapter;
	
	public TamanhoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_tamanho);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
        txtTitulo   = (TextView) findViewById(R.id.txtTitulo); 
        lvTamanhos = (ListView) findViewById(R.id.lvTamanhos);

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
	 
	 public void setListaTamanhos(List<Tamanho> listaTamanhos, boolean precoMaiorProduto) {
		 this.tamanhoAdapter = new TamanhoAdapter(context, listaTamanhos, precoMaiorProduto);
		 lvTamanhos.setAdapter(tamanhoAdapter);
	 }

	public List<Tamanho> getListaTamanhos() {
		return tamanhoAdapter.getListaProdutosEscolhidos();
	}
	
	public Tamanho getTamanho() {
		return tamanhoAdapter.getTamanho();
	}
	 
}
