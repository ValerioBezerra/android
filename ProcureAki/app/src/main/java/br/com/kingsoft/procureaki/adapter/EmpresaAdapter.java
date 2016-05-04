package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Empresa;


public class EmpresaAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Empresa> listaEmpresas;
	
	static class EmpresaViewHolder {
		TextView txtNome;
		TextView txtEnderecoNumero;
		TextView txtBairroCidadeUf;
		TextView txtQuantidadeDiasAtualizacao;
		TextView txtDistanciaKm;
	}
	
	public EmpresaAdapter(Context context, List<Empresa> listaEmpresas) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaEmpresas = listaEmpresas;
	}

	@Override
	public int getCount() {
		return listaEmpresas.size();
	}

	@Override
	public Object getItem(int position) {
		return listaEmpresas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaEmpresas.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Empresa empresa = listaEmpresas.get(position);
		EmpresaViewHolder empresaViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_empresa, parent, false);
			
			empresaViewHolder = new EmpresaViewHolder();
		
			empresaViewHolder.txtNome                      = (TextView) view.findViewById(R.id.txtNome);
			empresaViewHolder.txtEnderecoNumero            = (TextView) view.findViewById(R.id.txtEnderecoNumero);
			empresaViewHolder.txtBairroCidadeUf            = (TextView) view.findViewById(R.id.txtBairroCidadeUf);
			empresaViewHolder.txtQuantidadeDiasAtualizacao = (TextView) view.findViewById(R.id.txtQuantidadeDiasAtualizacao);
			empresaViewHolder.txtDistanciaKm               = (TextView) view.findViewById(R.id.txtDistanciaKm);

			view.setTag(empresaViewHolder);
		} else {
			empresaViewHolder = (EmpresaViewHolder) view.getTag();
		}
		
		empresaViewHolder.txtNome.setText(empresa.getNome());
		empresaViewHolder.txtEnderecoNumero.setText(empresa.getEndereco().getLogradouro() + ", " + empresa.getEndereco().getNumero());
		empresaViewHolder.txtBairroCidadeUf.setText(empresa.getEndereco().getBairro().getNome() + ". " + empresa.getEndereco().getBairro().getCidade().getNome() + "-" + empresa.getEndereco().getBairro().getCidade().getUf());
		empresaViewHolder.txtQuantidadeDiasAtualizacao.setText(empresa.getQuantidadeDiasAtualizacao() + " dia(s)");
		empresaViewHolder.txtDistanciaKm.setText(new DecimalFormat("0.00").format(empresa.getDistanciaKm()) + " Km");

		return view;
	}
	
}
