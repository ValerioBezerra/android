package br.com.encontredelivery.activity;

import java.util.Arrays;

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
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

public class LoginActivity extends ActionBarActivity {
	private EditText edtEmail;
	private EditText edtSenha;
	private LoginButton btnEntrarFacebook;
	
	private Cliente cliente;
	private Cliente clienteFacebook;
	private ClienteDao clienteDao;
	
	private Handler handlerErros;
	private Handler handlerLoginEmail;
	private Handler handlerVerificarFacebook;
	private Handler handlerAlterarGCM;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private static final int REQUEST_CADASTRO_ACTIVITY = 0;
	private static final String REGISTRO_GCM           = "REGISTRO_GCM";
	
	
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			onSessionStateChanged(session, state, exception);
		}
	};	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		Util.showHashKey(this);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		edtEmail          = (EditText) findViewById(R.id.edtEmail);
		edtSenha          = (EditText) findViewById(R.id.edtSenha);
		btnEntrarFacebook = (LoginButton) findViewById(R.id.btnEntrarFacebook); 
		
		cliente    = null;
		clienteDao = new ClienteDao(this);
		
		progressoDialog = new ProgressoDialog(this);
		
        handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(LoginActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerLoginEmail = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(LoginActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        
				if (!mensagem.trim().equals("")) {
			        erroAvisoDialog.setMessage("Erro ao tentar logar. Tente novamente mais tarde.");
			        erroAvisoDialog.show();
				} else {
					if (cliente == null) {
				        erroAvisoDialog.setMessage("Email ou senha incorretos.");
				        erroAvisoDialog.show();
					} else {
						clienteDao.inserir(cliente);
						alterarGCM();
					}
				} 
			}
		};
		
		handlerVerificarFacebook = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				
				if (cliente == null) {
					Bundle extras = new Bundle();
					extras.putSerializable("cliente", clienteFacebook);
					Intent intent = new Intent(LoginActivity.this, CadastroActivity.class);
					intent.putExtras(extras);
			    	startActivityForResult(intent, REQUEST_CADASTRO_ACTIVITY);
				} else {
					clienteDao.inserir(cliente);
					alterarGCM();
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
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);
		
		Session session = Session.getActiveSession();
		if((session != null) && (session.isOpened())){
			session.closeAndClearTokenInformation();
		}
		
		
		btnEntrarFacebook.setPublishPermissions(Arrays.asList("email", "public_profile", " user_birthday"));
		btnEntrarFacebook.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				progressoDialog.setMessage("Aguarde. Verificando facebook...");
				progressoDialog.show();
			}
		});
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void clickEntrar(View view) {
		if (testarCampos()) {
	        progressoDialog = new ProgressoDialog(LoginActivity.this);
			progressoDialog.setMessage("Aguarde. Efetuando login...");
			progressoDialog.show();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        ClienteRest clienteRest = new ClienteRest();
			        try {
			        	cliente = clienteRest.getCliente(edtEmail.getText().toString(), Retorno.getMD5(edtSenha.getText().toString()));
			        	Util.messagem("", handlerLoginEmail);
					} catch (Exception ex) {
						Util.messagem(ex.getMessage(), handlerErros);
					}
				}
	    	});
	    	
	    	thread.start();
		}
	}
	
	public void clickQueroMeCadastrar(View view) {
		Bundle extras = new Bundle();
		extras.putSerializable("cliente", null);
		Intent intent = new Intent(this, CadastroActivity.class);
		intent.putExtras(extras);
    	startActivityForResult(intent, REQUEST_CADASTRO_ACTIVITY);
	}
	
	public void clickEsqueciMinhaSenha(View view) {
		Intent intent = new Intent(this, EsqueciMinhaSenhaActivity.class);
		startActivity(intent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		uiHelper.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}
	
	
	@Override
	protected void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		uiHelper.onSaveInstanceState(bundle);
	}
	
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == REQUEST_CADASTRO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK);
				finish();
			} else {
				Session session = Session.getActiveSession();
				if((session != null) && (session.isOpened())){
					session.closeAndClearTokenInformation();
				}
			}
		} else {
			progressoDialog.dismiss();
			uiHelper.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	private boolean testarCampos() {
		boolean erros = false;
		
		edtEmail.setError(null);
		edtSenha.setError(null);
		
		if (edtEmail.getText().toString().trim().equals("")) {
			edtEmail.setError("Email não preenchido.");
			erros = true;
		} else if (!Util.validarEmail(edtEmail.getText().toString().trim())) {
			edtEmail.setError("Email inválido.");
			erros = true;
		}
		
		if (edtSenha.getText().toString().trim().length() < 6) {
			edtSenha.setError("Senha tem que ter no mínimo 6 caracteres.");
			erros = true;
		} 
		
		return !erros;
	}
	
	public void onSessionStateChanged(final Session session, SessionState state, Exception exception){
		if (session != null && session.isOpened()){
			Request.newMeRequest(session, new Request.GraphUserCallback() {
				@Override
				public void onCompleted(GraphUser user, Response response) {
					if (user != null) {
						progressoDialog.setMessage("Aguarde. Verificando facebook...");
						progressoDialog.show();
						
						clienteFacebook = new Cliente();
						clienteFacebook.setNome(user.getFirstName() + " "+ user.getLastName());
						clienteFacebook.setDataAniversario(user.getBirthday());
						clienteFacebook.setIdFacebook(user.getId());
						Thread thread = new Thread(new Runnable() {
							@Override
							public void run() {
						        ClienteRest clienteRest = new ClienteRest();
						        try {
						        	cliente = clienteRest.getClienteFacebook(clienteFacebook.getIdFacebook());
						        	Util.messagem("", handlerVerificarFacebook);
								} catch (Exception ex) {
									Util.messagem(ex.getMessage(), handlerErros);
								}
							}
				    	});
				    	
				    	thread.start();						
					} 
				}
			}).executeAsync();
		} 
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
