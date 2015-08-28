package br.com.encontredelivery.activity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.PedidoRest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ConfirmarPedidoActivity extends ActionBarActivity {
	private LinearLayout llVerificandoRecebimento;
	private TextView txtPedido;
	private TextView txtTempo;
	private ProgressBar pbTempo;
	private LinearLayout llPedidoRecebido;
	private LinearLayout llPedidoCancelado;
	private TextView txtFone;
	private Button btnTelaInicial;
	
	private Empresa empresa;
	private long idPedido;
	private static final int TEMPO_PARAMETRO = 510;
	private int tempoCrescente;
	private int tempoDecrescente;
	private Timer timer;
	private boolean recebido;
	private boolean cancelado;
	
	private Handler handlerErros;
	private Handler handlerVerificarRecebimento;
	private Handler handlerCancelarAutomatico;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmar_pedido);		
		
		llVerificandoRecebimento = (LinearLayout) findViewById(R.id.llVerificandoRecebimento);
		txtPedido                = (TextView) findViewById(R.id.txtPedido);		
		txtTempo                 = (TextView) findViewById(R.id.txtTempo);	
		pbTempo                  = (ProgressBar) findViewById(R.id.pbTempo);
		llPedidoRecebido         = (LinearLayout) findViewById(R.id.llPedidoRecebido);
		llPedidoCancelado        = (LinearLayout) findViewById(R.id.llPedidoCancelado);
		txtFone                  = (TextView) findViewById(R.id.txtFone);
		btnTelaInicial           = (Button) findViewById(R.id.btnTelaInicial);
		
		Bundle extras = getIntent().getExtras();
		idPedido      = extras.getLong("idPedido");
		empresa       = (Empresa) extras.getSerializable("empresa");
		
		progressoDialog = new ProgressoDialog(this);
		erroAvisoDialog = new ErroAvisoDialog(this);

		String fones         = "";
		String separadorFone = "";
		for (String fone: empresa.getListaFones()) {
			fones         += separadorFone + fone;
			separadorFone  = "\n";
		}
		txtFone.setText(fones);

		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage("Entre em contato com o restaurante: \n" + mensagem);
		        erroAvisoDialog.show();
			}
		};	
		
		handlerVerificarRecebimento = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				erroAvisoDialog.dismiss();
				
				if (recebido) {
					timer.cancel();
					
					llPedidoRecebido.setVisibility(View.VISIBLE);
					btnTelaInicial.setVisibility(View.VISIBLE);
					llVerificandoRecebimento.setVisibility(View.GONE);
				}
			}
		};	
		
		handlerCancelarAutomatico = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				erroAvisoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				if (mensagem.trim().equals("s")) {
					cancelado = true;
					llPedidoCancelado.setVisibility(View.VISIBLE);
					btnTelaInicial.setVisibility(View.VISIBLE);
					llVerificandoRecebimento.setVisibility(View.GONE);
				} else if (mensagem.trim().equals("r")) {
					recebido = true;
					
					llPedidoRecebido.setVisibility(View.VISIBLE);
					btnTelaInicial.setVisibility(View.VISIBLE);
					llVerificandoRecebimento.setVisibility(View.GONE);
				} else {
			        erroAvisoDialog.setTitle("Erro");
			        erroAvisoDialog.setMessage("Erro na tentativa de cancelamento, entre em contato com o restaurante: \n" + mensagem);
			        erroAvisoDialog.show();					
				} 				
			}
		};			
		
		llVerificandoRecebimento.setVisibility(View.VISIBLE);
		txtPedido.setText("Nro Pedido: " + idPedido);
		llPedidoRecebido.setVisibility(View.GONE);
		llPedidoCancelado.setVisibility(View.GONE);
		btnTelaInicial.setVisibility(View.GONE);
		
		iniciarCronometroRegressivo();
	}
		
	@Override
	public void onBackPressed() {
		voltar();
	}		
	
	public void clickAcompanharPedido(View view) {
		Bundle extras = new Bundle();
		extras.putLong("idPedido", idPedido);
		extras.putSerializable("empresa", empresa);
		
		Intent intent = new Intent(this, DetalhesPedidoActivity.class);
		intent.putExtras(extras);
		startActivity(intent);
		finish();
	}
	
	public void clickTelaInicial(View view) {
		finish();
	}
	
	
	private void iniciarCronometroRegressivo() {
		tempoCrescente   = 0;
		tempoDecrescente = TEMPO_PARAMETRO;
		recebido         = false;
		cancelado        = false;
		timer            = new Timer();
		
		txtTempo.setText(tempoMascarado(tempoDecrescente));
		pbTempo.setMax(tempoDecrescente);
		timer.scheduleAtFixedRate(new TimerTask() {         
	        @Override
	        public void run() {
	            runOnUiThread(new Runnable()
	            {
	                @Override
	                public void run()
	                {
	                	if (tempoDecrescente > 0) {
		                    if (tempoCrescente % 30 == 0) {
		                    	verificarRecebimento();
		                    }
	                		
		                    tempoDecrescente--;
		                	tempoCrescente++;
		                	txtTempo.setText(tempoMascarado(tempoDecrescente));
		                    pbTempo.setProgress(pbTempo.getProgress() + 1);
	                	} else {
	                		timer.cancel();
	                		
	                		if (!recebido) {
	                			cancelarAutomatico();
	                		}
	                	}
	                }
	            });
	        }
	  
		}, 1000, 1000);		
	}
	
	private String tempoMascarado(int segundos) {
		int ss = segundos % 60;
		segundos /= 60;  
		int mm = segundos % 60;  
		
		NumberFormat numberFormat = new DecimalFormat("00");
		return numberFormat.format(mm) + ":" + numberFormat.format(ss);
	}
	
	private void verificarRecebimento() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        PedidoRest pedidoRest = new PedidoRest();
		        try {
		        	recebido = pedidoRest.verificarRecebimento(idPedido);
		        	Util.messagem("", handlerVerificarRecebimento);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});	
		
		thread.start();
	}
	
	private void cancelarAutomatico() {
		progressoDialog.setMessage("Aguarde. Cancelando pedido...");
		progressoDialog.show();
		
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        PedidoRest pedidoRest = new PedidoRest();
		        try {
		        	String resposta = pedidoRest.cancelarAutomatico(idPedido);
		        	Util.messagem(resposta, handlerCancelarAutomatico);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});	
		
		thread.start();
	}
	
    private void voltar() {
    	if ((!recebido) && (!cancelado)) {
	        erroAvisoDialog.setTitle("Atenção");
	        erroAvisoDialog.setMessage("Você não pode voltar enquanto não confirmar o recebimento ou o pedido for cancelado.");
	        erroAvisoDialog.show();
    	} else {
            finish();
    	}
	}    
	
	
}
