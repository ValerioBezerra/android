package br.com.encontredelivery.adapter;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ProdutoEscolhidoDetalheDialog;
import br.com.encontredelivery.model.ProdutoEscolhido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProdutoEscolhidoQuantidadeAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<ProdutoEscolhido> listaProdutoEscolhidos;
	private int maximoQuantidade;
	private int maximoProduto;
	private int totalQuantidade;
	
	static class ProdutoEscolhidoViewHolder {
		 Button btnDiminuir;
		 TextView txtQuantidade;
		 Button btnAumentar;
		 TextView txtDescricao;
		 ImageButton ibtProduto;
	}		
	
	public ProdutoEscolhidoQuantidadeAdapter(Context context, int maximoQuantidade, int maximoProduto, List<ProdutoEscolhido> listaProdutoEscolhidos) {
		this.context                = context;
		this.inflater               = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.maximoQuantidade       = maximoQuantidade; 
		this.maximoProduto          = maximoProduto;
		this.listaProdutoEscolhidos = listaProdutoEscolhidos;
		this.totalQuantidade        = 0;
	}

	@Override
	public int getCount() {
		return listaProdutoEscolhidos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaProdutoEscolhidos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ProdutoEscolhido produtoEscolhido = listaProdutoEscolhidos.get(position);
		ProdutoEscolhidoViewHolder produtoEscolhidoViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_produto_escolhido_quantidade, parent, false);
			
			produtoEscolhidoViewHolder = new ProdutoEscolhidoViewHolder();
			
			produtoEscolhidoViewHolder.btnDiminuir   = (Button) view.findViewById(R.id.btnDiminuir);
			produtoEscolhidoViewHolder.txtQuantidade = (TextView) view.findViewById(R.id.txtQuantidade);
			produtoEscolhidoViewHolder.btnAumentar   = (Button) view.findViewById(R.id.btnAumentar);
			produtoEscolhidoViewHolder.txtDescricao  = (TextView) view.findViewById(R.id.txtDescricao);
			produtoEscolhidoViewHolder.ibtProduto    = (ImageButton) view.findViewById(R.id.ibtProduto);
			
			view.setTag(produtoEscolhidoViewHolder);
		} else {
			produtoEscolhidoViewHolder = (ProdutoEscolhidoViewHolder) view.getTag();
		}
		
		produtoEscolhidoViewHolder.txtQuantidade.setText(String.valueOf(produtoEscolhido.getQuantidade()));
		produtoEscolhidoViewHolder.txtDescricao.setText(produtoEscolhido.getProduto().getDescricao());
		
		if (produtoEscolhido.getQuantidade() <= 0) {
			produtoEscolhidoViewHolder.btnDiminuir.setEnabled(false);
		} else {
			produtoEscolhidoViewHolder.btnDiminuir.setEnabled(true);
		}
		
		if ((totalQuantidade < maximoQuantidade) && ((maximoProduto == 0) || (maximoProduto > produtoEscolhido.getQuantidade()))) {
			produtoEscolhidoViewHolder.btnAumentar.setEnabled(true);
		} else {
			produtoEscolhidoViewHolder.btnAumentar.setEnabled(false);
		}
		
		produtoEscolhidoViewHolder.btnDiminuir.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				produtoEscolhido.setQuantidade(produtoEscolhido.getQuantidade() - 1);
				totalQuantidade--;
				notifyDataSetChanged();
			}
		});
		
		produtoEscolhidoViewHolder.btnAumentar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				produtoEscolhido.setQuantidade(produtoEscolhido.getQuantidade() + 1);
				totalQuantidade++;
				notifyDataSetChanged();
			}
		});	
		
		if ((produtoEscolhido.getProduto().getDetalhamento().trim().equals("")) && (produtoEscolhido.getProduto().getUrlImagem().trim().equals(""))) {
			produtoEscolhidoViewHolder.ibtProduto.setVisibility(View.GONE);
		} else {
			produtoEscolhidoViewHolder.ibtProduto.setVisibility(View.VISIBLE);
			produtoEscolhidoViewHolder.ibtProduto.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					ProdutoEscolhidoDetalheDialog produtoEscolhidoDialog = new ProdutoEscolhidoDetalheDialog(context, produtoEscolhido);
					produtoEscolhidoDialog.show();
				}
			});
		}
		
		return view;
	}
	
}
