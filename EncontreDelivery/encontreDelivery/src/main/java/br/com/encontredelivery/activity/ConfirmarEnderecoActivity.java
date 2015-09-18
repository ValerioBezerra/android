package br.com.encontredelivery.activity;



import br.com.encontredelivery.R;
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
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ConfirmarEnderecoActivity extends ActionBarActivity {
	private TextView txtEndereco;
	private TextView txtBairro;
	private TextView txtCidadeUF;
	private EditText edtNumero;
	private EditText edtComplementoReferencia;
	
	private Cliente cliente;
	private Endereco endereco;
	private boolean adicionar;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private Handler handlerErros;
	private Handler handlerCadastrarEnderecoCliente;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmar_endereco);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		txtEndereco 			 = (TextView) findViewById(R.id.txtEndereco);
		txtBairro   			 = (TextView) findViewById(R.id.txtBairro);
		txtCidadeUF 			 = (TextView) findViewById(R.id.txtCidadeUF);
		edtNumero   			 = (EditText) findViewById(R.id.edtNumero);
		edtComplementoReferencia = (EditText) findViewById(R.id.edtComplementoReferencia);
		
		Bundle extras = getIntent().getExtras();
		cliente       = (Cliente) extras.getSerializable("cliente");
		endereco      = (Endereco) extras.getSerializable("endereco");
		adicionar     = extras.getBoolean("adicionar");

		if (adicionar) {
			setTitle(R.string.confirmar_endereco_titulo);
		} else {
			setTitle(R.string.editar_endereco);
			edtNumero.setText(endereco.getNumero());
			edtComplementoReferencia.setText(endereco.getComplemento());
		}
		
		if (endereco != null) {
			txtEndereco.setText(endereco.getLogradouro());
			txtBairro.setText(endereco.getBairro().getNome());
			txtCidadeUF.setText(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
		}
		
        handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(ConfirmarEnderecoActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCadastrarEnderecoCliente = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(ConfirmarEnderecoActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        
				if (mensagem.trim().equals("n")) {
			        erroAvisoDialog.setMessage("Erro ao verificar endereco. Tente novamente mais tarde.");
			        erroAvisoDialog.show();
				} else {
					Bundle extras = new Bundle();
					endereco.setCadastrado(true);
					extras.putSerializable("endereco", endereco);
					Intent data = new Intent();
					data.putExtras(extras);
					setResult(RESULT_OK, data);
					finish();
				} 
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
	
	public void clickConfirmar(View view) {
		if (testarCampos()) {
			endereco.setNumero(edtNumero.getText().toString());
			endereco.setComplemento(edtComplementoReferencia.getText().toString());	
			
			final ConfirmacaoEnderecoDialog confirmacaoEnderecoDialog = new ConfirmacaoEnderecoDialog(this);
			confirmacaoEnderecoDialog.setTitle(getResources().getString(R.string.confirmar_endereco));
			confirmacaoEnderecoDialog.setCEPLogradouro("(" + Retorno.getMascaraCep(endereco.getCep()) + ") " + endereco.getLogradouro() + ", " + 
			                                           endereco.getNumero());
			confirmacaoEnderecoDialog.setBairro(endereco.getBairro().getNome());
			confirmacaoEnderecoDialog.setCidadeUF(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
			confirmacaoEnderecoDialog.setComplementoReferencia(endereco.getComplemento());
			confirmacaoEnderecoDialog.show();
			
			Button btnSim = (Button) confirmacaoEnderecoDialog.findViewById(R.id.btnSim);
			btnSim.setOnClickListener(new android.view.View.OnClickListener() {				
				@Override
				public void onClick(View arg0) {
					confirmacaoEnderecoDialog.dismiss();
					
					progressoDialog = new ProgressoDialog(ConfirmarEnderecoActivity.this);
					progressoDialog.setMessage("Aguarde. Verificando endereço...");
					progressoDialog.show();
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
					        EnderecoRest enderecoRest = new EnderecoRest();
							String resposta           = null;
					        try {
								if (adicionar)
					        		resposta = enderecoRest.cadastrarEnderecoCliente(cliente, endereco);
								else
									resposta = enderecoRest.alterarEnderecoCliente(cliente, endereco);

					        	Util.messagem(resposta, handlerCadastrarEnderecoCliente);
							} catch (Exception ex) {
								Util.messagem(ex.getMessage(), handlerErros);
							}
						}
								    	});
			
					thread.start();
				}
			});		
		}
	
	}
	
	private boolean testarCampos() {
		boolean erros = false;		
		edtNumero.setError(null);
		
		if ((edtNumero.getText().toString().equals("")) || (edtNumero.getText().toString().equals("0"))) {
			edtNumero.setError("Número não preenchido.");
			erros = true;
		}
		
		return (!erros);
	}
	
}
