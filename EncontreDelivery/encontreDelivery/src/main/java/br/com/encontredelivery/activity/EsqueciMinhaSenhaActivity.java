package br.com.encontredelivery.activity;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.ClienteRest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

public class EsqueciMinhaSenhaActivity extends ActionBarActivity {
	private EditText edtEmail;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private Handler handlerErros;
	private Handler handlerEsqueciMinhaSenha;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_esqueci_minha_senha);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		edtEmail = (EditText) findViewById(R.id.edtEmail);
		
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
		
		handlerEsqueciMinhaSenha = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				if (!mensagem.trim().equals("")) {
					erroAvisoDialog.setTitle("Erro");
					erroAvisoDialog.setMessage(mensagem);
				} else {
					erroAvisoDialog.setTitle("Aviso");
					erroAvisoDialog.setMessage("Foi enviado para seu e-mail as instruções de como redefinir sua senha.\n" +
							                   "Caso não consiga ver o e-mail verifique sua caixa de spam.");
				}
				
		        erroAvisoDialog.show();
			}
		};	
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void clickOk(View view) {
		if (testarCampos()) {
			progressoDialog.setMessage("Aguarde. Enviando e-mail...");
			progressoDialog.show();		
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        ClienteRest clienteRest = new ClienteRest();
			        try {
			        	String resposta = clienteRest.recuperarSenha(edtEmail.getText().toString());
			        	Util.messagem(resposta, handlerEsqueciMinhaSenha);
					} catch (Exception ex) {
						Util.messagem(ex.getMessage(), handlerErros);
					}
				}
	    	});
	    	
	    	thread.start();			        			
		}
	}
	
	private boolean testarCampos() {
		boolean erros = false;
		
		edtEmail.setError(null);		
		if (edtEmail.getText().toString().trim().equals("")) {
			edtEmail.setError("Email não preenchido.");
			erros = true;
		} else if (!Util.validarEmail(edtEmail.getText().toString().trim())) {
			edtEmail.setError("Email inválido.");
			erros = true;
		}
		
		return !erros;
	}

}
