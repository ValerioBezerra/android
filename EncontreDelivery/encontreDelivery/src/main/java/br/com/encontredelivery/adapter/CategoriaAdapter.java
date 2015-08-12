package br.com.encontredelivery.adapter;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Categoria;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CategoriaAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Categoria> listaCategorias;
	
	static class CategoriaViewHolder {
		 TextView txtDescricao;
	}		
	
	public CategoriaAdapter(Context context, List<Categoria> listaCategorias) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaCategorias = listaCategorias;
	}

	@Override
	public int getCount() {
		return listaCategorias.size();
	}

	@Override
	public Object getItem(int position) {
		return listaCategorias.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaCategorias.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Categoria categoria = listaCategorias.get(position);
		CategoriaViewHolder categoriaViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_categoria, parent, false);
			
			categoriaViewHolder = new CategoriaViewHolder();
		
			categoriaViewHolder.txtDescricao = (TextView) view.findViewById(R.id.txtDescricao);
			
			view.setTag(categoriaViewHolder);
		} else {
			categoriaViewHolder = (CategoriaViewHolder) view.getTag();
		}
		
		categoriaViewHolder.txtDescricao.setText(categoria.getDescricao());
		
		return view;
	}
	
}
