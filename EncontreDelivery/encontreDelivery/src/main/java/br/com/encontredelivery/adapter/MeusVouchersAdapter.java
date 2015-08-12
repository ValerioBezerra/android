package br.com.encontredelivery.adapter;

import java.util.List;


import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Voucher;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MeusVouchersAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Voucher> listaVouchers;
	
	static class VoucherViewHolder {
		TextView txtCodigo;
		TextView txtDescricao;
	}	
	
	public MeusVouchersAdapter(Context context, List<Voucher> listaVouchers) {
		this.inflater      = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaVouchers = listaVouchers;
	}

	@Override
	public int getCount() {
		return listaVouchers.size();
	}

	@Override
	public Object getItem(int position) {
		return listaVouchers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaVouchers.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Voucher voucher = listaVouchers.get(position);
		VoucherViewHolder VoucherViewHolder;
		
		if (view == null) {
		
			view = inflater.inflate(R.layout.adapter_meus_vouchers, parent, false);
			
			VoucherViewHolder = new VoucherViewHolder();
			
			VoucherViewHolder.txtCodigo    = (TextView) view.findViewById(R.id.txtCodigo);
			VoucherViewHolder.txtDescricao = (TextView) view.findViewById(R.id.txtDescricao);
			
			view.setTag(VoucherViewHolder);
		} else {
			VoucherViewHolder = (VoucherViewHolder) view.getTag();
		}
		
		VoucherViewHolder.txtCodigo.setText(voucher.getCodigo());
		VoucherViewHolder.txtDescricao.setText(voucher.getDescricao());
		
		return view;
	}
}
