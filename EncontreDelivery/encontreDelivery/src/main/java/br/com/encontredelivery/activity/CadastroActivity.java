package br.com.encontredelivery.activity;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.GCM;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.ClienteRest;
import br.com.encontredelivery.webservice.GCMRest;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class CadastroActivity extends ActionBarActivity {
	private EditText edtNome;
	private EditText edtEmail;
	private EditText edtDDDFone;
	private EditText edtFone;
	private EditText edtSenha;
	private EditText edtConfirmarSenha;
	
	private Cliente cliente;
	private ClienteDao clienteDao;
	
	private Handler handlerErros;
	private Handler handlerCadastrarCliente;
	private Handler handlerAlterarGCM;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private static final String REGISTRO_GCM = "REGISTRO_GCM";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cadastro);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		edtNome           = (EditText) findViewById(R.id.edtNome);
		edtEmail          = (EditText) findViewById(R.id.edtEmail);
		edtDDDFone        = (EditText) findViewById(R.id.edtDDDFone);
		edtFone           = (EditText) findViewById(R.id.edtFone);
		edtSenha          = (EditText) findViewById(R.id.edtSenha);
		edtConfirmarSenha = (EditText) findViewById(R.id.edtConfirmarSenha);
		
		Bundle extras = getIntent().getExtras();
		cliente       = (Cliente) extras.getSerializable("cliente");
		clienteDao    = new ClienteDao(this);
		
		if (cliente != null) {
			if (!cliente.getIdFacebook().equals("")) {
				edtNome.setText(cliente.getNome());
				edtNome.setEnabled(false);
				edtEmail.setVisibility(View.GONE);
				edtSenha.setVisibility(View.GONE);
				edtConfirmarSenha.setVisibility(View.GONE);
			}
		}
		
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
						clienteDao.inserir(cliente);
						alterarGCM();
					} catch (NumberFormatException ex) {
						progressoDialog.dismiss();
						erroAvisoDialog.setMessage(mensagem);
				        erroAvisoDialog.show();
				    }
			        
				} 
			}
		};
		
		handlerAlterarGCM = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				setResult(RESULT_OK);
				finish();
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
			
        	if ((cliente == null) || (cliente.getIdFacebook().equals(""))) {
            	cliente = new Cliente();
	        	cliente.setNome(edtNome.getText().toString());
	        	cliente.setEmail(edtEmail.getText().toString());
        		cliente.setSenha(Retorno.getMD5(edtSenha.getText().toString()));
        	}
        	
        	cliente.setFone("(" + edtDDDFone.getText().toString() + ") " + edtFone.getText().toString());
			
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        ClienteRest clienteRest = new ClienteRest();
			        try {
			        	String resposta = clienteRest.cadastrar(cliente);
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
		boolean testar = ((cliente == null) || (cliente.getIdFacebook().equals("")));
		
		edtNome.setError(null);
		edtEmail.setError(null);
		edtDDDFone.setError(null);
		edtFone.setError(null);
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
		
		if (edtDDDFone.getText().toString().trim().equals("")) {
			edtDDDFone.setError("DDD não preenchido.");
			erros = true;
		} else if (edtDDDFone.getText().toString().length() < 2) { 
			edtDDDFone.setError("DDD inválido.");
			erros = true;
		}
		
		if (edtFone.getText().toString().trim().equals("")) {
			edtFone.setError("Fone não preenchido.");
			erros = true;
		} else if (edtFone.getText().toString().length() < 8) { 
			edtFone.setError("Fone inválido.");
			erros = true;
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
	
	private void alterarGCM() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        try {
		    		SharedPreferences sharedPreferences = getSharedPreferences(DashboardActivity.class.getSimpleName(), Context.MODE_PRIVATE);
		    		String registroID                   = sharedPreferences.getString(REGISTRO_GCM, "");
		        	
		    		GCM gcm = new GCM();
		    		gcm.setRegId(registroID);
		    		gcm.setCliente(cliente);
		    		
		    		GCMRest gcmRest = new GCMRest();
		    		gcmRest.cadastrarAlterar(gcm, registroID);
		    		
		    		Util.messagem("", handlerAlterarGCM);
				} catch (Exception ex) {
				}
			}
    	});
    	thread.start();
    }
	
}
