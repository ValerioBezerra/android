package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Horario;


public class HorarioAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Horario> listaHorarios;
	
	static class HorarioViewHolder {
		TextView txtDia;
		TextView txtHorario;
	}
	
	public HorarioAdapter(Context context, List<Horario> listaHorarios) {
		this.context        = context;
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaHorarios = listaHorarios;
	}

	@Override
	public int getCount() {
		return listaHorarios.size();
	}

	@Override
	public Object getItem(int position) {
		return listaHorarios.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaHorarios.get(position).getCodigo();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Horario horario = listaHorarios.get(position);
		HorarioViewHolder horarioViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_horario, parent, false);
			
			horarioViewHolder = new HorarioViewHolder();

			horarioViewHolder.txtDia     = (TextView) view.findViewById(R.id.txtDia);
			horarioViewHolder.txtHorario = (TextView) view.findViewById(R.id.txtHorario);

			view.setTag(horarioViewHolder);
		} else {
			horarioViewHolder = (HorarioViewHolder) view.getTag();
		}

		switch (horario.getCodigo()) {
			case 1: horarioViewHolder.txtDia.setText("Domingo");
				break;
			case 2: horarioViewHolder.txtDia.setText("Segunda");
				break;
			case 3: horarioViewHolder.txtDia.setText("Terça");
				break;
			case 4: horarioViewHolder.txtDia.setText("Quarta");
				break;
			case 5: horarioViewHolder.txtDia.setText("Quinta");
				break;
			case 6: horarioViewHolder.txtDia.setText("Sexta");
				break;
			case 7: horarioViewHolder.txtDia.setText("Sábado");
				break;

			default: break;
		}


		horarioViewHolder.txtHorario.setText(horario.getHoraInicial() + " às " + horario.getHoraFinal());

		return view;
	}
	
}
