package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Produto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ProdutoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Produto> listaProdutos;
	
	static class ProdutoViewHolder {
		 TextView txtDescricao;
		 TextView txtPrecoPromocional;
		 TextView txtPreco;
	}		
	
	public ProdutoAdapter(Context context, List<Produto> listaProdutos) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaProdutos = listaProdutos;
	}

	@Override
	public int getCount() {
		return listaProdutos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaProdutos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaProdutos.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Produto produto = listaProdutos.get(position);
		ProdutoViewHolder produtoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_produto, parent, false);
			
			produtoViewHolder = new ProdutoViewHolder();
		
			produtoViewHolder.txtDescricao        = (TextView) view.findViewById(R.id.txtDescricao);
			produtoViewHolder.txtPrecoPromocional = (TextView) view.findViewById(R.id.txtPrecoPromocional);
			produtoViewHolder.txtPreco            = (TextView) view.findViewById(R.id.txtPreco);
			
			view.setTag(produtoViewHolder);
		} else {
			produtoViewHolder = (ProdutoViewHolder) view.getTag();
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
		produtoViewHolder.txtDescricao.setText(produto.getDescricao());
		
		String textoPreco = "R$ ";
		
		if ((produto.isEscolheProduto() && produto.isPrecoMaiorProduto()) || (produto.isUsaTamanho())) {
			textoPreco = "A partir de " + textoPreco;
		}
		
		if (produto.isPromocao()) {
			produtoViewHolder.txtPrecoPromocional.setText("De R$ " + decimalFormat.format(produto.getPreco()).replace(".", ",") + " por");
			produtoViewHolder.txtPrecoPromocional.setVisibility(View.VISIBLE);
			produtoViewHolder.txtPreco.setText(textoPreco + decimalFormat.format(produto.getPrecoPromocao()).replace(".", ","));
		} else {
			produtoViewHolder.txtPrecoPromocional.setVisibility(View.GONE);
			produtoViewHolder.txtPreco.setText(textoPreco + decimalFormat.format(produto.getPreco()).replace(".", ","));
		}
		
		return view;
	}
	
}
