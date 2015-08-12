package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import br.com.encontredelivery.activity.ProdutosActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Adicional;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class AdicionalUnicoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Adicional> listaAdicionais;
	private int posicaoEscolhida;
	private double valorAdicional;
	
	static class AdicionalViewHolder {
		 RadioButton rbDescricao;
		 TextView txtValor;
	}		
	
	public AdicionalUnicoAdapter(Context context, List<Adicional> listaAdicionais) {
		this.context          = context;
		this.inflater         = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaAdicionais  = listaAdicionais;
		this.posicaoEscolhida = -1;
		this.valorAdicional   = 0;
		
		int posicao = 0;
		for (Adicional adicional: listaAdicionais) {
			if (adicional.isEscolhido()) {
				posicaoEscolhida = posicao;
				break;
			}
			
			posicao++;
		}
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
	
	public Adicional getAdicional() {
		if (posicaoEscolhida != -1) {
			return listaAdicionais.get(posicaoEscolhida);
		} else {
			return null;
		}
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Adicional adicional = listaAdicionais.get(position);
		AdicionalViewHolder adicionalViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_adicional_escolhido_unico, parent, false);
			
			adicionalViewHolder = new AdicionalViewHolder();
			
			adicionalViewHolder.rbDescricao         = (RadioButton) view.findViewById(R.id.rbDescricao);
			adicionalViewHolder.txtValor            = (TextView) view.findViewById(R.id.txtValor);
			
			view.setTag(adicionalViewHolder);
		} else {
			adicionalViewHolder = (AdicionalViewHolder) view.getTag();
		}
		
		
		if (posicaoEscolhida == position) {
			adicionalViewHolder.rbDescricao.setChecked(true);
			adicional.setEscolhido(true);
			valorAdicional = adicional.getValor();
			((ProdutosActivity) context).setValorAdicional(valorAdicional);
		} else {
			adicionalViewHolder.rbDescricao.setChecked(false);
			adicional.setEscolhido(false);
		}
		
		adicionalViewHolder.rbDescricao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				posicaoEscolhida = position;
				notifyDataSetChanged();
			}
		});
		
		adicionalViewHolder.rbDescricao.setText(adicional.getDescricao());
		
		adicionalViewHolder.txtValor.setVisibility(View.GONE);
		
		if (adicional.getValor() > 0) {	
			adicionalViewHolder.txtValor.setVisibility(View.VISIBLE);
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			
			adicionalViewHolder.txtValor.setText("R$ " + decimalFormat.format(adicional.getValor()).replace(".", ","));
		}
		
		return view;
	}
	
}
