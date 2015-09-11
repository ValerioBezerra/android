package br.com.encontredelivery.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EscolherFormaPagamentoAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.dialog.VoucherDialog;
import br.com.encontredelivery.model.Adicional;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Voucher;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.FormaPagamentoRest;
import br.com.encontredelivery.webservice.PedidoRest;
import br.com.encontredelivery.webservice.VoucherRest;

public class FinalizarPedidoActivity extends ActionBarActivity {
	private TextView txtNomeCliente;
	private TextView txtEnderecoNumero;
	private TextView txtBairro;
	private TextView txtCidadeUF;
	private TextView txtComplementoReferencia;
	private ListView lvFormasPagamento;
	private LinearLayout llVazioFormasPagamento;
	private CheckBox cbVoucher;
	private TextView txtTotalProdutos;
	private TextView txtDesconto;
	private TextView txtTaxaEntrega;
	private TextView txtTotal;
	
	private Empresa empresa;
	private Cliente cliente;
	private Endereco endereco;
	private List<ProdutoPedido> listaProdutosPedido;
	private FormaPagamento formaPagamento;
	private Voucher voucher;
	private double troco;
	private double totalProdutos;
	private double desconto;
	
	private List<FormaPagamento> listaFormasPagamento;
	private EscolherFormaPagamentoAdapter escolherFormaPagamentoAdapter;
	
	private List<Voucher> listaVouchers;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;
	
	private Handler handlerErros;
	private Handler handlerErrosVoucher;
	private Handler handlerCarregarFormasPagamento;
	private Handler handlerVerificarVoucher;
	private Handler handlerEnviarPedido;
	private Handler handlerCarregarVouchers;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_finalizar_pedido);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		txtNomeCliente           = (TextView) findViewById(R.id.txtNomeCliente);
		txtEnderecoNumero        = (TextView) findViewById(R.id.txtEnderecoNumero);
		txtBairro                = (TextView) findViewById(R.id.txtBairro);
		txtCidadeUF              = (TextView) findViewById(R.id.txtCidadeUF);
		txtComplementoReferencia = (TextView) findViewById(R.id.txtComplementoReferencia);
		lvFormasPagamento        = (ListView) findViewById(R.id.lvFormasPagamento);
		llVazioFormasPagamento   = (LinearLayout) findViewById(R.id.llVazioFormasPagamento);
		cbVoucher                = (CheckBox) findViewById(R.id.cbVoucher);
		txtTotalProdutos   	     = (TextView) findViewById(R.id.txtTotalProdutos);
		txtDesconto              = (TextView) findViewById(R.id.txtDesconto);
		txtTaxaEntrega   		 = (TextView) findViewById(R.id.txtTaxaEntrega);		
		txtTotal   		         = (TextView) findViewById(R.id.txtTotal);	
		
		Bundle extras 		= getIntent().getExtras();
		empresa       		= (Empresa) extras.getSerializable("empresa");
		cliente       		= (Cliente) extras.getSerializable("cliente");
		endereco            = (Endereco) extras.getSerializable("endereco");
		listaProdutosPedido = (ArrayList<ProdutoPedido>) extras.getSerializable("listaProdutosPedido");
		desconto            = 0;
		
		escolherFormaPagamentoAdapter = new EscolherFormaPagamentoAdapter(this, new ArrayList<FormaPagamento>());
		
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

		handlerErrosVoucher = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;

				cbVoucher.setChecked(false);

				erroAvisoDialog.setTitle("Erro");
				erroAvisoDialog.setMessage(mensagem);
				erroAvisoDialog.show();
			}
		};

		
		handlerCarregarFormasPagamento = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				escolherFormaPagamentoAdapter = new EscolherFormaPagamentoAdapter(FinalizarPedidoActivity.this, listaFormasPagamento);
				lvFormasPagamento.setAdapter(escolherFormaPagamentoAdapter);
				
				if (listaFormasPagamento.isEmpty()) {
					llVazioFormasPagamento.setVisibility(View.VISIBLE);
					lvFormasPagamento.setVisibility(View.GONE);
				} else {
					llVazioFormasPagamento.setVisibility(View.GONE);
					lvFormasPagamento.setVisibility(View.VISIBLE);
				}
				
				progressoDialog.dismiss();
			}
		};
		
		handlerVerificarVoucher = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (voucher == null) {
					erroAvisoDialog.setTitle("Erro");
			        erroAvisoDialog.setMessage("Código voucher inválido.");
			        erroAvisoDialog.show();
				} else {
					if (voucher.getTipo().equals("d")) {
						desconto = voucher.getValor();
					} else {
						desconto = (totalProdutos * voucher.getValor()) / 100; 
					}
					
					calcularTotal();
				}
			
				progressoDialog.dismiss();
			}
		};
		
		
		handlerEnviarPedido = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				if (mensagem.trim().equals("n")) {
					erroAvisoDialog.setTitle("Erro");
			        erroAvisoDialog.setMessage("Erro ao tentar enviar pedido. Tente novamente mais tarde.");
			        erroAvisoDialog.show();
				} else {
					try {
						Bundle extras = new Bundle();
						extras.putLong("idPedido", Long.parseLong((mensagem.substring(1, mensagem.length()))));
//						extras.putSerializable("empresa", empresa);
						Intent data = new Intent();
						data.putExtras(extras);
						setResult(RESULT_OK, data);
						finish();						
					} catch (NumberFormatException ex) {
						erroAvisoDialog.setMessage(mensagem);
				        erroAvisoDialog.show();
				    }
			        
				} 
			}
		};
		
		handlerCarregarVouchers = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				abrirDialogVoucher();
			}
		};

		txtNomeCliente.setText(cliente.getNome());
		txtEnderecoNumero.setText(endereco.getLogradouro() + ", " + endereco.getNumero());
		txtBairro.setText(endereco.getBairro().getNome());
		txtCidadeUF.setText(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
		
		if (endereco.getComplemento().trim().equals("")) {
			txtComplementoReferencia.setVisibility(View.GONE);
		} else {
			txtComplementoReferencia.setText("Ref.: " + endereco.getComplemento());
		}

		cbVoucher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					carregarVouchers();
				} else {
					desconto = 0;
					calcularTotal();
				}
			}
		});
		
		calcularTotal();
		carregarFormasPagamento();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home: finish();
	        				        break;
//	    	case R.id.acao_voucher: carregarVouchers();
//	    							break;
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void clickEnviarPedido(View view) {
		if (testarCampos()) {
			progressoDialog.setMessage("Aguarde. Enviando pedido...");
			progressoDialog.show();
			
			Thread thread = new Thread(new Runnable() {
				@Override
				public void run() {
			        PedidoRest pedidoRest = new PedidoRest();
			        try {
			        	String resposta = pedidoRest.enviar(empresa, cliente, endereco, listaProdutosPedido, formaPagamento, voucher, desconto, troco);
			        	Util.messagem(resposta, handlerEnviarPedido);
					} catch (Exception ex) {
						Util.messagem(ex.getMessage(), handlerErros);
					}
				}
				    	});
			
			thread.start();
			
		}
	}
	
	private void carregarFormasPagamento() {
		progressoDialog.setMessage("Aguarde. Carregando formas de pagamento...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        FormaPagamentoRest formaPagamentoRest = new FormaPagamentoRest();
		        try {
		        	listaFormasPagamento = formaPagamentoRest.getFormaPagamentos(empresa.getId());
		        	Util.messagem("", handlerCarregarFormasPagamento);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
		    	});
    	
    	thread.start();
	}
	
	private void calcularTotal() {
		totalProdutos = 0;
		
		for (ProdutoPedido produtoPedido: listaProdutosPedido) {
			double valorAdicionais = 0;
			
			for (Adicional adicional: produtoPedido.getListaAdicionais()) {
				valorAdicionais += adicional.getValor();
			}
			
			totalProdutos += produtoPedido.getQuantidade() * (produtoPedido.getPreco() + valorAdicionais);
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		txtTotalProdutos.setText("R$ " + decimalFormat.format(totalProdutos).replace(".", ","));
		txtDesconto.setText("R$ " + decimalFormat.format(desconto).replace(".", ","));
		txtTaxaEntrega.setText("R$ " + decimalFormat.format(empresa.getTaxaEntrega()).replace(".", ","));
		txtTotal.setText("R$ " + decimalFormat.format((totalProdutos - desconto) + empresa.getTaxaEntrega()).replace(".", ","));
	}
	
	private boolean testarCampos() {
		String msgErros  = "";
		String separador = "";
		
		if (totalProdutos < desconto) {
			msgErros  += separador + "- Desconto maior que total dos produtos";
			separador  = "\n";
		}
		
		formaPagamento = escolherFormaPagamentoAdapter.getFormaPagamento();
		if (formaPagamento == null) {
			msgErros  += separador + "- Escolha uma forma de pagamento";
			separador  = "\n";
		} else {
			if (formaPagamento.isCalculaTroco()) {
				troco = escolherFormaPagamentoAdapter.getTroco();
				
				if ((troco > 0) && (troco <= (totalProdutos - desconto + empresa.getTaxaEntrega()))) {
					msgErros  += separador + "- Valor do troco menor ou igual que o valor total";
					separador  = "\n";
				}
			}
		}
			
		if (!msgErros.trim().equals("")) {
			ErroAvisoDialog erroAvisoDialog = new ErroAvisoDialog(this);
	        erroAvisoDialog.setTitle("Inconsistência(s)");
	        erroAvisoDialog.setMessage(msgErros);
	        erroAvisoDialog.show();			
		}
		
		return (msgErros.trim().equals(""));
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
					Util.messagem(ex.getMessage(), handlerErrosVoucher);
				}
			}
		    	});
    	
    	thread.start();
	}
	
	private void abrirDialogVoucher() {
		final VoucherDialog voucherDialog = new VoucherDialog(this, listaVouchers);

		ListView lvVouchers = (ListView) voucherDialog.findViewById(R.id.lvVouchers);
		Button btnCancelar  = (Button) voucherDialog.findViewById(R.id.btnCancelar);
		lvVouchers.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				voucherDialog.dismiss();
				verificarVoucher(listaVouchers.get(position).getCodigo());
			}
		});

		btnCancelar.setOnClickListener(new android.view.View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				voucherDialog.dismiss();
				cbVoucher.setChecked(false);
			}
		});


		voucherDialog.show();
	}
	
	private void verificarVoucher(final String codigo) {
		progressoDialog.setMessage("Aguarde. Verificando código...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        VoucherRest voucherRest = new VoucherRest();
		        try {
		        	voucher = voucherRest.getVoucher(codigo, cliente.getId(), empresa.getId());
		        	Util.messagem("", handlerVerificarVoucher);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErrosVoucher);
				}
			}
		    	});

    	thread.start();
	}
	
	
}
