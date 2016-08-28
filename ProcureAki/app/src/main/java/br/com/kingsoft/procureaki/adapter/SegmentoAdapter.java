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
import br.com.kingsoft.procureaki.model.Segmento;


public class SegmentoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Segmento> listaSegmentos;
	
	static class SegmentoViewHolder {
		ImageView imgIcone;
		TextView txtDescricao;
	}
	
	public SegmentoAdapter(Context context, List<Segmento> listaSegmentos) {
		this.context        = context;
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

			segmentoViewHolder.imgIcone     = (ImageView) view.findViewById(R.id.imgIcone);
			segmentoViewHolder.txtDescricao = (TextView) view.findViewById(R.id.txtDescricao);

			view.setTag(segmentoViewHolder);
		} else {
			segmentoViewHolder = (SegmentoViewHolder) view.getTag();
		}

		int id = context.getResources().getIdentifier("br.com.kingsoft.procureaki:drawable/" + segmento.getIcone(), null, null);
		segmentoViewHolder.imgIcone.setImageResource(id);
		segmentoViewHolder.txtDescricao.setText(segmento.getDescricao());

		return view;
	}
	
}
