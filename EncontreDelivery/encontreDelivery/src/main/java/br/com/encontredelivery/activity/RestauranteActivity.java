package br.com.encontredelivery.activity;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.RestauranteTabAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.Horario;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.CategoriaRest;
import br.com.encontredelivery.webservice.FormaPagamentoRest;
import br.com.encontredelivery.webservice.HorarioRest;

public class RestauranteActivity extends AppCompatActivity {
	private Cliente cliente;
	private Empresa empresa;
	private List<ProdutoPedido> listaProdutosPedido;
	private List<Categoria> listaCategorias;
	private List<Horario> listaHorarios;
	private List<FormaPagamento> listaFormasPagamento;

	private RestauranteTabAdapter restauranteTabAdapter;

	private ProgressoDialog progressoDialog;

	private Handler handlerErros;
	private Handler handlerCarregarCategorias;
	private Handler handlerCarregarHorarios;
	private Handler handlerCarregarFormasPagamento;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurante);

		Toolbar toolbar     = (Toolbar) findViewById(R.id.toolbar);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

		Bundle extras       = getIntent().getExtras();
		cliente             = (Cliente) extras.getSerializable("cliente");
		empresa             = (Empresa) extras.getSerializable("empresa");
		listaProdutosPedido = new ArrayList<ProdutoPedido>();

		setTitle(empresa.getNome());

		carregarCategorias();

		restauranteTabAdapter = new RestauranteTabAdapter(this, getSupportFragmentManager());
		setSupportActionBar(toolbar);
		viewPager.setAdapter(restauranteTabAdapter);
		tabLayout.setupWithViewPager(viewPager);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;

				ErroAvisoDialog erroAvisoDialog = new ErroAvisoDialog(RestauranteActivity.this);
				erroAvisoDialog.setTitle("Erro");
				erroAvisoDialog.setMessage(mensagem);
				erroAvisoDialog.show();
			}
		};

		handlerCarregarCategorias = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				restauranteTabAdapter.carregarCategorias(listaCategorias);
				progressoDialog.dismiss();
				carregarHorarios();
			}
		};

		handlerCarregarHorarios = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				restauranteTabAdapter.carregarHorarios(listaHorarios);
				progressoDialog.dismiss();
				carregarFormasPagamento();
			}
		};

		handlerCarregarFormasPagamento = new Handler() {
			@Override
			public void handleMessage(Message msg) {
			restauranteTabAdapter.carregarFormasPagamento(listaFormasPagamento);
			progressoDialog.dismiss();
			}
		};

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home: finish();
	    		 break;
	    }
	    return super.onOptionsItemSelected(item);
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
		progressoDialog = new ProgressoDialog(this);
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

	public void trocarCardapioFragment(Categoria categoria) {
		restauranteTabAdapter.trocarCardapioFragment(categoria);
	}

}
