package br.com.encontredelivery.fragment;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.activity.ProdutosActivity;
import br.com.encontredelivery.activity.RestauranteActivity;
import br.com.encontredelivery.adapter.ProdutoAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Produto;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.ProdutoRest;


public class RestauranteProdutosFragment extends Fragment {
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

	public RestauranteProdutosFragment() {}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurante_produtos, container, false);

		nivImagem 		  = (NetworkImageView) view.findViewById(R.id.nivImagem);
		txtNome           = (TextView) view.findViewById(R.id.txtNome);
		imgAbertoFechado  = (ImageView) view.findViewById(R.id.imgAbertoFechado);
		txtSegmentos      = (TextView) view.findViewById(R.id.txtSegmentos);
		txtCategoria      = (TextView) view.findViewById(R.id.txtCategoria);
		lvProdutos        = (ListView) view.findViewById(R.id.lvProdutos);
		llVazioProdutos   = (LinearLayout) view.findViewById(R.id.llVazioProdutos);

		Bundle extras 		= getActivity().getIntent().getExtras();
		cliente       		= (Cliente) extras.getSerializable("cliente");
		empresa             = (Empresa) extras.getSerializable("empresa");
		listaProdutosPedido = (ArrayList<ProdutoPedido>) extras.getSerializable("listaProdutosPedido");

		Bundle arguments = getArguments();
		categoria        = (Categoria) arguments.getSerializable("categoria");

		requestQueue = Volley.newRequestQueue(getActivity());
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

		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;

				erroAvisoDialog = new ErroAvisoDialog(getActivity());
				erroAvisoDialog.setTitle("Erro");
				erroAvisoDialog.setMessage(mensagem);
				erroAvisoDialog.show();
			}
		};

		handlerCarregarProdutos = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				produtoAdapter = new ProdutoAdapter(getActivity(), listaProdutos);
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
		
		return view;
	}

	private AdapterView.OnItemClickListener clickLvProdutos() {
		return new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
									long id) {
				produto = listaProdutos.get(position);

				Bundle extras = new Bundle();
				extras.putSerializable("empresa", empresa);
				extras.putSerializable("produto", produto);
				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) ((RestauranteActivity) getActivity()).getListaProdutosPedido());
				Intent intent = new Intent(getActivity(), ProdutosActivity.class);
				intent.putExtras(extras);
				getActivity().startActivityForResult(intent, RestauranteActivity.REQUEST_PRODUTO_ACTIVITY);
			}
		};
	}

	private void carregarProdutos() {
		progressoDialog = new ProgressoDialog(getActivity());
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
}