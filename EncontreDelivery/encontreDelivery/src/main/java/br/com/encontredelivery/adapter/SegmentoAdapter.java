package br.com.encontredelivery.adapter;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Segmento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SegmentoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Segmento> listaSegmentos;
	
	static class SegmentoViewHolder {
		 CheckBox cbDescricao;
	}		
	
	public SegmentoAdapter(Context context, List<Segmento> listaSegmentos) {
		this.inflater         = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaSegmentos  = listaSegmentos;
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
		return 0;
	}
	
	public List<Segmento> getListaSegmentosEscolhidos() {
		return listaSegmentos;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Segmento segmento = listaSegmentos.get(position);
		SegmentoViewHolder segmentoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_segmento, parent, false);
			
			segmentoViewHolder = new SegmentoViewHolder();
			
			segmentoViewHolder.cbDescricao = (CheckBox) view.findViewById(R.id.cbDescricao);
			
			view.setTag(segmentoViewHolder);
		} else {
			segmentoViewHolder = (SegmentoViewHolder) view.getTag();
		}
		
		
		segmentoViewHolder.cbDescricao.setText(segmento.getDescricao());
		segmentoViewHolder.cbDescricao.setChecked(segmento.isEscolhido());
		segmentoViewHolder.cbDescricao.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				segmento.setEscolhido(isChecked);
			}
		});
		
		return view;
	}
	
}
