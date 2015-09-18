package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;


import br.com.encontredelivery.activity.DetalhesPedidoActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Pedido;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MeusPedidosAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Pedido> listaPedidos;
	
	static class PedidoViewHolder {
		TextView txtNumeroPedido;
		ImageView imgStatus;
		TextView txtStatus;
		TextView txtNomeEmpresa;
		TextView txtFoneEmpresa;
		TextView txtDescricaoProdutos;
		TextView txtPrecoProdutos;
		TextView txtDesconto;
		TextView txtTaxaEntrega;
		TextView txtFormaPagamento;
		TextView txtTotal;
		Button btnDetalhesPedido;
	}	
	
	public MeusPedidosAdapter(Context context, List<Pedido> listaPedidos) {
		this.context      = context;
		this.inflater     = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaPedidos = listaPedidos;
	}

	@Override
	public int getCount() {
		return listaPedidos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaPedidos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaPedidos.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Pedido pedido = listaPedidos.get(position);
		PedidoViewHolder PedidoViewHolder;
		
		if (view == null) {
		
			view = inflater.inflate(R.layout.adapter_meus_pedidos, parent, false);
			
			PedidoViewHolder = new PedidoViewHolder();
			
			PedidoViewHolder.txtNumeroPedido 	  = (TextView) view.findViewById(R.id.txtNumeroPedido);
			PedidoViewHolder.imgStatus       	  = (ImageView) view.findViewById(R.id.imgStatus);
			PedidoViewHolder.txtStatus 			  = (TextView) view.findViewById(R.id.txtStatus);
			PedidoViewHolder.txtNomeEmpresa       = (TextView) view.findViewById(R.id.txtNomeEmpresa);
			PedidoViewHolder.txtFoneEmpresa    	  = (TextView) view.findViewById(R.id.txtFoneEmpresa);
			PedidoViewHolder.txtDescricaoProdutos = (TextView) view.findViewById(R.id.txtDescricaoProdutos);
			PedidoViewHolder.txtPrecoProdutos     = (TextView) view.findViewById(R.id.txtPrecoProdutos);
			PedidoViewHolder.txtDesconto		  = (TextView) view.findViewById(R.id.txtDesconto);
			PedidoViewHolder.txtTaxaEntrega		  = (TextView) view.findViewById(R.id.txtTaxaEntrega);
			PedidoViewHolder.txtFormaPagamento    = (TextView) view.findViewById(R.id.txtFormaPagamento);
			PedidoViewHolder.txtTotal     		  = (TextView) view.findViewById(R.id.txtTotal);
			PedidoViewHolder.btnDetalhesPedido  = (Button) view.findViewById(R.id.btnDetalhesPedido);
			
			view.setTag(PedidoViewHolder);
		} else {
			PedidoViewHolder = (PedidoViewHolder) view.getTag();
		}
		
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		PedidoViewHolder.txtNumeroPedido.setText("Nro Pedido: " + pedido.getId());

		if (pedido.getStatus() == null) {
			PedidoViewHolder.imgStatus.setVisibility(View.GONE);
			PedidoViewHolder.txtStatus.setVisibility(View.GONE);
		} else {
			if (pedido.getStatus().getIndicador() != 3) {
				PedidoViewHolder.imgStatus.setImageResource(R.drawable.ball_verde);
				
			} else {
				PedidoViewHolder.imgStatus.setImageResource(R.drawable.ball_vermelha);
			}
			PedidoViewHolder.txtStatus.setText(pedido.getStatus().getDescricao());
		}
		
		PedidoViewHolder.txtNomeEmpresa.setText(pedido.getEmpresa().getNome());

		String fones         = "";
		String separadorFone = "";
		for (String fone: pedido.getEmpresa().getListaFones()) {
			fones         += separadorFone + fone;
			separadorFone  = "\n";
		}
		PedidoViewHolder.txtFoneEmpresa.setText(fones);
		PedidoViewHolder.txtDescricaoProdutos.setText(pedido.getDescricaoProdutos());
		PedidoViewHolder.txtPrecoProdutos.setText(pedido.getPrecoProdutos());
		PedidoViewHolder.txtDesconto.setText("R$ " + decimalFormat.format(pedido.getDesconto()).replace(".", ","));
		PedidoViewHolder.txtTaxaEntrega.setText("R$ " + decimalFormat.format(pedido.getTaxaEntrega()).replace(".", ","));
		PedidoViewHolder.txtFormaPagamento.setText(pedido.getFormaPagamento().getDescricao());
		PedidoViewHolder.txtTotal.setText("R$ " + decimalFormat.format(pedido.getValorTotal() - pedido.getDesconto() + pedido.getTaxaEntrega()).replace(".", ","));
		
		PedidoViewHolder.btnDetalhesPedido.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle extras = new Bundle();
				extras.putLong("idPedido", pedido.getId());
				extras.putSerializable("empresa", pedido.getEmpresa());
				
				Intent intent = new Intent(context, DetalhesPedidoActivity.class);
				intent.putExtras(extras);
				context.startActivity(intent);
			}
		});
		
		return view;
	}
	
}
