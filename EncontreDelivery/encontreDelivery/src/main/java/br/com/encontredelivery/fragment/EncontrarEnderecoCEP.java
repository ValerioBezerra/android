package br.com.encontredelivery.fragment;

import br.com.encontredelivery.activity.ConfirmarEnderecoActivity;
import br.com.encontredelivery.activity.NavigationDrawerActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ConfirmacaoEnderecoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.EnderecoRest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class EncontrarEnderecoCEP extends Fragment {
	private Context ctx;
	private EditText edtCEP;
	private Button btnEncontrar;
	
	private Endereco endereco;
	
	private Handler handlerErros;
	private Handler handlerCarregarEndereco;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	private ConfirmacaoEnderecoDialog confirmacaoEnderecoDialog;

	private static final int REQUEST_CONFIRMAR_ENDERECO_ACTIVITY = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.ctx  = container.getContext();
		View view = inflater.inflate(R.layout.fragment_encontrar_endereco_cep, container, false);
		
		edtCEP 		 = (EditText) view.findViewById(R.id.edtCEP);
		btnEncontrar = (Button) view.findViewById(R.id.btnEncontrar);
		
		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(ctx);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCarregarEndereco = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				
				if (endereco == null) {
					erroAvisoDialog = new ErroAvisoDialog(ctx);
			        erroAvisoDialog.setTitle("Aviso");
			        erroAvisoDialog.setMessage("- Nenhum endereço encontrado.");
			        erroAvisoDialog.show();	
				} else {
					confirmacaoEnderecoDialog = new ConfirmacaoEnderecoDialog(ctx);
					confirmacaoEnderecoDialog.setTitle(getResources().getString(R.string.confirmar_endereco));
					confirmacaoEnderecoDialog.setCEPLogradouro("(" + Retorno.getMascaraCep(endereco.getCep()) + ") " + endereco.getLogradouro());
					confirmacaoEnderecoDialog.setBairro(endereco.getBairro().getNome());
					confirmacaoEnderecoDialog.setCidadeUF(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
					confirmacaoEnderecoDialog.show();	
					
					Button btnSim = (Button) confirmacaoEnderecoDialog.findViewById(R.id.btnSim);
					
					btnSim.setOnClickListener(new android.view.View.OnClickListener() {				
						@Override
						public void onClick(View arg0) {
							confirmacaoEnderecoDialog.dismiss();
							
							Bundle extras = new Bundle();
							extras.putSerializable("cliente", (Cliente) getActivity().getIntent().getExtras().getSerializable("cliente"));
							extras.putSerializable("endereco", endereco);

							if (getActivity().getIntent().getExtras().getBoolean("adicionar")) {
								Intent intent = new Intent(getActivity(), ConfirmarEnderecoActivity.class);
								intent.putExtras(extras);
								startActivityForResult(intent, REQUEST_CONFIRMAR_ENDERECO_ACTIVITY);
							} else {
								Intent intent = new Intent(getActivity(), NavigationDrawerActivity.class);
								intent.putExtras(extras);
								getActivity().startActivity(intent);
								getActivity().setResult(Activity.RESULT_OK);
								getActivity().finish();
							}
						}
					});							
				}
				
			}
		};
		
		btnEncontrar.setOnClickListener(clickEncontrar());
		
		return view;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CONFIRMAR_ENDERECO_ACTIVITY) {
			if (resultCode == getActivity().RESULT_OK) {
				endereco = (Endereco) data.getExtras().getSerializable("endereco");

				Bundle extras = new Bundle();
				extras.putSerializable("cliente", (Cliente) getActivity().getIntent().getExtras().getSerializable("cliente"));
				extras.putSerializable("endereco", endereco);
				Intent intent = new Intent(getActivity(), NavigationDrawerActivity.class);
				intent.putExtras(extras);
				getActivity().startActivity(intent);
				getActivity().setResult(Activity.RESULT_OK);
				getActivity().finish();
			}
		}
	}

	private OnClickListener clickEncontrar() {
		OnClickListener clickEncontrar = new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (testarDados()) {
					carregarEndereco();
				}
			}
		};
		
		return clickEncontrar;
	}
	
	private void carregarEndereco() {
        progressoDialog = new ProgressoDialog(ctx);
		progressoDialog.setMessage("Aguarde. Encontrando endereco...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        EnderecoRest enderecoRest = new EnderecoRest();
		        try {
		        	endereco = enderecoRest.getEndereco(edtCEP.getText().toString());
		        	Util.messagem("", handlerCarregarEndereco);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
		    	});
    	
    	thread.start();
	}	
	
	private boolean testarDados() {
		boolean erros   = false;
		
		edtCEP.setError(null);
		
		if (edtCEP.getText().toString().trim().equals("")) {
			edtCEP.setError("CEP não informado.");
			erros = true;
		} else {
			if (edtCEP.getText().toString().trim().length() < 8) {
				edtCEP.setError("CEP inválido.");
				erros = true;
			}			
		}
		
		return !erros;
		
	}
}