package br.com.encontredelivery.adapter;

import java.util.List;


import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.Retorno;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class EnderecoAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<Endereco> listaEnderecos;
	
	static class EnderecoViewHolder {
		TextView txtCEPLogradouroNumero;
		TextView txtBairro;
		TextView txtCidadeUF;
	}	
	
	public EnderecoAdapter(Context context, List<Endereco> listaEnderecos) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaEnderecos = listaEnderecos;
	}

	@Override
	public int getCount() {
		return listaEnderecos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaEnderecos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaEnderecos.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Endereco endereco = listaEnderecos.get(position);
		EnderecoViewHolder enderecoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_endereco, parent, false);
			
			enderecoViewHolder = new EnderecoViewHolder();
			
			enderecoViewHolder.txtCEPLogradouroNumero = (TextView) view.findViewById(R.id.txtCEPLogradouroNumero);
			enderecoViewHolder.txtBairro              = (TextView) view.findViewById(R.id.txtBairro);
			enderecoViewHolder.txtCidadeUF            = (TextView) view.findViewById(R.id.txtCidadeUF);
			
			view.setTag(enderecoViewHolder);
		} else {
			enderecoViewHolder = (EnderecoViewHolder) view.getTag();
		}

		
		enderecoViewHolder.txtCEPLogradouroNumero.setText("(" + Retorno.getMascaraCep(endereco.getCep() + ") " + endereco.getLogradouro()));
		enderecoViewHolder.txtBairro.setText(endereco.getBairro().getNome());
		enderecoViewHolder.txtCidadeUF.setText(endereco.getBairro().getCidade().getNome() + " - " + endereco.getBairro().getCidade().getUf());
		
		if (!endereco.getNumero().trim().equals("")) {
			enderecoViewHolder.txtCEPLogradouroNumero.setText(enderecoViewHolder.txtCEPLogradouroNumero.getText().toString() + ", " + endereco.getNumero());
		}
		
		return view;
	}
	
}
