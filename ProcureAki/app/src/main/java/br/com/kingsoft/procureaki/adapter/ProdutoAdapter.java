package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Produto;


public class ProdutoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Produto> listaProdutos;
	private ImageLoader imageLoader;

	static class ProdutoViewHolder {
		NetworkImageView nivImagem;
		TextView txtDescricao;
		TextView txtPreco;
	}		
	
	public ProdutoAdapter(Context context, List<Produto> listaProdutos, ImageLoader imageLoader) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaProdutos  = listaProdutos;
		this.imageLoader    = imageLoader;
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

			produtoViewHolder.nivImagem     = (NetworkImageView) view.findViewById(R.id.nivImagem);
			produtoViewHolder.txtDescricao  = (TextView) view.findViewById(R.id.txtDescricao);
			produtoViewHolder.txtPreco      = (TextView) view.findViewById(R.id.txtPreco);
			
			view.setTag(produtoViewHolder);
		} else {
			produtoViewHolder = (ProdutoViewHolder) view.getTag();
		}
		
		produtoViewHolder.txtDescricao.setText(produto.getDescricao());
		produtoViewHolder.txtPreco.setText(new DecimalFormat("0.00").format(produto.getPreco()).replace('.', ','));

		produtoViewHolder.nivImagem.setImageUrl(produto.getUrlImagemEmpresa(), imageLoader);
		produtoViewHolder.nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		produtoViewHolder.nivImagem.setErrorImageResId(R.drawable.ic_launcher);


		return view;
	}
	
}
