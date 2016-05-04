package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Segmento;


public class SegmentoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Segmento> listaSegmentos;
	
	static class SegmentoViewHolder {
		TextView txtDescricao;
		TextView txtQuantidadeEmpresas;
	}		
	
	public SegmentoAdapter(Context context, List<Segmento> listaSegmentos) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaSegmentos = listaSegmentos;
	}

	@Override
	public int getCount() {
		return listaSegmentos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaSegmentos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaSegmentos.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Segmento segmento = listaSegmentos.get(position);
		SegmentoViewHolder segmentoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_segmento, parent, false);
			
			segmentoViewHolder = new SegmentoViewHolder();
		
			segmentoViewHolder.txtDescricao          = (TextView) view.findViewById(R.id.txtDescricao);
			segmentoViewHolder.txtQuantidadeEmpresas = (TextView) view.findViewById(R.id.txtQuantidadeEmpresas);
			
			view.setTag(segmentoViewHolder);
		} else {
			segmentoViewHolder = (SegmentoViewHolder) view.getTag();
		}
		
		segmentoViewHolder.txtDescricao.setText(segmento.getDescricao());
		segmentoViewHolder.txtQuantidadeEmpresas.setText("(" + segmento.getQuantidadeEmpresas() + ")");

		return view;
	}
	
}
