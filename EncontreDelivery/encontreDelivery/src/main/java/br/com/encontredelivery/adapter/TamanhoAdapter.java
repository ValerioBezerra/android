package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Tamanho;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

public class TamanhoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Tamanho> listaTamanhos;
	private boolean precoMaiorProduto;
	private int posicaoEscolhida;
	
	static class TamanhoViewHolder {
		 RadioButton rbDescricao;
		 TextView txtPrecoPromocional;
		 TextView txtPreco;
		 ImageButton ibtProduto;
	}		
	
	public TamanhoAdapter(Context context, List<Tamanho> listaTamanhos, boolean precoMaiorProduto) {
		this.inflater          = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaTamanhos     = listaTamanhos;
		this.precoMaiorProduto = precoMaiorProduto;
		this.posicaoEscolhida  = -1;
		
		int posicao = 0;
		for (Tamanho tamanho: listaTamanhos) {
			if (tamanho.isEscolhido()) {
				posicaoEscolhida = posicao;
				break;
			}			
			posicao++;
		}
	}

	@Override
	public int getCount() {
		return listaTamanhos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaTamanhos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	public List<Tamanho> getListaProdutosEscolhidos() {
		return listaTamanhos;
	}
	
	public Tamanho getTamanho() {
		if (posicaoEscolhida != -1) {
			return listaTamanhos.get(posicaoEscolhida);
		} else {
			return null;
		}
	}
	
	@Override
	public View getView(final int position, View view, ViewGroup parent) {
		final Tamanho tamanho = listaTamanhos.get(position);
		TamanhoViewHolder tamanhoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_tamanho, parent, false);
			
			tamanhoViewHolder = new TamanhoViewHolder();
			
			tamanhoViewHolder.rbDescricao         = (RadioButton) view.findViewById(R.id.rbDescricao);
			tamanhoViewHolder.txtPrecoPromocional = (TextView) view.findViewById(R.id.txtPrecoPromocional);
			tamanhoViewHolder.txtPreco            = (TextView) view.findViewById(R.id.txtPreco);
			tamanhoViewHolder.ibtProduto          = (ImageButton) view.findViewById(R.id.ibtProduto);
			
			view.setTag(tamanhoViewHolder);
		} else {
			tamanhoViewHolder = (TamanhoViewHolder) view.getTag();
		}
		
		
		if (posicaoEscolhida == position) {
			tamanhoViewHolder.rbDescricao.setChecked(true);
			tamanho.setEscolhido(true);
		} else {
			tamanhoViewHolder.rbDescricao.setChecked(false);
			tamanho.setEscolhido(false);
		}
		
		tamanhoViewHolder.rbDescricao.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				posicaoEscolhida = position;
				notifyDataSetChanged();
			}
		});
		
		tamanhoViewHolder.rbDescricao.setText(tamanho.getDescricao());
		
		tamanhoViewHolder.txtPrecoPromocional.setVisibility(View.GONE);
		tamanhoViewHolder.txtPreco.setVisibility(View.GONE);
		
		if (!precoMaiorProduto) {
			tamanhoViewHolder.txtPreco.setVisibility(View.VISIBLE);
			DecimalFormat decimalFormat = new DecimalFormat("0.00");
			
			if (tamanho.isPromocao()) {
				tamanhoViewHolder.txtPrecoPromocional.setText("De R$ " + decimalFormat.format(tamanho.getPreco()).replace(".", ",") + " por");
				tamanhoViewHolder.txtPrecoPromocional.setVisibility(View.VISIBLE);
				tamanhoViewHolder.txtPreco.setText("R$ " + decimalFormat.format(tamanho.getPrecoPromocao()).replace(".", ","));
			} else {
				tamanhoViewHolder.txtPreco.setText("R$ " + decimalFormat.format(tamanho.getPreco()).replace(".", ","));
			}
		}
		
		return view;
	}
	
}
