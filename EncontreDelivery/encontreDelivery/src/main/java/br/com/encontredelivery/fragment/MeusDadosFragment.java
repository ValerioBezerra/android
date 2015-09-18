package br.com.encontredelivery.fragment;


import br.com.encontredelivery.activity.NavigationDrawerActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.ClienteRest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class MeusDadosFragment extends Fragment {
	private EditText edtNome;
	private EditText edtEmail;
	private EditText edtDDDFone;
	private EditText edtFone;
	private EditText edtSenhaAtual;
	private EditText edtNovaSenha;
	private EditText edtConfirmarSenha;
	private Button btnAlterarDados;
	
	private Cliente cliente;
	
	private Handler handlerErros;
	private Handler handlerAlterarCliente;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meus_dados, container, false);
		
		edtNome      	  = (EditText) view.findViewById(R.id.edtNome); 
		edtEmail      	  = (EditText) view.findViewById(R.id.edtEmail); 
		edtDDDFone        = (EditText) view.findViewById(R.id.edtDDDFone); 
		edtFone      	  = (EditText) view.findViewById(R.id.edtFone); 
		edtSenhaAtual     = (EditText) view.findViewById(R.id.edtSenhaAtual);
		edtNovaSenha      = (EditText) view.findViewById(R.id.edtNovaSenha);
		edtConfirmarSenha = (EditText) view.findViewById(R.id.edtConfirmarSenha);
		btnAlterarDados   = (Button) view.findViewById(R.id.btnAlterarDados); 
		
		progressoDialog = new ProgressoDialog(getActivity());
		erroAvisoDialog = new ErroAvisoDialog(getActivity());
		
		carregarCliente();
		
		btnAlterarDados.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if (testarCampos()) {
					alterarCliente();
				}
				
			}
		});
		
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
		
		handlerAlterarCliente = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				if (!mensagem.trim().equals("s")) {
					erroAvisoDialog.setTitle("Erro");
			        erroAvisoDialog.setMessage("Erro ao tentar alterar. Tente novamente mais tarde.");
			        erroAvisoDialog.show();
				} else {
					ClienteDao clienteDao = new ClienteDao(getActivity());
					clienteDao.editar(cliente);
				    carregarCliente();
					erroAvisoDialog.setTitle("Aviso");
					erroAvisoDialog.setMessage("Dados alterados com sucesso.");
				    erroAvisoDialog.show();
				    
			    	Button btnOK = (Button) erroAvisoDialog.findViewById(R.id.btnOK);
			    	btnOK.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
						    if (getActivity() instanceof NavigationDrawerActivity ) {
						    	erroAvisoDialog.dismiss();
//						    	NavigationDrawerActivity.navigationDrawerFragment.carregarDadosClientes(cliente);
//						    	NavigationDrawerActivity.navigationDrawerFragment.selectItem(1);
						    } else {
						    	erroAvisoDialog.dismiss();
								getActivity().finish();
						    }
						}
					});
				} 				
			}
		};
		
		return view;
	}

	private void carregarCliente() {
		cliente = new ClienteDao(getActivity()).getCliente();
		
		edtNome.setText(cliente.getNome());
		
		if ((cliente.getIdFacebook() == null) || (cliente.getIdFacebook().equals(""))) {
			edtEmail.setText(cliente.getEmail());
			edtSenhaAtual.setText("");
			edtNovaSenha.setText("");
			edtConfirmarSenha.setText("");

		} else {
			edtEmail.setVisibility(View.GONE);
			edtSenhaAtual.setVisibility(View.GONE);
			edtNovaSenha.setVisibility(View.GONE);
			edtConfirmarSenha.setVisibility(View.GONE);
		} 
		
		edtDDDFone.setText(cliente.getFone().substring(1, 3));
		edtFone.setText(cliente.getFone().substring(5));
		
	}
	
	private void alterarCliente() {
		progressoDialog.setMessage("Aguarde. Realizando alterações...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
	        	cliente.setNome(edtNome.getText().toString());
	        	cliente.setFone("(" + edtDDDFone.getText().toString() + ") " + edtFone.getText().toString());
	    		if ((cliente.getIdFacebook() == null) || (cliente.getIdFacebook().equals(""))) {
	    			if (!edtNovaSenha.getText().toString().equals("")) {
	    				cliente.setSenha(Retorno.getMD5(edtNovaSenha.getText().toString()));
	    			} else {
	    				cliente.setSenha("");
	    			}
	        	} else {
	        		cliente.setSenha("");
	        	}
				
		        try {
			        ClienteRest clienteRest = new ClienteRest();
		        	String resposta = clienteRest.alterar(cliente);
		        	Util.messagem(resposta, handlerAlterarCliente);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private boolean testarCampos() {
		boolean erros  = false;
		boolean testar = ((cliente.getIdFacebook() == null) || (cliente.getIdFacebook().equals("")));
		
		edtNome.setError(null);
		edtEmail.setError(null);
		edtDDDFone.setError(null);
		edtFone.setError(null);
		edtSenhaAtual.setError(null);
		edtNovaSenha.setError(null);
		edtConfirmarSenha.setError(null);
		
		if (edtNome.getText().toString().trim().equals("")) {
			edtNome.setError("Nome não preenchido.");
			erros = true;
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
			if (!(edtSenhaAtual.getText().toString().equals("")) || !(edtNovaSenha.getText().toString().equals("")) || !(edtConfirmarSenha.getText().toString().equals(""))) {
				if (!Retorno.getMD5(edtSenhaAtual.getText().toString()).equals(cliente.getSenha())) {
					edtSenhaAtual.setError("Senha atual incorreta.");
					erros = true;
				}
				if (edtNovaSenha.getText().toString().trim().length() < 6) {
					edtNovaSenha.setError("Nova senha tem que ter no mínimo 6 caracteres.");
					erros = true;
				} 
				
				if (edtConfirmarSenha.getText().toString().length() < 6) {
					edtConfirmarSenha.setError("Confirmação de senha tem que ter no mínimo 6 caracteres.");
					erros = true;
				} 
				
				if (!(edtNovaSenha.getText().toString().trim().length() < 6) && !(edtConfirmarSenha.getText().toString().length() < 6)) {
					if (!edtNovaSenha.getText().toString().trim().equals(edtConfirmarSenha.getText().toString().trim())) {
						edtConfirmarSenha.setError("Nova senha diferente da senha de confirmação.");
						erros = true;
					}
				}
			}
		}
		
		return !erros;
	}
	
	

}