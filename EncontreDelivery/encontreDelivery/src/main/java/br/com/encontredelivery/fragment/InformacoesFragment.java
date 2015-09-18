package br.com.encontredelivery.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.FormaPagamentoAdapter;
import br.com.encontredelivery.adapter.HorarioAdapter;
import br.com.encontredelivery.dialog.FoneDialog;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.Horario;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.view.NoScrollListView;

public class InformacoesFragment extends Fragment {
	private ScrollView scrollView;
	private NetworkImageView nivImagem;
	private TextView txtNome;
	private ImageView imgAbertoFechado;
	private TextView txtSegmentos;
	private TextView txtFone;
	private FloatingActionButton fabLigar;
	private TextView txtDetalhamento;
	private NoScrollListView lvHorarios;
	private LinearLayout llVazioHorarios;
	private NoScrollListView lvFormasPagamento;
	private LinearLayout llVazioFormasPagamento;

	private RequestQueue requestQueue;
	private ImageLoader imageLoader;

	private Empresa empresa;

	private HorarioAdapter horarioAdapter;

	private ImageLoader imageLoaderFormaPagamento;
	private FormaPagamentoAdapter formaPagamentoAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_informacoes, container, false);

		scrollView			   = (ScrollView) view.findViewById(R.id.scrollView);
		nivImagem			   = (NetworkImageView) view.findViewById(R.id.nivImagem);
		txtNome				   = (TextView) view.findViewById(R.id.txtNome);
		imgAbertoFechado	   = (ImageView) view.findViewById(R.id.imgAbertoFechado);
		txtSegmentos		   = (TextView) view.findViewById(R.id.txtSegmentos);
		txtFone 			   = (TextView) view.findViewById(R.id.txtFone);
		fabLigar               = (FloatingActionButton) view.findViewById(R.id.fabLigar);
		txtDetalhamento 	   = (TextView) view.findViewById(R.id.txtDetalhamento);
		lvHorarios             = (NoScrollListView) view.findViewById(R.id.lvHorarios);
		llVazioHorarios        = (LinearLayout) view.findViewById(R.id.llVazioHorarios);
		lvFormasPagamento      = (NoScrollListView) view.findViewById(R.id.lvFormasPagamento);
		llVazioFormasPagamento = (LinearLayout) view.findViewById(R.id.llVazioFormasPagamento);

		Bundle extras = getActivity().getIntent().getExtras();
		empresa = (Empresa) extras.getSerializable("empresa");

		requestQueue = Volley.newRequestQueue(getActivity());
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

		imageLoaderFormaPagamento = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
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

		for (Segmento segmento : empresa.getListaSegmentos()) {
			segmentos += separador + segmento.getDescricao();
			separador = " / ";
		}

		txtNome.setText(empresa.getNome());
		txtSegmentos.setText(segmentos);

		String fones         = "";
		String separadorFone = "";
		for (String fone: empresa.getListaFones()) {
			fones         += separadorFone + fone;
			separadorFone  = "\n";
		}
		txtFone.setText(fones);

		txtDetalhamento.setText(empresa.getDetalhamento());

		if (empresa.isAberto()) {
			imgAbertoFechado.setImageResource(R.drawable.ball_verde);
		} else {
			imgAbertoFechado.setImageResource(R.drawable.ball_vermelha);
		}

		nivImagem.setImageUrl(empresa.getUrlImagem(), imageLoader);
		nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		nivImagem.setErrorImageResId(R.drawable.ic_launcher);

		fabLigar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (empresa != null) {
					if (empresa.getListaFones().size() == 1) {
						Intent callIntent = new Intent(Intent.ACTION_DIAL);
						callIntent.setData(Uri.parse("tel:" + Retorno.getSomenteNumeros(empresa.getListaFones().get(0))));
						startActivity(callIntent);
					} else {
						FoneDialog foneDialog = new FoneDialog(getActivity());
						foneDialog.setListaFones(empresa.getListaFones());
						foneDialog.show();
					}
				}

			}
		});

		return view;
	}

	public void carregarHorarios(List<Horario> listaHorarios) {
		horarioAdapter = new HorarioAdapter(getActivity(), listaHorarios);
		lvHorarios.setAdapter(horarioAdapter);

		if (listaHorarios.isEmpty()) {
			llVazioHorarios.setVisibility(View.VISIBLE);
			lvHorarios.setVisibility(View.GONE);
		} else {
			llVazioHorarios.setVisibility(View.GONE);
			lvHorarios.setVisibility(View.VISIBLE);
		}
	}

	public void carregarFormasPagamento(List<FormaPagamento> listaFormasPagamento) {
		formaPagamentoAdapter = new FormaPagamentoAdapter(getActivity(), listaFormasPagamento, imageLoaderFormaPagamento);
		lvFormasPagamento.setAdapter(formaPagamentoAdapter);

		if (listaFormasPagamento.isEmpty()) {
			llVazioFormasPagamento.setVisibility(View.VISIBLE);
			lvFormasPagamento.setVisibility(View.GONE);
		} else {
			llVazioFormasPagamento.setVisibility(View.GONE);
			lvFormasPagamento.setVisibility(View.VISIBLE);
		}
	}
}

