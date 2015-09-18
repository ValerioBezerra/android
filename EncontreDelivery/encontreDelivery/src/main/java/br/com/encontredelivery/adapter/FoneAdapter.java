package br.com.encontredelivery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.encontredelivery.R;

public class FoneAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private List<String> listaFones;

	static class EnderecoViewHolder {
		TextView txtFone;
	}

	public FoneAdapter(Context context, List<String> listaFones) {
		this.inflater   = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaFones = listaFones;
	}

	@Override
	public int getCount() {
		return listaFones.size();
	}

	@Override
	public Object getItem(int position) {
		return listaFones.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final String fone = listaFones.get(position);
		EnderecoViewHolder enderecoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_fone, parent, false);
			
			enderecoViewHolder         = new EnderecoViewHolder();
			enderecoViewHolder.txtFone = (TextView) view.findViewById(R.id.txtFone);

			view.setTag(enderecoViewHolder);
		} else {
			enderecoViewHolder = (EnderecoViewHolder) view.getTag();
		}


		enderecoViewHolder.txtFone.setText(fone);

		return view;
	}
	
}
