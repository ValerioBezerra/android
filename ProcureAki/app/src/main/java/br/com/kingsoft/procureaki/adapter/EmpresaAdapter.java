package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.text.DecimalFormat;
import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Empresa;


public class EmpresaAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Empresa> listaEmpresas;
	private ImageLoader imageLoader;

	static class EmpresaViewHolder {
		NetworkImageView nivImagem;
		TextView txtNome;
		ImageView imgAbertoFechado;
		TextView txtDistancia;
	}
	
	public EmpresaAdapter(Context context, List<Empresa> listaEmpresas, ImageLoader imageLoader) {
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaEmpresas = listaEmpresas;
		this.imageLoader    = imageLoader;
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
			empresaViewHolder.imgAbertoFechado = (ImageView) view.findViewById(R.id.imgAbertoFechado);
			empresaViewHolder.txtDistancia     = (TextView) view.findViewById(R.id.txtDistancia);

			view.setTag(empresaViewHolder);
		} else {
			empresaViewHolder = (EmpresaViewHolder) view.getTag();
		}
		
		empresaViewHolder.txtNome.setText(empresa.getNome());

		if (empresa.isAberto())
			empresaViewHolder.imgAbertoFechado.setImageResource(R.drawable.paki_aberto);
		else
			empresaViewHolder.imgAbertoFechado.setImageResource(R.drawable.paki_fechado);

		empresaViewHolder.txtDistancia.setText(new DecimalFormat("0.0").format(empresa.getDistanciaKm()) + " Km");

		empresaViewHolder.nivImagem.setImageUrl(empresa.getUrlImagem(), imageLoader);
		empresaViewHolder.nivImagem.setDefaultImageResId(R.drawable.ic_launcher);
		empresaViewHolder.nivImagem.setErrorImageResId(R.drawable.ic_launcher);


		return view;
	}
	
}
