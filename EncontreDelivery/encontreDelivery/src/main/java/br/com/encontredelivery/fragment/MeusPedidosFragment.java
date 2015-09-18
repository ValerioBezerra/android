package br.com.encontredelivery.fragment;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.MeusPedidosAdapter;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.FiltroPedidoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Pedido;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.PedidoRest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

public class MeusPedidosFragment extends Fragment {
	private ListView lvPedidos;
	private LinearLayout llVazioPedidos;
	
	private Cliente cliente;
	private List<Pedido> listaPedidos;
	private MeusPedidosAdapter meusPedidosAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarPedidos;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	private FiltroPedidoDialog filtroPedidoDialog;
	
	private int opcao;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_meus_pedidos, container, false);
		
		lvPedidos      = (ListView) view.findViewById(R.id.lvPedidos); 
		llVazioPedidos = (LinearLayout) view.findViewById(R.id.llVazioPedidos); 
		
		progressoDialog    = new ProgressoDialog(getActivity());
		erroAvisoDialog    = new ErroAvisoDialog(getActivity());
		
		opcao = 0;
		
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
		
		handlerCarregarPedidos = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				meusPedidosAdapter = new MeusPedidosAdapter(getActivity(), listaPedidos);
				lvPedidos.setAdapter(meusPedidosAdapter);
				
				if (listaPedidos.isEmpty()) {
					llVazioPedidos.setVisibility(View.VISIBLE);
					lvPedidos.setVisibility(View.GONE);
				} else {
					llVazioPedidos.setVisibility(View.GONE);
					lvPedidos.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();
			}
		};
		
		carregarPedidos();
		
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
        inflater.inflate(R.menu.meus_pedidos, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		if (id == R.id.acao_refresh) {
			carregarPedidos();
		}
		
		if (id == R.id.acao_filtros) {
			filtroPedidoDialog = new FiltroPedidoDialog(getActivity(), opcao);
			Button btnFiltrar = (Button) filtroPedidoDialog.findViewById(R.id.btnFiltrar);
			btnFiltrar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					opcao = filtroPedidoDialog.getOpcao();
					filtroPedidoDialog.dismiss();
					carregarPedidos();
				}
			});
			filtroPedidoDialog.show();
		}		
		
		return super.onOptionsItemSelected(item);
	}		
	
	private void carregarPedidos() {
		progressoDialog.setMessage("Aguarde. Carregando pedidos...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        PedidoRest pedidoRest = new PedidoRest();
		        try {
		        	listaPedidos = pedidoRest.getPedidosCliente(cliente.getId(), opcao);
		        	Util.messagem("", handlerCarregarPedidos);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	

}