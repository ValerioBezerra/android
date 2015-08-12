package br.com.encontredelivery.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.CategoriaAdapter;
import br.com.encontredelivery.dialog.ConfirmacaoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.listener.ListenerIcon;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.CategoriaRest;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class EstabelecimentoCategoriasActivity extends ActionBarActivity {
	private NetworkImageView nivImagem;
	private TextView txtNome;
	private ImageView imgAbertoFechado;
	private TextView txtSegmentos;
	private TextView txtEntrega;
	private ListView lvCategorias;
	private LinearLayout llVazioCategorias;
	
	private TextView txtBadge;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private Cliente cliente;
	private Empresa empresa;
	private List<ProdutoPedido> listaProdutosPedido;
	
	private Categoria categoria;
	private List<Categoria> listaCategorias;
	private CategoriaAdapter categoriaAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarCategorias;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;	
		
	private static final int REQUEST_ESTABELECIMENTO_PRODUTOS_ACTIVITY = 0;
	private static final int REQUEST_CARRINHO_ACTIVITY                 = 1;
	
	private static final String STATE_LISTA_PRODUTOS_PEDIDO = "listaProdutosPedido";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_estabelecimento_categorias);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		nivImagem 		  = (NetworkImageView) findViewById(R.id.nivImagem);
		txtNome           = (TextView) findViewById(R.id.txtNome);
		imgAbertoFechado  = (ImageView) findViewById(R.id.imgAbertoFechado);
		txtSegmentos      = (TextView) findViewById(R.id.txtSegmentos);
		txtEntrega     = (TextView) findViewById(R.id.txtEntrega);
		lvCategorias      = (ListView) findViewById(R.id.lvCategorias);
		llVazioCategorias = (LinearLayout) findViewById(R.id.llVazioCategorias);
		
		Bundle extras       = getIntent().getExtras();
		cliente             = (Cliente) extras.getSerializable("cliente");
		empresa             = (Empresa) extras.getSerializable("empresa");
		listaProdutosPedido = new ArrayList<ProdutoPedido>();
		
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
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		txtNome.setText(empresa.getNome());
		txtSegmentos.setText(segmentos);
		txtEntrega.setText("R$ " + decimalFormat.format(empresa.getTaxaEntrega()).replace(".", ",") + " / " + empresa.getTempoMedio());
		
		if (empresa.isAberto()) {
			imgAbertoFechado.setImageResource(R.drawable.bola_verde);
		} else {
			imgAbertoFechado.setImageResource(R.drawable.bola_vermelha);
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
				
				erroAvisoDialog = new ErroAvisoDialog(EstabelecimentoCategoriasActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCarregarCategorias = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				categoriaAdapter = new CategoriaAdapter(EstabelecimentoCategoriasActivity.this, listaCategorias);
				lvCategorias.setAdapter(categoriaAdapter);
				
				if (listaCategorias.isEmpty()) {
					llVazioCategorias.setVisibility(View.VISIBLE);
					lvCategorias.setVisibility(View.GONE);
				} else {
					llVazioCategorias.setVisibility(View.GONE);
					lvCategorias.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();
			}
		};
		
		carregarCategorias();
		lvCategorias.setOnItemClickListener(clickLvCategorias());
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
		if (requestCode == REQUEST_ESTABELECIMENTO_PRODUTOS_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				long idPedido =  data.getExtras().getLong("idPedido");
				if (idPedido != -1) {
					setResult(RESULT_OK, data);
					finish();
				} else {				
					listaProdutosPedido            = (ArrayList<ProdutoPedido>) data.getExtras().getSerializable("listaProdutosPedido");
					boolean chamarCarrinhoActivity =  data.getExtras().getBoolean("chamarCarrinhoActivity");
					setBadget(listaProdutosPedido.size());
					
					if (chamarCarrinhoActivity) {
						chamarCarrinhoActivity();
					}
				}
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
            	chamarCarrinhoActivity();
            }
        };
        
        setBadget(listaProdutosPedido.size());
        
        return super.onCreateOptionsMenu(menu);
    }
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == android.R.id.home) {
			return (!voltar());
		}
		
		if (id == R.id.informacoes) {
			Bundle extras = new Bundle();
			extras.putSerializable("empresa", empresa);
			Intent intent = new Intent(this, InformacoesActivity.class);
			intent.putExtras(extras);
			startActivity(intent);
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
	   if (voltar()) {
		   finish();
	   }
	}	
	private OnItemClickListener clickLvCategorias() {
		OnItemClickListener clickLvCategorias = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				categoria = listaCategorias.get(position);
				
				Bundle extras = new Bundle();
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("empresa", empresa);
				extras.putSerializable("categoria", categoria);
				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
				Intent intent = new Intent(EstabelecimentoCategoriasActivity.this, EstabelecimentoProdutosActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_ESTABELECIMENTO_PRODUTOS_ACTIVITY);			
			}
		};
		
		return clickLvCategorias;
	}	
	
	private void carregarCategorias() {
        progressoDialog = new ProgressoDialog(this);
		progressoDialog.setMessage("Aguarde. Carregando categorias...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        CategoriaRest categoriaRest = new CategoriaRest();
		        try {
		        	listaCategorias = categoriaRest.getCategorias(empresa.getId());
		        	Util.messagem("", handlerCarregarCategorias);
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
    
    private boolean voltar() {
    	boolean voltar = true;
    	
    	if (!listaProdutosPedido.isEmpty()) {
    		voltar = false;
    		
    		ConfirmacaoDialog confirmacaoDialog = new ConfirmacaoDialog(this);
			confirmacaoDialog.setTitle("Atenção");
			confirmacaoDialog.setMessage("Há produto(s) adicionado(s) ao carrinho.\n" +
					                     "Realmente deseja sair?");
			
			Button btnSim = (Button) confirmacaoDialog.findViewById(R.id.btnSim);
			btnSim.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {					
					finish();
				}
			});
			
			confirmacaoDialog.show();
    	} else {
    		finish();
    	}
    	
    	return voltar;
    }
    
    private void chamarCarrinhoActivity() {
		Bundle extras = new Bundle();
		extras.putSerializable("cliente", cliente);
		extras.putSerializable("empresa", empresa);
		extras.putSerializable("categoria", categoria);
		extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
		Intent intent = new Intent(EstabelecimentoCategoriasActivity.this, CarrinhoActivity.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_CARRINHO_ACTIVITY);			
    }
	    
}
