package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Tipo;


public class TipoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Tipo> listaTipos;
	
	static class TipoViewHolder {
		TextView txtDescricao;
		TextView txtQuantidadeEmpresas;
	}		
	
	public TipoAdapter(Context context, List<Tipo> listaTipos) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaTipos = listaTipos;
	}

	@Override
	public int getCount() {
		return listaTipos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaTipos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaTipos.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Tipo tipo = listaTipos.get(position);
		TipoViewHolder tipoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_tipo, parent, false);
			
			tipoViewHolder = new TipoViewHolder();
		
			tipoViewHolder.txtDescricao          = (TextView) view.findViewById(R.id.txtDescricao);
			tipoViewHolder.txtQuantidadeEmpresas = (TextView) view.findViewById(R.id.txtQuantidadeEmpresas);
			
			view.setTag(tipoViewHolder);
		} else {
			tipoViewHolder = (TipoViewHolder) view.getTag();
		}
		
		tipoViewHolder.txtDescricao.setText(tipo.getDescricao());
		tipoViewHolder.txtQuantidadeEmpresas.setText("(" + tipo.getQuantidadeEmpresas() + ")");

		return view;
	}
	
}
