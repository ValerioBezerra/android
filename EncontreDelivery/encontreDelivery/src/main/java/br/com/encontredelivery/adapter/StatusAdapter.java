package br.com.encontredelivery.adapter;

import java.util.List;


import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Status;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Status> listaStatuss;
	
	static class StatusViewHolder {
		TextView txtStatus;
		ImageView imgStatus;
		TextView txtMotivoCancelamento;
		TextView txtDataHora;
	}	
	
	public StatusAdapter(Context context, List<Status> listaStatuss) {
		this.inflater     = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaStatuss = listaStatuss;
	}

	@Override
	public int getCount() {
		return listaStatuss.size();
	}

	@Override
	public Object getItem(int position) {
		return listaStatuss.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaStatuss.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Status status = listaStatuss.get(position);
		StatusViewHolder StatusViewHolder;
		
		if (view == null) {
		
			view = inflater.inflate(R.layout.adapter_status, parent, false);
			
			StatusViewHolder = new StatusViewHolder();
			
			StatusViewHolder.txtStatus 			   = (TextView) view.findViewById(R.id.txtStatus);
			StatusViewHolder.imgStatus       	   = (ImageView) view.findViewById(R.id.imgStatus);
			StatusViewHolder.txtMotivoCancelamento = (TextView) view.findViewById(R.id.txtMotivoCancelamento);
			StatusViewHolder.txtDataHora    	   = (TextView) view.findViewById(R.id.txtDataHora);
			
			view.setTag(StatusViewHolder);
		} else {
			StatusViewHolder = (StatusViewHolder) view.getTag();
		}
		
		StatusViewHolder.txtStatus.setText(status.getDescricao());
		if (status.getIndicador() != 3) {
			StatusViewHolder.imgStatus.setImageResource(R.drawable.ball_verde);
			StatusViewHolder.txtMotivoCancelamento.setVisibility(View.GONE);
		} else {
			StatusViewHolder.imgStatus.setImageResource(R.drawable.ball_vermelha);
			StatusViewHolder.txtMotivoCancelamento.setText("Motivo: " + status.getMotivoCancelamento());
			StatusViewHolder.txtMotivoCancelamento.setVisibility(View.VISIBLE);
		}
		StatusViewHolder.txtDataHora.setText(status.getDataHora());
		
		return view;
	}
	
}
