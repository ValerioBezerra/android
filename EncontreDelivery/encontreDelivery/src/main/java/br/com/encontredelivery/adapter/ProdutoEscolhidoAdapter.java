package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ProdutoEscolhidoDetalheDialog;
import br.com.encontredelivery.model.Produto;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.model.Tamanho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class ProdutoEscolhidoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private Produto produto;
	private List<ProdutoEscolhido> listaProdutoEscolhidos;
	private Tamanho tamanho;
	private Tamanho tamanhoEscolhido;
	private int posicaoEscolhida;
	private double precoAplicado;
	
	static class ProdutoEscolhidoViewHolder {
		 RadioButton rbDescricao;
		 TextView txtPrecoPromocional;
		 TextView txtPreco;
		 ImageButton ibtProduto;
	}		
	
	public ProdutoEscolhidoAdapter(Context context, Produto produto, List<ProdutoEscolhido> listaProdutoEscolhidos, Tamanho tamanho) {
		this.context                = context;
		this.inflater               = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.produto                = produto; 
		this.listaProdutoEscolhidos = listaProdutoEscolhidos;
		this.tamanho                = tamanho;
		this.tamanhoEscolhido       = null;
		this.posicaoEscolhida       = -1;
		this.precoAplicado          = 0;
		
		int posicao = 0;
		for (ProdutoEscolhido produtoEscolhido: listaProdutoEscolhidos) {
			if (produtoEscolhido.isEscolhido()) {
				posicaoEscolhida = posicao;
				break;
			}
			
			posicao++;
		}
	}

	@Override
	public int getCount() {
		return listaProdutoEscolhidos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaProdutoEscolhidos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public List<ProdutoEscolhido> getListaProdutosEscolhidos() {
		return listaProdutoEscolhidos;
	}
	
	public ProdutoEscolhido getProdutoEscolhido() {
		if (posicaoEscolhida != -1) {
			return listaProdutoEscolhidos.get(posicaoEscolhida);
		} else {
			return null;
		}
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final ProdutoEscolhido produtoEscolhido = listaProdutoEscolhidos.get(position);
		ProdutoEscolhidoViewHolder produtoEscolhidoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_produto_escolhido, parent, false);
			
			produtoEscolhidoViewHolder = new ProdutoEscolhidoViewHolder();
			
			produtoEscolhidoViewHolder.rbDescricao         = (RadioButton) view.findViewById(R.id.rbDescricao);
			produtoEscolhidoViewHolder.txtPrecoPromocional = (TextView) view.findViewById(R.id.txtPrecoPromocional);
			produtoEscolhidoViewHolder.txtPreco            = (TextView) view.findViewById(R.id.txtPreco);
			produtoEscolhidoViewHolder.ibtProduto          = (ImageButton) view.findViewById(R.id.ibtProduto);
			
			view.setTag(produtoEscolhidoViewHolder);
		} else {
			produtoEscolhidoViewHolder = (ProdutoEscolhidoViewHolder) view.getTag();
		}
		
		
		if (posicaoEscolhida == position) {
			produtoEscolhidoViewHolder.rbDescricao.setChecked(true);
			produtoEscolhido.setEscolhido(true);
		} else {
			produtoEscolhidoViewHolder.rbDescricao.setChecked(false);
			produtoEscolhido.setEscolhido(false);
		}
		
		produtoEscolhidoViewHolder.rbDescricao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				posicaoEscolhida = position;
				notifyDataSetChanged();
			}
		});
		
		produtoEscolhidoViewHolder.rbDescricao.setText(produtoEscolhido.getProduto().getDescricao());
		
		produtoEscolhidoViewHolder.txtPrecoPromocional.setVisibility(View.GONE);
		produtoEscolhidoViewHolder.txtPreco.setVisibility(View.GONE);
		
		if (produto.isPrecoMaiorProduto()) {	
			produtoEscolhidoViewHolder.txtPreco.setVisibility(View.VISIBLE);
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			
			verificarTamanho(produtoEscolhido.getProduto());
			
			if (tamanhoEscolhido == null) {
				if (produtoEscolhido.getProduto().isPromocao()) {
					produtoEscolhidoViewHolder.txtPrecoPromocional.setText("De R$ " + decimalFormat.format(produtoEscolhido.getProduto().getPreco()).replace(".", ",") + " por");
					produtoEscolhidoViewHolder.txtPrecoPromocional.setVisibility(View.VISIBLE);
					produtoEscolhidoViewHolder.txtPreco.setText("R$ " + decimalFormat.format(produtoEscolhido.getProduto().getPrecoPromocao()).replace(".", ","));
				} else {
					produtoEscolhidoViewHolder.txtPreco.setText("R$ " + decimalFormat.format(produtoEscolhido.getProduto().getPreco()).replace(".", ","));
				}
			} else {
				if (tamanhoEscolhido.isPromocao()) {
					produtoEscolhidoViewHolder.txtPrecoPromocional.setText("De R$ " + decimalFormat.format(tamanhoEscolhido.getPreco()).replace(".", ",") + " por");
					produtoEscolhidoViewHolder.txtPrecoPromocional.setVisibility(View.VISIBLE);
					produtoEscolhidoViewHolder.txtPreco.setText("R$ " + decimalFormat.format(tamanhoEscolhido.getPrecoPromocao()).replace(".", ","));
				} else {
					produtoEscolhidoViewHolder.txtPreco.setText("R$ " + decimalFormat.format(tamanhoEscolhido.getPreco()).replace(".", ","));
				}				
			}
		}
		
		if ((produtoEscolhido.getProduto().getDetalhamento().trim().equals("")) && (produtoEscolhido.getProduto().getUrlImagem().trim().equals(""))) {
			produtoEscolhidoViewHolder.ibtProduto.setVisibility(View.GONE);
		} else {
			produtoEscolhidoViewHolder.ibtProduto.setVisibility(View.VISIBLE);
			produtoEscolhidoViewHolder.ibtProduto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProdutoEscolhidoDetalheDialog produtoEscolhidoDialog = new ProdutoEscolhidoDetalheDialog(context, produtoEscolhido);
					produtoEscolhidoDialog.show();
				}
			});
		}
		
		return view;
	}
	
	public void setPrecoAplicado(Produto produto) {
		verificarTamanho(produto);
		
		if (tamanhoEscolhido == null) {
			if (produto.isPromocao()) {
				precoAplicado = produto.getPrecoPromocao();
			} else {
				precoAplicado = produto.getPreco();
			}	
		} else {
			if (tamanhoEscolhido.isPromocao()) {
				precoAplicado = tamanhoEscolhido.getPrecoPromocao();
			} else {
				precoAplicado = tamanhoEscolhido.getPreco();
			}	
		}
	}
	
	public double getPrecoAplicado() {
		return precoAplicado;
	}
	
	private void verificarTamanho(Produto produto) {
		tamanhoEscolhido = null;
		
		for (Tamanho t: produto.getListaTamanhos()) {
			if (tamanho != null) {
				if (t.getId() == tamanho.getId()) {
					tamanhoEscolhido = t;
				}
			}
		}
	}
	
}
