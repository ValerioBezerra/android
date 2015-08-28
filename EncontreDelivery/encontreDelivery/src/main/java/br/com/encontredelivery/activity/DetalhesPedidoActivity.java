package br.com.encontredelivery.activity;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.StatusAdapter;
import br.com.encontredelivery.dialog.EnderecoDialog;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.FoneDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Pedido;
import br.com.encontredelivery.util.Retorno;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.view.NoScrollListView;
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
import android.widget.ScrollView;
import android.widget.TextView;

public class DetalhesPedidoActivity extends ActionBarActivity {
    private ScrollView scrollView;
	private TextView txtNumeroPedido;
	private TextView txtNomeEmpresa;
	private TextView txtFoneEmpresa;
    private TextView txtDataPedido;
    private TextView txtHorarioPedido;
    private TextView txtCEPLogradouroNumero;
    private TextView txtBairroCidadeUF;
    private TextView txtComplementoReferencia;
	private NoScrollListView lvStatus;

	private long idPedido;
	private Pedido pedido;
	
	private StatusAdapter statusAdapter;
	
	private Handler handlerErros;
	private Handler handlerCarregarDetalhes;

	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detalhes_pedido);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scrollView               = (ScrollView) findViewById(R.id.scrollView);
		txtNumeroPedido          = (TextView) findViewById(R.id.txtNumeroPedido);
		txtNomeEmpresa           = (TextView) findViewById(R.id.txtNomeEmpresa);
		txtFoneEmpresa           = (TextView) findViewById(R.id.txtFoneEmpresa);
        txtDataPedido            = (TextView) findViewById(R.id.txtDataPedido);
        txtHorarioPedido         = (TextView) findViewById(R.id.txtHorarioPedido);
        txtCEPLogradouroNumero   = (TextView) findViewById(R.id.txtCEPLogradouroNumero);
        txtBairroCidadeUF        = (TextView) findViewById(R.id.txtBairroCidadeUF);
        txtComplementoReferencia = (TextView) findViewById(R.id.txtComplementoReferencia);
		lvStatus        = (NoScrollListView) findViewById(R.id.lvStatus);

		progressoDialog = new ProgressoDialog(this);
		erroAvisoDialog = new ErroAvisoDialog(this);
		
		Bundle extras = getIntent().getExtras();
		idPedido      = extras.getLong("idPedido");

		txtNumeroPedido.setText("Nro Pedido: " + idPedido);
		txtNomeEmpresa.setText("");
		txtFoneEmpresa.setText("");
        txtDataPedido.setText("");
        txtHorarioPedido.setText("");
        txtCEPLogradouroNumero.setText("");
        txtBairroCidadeUF.setText("");
        txtComplementoReferencia.setText("");

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

        handlerCarregarDetalhes = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                txtNomeEmpresa.setText(pedido.getEmpresa().getNome());
                txtDataPedido.setText(pedido.getData());
                txtHorarioPedido.setText(pedido.getHora());
                txtCEPLogradouroNumero.setText("(" +  Retorno.getMascaraCep(pedido.getEndereco().getCep()) + ") " + pedido.getEndereco().getLogradouro() + ", " + pedido.getEndereco().getNumero());
                txtBairroCidadeUF.setText(pedido.getEndereco().getBairro().getNome() + ". " + pedido.getEndereco().getBairro().getCidade().getNome() + "-" + pedido.getEndereco().getBairro().getCidade().getUf());

                if (pedido.getEndereco().getComplemento().equals(""))
                    txtComplementoReferencia.setVisibility(View.GONE);
                else
                    txtComplementoReferencia.setText("Ref.: " + pedido.getEndereco().getComplemento());

                String fones         = "";
                String separadorFone = "";
                for (String fone: pedido.getEmpresa().getListaFones()) {
                    fones         += separadorFone + fone;
                    separadorFone  = "\n";
                }
                txtFoneEmpresa.setText(fones);


                statusAdapter = new StatusAdapter(DetalhesPedidoActivity.this, pedido.getListaStatus());
                lvStatus.setAdapter(statusAdapter);

                if (pedido.getListaStatus().isEmpty())
                    lvStatus.setVisibility(View.GONE);
                else
                    lvStatus.setVisibility(View.VISIBLE);

                scrollView.fling(0);
                progressoDialog.dismiss();
            }
        };

        carregarDetalhesPedido();
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
	    	case R.id.acao_refresh: carregarDetalhesPedido();
	    		 break;
	    }
	    return super.onOptionsItemSelected(item);
	}

	private void carregarDetalhesPedido() {
		progressoDialog.setMessage("Aguarde. Carregando detalhes do pedido...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				PedidoRest pedidoRest = new PedidoRest();
				try {
					pedido = pedidoRest.getDetalhesPedido(idPedido);
					Util.messagem("", handlerCarregarDetalhes);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
		});

		thread.start();
	}

	public void clickLigar(View view) {
        if (pedido != null) {
            if (pedido.getEmpresa().getListaFones().size() == 1) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + Retorno.getSomenteNumeros(pedido.getEmpresa().getListaFones().get(0))));
                startActivity(callIntent);
            } else {
                FoneDialog foneDialog = new FoneDialog(this);
                foneDialog.setListaFones(pedido.getEmpresa().getListaFones());
                foneDialog.show();
			}
        }
	}

}
