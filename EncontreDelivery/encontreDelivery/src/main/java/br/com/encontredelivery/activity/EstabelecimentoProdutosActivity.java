package br.com.encontredelivery.activity;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.ProdutoAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.listener.ListenerIcon;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.Produto;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.ProdutoRest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EstabelecimentoProdutosActivity extends ActionBarActivity {
	private NetworkImageView nivImagem;
	private TextView txtNome;
	private ImageView imgAbertoFechado;
	private TextView txtSegmentos;
	private TextView txtCategoria;
	private ListView lvProdutos;
	private LinearLayout llVazioProdutos;
	
	private TextView txtBadge;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private Cliente cliente;
	private Empresa empresa;
	private Categoria categoria;
	private List<ProdutoPedido> listaProdutosPedido;
	
	private Produto produto;
	private List<Produto> listaProdutos;
	private ProdutoAdapter produtoAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarProdutos;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;	
	
	private static final int REQUEST_PRODUTO_ACTIVITY  = 0;
	private static final int REQUEST_CARRINHO_ACTIVITY = 1;
	
	private static final String STATE_LISTA_PRODUTOS_PEDIDO = "listaProdutosPedido";
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurante_produtos);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		nivImagem 		  = (NetworkImageView) findViewById(R.id.nivImagem);
		txtNome           = (TextView) findViewById(R.id.txtNome);
		imgAbertoFechado  = (ImageView) findViewById(R.id.imgAbertoFechado);
		txtSegmentos      = (TextView) findViewById(R.id.txtSegmentos);
		txtCategoria      = (TextView) findViewById(R.id.txtCategoria);
		lvProdutos      = (ListView) findViewById(R.id.lvProdutos);
		llVazioProdutos = (LinearLayout) findViewById(R.id.llVazioProdutos);
		
		
		Bundle extras 		= getIntent().getExtras();
		cliente       		= (Cliente) extras.getSerializable("cliente");
		empresa             = (Empresa) extras.getSerializable("empresa");
		categoria           = (Categoria) extras.getSerializable("categoria");
		listaProdutosPedido = (ArrayList<ProdutoPedido>) extras.getSerializable("listaProdutosPedido");
		
		requestQueue = Volley.newRequestQueue(this);
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
	    
	    String segmentos = "";
		String separador = "";
		
		for (Segmento segmento: empresa.getListaSegmentos()) {
			segmentos += separador + segmento.getDescricao();
			separador  = "/";
		}
		
		txtNome.setText(empresa.getNome());
		txtSegmentos.setText(segmentos);
		txtCategoria.setText(categoria.getDescricao());
		
		if (empresa.isAberto()) {
			imgAbertoFechado.setImageResource(R.drawable.ball_verde);
		} else {
			imgAbertoFechado.setImageResource(R.drawable.ball_vermelha);
		}
		
		nivImagem.setImageUrl(empresa.getUrlImagem(), imageLoader);
		nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		nivImagem.setErrorImageResId(R.drawable.ic_launcher);	
		
		setTitle(empresa.getNome());
		
		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(EstabelecimentoProdutosActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCarregarProdutos = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				produtoAdapter = new ProdutoAdapter(EstabelecimentoProdutosActivity.this, listaProdutos);
				lvProdutos.setAdapter(produtoAdapter);
				
				if (listaProdutos.isEmpty()) {
					llVazioProdutos.setVisibility(View.VISIBLE);
					lvProdutos.setVisibility(View.GONE);
				} else {
					llVazioProdutos.setVisibility(View.GONE);
					lvProdutos.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();
			}
		};
		
		carregarProdutos();
		lvProdutos.setOnItemClickListener(clickLvProdutos());
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putSerializable(STATE_LISTA_PRODUTOS_PEDIDO, (ArrayList<ProdutoPedido>) listaProdutosPedido);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@SuppressWarnings("unchecked")
	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    listaProdutosPedido = (ArrayList<ProdutoPedido>) savedInstanceState.getSerializable(STATE_LISTA_PRODUTOS_PEDIDO);
	    setBadget(listaProdutosPedido.size());
	}
	
	@SuppressWarnings("unchecked")
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == REQUEST_PRODUTO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
			}
		}
		
		if (requestCode == REQUEST_CARRINHO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				long idPedido =  data.getExtras().getLong("idPedido");
				if (idPedido != -1) {
					setResult(RESULT_OK, data);
					finish();					
				} else {
					listaProdutosPedido = (ArrayList<ProdutoPedido>) data.getExtras().getSerializable("listaProdutosPedido");
					setBadget(listaProdutosPedido.size());
				}
			}
		}
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.estabelecimento_categorias_produtos, menu);

        MenuItem itemCarrinho = menu.findItem(R.id.carrinho);
        final View menuCarrinho = MenuItemCompat.getActionView(itemCarrinho);
        txtBadge = (TextView) menuCarrinho.findViewById(R.id.txtBadge);
        new ListenerIcon(menuCarrinho, getString(R.string.carrinho)) {
            @Override
            public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("empresa", empresa);
				extras.putSerializable("categoria", categoria);
				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
				Intent intent = new Intent(EstabelecimentoProdutosActivity.this, CarrinhoActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CARRINHO_ACTIVITY);			
            }
        };
        
        setBadget(listaProdutosPedido.size());
        
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.informacoes) {
			Bundle extras = new Bundle();
			extras.putSerializable("empresa", empresa);
			Intent intent = new Intent(this, InformacoesActivity.class);
			intent.putExtras(extras);
			startActivity(intent);
		}
		
		if (id == android.R.id.home) {
			voltar();
		}
		
		return super.onOptionsItemSelected(item);
	}	   
	
	@Override
	public void onBackPressed() {
		voltar();
	}		
	
	private OnItemClickListener clickLvProdutos() {
		OnItemClickListener clickLvProdutos = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				produto = listaProdutos.get(position);
				
				Bundle extras = new Bundle();
				extras.putSerializable("empresa", empresa);
				extras.putSerializable("produto", produto);
				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
				Intent intent = new Intent(EstabelecimentoProdutosActivity.this, ProdutosActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_PRODUTO_ACTIVITY);			
			}
		};
		
		return clickLvProdutos;
	}	
	
	
	private void carregarProdutos() {
        progressoDialog = new ProgressoDialog(this);
		progressoDialog.setMessage("Aguarde. Carregando produtos...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        ProdutoRest produtoRest = new ProdutoRest();
		        try {
		        	listaProdutos = produtoRest.getProdutos(categoria.getId());
		        	Util.messagem("", handlerCarregarProdutos);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
    private void setBadget(final int badge) {
        if (txtBadge == null) return;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (badge == 0)
                	txtBadge.setVisibility(View.INVISIBLE);
                else {
                	txtBadge.setVisibility(View.VISIBLE);
                	txtBadge.setText(Integer.toString(badge));
                }
            }
        });
    } 
    
    private void voltar() {
		Bundle extras = new Bundle();
		extras.putLong("idPedido", -1);
		extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
		Intent data = new Intent();
		data.putExtras(extras);

		setResult(RESULT_OK, data);
		finish();
	}    
   
}
