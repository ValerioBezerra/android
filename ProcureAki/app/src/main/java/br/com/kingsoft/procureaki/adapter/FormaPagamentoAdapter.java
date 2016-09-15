package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.FormaPagamento;


public class FormaPagamentoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<FormaPagamento> listaFormaPagamentos;
	
	static class FormaPagamentoViewHolder {
		ImageView imgIcone;
		TextView txtDescricao;
	}
	
	public FormaPagamentoAdapter(Context context, List<FormaPagamento> listaFormaPagamentos) {
		this.context        = context;
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaFormaPagamentos = listaFormaPagamentos;
	}

	@Override
	public int getCount() {
		return listaFormaPagamentos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaFormaPagamentos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaFormaPagamentos.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final FormaPagamento formaPagamento = listaFormaPagamentos.get(position);
		FormaPagamentoViewHolder formaPagamentoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_forma_pagamento, parent, false);
			
			formaPagamentoViewHolder = new FormaPagamentoViewHolder();

			formaPagamentoViewHolder.imgIcone     = (ImageView) view.findViewById(R.id.imgIcone);
			formaPagamentoViewHolder.txtDescricao = (TextView) view.findViewById(R.id.txtDescricao);

			view.setTag(formaPagamentoViewHolder);
		} else {
			formaPagamentoViewHolder = (FormaPagamentoViewHolder) view.getTag();
		}

		int id = context.getResources().getIdentifier("br.com.kingsoft.procureaki:drawable/" + formaPagamento.getIcone(), null, null);
		formaPagamentoViewHolder.imgIcone.setImageResource(id);
		formaPagamentoViewHolder.txtDescricao.setText(formaPagamento.getDescricao());

		return view;
	}
	
}
