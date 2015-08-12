package br.com.encontredelivery.adapter;

import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.FormaPagamento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class FormaPagamentoAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<FormaPagamento> listaFormasPagamento;
	private ImageLoader imageLoader;
	
	static class FormaPagamentoViewHolder {
		NetworkImageView nivImagem;
		TextView txtDescricao;
	}
	
	public FormaPagamentoAdapter(Context context, List<FormaPagamento> listaFormasPagamento, ImageLoader imageLoader) {
		this.inflater             = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaFormasPagamento = listaFormasPagamento;
		this.imageLoader          = imageLoader;
	}

	@Override
	public int getCount() {
		return listaFormasPagamento.size();
	}

	@Override
	public Object getItem(int position) {
		return listaFormasPagamento.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaFormasPagamento.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final FormaPagamento formaPagamento = listaFormasPagamento.get(position);
		FormaPagamentoViewHolder formaPagamentoViewHolder;
		
		if (view == null) {			
			view = inflater.inflate(R.layout.adapter_forma_pagamento, parent, false);
			
			formaPagamentoViewHolder = new FormaPagamentoViewHolder();
			
			
			formaPagamentoViewHolder.nivImagem = (NetworkImageView) view.findViewById(R.id.nivImagem);
			formaPagamentoViewHolder.txtDescricao = (TextView) view.findViewById(R.id.txtDescricao);
		
			view.setTag(formaPagamentoViewHolder);
		} else {
			formaPagamentoViewHolder = (FormaPagamentoViewHolder) view.getTag();
		}
		
		formaPagamentoViewHolder.txtDescricao.setText(formaPagamento.getDescricao());
		formaPagamentoViewHolder.nivImagem.setImageUrl(formaPagamento.getUrlImagem(), imageLoader);
		formaPagamentoViewHolder.nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		formaPagamentoViewHolder.nivImagem.setErrorImageResId(R.drawable.ic_launcher);
		
		return view;
	}
	
}
