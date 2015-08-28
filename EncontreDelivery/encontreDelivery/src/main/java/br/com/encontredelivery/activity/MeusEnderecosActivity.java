package br.com.encontredelivery.activity;

import java.util.List;


import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EnderecoAdapter;
import br.com.encontredelivery.dialog.ConfirmacaoDialog;
import br.com.encontredelivery.dialog.ConfirmacaoEnderecoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.EnderecoRest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class MeusEnderecosActivity extends AppCompatActivity {
	private ListView lvEnderecos;
	private LinearLayout llVazioEnderecos;
	private FloatingActionButton fabEditar;
	private FloatingActionButton fabApagar;

	private Cliente cliente;
	
	private Endereco endereco;
	private List<Endereco> listaEnderecos;
	private EnderecoAdapter enderecoAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarEnderecos;
	private Handler handlerApagarEnderecoCliente;	
	
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	private ConfirmacaoEnderecoDialog confirmacaoEnderecoDialog;

	private static final int REQUEST_ENCONTRAR_ENDERECO_ACTIVITY = 0;
	private static final int REQUEST_CONFIRMAR_ENDERECO_ACTIVITY = 1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_meus_enderecos);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		lvEnderecos      = (ListView) findViewById(R.id.lvEnderecos);
		llVazioEnderecos = (LinearLayout) findViewById(R.id.llVazioEnderecos);
		fabEditar        = (FloatingActionButton) findViewById(R.id.fabEditar);
		fabApagar        = (FloatingActionButton) findViewById(R.id.fabApagar);

		fabEditar.setVisibility(View.GONE);
		fabApagar.setVisibility(View.GONE);

		Bundle extras = getIntent().getExtras();
		cliente       = (Cliente) extras.getSerializable("cliente");
		
		progressoDialog = new ProgressoDialog(this);		
		erroAvisoDialog = new ErroAvisoDialog(this);

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
		
		handlerCarregarEnderecos = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				enderecoAdapter = new EnderecoAdapter(MeusEnderecosActivity.this, listaEnderecos);
				lvEnderecos.setAdapter(enderecoAdapter);
				
				if (listaEnderecos.isEmpty()) {
					llVazioEnderecos.setVisibility(View.VISIBLE);
					lvEnderecos.setVisibility(View.GONE);
				} else {
					llVazioEnderecos.setVisibility(View.GONE);
					lvEnderecos.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();		
			}
		};
		
		handlerApagarEnderecoCliente = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
		        erroAvisoDialog.setTitle("Erro");		        
				if (mensagem.trim().equals("n")) {
			        erroAvisoDialog.setMessage("Erro ao apagar endereco. Tente novamente mais tarde.");
			        erroAvisoDialog.show();
				} else {
					carregarEnderecos();
				} 
			}
		};
		
		lvEnderecos.setOnItemClickListener(clickLvEnderecos());
		lvEnderecos.setOnItemLongClickListener(clickLongLvEnderecos());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENCONTRAR_ENDERECO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			}
		} else if (requestCode == REQUEST_CONFIRMAR_ENDERECO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				endereco = (Endereco) data.getExtras().getSerializable("endereco");

				Bundle extras = new Bundle();
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("endereco", endereco);
				Intent intent = new Intent(MeusEnderecosActivity.this, NavigationDrawerActivity.class);
				intent.putExtras(extras);
				startActivity(intent);
				setResult(RESULT_OK);
				finish();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home: finish();
	    		 break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		carregarEnderecos();
	}
	
	private void carregarEnderecos() {
		progressoDialog.setMessage("Aguarde. Carregando enderecos...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        EnderecoRest enderecoRest = new EnderecoRest();
		        try {
		        	listaEnderecos = enderecoRest.getEnderecosCliente(cliente.getId());
		        	Util.messagem("", handlerCarregarEnderecos);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private OnItemClickListener clickLvEnderecos() {
		return new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				endereco = listaEnderecos.get(position);

				confirmacaoEnderecoDialog = new ConfirmacaoEnderecoDialog(MeusEnderecosActivity.this);
				confirmacaoEnderecoDialog.setTitle(getResources().getString(R.string.confirmar_endereco));
				confirmacaoEnderecoDialog.setCEPLogradouro("(" + Retorno.getMascaraCep(endereco.getCep()) + ") " + endereco.getLogradouro() + ", " +
				                                           endereco.getNumero());
				confirmacaoEnderecoDialog.setBairro(endereco.getBairro().getNome());
				confirmacaoEnderecoDialog.setCidadeUF(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
				confirmacaoEnderecoDialog.show();

				Button btnSim = (Button) confirmacaoEnderecoDialog.findViewById(R.id.btnSim);

				btnSim.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						confirmacaoEnderecoDialog.dismiss();

						Bundle extras = new Bundle();
						extras.putSerializable("cliente", cliente);
						extras.putSerializable("endereco", endereco);
						Intent intent = new Intent(MeusEnderecosActivity.this, NavigationDrawerActivity.class);
						intent.putExtras(extras);
						startActivity(intent);
						setResult(RESULT_OK);
						finish();
					}
				});
			}
		};
	}
	
	private OnItemLongClickListener clickLongLvEnderecos() {
		return new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
			endereco = listaEnderecos.get(position);

			fabEditar.setVisibility(View.VISIBLE);
			fabApagar.setVisibility(View.VISIBLE);
			return true;
			}
		};
	}

	public void clickAdicionar(View view) {
		Bundle extras = new Bundle();
		extras.putBoolean("adicionar", true);
		extras.putSerializable("cliente", cliente);
		Intent intent = new Intent(this, EncontrarEnderecoActivity.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_ENCONTRAR_ENDERECO_ACTIVITY);
	}

	public void clickEditar(View view) {
		Bundle extras = new Bundle();
		extras.putSerializable("cliente", cliente);
		extras.putSerializable("endereco", endereco);
		extras.putBoolean("adicionar", false);
		Intent intent = new Intent(this, ConfirmarEnderecoActivity.class);
		intent.putExtras(extras);
		startActivityForResult(intent, REQUEST_CONFIRMAR_ENDERECO_ACTIVITY);

		fabEditar.setVisibility(View.GONE);
		fabApagar.setVisibility(View.GONE);
	}

	public void clickApagar(View view) {
		final ConfirmacaoDialog CONFIRMACAO_DIALOG = new ConfirmacaoDialog(MeusEnderecosActivity.this);
		CONFIRMACAO_DIALOG.setTitle("Atenção");
		CONFIRMACAO_DIALOG.setMessage("Deseja apagar este endereço?");

		Button btnSim = (Button) CONFIRMACAO_DIALOG.findViewById(R.id.btnSim);
		btnSim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CONFIRMACAO_DIALOG.dismiss();

				progressoDialog.setMessage("Aguarde. Apagando endereço...");
				progressoDialog.show();
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						EnderecoRest enderecoRest = new EnderecoRest();
						try {
							String resposta = enderecoRest.apagarEnderecoCliente(endereco);
							Util.messagem(resposta, handlerApagarEnderecoCliente);
						} catch (Exception ex) {
							Util.messagem(ex.getMessage(), handlerErros);
						}
					}
				});

				thread.start();
			}
		});

		CONFIRMACAO_DIALOG.show();
		fabEditar.setVisibility(View.GONE);
		fabApagar.setVisibility(View.GONE);
	}


}
