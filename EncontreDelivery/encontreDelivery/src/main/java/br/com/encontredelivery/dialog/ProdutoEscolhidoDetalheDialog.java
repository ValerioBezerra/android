package br.com.encontredelivery.dialog;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.util.LruBitmapCache;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ProdutoEscolhidoDetalheDialog extends Dialog {
	
	private NetworkImageView nivImagem; 
	private TextView txtDescricao;
	private TextView txtDetalhamento;
	private Button btnOK;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private android.view.View.OnClickListener clickOk = new android.view.View.OnClickListener() {
		@Override
		public void onClick(View arg0) {
			dismiss();
		}
	};

	public ProdutoEscolhidoDetalheDialog(Context context, ProdutoEscolhido produtoEscolhido) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_produto_escolhido_detalhe);
        
		requestQueue = Volley.newRequestQueue(context);
	    imageLoader  = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
	    	private LruBitmapCache lruCache = new LruBitmapCache();
			
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				lruCache.put(url, bitmap);
			}
			
			@Override
			public Bitmap getBitmap(String url) {
				return lruCache.get(url);
			}
		});	        
        
        View view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);    
          
        nivImagem       = (NetworkImageView) findViewById(R.id.nivImagem);    
        txtDescricao    = (TextView) findViewById(R.id.txtDescricao);
        txtDetalhamento = (TextView) findViewById(R.id.txtDetalhamento);        
        
        btnOK = (Button) findViewById(R.id.btnOK);
    	btnOK.setOnClickListener(clickOk);
    	
		nivImagem.setVisibility(View.GONE);
		if (!produtoEscolhido.getProduto().getUrlImagem().trim().equals("")) {
			nivImagem.setVisibility(View.VISIBLE);
			nivImagem.setImageUrl(produtoEscolhido.getProduto().getUrlImagem(), imageLoader);
			nivImagem.setDefaultImageResId(android.R.color.transparent);
			nivImagem.setErrorImageResId(android.R.color.transparent);
		}	
		
		txtDescricao.setText(produtoEscolhido.getProduto().getDescricao());
		txtDetalhamento.setText(produtoEscolhido.getProduto().getDetalhamento());
		
		if (produtoEscolhido.getProduto().getDetalhamento().trim().equals("")) {
			txtDetalhamento.setVisibility(View.GONE);
		}    	

        setCancelable(false);
	}
}
