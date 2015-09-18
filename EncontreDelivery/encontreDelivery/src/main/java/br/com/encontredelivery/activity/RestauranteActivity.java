package br.com.encontredelivery.activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.RestauranteTabAdapter;
import br.com.encontredelivery.dialog.ConfirmacaoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.listener.ListenerIcon;
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
	private TextView txtBadge;

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

	private static final int REQUEST_CARRINHO_ACTIVITY = 0;
	public static final int REQUEST_PRODUTO_ACTIVITY   = 1;

	public List<ProdutoPedido> getListaProdutosPedido() {
		return listaProdutosPedido;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_restaurante);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

		Bundle extras = getIntent().getExtras();
		cliente = (Cliente) extras.getSerializable("cliente");
		empresa = (Empresa) extras.getSerializable("empresa");
		listaProdutosPedido = new ArrayList<ProdutoPedido>();

		setTitle(empresa.getNome());
		listaProdutosPedido = new ArrayList<ProdutoPedido>();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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

		if (requestCode == REQUEST_PRODUTO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				listaProdutosPedido = (ArrayList<ProdutoPedido>) data.getExtras().getSerializable("listaProdutosPedido");
				setBadget(listaProdutosPedido.size());

				Runnable r = new Runnable() {
					@Override
					public void run() {
						getSupportFragmentManager().popBackStack();
					}
				};

				Handler h = new Handler();
				h.post(r);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.restaurante_carrinho, menu);

		MenuItem itemCarrinho = menu.findItem(R.id.carrinho);
		final View menuCarrinho = MenuItemCompat.getActionView(itemCarrinho);
		txtBadge = (TextView) menuCarrinho.findViewById(R.id.txtBadge);
		new ListenerIcon(menuCarrinho, getString(R.string.carrinho)) {
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("empresa", empresa);
				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
				Intent intent = new Intent(RestauranteActivity.this, CarrinhoActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CARRINHO_ACTIVITY);
			}
		};

		setBadget(listaProdutosPedido.size());

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				voltar();
				break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void setBadget(final int badge) {
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

	public void replaceFragment(Categoria categoria) {
		restauranteTabAdapter.replaceFragment(categoria);
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
			btnSim.setOnClickListener(new View.OnClickListener() {
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

	@Override
	public void onBackPressed() {
		if ((getSupportFragmentManager().getBackStackEntryCount() == 0) || (getSupportFragmentManager().getBackStackEntryCount() == 1)) {
			voltar();
		} else {
			getSupportFragmentManager().popBackStack();
		}
	}
}