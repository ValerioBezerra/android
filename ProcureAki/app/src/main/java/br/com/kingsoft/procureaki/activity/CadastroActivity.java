package br.com.kingsoft.procureaki.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.dao.ClienteDao;
import br.com.kingsoft.procureaki.dialog.ErroAvisoDialog;
import br.com.kingsoft.procureaki.dialog.ProgressoDialog;
import br.com.kingsoft.procureaki.model.Cliente;
import br.com.kingsoft.procureaki.util.Retorno;
import br.com.kingsoft.procureaki.util.Util;
import br.com.kingsoft.procureaki.webservice.ClienteRest;


public class CadastroActivity extends ActionBarActivity {
	private EditText edtNome;
	private EditText edtEmail;
	private EditText edtSenha;
	private EditText edtConfirmarSenha;
	
	private Cliente cliente;
	private ClienteDao clienteDao;
	
	private Handler handlerErros;
	private Handler handlerCadastrarCliente;

	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private static final String REGISTRO_GCM = "REGISTRO_GCM";

	boolean atualizar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		edtNome           = (EditText) findViewById(R.id.edtNome);
		edtEmail          = (EditText) findViewById(R.id.edtEmail);
		edtSenha          = (EditText) findViewById(R.id.edtSenha);
		edtConfirmarSenha = (EditText) findViewById(R.id.edtConfirmarSenha);
		
		Bundle extras = getIntent().getExtras();
		cliente       = (Cliente) extras.getSerializable("cliente");
		clienteDao    = new ClienteDao(this);
		
		if (cliente != null) {
			edtNome.setText(cliente.getNome());

			if (cliente.getIdFacebook() != null && !cliente.getIdFacebook().equals("")) {
				edtNome.setEnabled(false);
				edtEmail.setVisibility(View.GONE);
				edtSenha.setVisibility(View.GONE);
				edtConfirmarSenha.setVisibility(View.GONE);
			} else {
				edtEmail.setText(cliente.getEmail());
				edtEmail.setEnabled(false);
			}

			setTitle("Alterar cadastro");
		}
		atualizar = (cliente != null);
		
        handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(CadastroActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCadastrarCliente = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(CadastroActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        
				if (mensagem.trim().equals("n")) {
					progressoDialog.dismiss();
			        erroAvisoDialog.setMessage("Erro ao tentar cadastrar. Tente novamente mais tarde.");
			        erroAvisoDialog.show();
				} else {
					try {
						cliente.setId(Long.parseLong((mensagem.substring(1, mensagem.length()))));
						if (!atualizar)
							clienteDao.inserir(cliente);
						else
							clienteDao.editar(cliente);
						progressoDialog.dismiss();
						setResult(RESULT_OK);
						finish();
					} catch (NumberFormatException ex) {
//						progressoDialog.dismiss();
//						erroAvisoDialog.setMessage(mensagem);
//				        erroAvisoDialog.show();
						finish();
				    }
			        
				} 
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
	
	public void clickCadastrar(View view) {
		if (testarCampos()) {
	        progressoDialog = new ProgressoDialog(CadastroActivity.this);
			progressoDialog.setMessage("Aguarde. Realizando cadastro...");
			progressoDialog.show();
			
        	if (!atualizar) {
            	cliente = new Cliente();
				cliente.setEmail(edtEmail.getText().toString());
        	}

			cliente.setNome(edtNome.getText().toString());
			cliente.setSenha(Retorno.getMD5(edtSenha.getText().toString()));

        	
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        ClienteRest clienteRest = new ClienteRest();
			        try {
						String resposta = "";
						if (!atualizar)
			        		resposta = clienteRest.cadastrar(cliente);
						else
							resposta = clienteRest.alterar(cliente);

			        	Util.messagem(resposta, handlerCadastrarCliente);
					} catch (Exception ex) {
						Util.messagem(ex.getMessage(), handlerErros);
					}
				}
	    	});
	    	
	    	thread.start();
		}
	}
	
	private boolean testarCampos() {
		boolean erros  = false;
		boolean testar = ((cliente.getIdFacebook() == null || !cliente.getIdFacebook().equals("")));
		
		edtNome.setError(null);
		edtEmail.setError(null);
		edtSenha.setError(null);
		edtConfirmarSenha.setError(null);
		
		if (edtNome.getText().toString().trim().equals("")) {
			edtNome.setError("Nome não preenchido.");
			erros = true;
		}
	
		if (testar) {
			if (edtEmail.getText().toString().trim().equals("")) {
				edtEmail.setError("Email não preenchido.");
				erros = true;
			} else if (!Util.validarEmail(edtEmail.getText().toString().trim())) {
				edtEmail.setError("Email inválido.");
				erros = true;
			}
		}
		
		if (testar) {
			if (edtSenha.getText().toString().trim().equals("")) {
				edtSenha.setError("Senha não preenchida.");
				erros = true;
			} 
			
			if (edtConfirmarSenha.getText().toString().trim().equals("")) {
				edtConfirmarSenha.setError("Confirmação de senha não preenchida.");
				erros = true;
			} 
			
			if (!(edtSenha.getText().toString().trim().equals("")) && !(edtConfirmarSenha.getText().toString().trim().equals(""))) {
				if (!edtSenha.getText().toString().trim().equals(edtConfirmarSenha.getText().toString().trim())) {
					edtConfirmarSenha.setError("Senha diferente da senha de confirmação.");
					erros = true;
				}
			}
		}
		
		return !erros;
	}
	

}
