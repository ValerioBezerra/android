package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import br.com.encontredelivery.activity.ProdutosActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Adicional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class AdicionalAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Adicional> listaAdicionais;
	private double valorAdicional;
	
	static class AdicionalViewHolder {
		 CheckBox cbDescricao;
		 TextView txtValor;
	}		
	
	public AdicionalAdapter(Context context, List<Adicional> listaAdicionais) {
		this.context          = context;
		this.inflater         = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaAdicionais  = listaAdicionais;
		this.valorAdicional  = 0;
	}

	@Override
	public int getCount() {
		return listaAdicionais.size();
	}

	@Override
	public Object getItem(int position) {
		return listaAdicionais.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public List<Adicional> getListaAdicionaisEscolhidos() {
		return listaAdicionais;
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Adicional adicional = listaAdicionais.get(position);
		AdicionalViewHolder adicionalViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_adicional_escolhido, parent, false);
			
			adicionalViewHolder = new AdicionalViewHolder();
			
			adicionalViewHolder.cbDescricao         = (CheckBox) view.findViewById(R.id.cbDescricao);
			adicionalViewHolder.txtValor            = (TextView) view.findViewById(R.id.txtValor);
			
			view.setTag(adicionalViewHolder);
		} else {
			adicionalViewHolder = (AdicionalViewHolder) view.getTag();
		}
		
		
		adicionalViewHolder.cbDescricao.setText(adicional.getDescricao());
		adicionalViewHolder.cbDescricao.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				adicional.setEscolhido(isChecked);
				
				if (isChecked) {
					valorAdicional += adicional.getValor();
				} else {
					valorAdicional -= adicional.getValor();
				}
				
				((ProdutosActivity) context).setValorAdicional(valorAdicional);
			}
		});
		
		adicionalViewHolder.txtValor.setVisibility(View.GONE);		
		if (adicional.getValor() > 0) {	
			adicionalViewHolder.txtValor.setVisibility(View.VISIBLE);
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			
			adicionalViewHolder.txtValor.setText("R$ " + decimalFormat.format(adicional.getValor()).replace(".", ","));
		}
		
		return view;
	}
	
}
