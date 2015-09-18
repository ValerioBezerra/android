package br.com.encontredelivery.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.ProdutoPedidoAdapter;
import br.com.encontredelivery.dao.ClienteDao;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.model.Adicional;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.ProdutoPedido;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class CarrinhoActivity extends ActionBarActivity {
	private ListView lvProdutosPedido;
	private LinearLayout llVazioProdutosCarrinho;
	private TextView txtValorMinimo;
	private TextView txtTotalProdutos;
	private TextView txtTaxaEntrega;
	private TextView txtTotal;
	private Button btnProximo;
	
	private Cliente cliente;
	private Endereco endereco;
	private Empresa empresa;
	private List<ProdutoPedido> listaProdutosPedido;
	private ProdutoPedidoAdapter produtoPedidoAdapter;
	
	private DecimalFormat decimalFormat;
	private double subTotal;
	
	private static final int REQUEST_LOGIN_ACTIVITY              = 0;
	private static final int REQUEST_CONFIRMAR_ENDERECO_ACTIVITY = 1;
	private static final int REQUEST_FINALIZAR_PEDIDO_ACTIVITY   = 2;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_carrinho);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		lvProdutosPedido        = (ListView) findViewById(R.id.lvProdutosPedido);
		llVazioProdutosCarrinho = (LinearLayout) findViewById(R.id.llVazioProdutosCarrinho);
		txtValorMinimo          = (TextView) findViewById(R.id.txtValorMinimo);		
		txtTotalProdutos   	    = (TextView) findViewById(R.id.txtTotalProdutos);
		txtTaxaEntrega   		= (TextView) findViewById(R.id.txtTaxaEntrega);		
		txtTotal   		        = (TextView) findViewById(R.id.txtTotal);	
		btnProximo              = (Button) findViewById(R.id.btnProximo);
		
		Bundle extras 		= getIntent().getExtras();
		cliente       		= new ClienteDao(this).getCliente();
		endereco       		= NavigationDrawerActivity.endereco;
		empresa       		= (Empresa) extras.getSerializable("empresa");
		listaProdutosPedido = (ArrayList<ProdutoPedido>) extras.getSerializable("listaProdutosPedido");
		
		setTitle(empresa.getNome() + " (Carrinho)");

		decimalFormat = new DecimalFormat("0.00");
		txtValorMinimo.setText("R$ " + decimalFormat.format(empresa.getValorMinimo()).replace(".", ","));
		txtTaxaEntrega.setText("R$ " + decimalFormat.format(empresa.getTaxaEntrega()).replace(".", ","));
		
		carregarProdutosPedido();
		calcularTotal();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	    	voltar();
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent data) { 
		if (requestCode == REQUEST_LOGIN_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				cliente = new ClienteDao(this).getCliente();
//				NavigationDrawerActivity.navigationDrawerFragment.carregarDadosClientes(cliente);
				clickProximo(null);
			}
		} else if (requestCode == REQUEST_CONFIRMAR_ENDERECO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				endereco = (Endereco) data.getExtras().getSerializable("endereco");
//				NavigationDrawerActivity.navigationDrawerFragment.carregarDadosEndereco(endereco);
				clickProximo(null);
			}
		} else if (requestCode == REQUEST_FINALIZAR_PEDIDO_ACTIVITY) {
			if (resultCode == RESULT_OK) {
				setResult(RESULT_OK, data);
				finish();
			}
		}
	}	
	
	@Override
	public void onBackPressed() {
		voltar();
	}
	
	public void clickProximo(View view) {
		if (testarCampos(view != null)) {
			if (cliente == null) {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivityForResult(intent, REQUEST_LOGIN_ACTIVITY);			
			} else if (!endereco.isCadastrado()) {
				Bundle extras = new Bundle();
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("endereco", endereco);
				extras.putBoolean("adicionar", true);
				Intent intent = new Intent(this, ConfirmarEnderecoActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_CONFIRMAR_ENDERECO_ACTIVITY);
			} else {
				Bundle extras = new Bundle();
				extras.putSerializable("empresa", empresa);
				extras.putSerializable("cliente", cliente);
				extras.putSerializable("endereco", endereco);
				extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
				Intent intent = new Intent(this, FinalizarPedidoActivity.class);
				intent.putExtras(extras);
				startActivityForResult(intent, REQUEST_FINALIZAR_PEDIDO_ACTIVITY);
			}
		}
	}
	
	private void carregarProdutosPedido() {
		produtoPedidoAdapter = new ProdutoPedidoAdapter(this, listaProdutosPedido);
		lvProdutosPedido.setAdapter(produtoPedidoAdapter);
		
		verificarListaProdutosPedido();		
	}
	
	private void voltar() {
		Bundle extras = new Bundle();
		extras.putLong("idPedido", -1);
		extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
		Intent data = new Intent();
		data.putExtras(extras);

		setResult(RESULT_OK, data);
		finish();
	}
	
	public void calcularTotal() {
		subTotal = 0;
		
		for (ProdutoPedido produtoPedido: listaProdutosPedido) {
			double valorAdicionais = 0;
			
			for (Adicional adicional: produtoPedido.getListaAdicionais()) {
				valorAdicionais += adicional.getValor();
			}
			
			subTotal += produtoPedido.getQuantidade() * (produtoPedido.getPreco() + valorAdicionais);
		}
		
		txtTotalProdutos.setText("R$ " + decimalFormat.format(subTotal).replace(".", ","));
		txtTotal.setText("R$ " + decimalFormat.format(subTotal + empresa.getTaxaEntrega()).replace(".", ","));
	}
	
	public void verificarListaProdutosPedido() {
		if (listaProdutosPedido.isEmpty()) {
			lvProdutosPedido.setVisibility(View.GONE);
			btnProximo.setVisibility(View.GONE);
			llVazioProdutosCarrinho.setVisibility(View.VISIBLE);
		} else {
			lvProdutosPedido.setVisibility(View.VISIBLE);
			btnProximo.setVisibility(View.VISIBLE);
			llVazioProdutosCarrinho.setVisibility(View.GONE);			
		}
	}
	
	private boolean testarCampos(boolean testar) {
		String msgErros  = "";
		String separador = "";
		
		if (testar) {
			if (subTotal < empresa.getValorMinimo()) {
				msgErros  += separador + "- Total dos produtos menor que o valor mínimo exigido pelo restaurante";
				separador  = "\n";
			}
			
			if (!msgErros.trim().equals("")) {
				ErroAvisoDialog erroAvisoDialog = new ErroAvisoDialog(this);
		        erroAvisoDialog.setTitle("Inconsistência(s)");
		        erroAvisoDialog.setMessage(msgErros);
		        erroAvisoDialog.show();			
			}
		}
		
		return (msgErros.trim().equals(""));
	}
}
