package br.com.encontredelivery.activity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.AdicionalAdapter;
import br.com.encontredelivery.adapter.AdicionalUnicoAdapter;
import br.com.encontredelivery.adapter.ProdutoEscolhidoQuantidadeAdapter;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProdutoEscolhidoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.dialog.TamanhoDialog;
import br.com.encontredelivery.model.Adicional;
import br.com.encontredelivery.model.Produto;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Tamanho;
import br.com.encontredelivery.util.LruBitmapCache;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.view.NoScrollListView;
import br.com.encontredelivery.webservice.AdicionalRest;
import br.com.encontredelivery.webservice.ProdutoRest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProdutosActivity extends ActionBarActivity {
	private NetworkImageView nivImagem; 
	private TextView txtDescricao;
	private TextView txtDetalhamento;
	private TextView txtPrecoTamanho;
	private Button btnTamanho;
	private LinearLayout llFracoes;
	private ImageButton imgFracao1;
	private ImageButton imgFracao2;
	private ImageButton imgFracao3;
	private ImageButton imgFracao4;
	private TextView txtPrecoOpcoes;
	private Button btnOpcao1;
	private Button btnOpcao2;
	private Button btnOpcao3;
	private Button btnOpcao4;
	private TextView txtPrecoProdutoEscolhido;
	private Button btnProdutoEscolhido;
	private Button btnDiminuir;
	private TextView txtQuantidade;
	private Button btnAumentar;
	private LinearLayout llProdutosEscolhidos;
	private TextView txtProdutosEscolhidos;
	private TextView txtMinimoProduto;
	private TextView txtMaximoProduto;
	private NoScrollListView lvProdutosEscolhidos;
	private LinearLayout llVazioProdutosEscolhidos;
	private LinearLayout llAdicionais;
	private TextView txtAdicionais;
	private NoScrollListView lvAdicionais;
	private TextView txtValorUnitario;
	private TextView txtValorTotal;
	private EditText edtObservacao;
	
	private RequestQueue requestQueue;
	private ImageLoader imageLoader;
	
	private Empresa empresa;
	private Produto produto;
	private List<ProdutoPedido> listaProdutosPedido;
	private double precoAplicado;
	private Tamanho tamanho;
	private int opcao;
	private ProdutoEscolhido opcao1;
	private ProdutoEscolhido opcao2;
	private ProdutoEscolhido opcao3;
	private ProdutoEscolhido opcao4;
	private ProdutoEscolhidoQuantidadeAdapter produtoEscolhidoQuantidadeAdapter;
	private ProdutoEscolhido produtoEscolhido;
	
	private List<Adicional> listaAdicionais;
	private AdicionalAdapter adicionalAdapter;
	private AdicionalUnicoAdapter adicionalUnicoAdapter;
	private double valorAdicional;
	
	private Handler handlerErros;
	private Handler handlerCarregarProduto;
	private Handler handlerCarregarAdicionais;
	
	private ProgressoDialog progressoDialog;
	private ErroAvisoDialog erroAvisoDialog;	
	
	private int quantidadeProdutos;
	
	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_produto);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		nivImagem                 = (NetworkImageView) findViewById(R.id.nivImagem);
		txtDescricao              = (TextView) findViewById(R.id.txtDescricao);
		txtDetalhamento           = (TextView) findViewById(R.id.txtDetalhamento);
		txtPrecoTamanho           = (TextView) findViewById(R.id.txtPrecoTamanho);
		btnTamanho				  = (Button) findViewById(R.id.btnTamanho);
		llFracoes                 = (LinearLayout) findViewById(R.id.llFracoes);
		imgFracao1                = (ImageButton) findViewById(R.id.imgFracao1);
		imgFracao2                = (ImageButton) findViewById(R.id.imgFracao2);
		imgFracao3                = (ImageButton) findViewById(R.id.imgFracao3);
		imgFracao4                = (ImageButton) findViewById(R.id.imgFracao4);
		txtPrecoOpcoes            = (TextView) findViewById(R.id.txtPrecoOpcoes);
		btnOpcao1				  = (Button) findViewById(R.id.btnOpcao1);
		btnOpcao2				  = (Button) findViewById(R.id.btnOpcao2);
		btnOpcao3				  = (Button) findViewById(R.id.btnOpcao3);
		btnOpcao4				  = (Button) findViewById(R.id.btnOpcao4);
		txtPrecoProdutoEscolhido  = (TextView) findViewById(R.id.txtPrecoProdutoEscolhido);
		btnProdutoEscolhido       = (Button) findViewById(R.id.btnProdutoEscolhido);
		btnDiminuir               = (Button) findViewById(R.id.btnDiminuir);
		txtQuantidade             = (TextView) findViewById(R.id.txtQuantidade);
		btnAumentar               = (Button) findViewById(R.id.btnAumentar);
		llProdutosEscolhidos      = (LinearLayout) findViewById(R.id.llProdutosEscolhidos);
		txtProdutosEscolhidos     = (TextView) findViewById(R.id.txtProdutosEscolhidos);
		txtMinimoProduto          = (TextView) findViewById(R.id.txtMinimoProduto);
		txtMaximoProduto          = (TextView) findViewById(R.id.txtMaximoProduto);
		lvProdutosEscolhidos      = (NoScrollListView) findViewById(R.id.lvProdutosEscolhidos);
		llVazioProdutosEscolhidos = (LinearLayout) findViewById(R.id.llVazioProdutosEscolhidos);
		llAdicionais              = (LinearLayout) findViewById(R.id.llAdicionais);
		txtAdicionais             = (TextView) findViewById(R.id.txtAdicionais);
		lvAdicionais              = (NoScrollListView) findViewById(R.id.lvAdicionais);
		txtValorUnitario          = (TextView) findViewById(R.id.txtValorUnitario);
		txtValorTotal             = (TextView) findViewById(R.id.txtValorTotal);
		edtObservacao             = (EditText) findViewById(R.id.edtObservacao);
		
		Bundle extras 		= getIntent().getExtras();
		empresa       		= (Empresa) extras.getSerializable("empresa");
		produto       		= (Produto) extras.getSerializable("produto");
		listaProdutosPedido = (ArrayList<ProdutoPedido>) extras.getSerializable("listaProdutosPedido");
		
		setTitle(empresa.getNome());
		nivImagem.setVisibility(View.GONE);
		txtDescricao.setText("");
		txtDetalhamento.setText("");
		txtDetalhamento.setVisibility(View.GONE);
		txtPrecoTamanho.setVisibility(View.GONE);
		btnTamanho.setVisibility(View.GONE);
		llFracoes.setVisibility(View.GONE);
		txtPrecoOpcoes.setVisibility(View.GONE);
		txtPrecoProdutoEscolhido.setVisibility(View.GONE);
		btnProdutoEscolhido.setVisibility(View.GONE);
		llProdutosEscolhidos.setVisibility(View.GONE);
		llAdicionais.setVisibility(View.GONE);		
		btnDiminuir.setEnabled(false);
		btnAumentar.setEnabled(false);
		txtQuantidade.setText("0");
		txtValorUnitario.setText("R$ 0,00");
		txtValorTotal.setText("R$ 0,00");
		
		valorAdicional = 0;			
		
		requestQueue = Volley.newRequestQueue(this);
	    imageLoader  = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
	    	private LruBitmapCache lruCache = new LruBitmapCache();
			
			@Override
			public void putBitmap(String url, Bitmap bitmap) {
				lruCache.put(url, bitmap);
			}
			
			@Override
			public Bitmap getBitmap(String url) {
				return lruCache.get(url);
			}
		});	
	    
		handlerErros = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				progressoDialog.dismiss();
				String mensagem = (String) msg.obj;
				
				erroAvisoDialog = new ErroAvisoDialog(ProdutosActivity.this);
		        erroAvisoDialog.setTitle("Erro");
		        erroAvisoDialog.setMessage(mensagem);
		        erroAvisoDialog.show();
			}
		};
		
		handlerCarregarProduto = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				
				nivImagem.setVisibility(View.GONE);
				if (!produto.getUrlImagem().trim().equals("")) {
					nivImagem.setVisibility(View.VISIBLE);
					nivImagem.setImageUrl(produto.getUrlImagem(), imageLoader);
					nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
					nivImagem.setErrorImageResId(R.drawable.ic_launcher);
				}				
				
				txtDescricao.setText(produto.getDescricao());
				txtDetalhamento.setText(produto.getDetalhamento());
				txtQuantidade.setText("1");
				btnAumentar.setEnabled(true);
				
				if (produto.isPromocao()) {
					precoAplicado = produto.getPrecoPromocao();
				} else {
					precoAplicado = produto.getPreco();
				}
				
				if ((produto.isEscolheProduto() && produto.isPrecoMaiorProduto())) {
					precoAplicado = 0;
				}
				
				if (produto.getDetalhamento().trim().equals("")) {
					txtDetalhamento.setVisibility(View.GONE);
				} else {
					txtDetalhamento.setVisibility(View.VISIBLE);
				}
				
				tamanho = null;
				btnTamanho.setVisibility(View.GONE);
				if (!produto.getListaTamanhos().isEmpty()) {
					txtPrecoTamanho.setVisibility(View.VISIBLE);
					btnTamanho.setVisibility(View.VISIBLE);
					precoAplicado = 0;
				}
				
				btnProdutoEscolhido.setVisibility(View.GONE);
				llProdutosEscolhidos.setVisibility(View.GONE);				
				if (produto.isEscolheProduto()) {
					if (produto.isPrecoMaiorProduto()) {
						txtPrecoTamanho.setVisibility(View.GONE);
						if (produto.isExibirFracao()) {
							txtPrecoOpcoes.setVisibility(View.VISIBLE);
						} else {
							txtPrecoProdutoEscolhido.setVisibility(View.VISIBLE);
						}
					}
					
					if (produto.getListaTamanhos().isEmpty()) {
						habilitarProdutosEscolhidos(produto.getQuantidade());
					} else {
						habilitarProdutosEscolhidos(0);
					}
				} 
				
				carregarAdicionais();
				
				setarValorUnitario();
			}
		};
		
		handlerCarregarAdicionais = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (!listaAdicionais.isEmpty()) {
					llAdicionais.setVisibility(View.VISIBLE);
					
					if (produto.isSomenteUmAdicional()) {
						Adicional adicional = new Adicional();
						adicional.setId(0);
						adicional.setDescricao("Nenhum");
						adicional.setEscolhido(true);
						listaAdicionais.add(adicional);
						
						txtAdicionais.setText("Selecione um adicional (Opcional)");
						adicionalUnicoAdapter = new AdicionalUnicoAdapter(ProdutosActivity.this, listaAdicionais);
						lvAdicionais.setAdapter(adicionalUnicoAdapter);
					} else {
						txtAdicionais.setText("Selecione um ou mais adicionais (Opcional)");
						adicionalAdapter = new AdicionalAdapter(ProdutosActivity.this, listaAdicionais);
						lvAdicionais.setAdapter(adicionalAdapter);
					}
				}
				
				valorAdicional = 0;			
				progressoDialog.dismiss();
			}
		};		
		
		carregarProdutos();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case android.R.id.home:
	        finish();
	    }
	    return super.onOptionsItemSelected(item);
	}
	
	public void clickTamanho(View view) {
		final TamanhoDialog tamanhoDialog = new TamanhoDialog(ProdutosActivity.this);
		tamanhoDialog.setTitle("Escolha um tamanho");
		tamanhoDialog.setListaTamanhos(produto.getListaTamanhos(), produto.isPrecoMaiorProduto());
		tamanhoDialog.show();
		
		Button btnOk = (Button) tamanhoDialog.findViewById(R.id.btnOK);
		btnOk.setOnClickListener(new OnClickListener() {							
			@Override
			public void onClick(View v) {
				tamanhoDialog.dismiss();
				produto.setListaTamanhos(tamanhoDialog.getListaTamanhos());
				Tamanho tamanhoAntigo = tamanho;
				tamanho               = tamanhoDialog.getTamanho();
				
				if (tamanho != null) {
					btnTamanho.setText("Tamanho: " + tamanho.getDescricao());
					if (!produto.isPrecoMaiorProduto()) {
						if (tamanho.isPromocao()) {
							precoAplicado = tamanho.getPrecoPromocao();
						} else {
							precoAplicado = tamanho.getPreco();
						}	
						
						setarValorUnitario();
					}
					
					if (produto.isEscolheProduto()) {
						if (tamanho != tamanhoAntigo) {
							for (ProdutoEscolhido produtoEscolhido: produto.getListaProdutosEscolhidos()) {
								produtoEscolhido.setEscolhido(false);
								produtoEscolhido.setQuantidade(0);
							}		
						
							habilitarProdutosEscolhidos(tamanho.getQuantidade());
						}
					}
				}
			}
		});		
	}
	
	public void clickFracao1(View view) {
		clickFracoes(1);
	}
	
	public void clickFracao2(View view) {
		clickFracoes(2);
	}
	
	public void clickFracao3(View view) {
		clickFracoes(3);
	}
	
	public void clickFracao4(View view) {
		clickFracoes(4);
	}
	
	public void clickOpcao1(View view) {
		clickOpcaoSabor(1);
	}
	
	public void clickOpcao2(View view) {
		clickOpcaoSabor(2);
	}
	
	public void clickOpcao3(View view) {
		clickOpcaoSabor(3);
	}
	
	public void clickOpcao4(View view) {
		clickOpcaoSabor(4);
	}	
	
	public void clickProdutoEscolhido(View view) {
		final ProdutoEscolhidoDialog produtoEscolhidoDialog = new ProdutoEscolhidoDialog(ProdutosActivity.this, produto);
		produtoEscolhidoDialog.setTitle("Escolha um(a) " + produto.getUnidade());
		produtoEscolhidoDialog.setListaProdutoEscolhidos(produto.getListaProdutosEscolhidos(), tamanho);
		produtoEscolhidoDialog.show();	
		
		Button btnOk = (Button) produtoEscolhidoDialog.findViewById(R.id.btnOK);
		btnOk.setOnClickListener(new OnClickListener() {							
			@Override
			public void onClick(View v) {
				produtoEscolhidoDialog.dismiss();
				produto.setListaProdutosEscolhidos(produtoEscolhidoDialog.getListaProdutoEscolhidos());
				produtoEscolhido = produtoEscolhidoDialog.getProdutoEscolhido();
				
				if (produtoEscolhido != null) {
					btnProdutoEscolhido.setText(produtoEscolhido.getProduto().getDescricao());
					
					if ((produto.isEscolheProduto() && produto.isPrecoMaiorProduto())) {
						produtoEscolhidoDialog.setPrecoAplicado(produtoEscolhido.getProduto());
						precoAplicado = produtoEscolhidoDialog.getPrecoAplicado();
						setarValorUnitario();
					}
				}
			}
		});		
	}
	
	public void clickDiminuir(View view) {
		int novaQuantidade  = Integer.parseInt(txtQuantidade.getText().toString()) - 1;
		
		if (novaQuantidade <= 1) {
			btnDiminuir.setEnabled(false);
		}
		
		if (novaQuantidade < 99) {
			btnAumentar.setEnabled(true);
		}
		
		txtQuantidade.setText(String.valueOf(novaQuantidade));
		calcularPrecoTotal();
	}
	
	public void clickAumentar(View view) {
		int novaQuantidade  = Integer.parseInt(txtQuantidade.getText().toString()) + 1;
		
		if (novaQuantidade > 1) {
			btnDiminuir.setEnabled(true);
		}
		
		if (novaQuantidade == 99) {
			btnAumentar.setEnabled(false);
		}
		
		txtQuantidade.setText(String.valueOf(novaQuantidade));
		calcularPrecoTotal();
	}
	
	public void clickAdicionarPedido(View view) {
		if (!empresa.isAberto()) {
			ErroAvisoDialog erroAvisoDialog = new ErroAvisoDialog(this);
			erroAvisoDialog.setTitle("Aviso");
			erroAvisoDialog.setMessage("Restaurante está fechado. Por favor, tente novamente mais tarde.");
			erroAvisoDialog.show();
		} else {
			adicionarProduto();
		}
	}
	
	private void carregarProdutos() {
        progressoDialog = new ProgressoDialog(this);
		progressoDialog.setMessage("Aguarde. Carregando dados do produto...");
		progressoDialog.show();
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        ProdutoRest produtoRest = new ProdutoRest();
		        try {
		        	produto = produtoRest.getProduto(produto);
		        	Util.messagem("", handlerCarregarProduto);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private void carregarAdicionais() {
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
		        AdicionalRest adicionalRest = new AdicionalRest();
		        try {
		        	listaAdicionais = adicionalRest.getAdicionais(produto.getId());
		        	Util.messagem("", handlerCarregarAdicionais);
				} catch (Exception ex) {
					Util.messagem(ex.getMessage(), handlerErros);
				}
			}
    	});
    	
    	thread.start();
	}
	
	private void calcularPrecoTotal() {
		if (precoAplicado != 0) {
			int quantidade = Integer.parseInt(txtQuantidade.getText().toString());
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			txtValorTotal.setText("R$ " + decimalFormat.format(quantidade * (precoAplicado + valorAdicional)).replace(".", ","));
		} else {
			txtValorTotal.setText("R$ --");
		}
	}
	
	public void setValorAdicional(double valorAdicional) {
		this.valorAdicional = valorAdicional;
		setarValorUnitario();
	}
	
	private void removerTamanhos() {
		List<Tamanho> listaTamanhos = new ArrayList<Tamanho>();
		for (Tamanho tamanho: produto.getListaTamanhos()) {
			if (tamanho.isEscolhido()) {
				listaTamanhos.add(tamanho);
			}
		}
		
		produto.setListaTamanhos(listaTamanhos);
	}
	
	private void removerProdutosEscolhidos() {
		List<ProdutoEscolhido> listaProdutosEscolhidos = new ArrayList<ProdutoEscolhido>();
		
		if (!produto.isExibirFracao()) {
			for (ProdutoEscolhido produtoEscolhido: produto.getListaProdutosEscolhidos()) {
				if ((produtoEscolhido.isEscolhido()) || (produtoEscolhido.getQuantidade() > 0)) {
					listaProdutosEscolhidos.add(produtoEscolhido);
				}
			}
		} else {
			if (opcao >= 1) {
				opcao1.setOpcao(1);
				listaProdutosEscolhidos.add(opcao1);
			}
			
			if (opcao >= 2) {
				opcao2.setOpcao(2);
				listaProdutosEscolhidos.add(opcao2);
			}

			if (opcao >= 3) {
				opcao3.setOpcao(3);
				listaProdutosEscolhidos.add(opcao3);
			}

			if (opcao == 4) {
				opcao4.setOpcao(4);
				listaProdutosEscolhidos.add(opcao4);
			}
		}
		
		produto.setListaProdutosEscolhidos(listaProdutosEscolhidos);
	}
	
	private void removerAdicionais() {
		List<Adicional> listaAdicionais = new ArrayList<Adicional>();
		for (Adicional adicional: this.listaAdicionais) {
			if ((adicional.isEscolhido()) && (adicional.getId() != 0)) {
				listaAdicionais.add(adicional);
			}
		}
		
		this.listaAdicionais = listaAdicionais;
	}
	
	private boolean testarCampos() {
		String msgErros  = "";
		String separador = "";
		
		quantidadeProdutos = produto.getQuantidade();
		
		if (!produto.getListaTamanhos().isEmpty()) {
			if (tamanho == null) {
				msgErros  += separador + "- Escolha um tamanho";
				separador  = "\n";
				quantidadeProdutos = 0;
			} else {
				quantidadeProdutos = tamanho.getQuantidade();
			}
		}
		
		if (produto.isEscolheProduto()) {
			if ((quantidadeProdutos != 0) && (!produto.isExibirFracao())) {
				if (quantidadeProdutos == 1) {
					boolean escolhido = false;
					for (ProdutoEscolhido produtoEscolhido: produto.getListaProdutosEscolhidos()) {
						if (produtoEscolhido.isEscolhido()) {
							escolhido = true;
							break;						
						}
					}
					
					if (!escolhido) {
						msgErros += separador + "- Escolha um(a) " + produto.getUnidade();
						separador = "\n";
					}
				} else {
					int quantidadeEscolhida = 0;
					
					String msgProdutosMinimo = "";
					for (ProdutoEscolhido produtoEscolhido: produto.getListaProdutosEscolhidos()) {
						quantidadeEscolhida += produtoEscolhido.getQuantidade();
						
						if (produtoEscolhido.getQuantidade() > 0) {
							if ((produto.getMinimoProduto() > 0) && (produto.getMinimoProduto() > produtoEscolhido.getQuantidade())) {
								msgProdutosMinimo += "\n\t\t•" + produtoEscolhido.getProduto().getDescricao() + " (Quantidade: " + produtoEscolhido.getQuantidade() + ")";
							}
						}
					}	
					
					if (quantidadeEscolhida < quantidadeProdutos) {
						msgErros += separador + "- Escolha " + quantidadeProdutos + " " + produto.getUnidade() +
								                " (Total escolhido(a): " + quantidadeEscolhida + ")";
						separador = "\n";
					}
					
					if (!msgProdutosMinimo.trim().equals("")) {
						msgErros += separador + "- Quantidade do item menor que a mínima: " + msgProdutosMinimo;
						separador = "\n";
						
					}
				}
			}
			
			if ((quantidadeProdutos != 0) && (produto.isExibirFracao())) {
				if (opcao == 0) {
					msgErros  += separador + "- Escolha uma opção";
					separador  = "\n";
				}
				if ((opcao >= 1) && (opcao1 == null)) {
					msgErros  += separador + "- Escolha a opção 1";
					separador  = "\n";
				}
				
				if ((opcao >= 2) && (opcao2 == null)) {
					msgErros  += separador + "- Escolha a opção 2";
					separador  = "\n";
				}
				
				if ((opcao >= 3) && (opcao3 == null)) {
					msgErros  += separador + "- Escolha a opção 3";
					separador  = "\n";
				}
				
				if ((opcao == 4) && (opcao4 == null)) {
					msgErros  += separador + "- Escolha a opção 4";
					separador  = "\n";
				}
			}
			
		}
		
		if (precoAplicado == 0) {
			msgErros  += separador + "- Preço zerado";
			separador  = "\n";
		}
		
		if (!msgErros.trim().equals("")) {
			erroAvisoDialog = new ErroAvisoDialog(this);
	        erroAvisoDialog.setTitle("Inconsistência(s)");
	        erroAvisoDialog.setMessage(msgErros);
	        erroAvisoDialog.show();			
		}
		
		return (msgErros.trim().equals(""));
	}
	
	private void adicionarProduto() {
		if (testarCampos()) {
			removerTamanhos();
			removerProdutosEscolhidos();
			removerAdicionais();

			int quantidade = Integer.parseInt(txtQuantidade.getText().toString());
			
			ProdutoPedido produtoPedido = new ProdutoPedido();
			produtoPedido.setProduto(produto);
			produtoPedido.setOpcao(opcao);
			produtoPedido.setQuantidade(quantidade);
			produtoPedido.setPreco(precoAplicado);
			produtoPedido.setObservacao(edtObservacao.getText().toString());
			produtoPedido.setListaAdicionais(listaAdicionais);
			
			listaProdutosPedido.add(produtoPedido);
			
			Bundle extras = new Bundle();
			extras.putSerializable("listaProdutosPedido", (ArrayList<ProdutoPedido>) listaProdutosPedido);
			Intent data = new Intent();
			data.putExtras(extras);
			setResult(RESULT_OK, data);
			finish();						
		}
	}
	
	private void habilitarProdutosEscolhidos(int quantidade) {
		llFracoes.setVisibility(View.GONE);
		btnProdutoEscolhido.setVisibility(View.GONE);
		llProdutosEscolhidos.setVisibility(View.GONE);			
		
		btnProdutoEscolhido.setText("Escolha um(a) " + produto.getUnidade());
		txtProdutosEscolhidos.setText("Escolha " + quantidade + " " + produto.getUnidade());
		
		if ((quantidade != 0) && (!produto.isExibirFracao())) {
			if (quantidade == 1) {
				btnProdutoEscolhido.setVisibility(View.VISIBLE);
			} else {
				llProdutosEscolhidos.setVisibility(View.VISIBLE);	
				txtMinimoProduto.setVisibility(View.VISIBLE);	
				txtMaximoProduto.setVisibility(View.VISIBLE);	
				lvProdutosEscolhidos.setVisibility(View.VISIBLE);
				llVazioProdutosEscolhidos.setVisibility(View.GONE);
				
				if (produto.getListaProdutosEscolhidos().isEmpty()) {
					txtMinimoProduto.setVisibility(View.GONE);	
					txtMaximoProduto.setVisibility(View.GONE);	
					lvProdutosEscolhidos.setVisibility(View.GONE);
					llVazioProdutosEscolhidos.setVisibility(View.VISIBLE);
				} else {
					if (produto.getMinimoProduto() <= 0) {
						txtMinimoProduto.setVisibility(View.GONE);	
					} else {
						txtMinimoProduto.setText("Quantidade mínima por cada item: " + produto.getMinimoProduto());
					}
					
					if (produto.getMaximoProduto() <= 0) {
						txtMaximoProduto.setVisibility(View.GONE);	
					} else {
						txtMaximoProduto.setText("Quantidade máxima por cada item: " + produto.getMaximoProduto());
					}
					
					produtoEscolhidoQuantidadeAdapter = new ProdutoEscolhidoQuantidadeAdapter(ProdutosActivity.this, quantidade, produto.getMaximoProduto(), produto.getListaProdutosEscolhidos());
					lvProdutosEscolhidos.setAdapter(produtoEscolhidoQuantidadeAdapter);
				}
			}
		}
		
		if ((produto.isExibirFracao()) && (quantidade != 0)) {
			if ((produto.getListaTamanhos().isEmpty()) || (tamanho != null)) {
				llFracoes.setVisibility(View.VISIBLE);
				opcao = 0;
				imgFracao1.setVisibility(View.GONE);	
				imgFracao2.setVisibility(View.GONE);	
				imgFracao3.setVisibility(View.GONE);	
				imgFracao4.setVisibility(View.GONE);	
				iniciarComponentesFracoes();
				
				if (quantidade >= 1) {
					imgFracao1.setVisibility(View.VISIBLE);	
				} 
				
				if (quantidade >= 2) {
					imgFracao2.setVisibility(View.VISIBLE);	
				} 
				
				if (quantidade >= 3) {
					imgFracao3.setVisibility(View.VISIBLE);	
				} 
				
				if (quantidade == 4) {
					imgFracao4.setVisibility(View.VISIBLE);	
				}
			}
		}
	}
	
	private void setarValorUnitario() {
		if (precoAplicado != 0) {
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			txtValorUnitario.setText("R$ " + decimalFormat.format(precoAplicado + valorAdicional).replace(".", ","));
		} else {
			txtValorUnitario.setText("R$ --");
		}
		
		calcularPrecoTotal();
	}
	
	private void clickFracoes(int opcao) {
		if (this.opcao != opcao) {
			this.opcao = opcao;
			
			iniciarComponentesFracoes();
			
			switch (opcao) {
				case 1: imgFracao1.setImageResource(R.drawable.fracao_1);
						break;
				case 2: imgFracao2.setImageResource(R.drawable.fracao_2);
						break;
				case 3: imgFracao3.setImageResource(R.drawable.fracao_3);
						break;
				case 4: imgFracao4.setImageResource(R.drawable.fracao_4);
						break;			
				default: break;
			}
			
			if (opcao >= 1) {
				btnOpcao1.setVisibility(View.VISIBLE);	
			} 
			
			if (opcao >= 2) {
				btnOpcao2.setVisibility(View.VISIBLE);	
			} 
			
			if (opcao >= 3) {
				btnOpcao3.setVisibility(View.VISIBLE);	
			} 
			
			if (opcao == 4) {
				btnOpcao4.setVisibility(View.VISIBLE);	
			}	
		}
	}
	
	private void clickOpcaoSabor(final int opcaoSabor) {
		ProdutoEscolhido produtoEscolhidoComparacao = null;
		
		switch (opcaoSabor) {
			case 1: produtoEscolhidoComparacao = opcao1;
					break;
			case 2: produtoEscolhidoComparacao = opcao2;
					break;
			case 3: produtoEscolhidoComparacao = opcao3;
					break;
			case 4: produtoEscolhidoComparacao = opcao4;
					break;
			default: break;
		}
		
		for (ProdutoEscolhido p: produto.getListaProdutosEscolhidos()) {
			p.setEscolhido(false);
			p.setQuantidade(0);
			
			if (produtoEscolhidoComparacao != null) {
				if (p.getProduto().getId() == produtoEscolhidoComparacao.getProduto().getId()) {
					p.setEscolhido(true);
				}
			}
		}
		
		final ProdutoEscolhidoDialog produtoEscolhidoDialog = new ProdutoEscolhidoDialog(ProdutosActivity.this, produto);
		produtoEscolhidoDialog.setTitle("Escolha a opção " + opcaoSabor + ": ");
		produtoEscolhidoDialog.setListaProdutoEscolhidos(produto.getListaProdutosEscolhidos(), tamanho);
		produtoEscolhidoDialog.show();	
		
		Button btnOk = (Button) produtoEscolhidoDialog.findViewById(R.id.btnOK);
		btnOk.setOnClickListener(new OnClickListener() {							
			@Override
			public void onClick(View v) {
				produtoEscolhidoDialog.dismiss();
				ProdutoEscolhido produtoEscolhido = produtoEscolhidoDialog.getProdutoEscolhido();
				
				if (produtoEscolhido != null) {
					switch (opcaoSabor) {
						case 1: btnOpcao1.setText("Opção 1:  " + produtoEscolhido.getProduto().getDescricao());
								opcao1 = produtoEscolhido;
								break;
						case 2: btnOpcao2.setText("Opção 2:  " + produtoEscolhido.getProduto().getDescricao());
								opcao2 = produtoEscolhido;
								break;
						case 3: btnOpcao3.setText("Opção 3:  " + produtoEscolhido.getProduto().getDescricao());
								opcao3 = produtoEscolhido;
								break;
						case 4: btnOpcao4.setText("Opção 4:  " + produtoEscolhido.getProduto().getDescricao());
								opcao4 = produtoEscolhido;
								break;
						default: break;
					}
					
					if ((produto.isEscolheProduto() && produto.isPrecoMaiorProduto())) {
						produtoEscolhidoDialog.setPrecoAplicado(produtoEscolhido.getProduto());
						double precoAplicadoAtual = produtoEscolhidoDialog.getPrecoAplicado();
						
						if (precoAplicadoAtual > precoAplicado) {
							precoAplicado = precoAplicadoAtual;
							setarValorUnitario();
						}
					}
				}
			}
		});		
	}
	
	private void iniciarComponentesFracoes() {
		imgFracao1.setImageResource(R.drawable.fracao_desabilitada_1);
		imgFracao2.setImageResource(R.drawable.fracao_desabilitada_2);
		imgFracao3.setImageResource(R.drawable.fracao_desabilitada_3);
		imgFracao4.setImageResource(R.drawable.fracao_desabilitada_4);		
		btnOpcao1.setText("Opção 1:");
		btnOpcao2.setText("Opção 2:");
		btnOpcao3.setText("Opção 3:");
		btnOpcao4.setText("Opção 4:");
		btnOpcao1.setVisibility(View.GONE);	
		btnOpcao2.setVisibility(View.GONE);	
		btnOpcao3.setVisibility(View.GONE);	
		btnOpcao4.setVisibility(View.GONE);	
		opcao1 = null;
		opcao2 = null;
		opcao3 = null;
		opcao4 = null;
		
		if ((produto.isEscolheProduto() && produto.isPrecoMaiorProduto())) {
			precoAplicado = 0;
			setarValorUnitario();
		}
	}
	
}
