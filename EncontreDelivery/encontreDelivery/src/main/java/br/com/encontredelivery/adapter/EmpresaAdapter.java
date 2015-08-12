package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Segmento;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmpresaAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Empresa> listaEmpresas;
	private ImageLoader imageLoader;
	
	static class EmpresaViewHolder {
		NetworkImageView nivImagem;
		TextView txtNome;
		TextView txtSegmentos;
		TextView txtEntrega;
		ImageView imgAbertoFechado;
		TextView txtAbertoFechado;
	}	
	
	public EmpresaAdapter(Context context, List<Empresa> listaEmpresas, ImageLoader imageLoader) {
		this.inflater      = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaEmpresas = listaEmpresas;
		this.imageLoader   = imageLoader;
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
			
			empresaViewHolder.nivImagem        = (NetworkImageView) view.findViewById(R.id.nivImagem);
			empresaViewHolder.txtNome          = (TextView) view.findViewById(R.id.txtNome);
			empresaViewHolder.txtSegmentos     = (TextView) view.findViewById(R.id.txtSegmentos);
			empresaViewHolder.txtEntrega       = (TextView) view.findViewById(R.id.txtEntrega);
			empresaViewHolder.imgAbertoFechado = (ImageView) view.findViewById(R.id.imgAbertoFechado);
			empresaViewHolder.txtAbertoFechado = (TextView) view.findViewById(R.id.txtAbertoFechado);
			
			view.setTag(empresaViewHolder);
		} else {
			empresaViewHolder = (EmpresaViewHolder) view.getTag();
		}
		
		String segmentos = "";
		String separador = "";
		
		for (Segmento segmento: empresa.getListaSegmentos()) {
			segmentos += separador + segmento.getDescricao();
			separador  = "/";
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		empresaViewHolder.txtNome.setText(empresa.getNome());
		empresaViewHolder.txtSegmentos.setText(segmentos);
		empresaViewHolder.txtEntrega.setText("R$ " + decimalFormat.format(empresa.getTaxaEntrega()).replace(".", ",") + " / " + empresa.getTempoMedio());
		
		
		if (empresa.isAberto()) {
			empresaViewHolder.imgAbertoFechado.setImageResource(R.drawable.bola_verde);
			empresaViewHolder.txtAbertoFechado.setText("Aberto");
		} else {
			empresaViewHolder.imgAbertoFechado.setImageResource(R.drawable.bola_vermelha);
			empresaViewHolder.txtAbertoFechado.setText("Fechado");			
		}
		
		empresaViewHolder.nivImagem.setImageUrl(empresa.getUrlImagem(), imageLoader);
		empresaViewHolder.nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		empresaViewHolder.nivImagem.setErrorImageResId(R.drawable.ic_launcher);

		return view;
	}
	
}
