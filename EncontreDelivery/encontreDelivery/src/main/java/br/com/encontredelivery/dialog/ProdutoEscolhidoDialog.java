package br.com.encontredelivery.dialog;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.ProdutoEscolhidoAdapter;
import br.com.encontredelivery.model.Produto;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.model.Tamanho;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

public class ProdutoEscolhidoDialog extends Dialog {
	
	private Context context;    
	private TextView txtTitulo;    
	private ListView lvProdutoEscolhidos;
	private View view; 
	
	private Produto produto;
	private ProdutoEscolhidoAdapter produtoEscolhidoAdapter;
	
	public ProdutoEscolhidoDialog(Context context, Produto produto) {
		super(context);
		this.context = context;
		this.produto = produto;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_produto_escolhido);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
        txtTitulo   = (TextView) findViewById(R.id.txtTitulo); 
        lvProdutoEscolhidos = (ListView) findViewById(R.id.lvProdutosEscolhidos);

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
	 
	 public void setListaProdutoEscolhidos(List<ProdutoEscolhido> listaProdutoEscolhidos, Tamanho tamanho) {
		 this.produtoEscolhidoAdapter = new ProdutoEscolhidoAdapter(context, produto, listaProdutoEscolhidos, tamanho);
		 lvProdutoEscolhidos.setAdapter(produtoEscolhidoAdapter);
	 }

	public List<ProdutoEscolhido> getListaProdutoEscolhidos() {
		return produtoEscolhidoAdapter.getListaProdutosEscolhidos();
	}
	
	public ProdutoEscolhido getProdutoEscolhido() {
		return produtoEscolhidoAdapter.getProdutoEscolhido();
	}
	
	public void setPrecoAplicado(Produto produto) {
		produtoEscolhidoAdapter.setPrecoAplicado(produto);
	}
	
	public double getPrecoAplicado() {
		return produtoEscolhidoAdapter.getPrecoAplicado();
	}
		 
}
