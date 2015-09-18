package br.com.encontredelivery.fragment;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.MeusVouchersAdapter;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Voucher;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.VoucherRest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MeusVouchersFragment extends Fragment {
	private ListView lvVouchers;
	private LinearLayout llVazioVouchers;
	
	private Cliente cliente;
	private List<Voucher> listaVouchers;
	private MeusVouchersAdapter meusVouchersAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarVouchers;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meus_vouchers, container, false);
		
		lvVouchers      = (ListView) view.findViewById(R.id.lvVouchers); 
		llVazioVouchers = (LinearLayout) view.findViewById(R.id.llVazioVouchers); 
		
		progressoDialog    = new ProgressoDialog(getActivity());
		erroAvisoDialog    = new ErroAvisoDialog(getActivity());
		
		cliente = new ClienteDao(getActivity()).getCliente();
		
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
		
		handlerCarregarVouchers = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				meusVouchersAdapter = new MeusVouchersAdapter(getActivity(), listaVouchers);
				lvVouchers.setAdapter(meusVouchersAdapter);
				
				if (listaVouchers.isEmpty()) {
					llVazioVouchers.setVisibility(View.VISIBLE);
					lvVouchers.setVisibility(View.GONE);
				} else {
					llVazioVouchers.setVisibility(View.GONE);
					lvVouchers.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();
			}
		};
		
		carregarVouchers();
		
		return view;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);	   
	    setHasOptionsMenu(true);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.refresh, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.acao_refresh) {
			carregarVouchers();
		}
		
		return super.onOptionsItemSelected(item);
	}		
	
	private void carregarVouchers() {
		progressoDialog.setMessage("Aguarde. Carregando vouchers...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        VoucherRest voucherRest = new VoucherRest();
		        try {
		        	listaVouchers = voucherRest.getVouchers(cliente.getId());
		        	Util.messagem("", handlerCarregarVouchers);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	

}