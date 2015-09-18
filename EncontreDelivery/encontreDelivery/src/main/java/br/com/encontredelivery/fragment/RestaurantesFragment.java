package br.com.encontredelivery.fragment;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


import br.com.encontredelivery.activity.RestauranteActivity;
import br.com.encontredelivery.activity.NavigationDrawerActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EmpresaAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.FiltroEstabelecimentoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.EmpresaRest;
import br.com.encontredelivery.webservice.SegmentoRest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnCloseListener;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class RestaurantesFragment extends Fragment implements OnQueryTextListener, OnCloseListener {
	private TextView txtEstabelecimentoAbertos;
	private TextView txtEstabelecimentoFechados;
	private ListView lvEstabelecimentos;
	private LinearLayout llVazioEstabelecimentos;
	
	private Cliente  cliente;
	private Endereco endereco;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private Empresa empresa;
	private List<Empresa> listaEmpresas;
	private EmpresaAdapter empresaAdapter;
	private List<Segmento> listaSegmentos;
	
	private Handler handlerErros;
	private Handler handlerCarregarEmpresas;
	private Handler handlerCarregarSegmentos;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	private FiltroEstabelecimentoDialog filtroEstabelecimentoDialog;
	
	private boolean segmentosCarregados;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setHasOptionsMenu(true);
	    
	    requestQueue        = Volley.newRequestQueue(getActivity());
	    segmentosCarregados = false;
	    
	    imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
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
	    
	    handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCarregarEmpresas = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				int quantidadeAbertas  = 0;
				int quantidadeFechadas = 0;;
				for (Empresa empresa: listaEmpresas) {
					if (empresa.isAberto()) {
						quantidadeAbertas++;
					} else {
						quantidadeFechadas++;
					}
				}
				
				txtEstabelecimentoAbertos.setText("Aberto: " + quantidadeAbertas);
				txtEstabelecimentoFechados.setText("Fechado: " + quantidadeFechadas);
				
				
				empresaAdapter = new EmpresaAdapter(getActivity(), listaEmpresas, imageLoader);
				lvEstabelecimentos.setAdapter(empresaAdapter);
				
				if (listaEmpresas.isEmpty()) {
					llVazioEstabelecimentos.setVisibility(View.VISIBLE);				
					lvEstabelecimentos.setVisibility(View.GONE);				
				} else {
					llVazioEstabelecimentos.setVisibility(View.GONE);				
					lvEstabelecimentos.setVisibility(View.VISIBLE);				
				}
				
				if (!segmentosCarregados) {
					carregarSegmentos();
				} else {
					progressoDialog.dismiss();
				}
			}
		};
		
		handlerCarregarSegmentos = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				segmentosCarregados         = true;
				filtroEstabelecimentoDialog = new FiltroEstabelecimentoDialog(getActivity(), listaSegmentos);
				
				progressoDialog.dismiss();
			}
		};	    
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_lista_estabelecimentos, container, false);
		
		txtEstabelecimentoAbertos  = (TextView) view.findViewById(R.id.txtEstabelecimentoAbertos);
		txtEstabelecimentoFechados = (TextView) view.findViewById(R.id.txtEstabelecimentoFechados);
		lvEstabelecimentos         = (ListView) view.findViewById(R.id.lvEstabelecimentos);
		llVazioEstabelecimentos    = (LinearLayout) view.findViewById(R.id.llVazioEstabelecimentos);
		
		txtEstabelecimentoAbertos.setText("");
		txtEstabelecimentoFechados.setText("");
		
		Bundle extras = getActivity().getIntent().getExtras();
		cliente  	  = (Cliente) extras.getSerializable("cliente");
		endereco 	  = NavigationDrawerActivity.endereco;
		
		listaEmpresas   = new ArrayList<Empresa>();
		listaSegmentos  = new ArrayList<Segmento>();
        progressoDialog = new ProgressoDialog(getActivity());
        erroAvisoDialog = new ErroAvisoDialog(getActivity());
		
		if (savedInstanceState == null) { 
			carregarEmpresas("", "+");
		} else {
			listaEmpresas  = (ArrayList<Empresa>) savedInstanceState.getSerializable("listaEmpresas");
			empresaAdapter = new EmpresaAdapter(getActivity(), listaEmpresas, imageLoader);
			lvEstabelecimentos.setAdapter(empresaAdapter);
			
			listaSegmentos = (ArrayList<Segmento>) savedInstanceState.getSerializable("listaSegmentos");
		}
		
		lvEstabelecimentos.setOnItemClickListener(clickLvEstabilecimentos());
		 
		return view;
	}
	
    @Override
	public void onSaveInstanceState(Bundle outState) {  
    	super.onSaveInstanceState(outState);  
    	outState.putSerializable("listaEmpresas", (ArrayList<Empresa>) listaEmpresas);  
    	outState.putSerializable("listaSegmentos", (ArrayList<Segmento>) listaSegmentos);
    }  	
    
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.restaurantes, menu);
        
        MenuItem menuItem = menu.findItem(R.id.acao_pesquisar);
        
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
		searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setIconifiedByDefault(true);
        searchView.setOnQueryTextListener(this);
        searchView.setOnCloseListener(this);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.acao_filtros) {
			Button btnFiltrar = (Button) filtroEstabelecimentoDialog.findViewById(R.id.btnFiltrar);
			btnFiltrar.setOnClickListener(new OnClickListener() {				
				@Override
				public void onClick(View v) {
					filtroEstabelecimentoDialog.dismiss();
					carregarEmpresas("", segmentosFiltro());
				}
			});
			
			filtroEstabelecimentoDialog.show();
		}
		
		return super.onOptionsItemSelected(item);
	}		
	

	@Override
	public boolean onQueryTextChange(String newText) {return false;}

	@Override
	public boolean onQueryTextSubmit(String query) {
		carregarEmpresas(query, segmentosFiltro());
		return false;
	}
	
	@Override
	public boolean onClose() {
		carregarEmpresas("", segmentosFiltro());
		return false;
	}
	
	private OnItemClickListener clickLvEstabilecimentos() {
		OnItemClickListener clickLvEstabilecimentos = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				empresa = listaEmpresas.get(position);
				
				Bundle extras = new Bundle();
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("empresa", empresa);
				Intent intent = new Intent(getActivity(), RestauranteActivity.class);
				intent.putExtras(extras);
				getActivity().startActivityForResult(intent, NavigationDrawerActivity.REQUEST_ESTABELECIMENTOS_CATEGORIA_ACTIVITY);
			}
		};
		
		return clickLvEstabilecimentos;
	}
	
	private void carregarEmpresas(final String nome, final String whereSegmentos) {
		progressoDialog.setMessage("Aguarde. Carregando restaurantes...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        EmpresaRest empresaRest = new EmpresaRest();
		        try {
		        	listaEmpresas = empresaRest.getEmpresas(nome, endereco.getCep(), endereco.getBairro().getCidade().getId(), endereco.getBairro().getId(), whereSegmentos);
		        	Util.messagem("", handlerCarregarEmpresas);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private void carregarSegmentos() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				SegmentoRest segmentoRest = new SegmentoRest();
		        try {
		        	listaSegmentos = segmentoRest.getSegmentos();
		        	Util.messagem("", handlerCarregarSegmentos);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private String segmentosFiltro() {
		int quantidade  = 0;
		String retorno   = "";
		String separador = "";
		for (Segmento segmento: listaSegmentos) {
			if (segmento.isEscolhido()) {
				retorno  += separador + segmento.getId();
				separador = "+";
				quantidade++;
			}
		}
		
		if ((quantidade > 0) && (quantidade != listaSegmentos.size())) {
			return retorno;
		} else {
			return "+";
		}
	}

}