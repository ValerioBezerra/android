package br.com.encontredelivery.activity;

import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.FormaPagamentoAdapter;
import br.com.encontredelivery.adapter.HorarioAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Horario;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.view.NoScrollListView;
import br.com.encontredelivery.webservice.FormaPagamentoRest;
import br.com.encontredelivery.webservice.HorarioRest;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class InformacoesActivity extends ActionBarActivity {
	private NetworkImageView nivImagem;
	private TextView txtNome;
	private ImageView imgAbertoFechado;
	private TextView txtDetalhamento;
	private TextView txtSegmentos;
	private TextView txtCEPLogradouroNumero;
	private TextView txtBairroCidadeUF;
	private TextView txtFone;
	private NoScrollListView lvHorarios;
	private LinearLayout llVazioHorarios;
	private NoScrollListView lvFormasPagamento;
	private LinearLayout llVazioFormasPagamento;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private Empresa empresa;
	
	private List<Horario> listaHorarios;
	private HorarioAdapter horarioAdapter;
	
	private List<FormaPagamento> listaFormasPagamento;
	private ImageLoader imageLoaderFormaPagamento;
	private FormaPagamentoAdapter formaPagamentoAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarHorarios;
	private Handler handlerCarregarFormasPagamento;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_informacoes);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		nivImagem 		       = (NetworkImageView) findViewById(R.id.nivImagem);
		txtNome                = (TextView) findViewById(R.id.txtNome);
		imgAbertoFechado       = (ImageView) findViewById(R.id.imgAbertoFechado);
		txtDetalhamento        = (TextView) findViewById(R.id.txtDetalhamento);
		txtSegmentos           = (TextView) findViewById(R.id.txtSegmentos);
		txtCEPLogradouroNumero = (TextView) findViewById(R.id.txtCEPLogradouroNumero);
		txtBairroCidadeUF      = (TextView) findViewById(R.id.txtBairroCidadeUF);
		txtFone				   = (TextView) findViewById(R.id.txtFone);
		lvHorarios			   = (NoScrollListView) findViewById(R.id.lvHorarios);	
		llVazioHorarios        = (LinearLayout) findViewById(R.id.llVazioHorarios);
		lvFormasPagamento      = (NoScrollListView) findViewById(R.id.lvFormasPagamento);
		llVazioFormasPagamento = (LinearLayout) findViewById(R.id.llVazioFormasPagamento);
		
		Bundle extras = getIntent().getExtras();
		empresa       = (Empresa) extras.getSerializable("empresa");
		
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
	    
	    imageLoaderFormaPagamento  = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
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
		txtDetalhamento.setText(empresa.getDetalhamento());
		txtSegmentos.setText(segmentos);
		txtCEPLogradouroNumero.setText("(" + Retorno.getMascaraCep(empresa.getEndereco().getCep() + ") " + empresa.getEndereco().getLogradouro() + ", " + empresa.getEndereco().getNumero()));
		txtBairroCidadeUF.setText(empresa.getEndereco().getBairro().getNome() + ", " + empresa.getEndereco().getBairro().getCidade().getNome() + " - " + empresa.getEndereco().getBairro().getCidade().getUf());


		String fones         = "";
		String separadorFone = "";
		for (String fone: empresa.getListaFones()) {
			fones         += separadorFone + fone;
			separadorFone  = "\n";
		}
		txtFone.setText(fones);

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
				
				erroAvisoDialog = new ErroAvisoDialog(InformacoesActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCarregarHorarios = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				horarioAdapter = new HorarioAdapter(InformacoesActivity.this, listaHorarios);
				lvHorarios.setAdapter(horarioAdapter);
				
				if (listaHorarios.isEmpty()) {
					llVazioHorarios.setVisibility(View.VISIBLE);
					lvHorarios.setVisibility(View.GONE);
				} else {
					llVazioHorarios.setVisibility(View.GONE);
					lvHorarios.setVisibility(View.VISIBLE);
				}
				
				carregarFormasPagamento();
			}
		};
		
		handlerCarregarFormasPagamento = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				formaPagamentoAdapter = new FormaPagamentoAdapter(InformacoesActivity.this, listaFormasPagamento, imageLoaderFormaPagamento);
				lvFormasPagamento.setAdapter(formaPagamentoAdapter);
				
				if (listaFormasPagamento.isEmpty()) {
					llVazioFormasPagamento.setVisibility(View.VISIBLE);
					lvFormasPagamento.setVisibility(View.GONE);
				} else {
					llVazioFormasPagamento.setVisibility(View.GONE);
					lvFormasPagamento.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();
			}
		};
		
		carregarHorarios();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private void carregarHorarios() {
        progressoDialog = new ProgressoDialog(this);
		progressoDialog.setMessage("Aguarde. Carregando horários...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        HorarioRest horarioRest = new HorarioRest();
		        try {
		        	listaHorarios = horarioRest.getHorarios(empresa.getId());
		        	Util.messagem("", handlerCarregarHorarios);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private void carregarFormasPagamento() {
		progressoDialog.setMessage("Aguarde. Carregando formas de pagamento...");
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        FormaPagamentoRest formaPagamentoRest = new FormaPagamentoRest();
		        try {
		        	listaFormasPagamento = formaPagamentoRest.getFormaPagamentos(empresa.getId());
		        	Util.messagem("", handlerCarregarFormasPagamento);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
}
