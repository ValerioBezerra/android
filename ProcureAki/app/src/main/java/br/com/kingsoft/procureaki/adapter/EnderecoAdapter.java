package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.util.Retorno;

public class EnderecoAdapter extends BaseAdapter {
	
	private LayoutInflater inflater;
	private List<Endereco> listaEnderecos;
	
	static class EnderecoViewHolder {
		TextView txtCEPLogradouroNumero;
		TextView txtBairro;
		TextView txtCidadeUF;
		TextView txtComplementoReferencia;
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
			
			enderecoViewHolder.txtCEPLogradouroNumero   = (TextView) view.findViewById(R.id.txtCEPLogradouroNumero);
			enderecoViewHolder.txtBairro                = (TextView) view.findViewById(R.id.txtBairro);
			enderecoViewHolder.txtCidadeUF              = (TextView) view.findViewById(R.id.txtCidadeUF);
			enderecoViewHolder.txtComplementoReferencia = (TextView) view.findViewById(R.id.txtComplementoReferencia);
			
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

		if ((endereco.getComplemento() != null) && (!endereco.getComplemento().trim().equals(""))) {
			enderecoViewHolder.txtComplementoReferencia.setText("Ref.: " + endereco.getComplemento());
		} else {
			enderecoViewHolder.txtComplementoReferencia.setVisibility(View.GONE);
		}
		
		return view;
	}
	
}
