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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.activity.RestauranteActivity;
import br.com.encontredelivery.activity.RestauranteProdutosActivity;
import br.com.encontredelivery.adapter.CategoriaAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.CategoriaRest;

public class RestauranteCategoriaFragment extends Fragment {
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

	private static final int REQUEST_ESTABELECIMENTO_PRODUTOS_ACTIVITY = 0;
	private static final int REQUEST_CARRINHO_ACTIVITY                 = 1;

	private static final String STATE_LISTA_PRODUTOS_PEDIDO = "listaProdutosPedido";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_restaurante_categorias, container, false);

		nivImagem 		  = (NetworkImageView) view.findViewById(R.id.nivImagem);
		txtNome           = (TextView) view.findViewById(R.id.txtNome);
		imgAbertoFechado  = (ImageView) view.findViewById(R.id.imgAbertoFechado);
		txtSegmentos      = (TextView) view.findViewById(R.id.txtSegmentos);
		txtEntrega     	  = (TextView) view.findViewById(R.id.txtEntrega);
		lvCategorias      = (ListView) view.findViewById(R.id.lvCategorias);
		llVazioCategorias = (LinearLayout) view.findViewById(R.id.llVazioCategorias);

		Bundle extras       = getActivity().getIntent().getExtras();
		cliente             = (Cliente) extras.getSerializable("cliente");
		empresa             = (Empresa) extras.getSerializable("empresa");
		listaProdutosPedido = new ArrayList<ProdutoPedido>();

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

		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		txtNome.setText(empresa.getNome());
		txtSegmentos.setText(segmentos);
		txtEntrega.setText("R$ " + decimalFormat.format(empresa.getTaxaEntrega()).replace(".", ",") + " / " + empresa.getTempoMedio());

		if (empresa.isAberto()) {
			imgAbertoFechado.setImageResource(R.drawable.ball_verde);
		} else {
			imgAbertoFechado.setImageResource(R.drawable.ball_vermelha);
		}

		nivImagem.setImageUrl(empresa.getUrlImagem(), imageLoader);
		nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		nivImagem.setErrorImageResId(R.drawable.ic_launcher);

		lvCategorias.setOnItemClickListener(clickLvCategorias());

		return view;
	}

	private AdapterView.OnItemClickListener clickLvCategorias() {
		AdapterView.OnItemClickListener clickLvCategorias = new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				categoria = listaCategorias.get(position);

				((RestauranteActivity) getActivity()).trocarCardapioFragment(categoria);

//				Bundle extras = new Bundle();
//				extras.putSerializable("cliente", cliente);
//				extras.putSerializable("empresa", empresa);
//				extras.putSerializable("categoria", categoria);
//				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
//				Intent intent = new Intent(getActivity(), RestauranteProdutosActivity.class);
//				intent.putExtras(extras);
//				startActivityForResult(intent, REQUEST_ESTABELECIMENTO_PRODUTOS_ACTIVITY);
			}
		};

		return clickLvCategorias;
	}

	public void carregarCategorias(List<Categoria> listaCategorias) {
		this.listaCategorias = listaCategorias;

		categoriaAdapter = new CategoriaAdapter(getActivity(), listaCategorias);
		lvCategorias.setAdapter(categoriaAdapter);

		if (listaCategorias.isEmpty()) {
			llVazioCategorias.setVisibility(View.VISIBLE);
			lvCategorias.setVisibility(View.GONE);
		} else {
			llVazioCategorias.setVisibility(View.GONE);
			lvCategorias.setVisibility(View.VISIBLE);
		}
	}

}