package br.com.encontredelivery.fragment;

import java.util.ArrayList;
import java.util.List;

import br.com.encontredelivery.activity.ConfirmarEnderecoActivity;
import br.com.encontredelivery.activity.NavigationDrawerActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EnderecoAdapter;
import br.com.encontredelivery.adapter.SpinnerArrayAdapter;
import br.com.encontredelivery.dialog.ConfirmacaoEnderecoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Bairro;
import br.com.encontredelivery.model.Cidade;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.BairroRest;
import br.com.encontredelivery.webservice.CidadeRest;
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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemSelectedListener;

public class EncontrarEnderecoLogradouroFragment extends Fragment {
	private Context ctx;
	
	private Spinner spnCidades;
	private Spinner spnBairros;
	private EditText edtLogradouro;
	private Button btnEncontrar;
	private ListView lvEnderecos;
	private LinearLayout llVazioEnderecos;
	
	private Cidade cidade;
	private List<Cidade> listaCidades;
	
	private Bairro bairro;
	private List<Bairro> listaBairros; 
	
	private Endereco endereco;
	private List<Endereco> listaEnderecos;
	private EnderecoAdapter enderecoAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarCidades;
	private Handler handlerCarregarBairros;
	private Handler handlerCarregarEnderecos;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	private ConfirmacaoEnderecoDialog confirmacaoEnderecoDialog;

	private static final int REQUEST_CONFIRMAR_ENDERECO_ACTIVITY = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		this.ctx  = container.getContext();		
		View view = inflater.inflate(R.layout.fragment_encontrar_endereco_logradouro, container, false);
		
		spnCidades    	 = (Spinner) view.findViewById(R.id.spnCidades);
		spnBairros    	 = (Spinner) view.findViewById(R.id.spnBairros);
		edtLogradouro    = (EditText) view.findViewById(R.id.edtLogradouro);
		btnEncontrar     = (Button) view.findViewById(R.id.btnEncontrar);
		lvEnderecos      = (ListView) view.findViewById(R.id.lvEnderecos); 
		llVazioEnderecos = (LinearLayout) view.findViewById(R.id.llVazioEnderecos); 
		
		carregarCidades(true);
		carregarBairros(false, 0);
		
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
		
		handlerCarregarCidades = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				SpinnerArrayAdapter<Cidade> cidadeAdapter = new SpinnerArrayAdapter<Cidade>(ctx, android.R.layout.simple_spinner_dropdown_item, listaCidades);
				cidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
				spnCidades.setAdapter(cidadeAdapter);
				
				progressoDialog.dismiss();
			}
		};		
		
		handlerCarregarBairros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				SpinnerArrayAdapter<Bairro> bairroAdapter = new SpinnerArrayAdapter<Bairro>(ctx, android.R.layout.simple_spinner_dropdown_item, listaBairros);
				bairroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
				spnBairros.setAdapter(bairroAdapter);
				
				progressoDialog.dismiss();
			}
		};	
		
		handlerCarregarEnderecos = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				enderecoAdapter = new EnderecoAdapter(ctx, listaEnderecos);
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
		
		spnCidades.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				cidade = (Cidade) parent.getSelectedItem();
				
				carregarBairros(false, 0);
				
				if (cidade != null) {
					if (cidade.getId() != 0) {
						carregarBairros(true, cidade.getId());
					}
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		spnBairros.setOnItemSelectedListener(new OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				bairro = (Bairro) parent.getSelectedItem();
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {}
		});
		
		btnEncontrar.setOnClickListener(clickEncontrar());
		lvEnderecos.setOnItemClickListener(clickLvEnderecos());
		
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
					carregarEnderecos();
				}
			}
		};
		
		return clickEncontrar;
	}
	
	private OnItemClickListener clickLvEnderecos() {
		OnItemClickListener clickLvEnderecos = new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
			endereco = listaEnderecos.get(position);

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
						extras.putBoolean("adicionar", true);
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
		};
		
		return clickLvEnderecos;
	}
	
	private void carregarCidades(final boolean webservice) {
		listaCidades = new ArrayList<Cidade>();
		
		Cidade cidade = new Cidade();
		cidade.setNome("Cidade");
		listaCidades.add(cidade);
		
		SpinnerArrayAdapter<Cidade> cidadeAdapter = new SpinnerArrayAdapter<Cidade>(ctx, android.R.layout.simple_spinner_dropdown_item, listaCidades);
		cidadeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		
		spnCidades.setAdapter(cidadeAdapter);
		
		if (webservice) {
	        progressoDialog = new ProgressoDialog(ctx);
			progressoDialog.setMessage("Aguarde. Carregando cidades...");
			progressoDialog.show();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        CidadeRest cidadeRest = new CidadeRest();
			        try {
			        	listaCidades = cidadeRest.getCidades();
			        	Util.messagem("", handlerCarregarCidades);
					} catch (Exception ex) {
						Util.messagem(ex.getMessage(), handlerErros);
					}
				}
				    	});
	    	
	    	thread.start();
		}
	}
	
	private void carregarBairros(final boolean webservice, final int idCidade) {
		listaBairros = new ArrayList<Bairro>();

		Bairro bairro = new Bairro();
		bairro.setNome("Bairro");
		listaBairros.add(bairro);
		
		SpinnerArrayAdapter<Bairro> bairroAdapter = new SpinnerArrayAdapter<Bairro>(ctx, android.R.layout.simple_spinner_dropdown_item, listaBairros);
		bairroAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);	
		spnBairros.setAdapter(bairroAdapter);
		
		if (webservice) {
	        progressoDialog = new ProgressoDialog(ctx);
			progressoDialog.setMessage("Aguarde. Carregando bairros...");
			progressoDialog.show();
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        BairroRest bairroRest = new BairroRest();
			        try {
			        	listaBairros = bairroRest.getBairros(idCidade);
			        	Util.messagem("", handlerCarregarBairros);
					} catch (Exception ex) {
						Util.messagem(ex.getMessage(), handlerErros);
					}
				}
				    	});
	    	
	    	thread.start();
		}
	}
	
	private void carregarEnderecos() {
        progressoDialog = new ProgressoDialog(ctx);
		progressoDialog.setMessage("Aguarde. Carregando enderecos...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        EnderecoRest enderecoRest = new EnderecoRest();
		        try {
		        	listaEnderecos = enderecoRest.getEnderecos(cidade.getId(), bairro.getId(), edtLogradouro.getText().toString());
		        	Util.messagem("", handlerCarregarEnderecos);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private boolean testarDados() {		
		boolean erros   = false;
		String msgErros = "";
		String separador = "";
		
		if ((cidade == null) || (cidade.getId() == 0)) {
			erros     = true;
			msgErros += separador + "- Escolha uma cidade";
			separador = "\n";
		}
		
		if ((bairro == null) || (bairro.getId() == 0)) {
			erros     = true;
			msgErros += separador + "- Escolha uma bairro";
			separador = "\n";
		}
		
		edtLogradouro.setError(null);
		
		if (edtLogradouro.getText().toString().trim().equals("")) {
			edtLogradouro.setError("Logradouro não informado.");
			erros = true;
		}
		
		if (!msgErros.trim().equals("")) {
			erroAvisoDialog = new ErroAvisoDialog(ctx);
	        erroAvisoDialog.setTitle("Inconsistência(s)");
	        erroAvisoDialog.setMessage(msgErros);
	        erroAvisoDialog.show();			
		}
		
		return !erros;		
	}
	
}