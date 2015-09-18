package br.com.encontredelivery.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Horario;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HorarioAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Horario> listaHorarios;
	
	static class HorarioViewHolder {
		TextView txtDia;
		TextView txtHorario;
	}	
	
	public HorarioAdapter(Context context, List<Horario> listaHorarios) {
		this.context       = context;
		this.inflater      = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		return listaHorarios.get(position).getDia();
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
		
		switch (horario.getDia()) {
			case 1: horarioViewHolder.txtDia.setText(context.getString(R.string.domingo) + ": ");			
				    break;
			case 2: horarioViewHolder.txtDia.setText(context.getString(R.string.segunda) + ": ");			
		    		break;
			case 3: horarioViewHolder.txtDia.setText(context.getString(R.string.terca) + ": ");			
		    		break;
			case 4: horarioViewHolder.txtDia.setText(context.getString(R.string.quarta) + ": ");			
		    		break;
			case 5: horarioViewHolder.txtDia.setText(context.getString(R.string.quinta) + ": ");			
		    		break;
			case 6: horarioViewHolder.txtDia.setText(context.getString(R.string.sexta) + ": ");			
		    		break;
			case 7: horarioViewHolder.txtDia.setText(context.getString(R.string.sabado) + ": ");			
					break;
	
			default: break;
		}

		horarioViewHolder.txtHorario.setText(horario.getHorarios());

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int day = cal.get(Calendar.DAY_OF_WEEK);

		if (day == horario.getDia()) {
			horarioViewHolder.txtDia.setTypeface(null, Typeface.BOLD);
			horarioViewHolder.txtHorario.setTypeface(null, Typeface.BOLD);
		} else {
			horarioViewHolder.txtDia.setTypeface(null, Typeface.NORMAL);
			horarioViewHolder.txtHorario.setTypeface(null, Typeface.NORMAL);
		}

		return view;
	}
	
}
