package br.com.encontredelivery.adapter;

import java.util.List;


import br.com.encontredelivery.R;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.view.CurrencyEditText;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class EscolherFormaPagamentoAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<FormaPagamento> listaFormasPagamento;
	private int posicaoEscolhida;
	private double troco;
	
	static class FormaPagamentoViewHolder {
		RadioButton rbDescricao;
		TextView txtTroco;
		CurrencyEditText edtTroco;
	}
	
	public EscolherFormaPagamentoAdapter(Context context, List<FormaPagamento> listaFormasPagamento) {
		this.inflater             = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaFormasPagamento = listaFormasPagamento;
		this.posicaoEscolhida     = -1;
		this.troco                = 0;
		
		int posicao = 0;
		for (FormaPagamento formaPagamento: listaFormasPagamento) {
			if (formaPagamento.isEscolhido()) {
				posicaoEscolhida = posicao;
				break;
			}
			
			posicao++;
		}
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
	
	public FormaPagamento getFormaPagamento() {
		if (posicaoEscolhida == -1) {
			return null;
		}
		
		return listaFormasPagamento.get(posicaoEscolhida);
	}
	
	public void setTroco(double troco) {
		this.troco = troco;
	}
	
	public double getTroco() {
		return troco;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final FormaPagamento formaPagamento = listaFormasPagamento.get(position);
		final FormaPagamentoViewHolder formaPagamentoViewHolder;
		
		if (view == null) {			
			view = inflater.inflate(R.layout.adapter_escolher_forma_pagamento, parent, false);
			
			formaPagamentoViewHolder = new FormaPagamentoViewHolder();
			
			
			formaPagamentoViewHolder.rbDescricao = (RadioButton) view.findViewById(R.id.rbDescricao);
			formaPagamentoViewHolder.txtTroco    = (TextView) view.findViewById(R.id.txtTroco);
			formaPagamentoViewHolder.edtTroco    = (CurrencyEditText) view.findViewById(R.id.edtTroco);
			
			formaPagamentoViewHolder.edtTroco.changeParameters("R$ ", 5, ".", 2, ",", this);
		
			view.setTag(formaPagamentoViewHolder);
		} else {
			formaPagamentoViewHolder = (FormaPagamentoViewHolder) view.getTag();
		}
		
		if (posicaoEscolhida == position) {
			formaPagamentoViewHolder.rbDescricao.setChecked(true);
			formaPagamento.setEscolhido(true);
			formaPagamentoViewHolder.edtTroco.setEnabled(true);
		} else {
			formaPagamentoViewHolder.rbDescricao.setChecked(false);
			formaPagamento.setEscolhido(false);
			formaPagamentoViewHolder.edtTroco.setEnabled(false);
		}
		
		formaPagamentoViewHolder.rbDescricao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				posicaoEscolhida = position;
				notifyDataSetChanged();
			}
		});
		
		formaPagamentoViewHolder.rbDescricao.setText(formaPagamento.getDescricao());
		
		if (formaPagamento.isCalculaTroco()) {
			formaPagamentoViewHolder.txtTroco.setVisibility(View.VISIBLE);
			formaPagamentoViewHolder.edtTroco.setVisibility(View.VISIBLE);
		} else {
			formaPagamentoViewHolder.txtTroco.setVisibility(View.GONE);
			formaPagamentoViewHolder.edtTroco.setVisibility(View.GONE);
		}
		
		return view;
	}
	
}
