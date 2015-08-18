package br.com.encontredelivery.activity;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.StatusAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Status;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.PedidoRest;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class DetalhesPedidoActivity extends ActionBarActivity {
	private TextView txtNumeroPedido;
	private TextView txtNomeEmpresa;
	private TextView txtFoneEmpresa;
	private ListView lvStatus;

	private long idPedido;
	private Empresa empresa;
	
	private List<Status> listaStatus;
	private StatusAdapter statusAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarStatus;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_pedido);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		txtNumeroPedido = (TextView) findViewById(R.id.txtNumeroPedido);
		txtNomeEmpresa  = (TextView) findViewById(R.id.txtNomeEmpresa);
		txtFoneEmpresa  = (TextView) findViewById(R.id.txtFoneEmpresa);
		lvStatus        = (ListView) findViewById(R.id.lvStatus);

		progressoDialog = new ProgressoDialog(this);
		erroAvisoDialog = new ErroAvisoDialog(this);
		
		Bundle extras = getIntent().getExtras();
		idPedido      = extras.getLong("idPedido");
		empresa       = (Empresa) extras.getSerializable("empresa");
		
		txtNumeroPedido.setText("Nro Pedido: " + idPedido);
		txtNomeEmpresa.setText(empresa.getNome());
		txtFoneEmpresa.setText(empresa.getFone());
		
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
		
		handlerCarregarStatus = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				statusAdapter = new StatusAdapter(DetalhesPedidoActivity.this, listaStatus);
				lvStatus.setAdapter(statusAdapter);
				
				if (listaStatus.isEmpty())
					lvStatus.setVisibility(View.GONE);
				else
					lvStatus.setVisibility(View.VISIBLE);

				
				progressoDialog.dismiss();
			}
		};
		
		carregarStatus();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.refresh, menu);

        return super.onCreateOptionsMenu(menu);
    }	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home: finish();
	    		 break;
	    	case R.id.acao_refresh: carregarStatus();
	    		 break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	private void carregarStatus() {
		progressoDialog.setMessage("Aguarde. Carregando status do pedido...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        PedidoRest pedidoRest = new PedidoRest();
		        try {
		        	listaStatus = pedidoRest.getStatusPedido(idPedido);
		        	Util.messagem("", handlerCarregarStatus);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}

	public void clickLigar(View view) {
		Intent callIntent = new Intent(Intent.ACTION_DIAL);
		callIntent.setData(Uri.parse("tel:8788655045"));
		startActivity(callIntent);
	}

}
