package br.com.encontredelivery.adapter;

import java.text.DecimalFormat;
import java.util.List;

import br.com.encontredelivery.activity.CarrinhoActivity;
import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ConfirmacaoDialog;
import br.com.encontredelivery.model.Adicional;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.model.ProdutoPedido;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class ProdutoPedidoAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<ProdutoPedido> listaProdutoPedidos;
	
	static class ProdutoPedidoViewHolder {
		TextView txtTamanho;
		TextView txtDescricao;
		TextView txtProdutosEscolhidos;
		TextView txtAdicionais;
		TextView txtObservacao;
		Button btnDiminuir;
		TextView txtQuantidade;
		Button btnAumentar;
		TextView txtValorUnitario;
		TextView txtValorTotal;
	}		
	
	public ProdutoPedidoAdapter(Context context, List<ProdutoPedido> listaProdutoPedidos) {
		this.context             = context;
		this.inflater            = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaProdutoPedidos = listaProdutoPedidos;
	}

	@Override
	public int getCount() {
		return listaProdutoPedidos.size();
	}

	@Override
	public Object getItem(int position) {
		return listaProdutoPedidos.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final ProdutoPedido produtoPedido = listaProdutoPedidos.get(position);
		final ProdutoPedidoViewHolder produtoPedidoViewHolder;
		double valorAdicionais      = 0;
		DecimalFormat decimalFormat = new DecimalFormat("0.00");
		
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_produto_pedido, parent, false);
			
			produtoPedidoViewHolder = new ProdutoPedidoViewHolder();
		
			produtoPedidoViewHolder.txtTamanho		      = (TextView) view.findViewById(R.id.txtTamanho);
			produtoPedidoViewHolder.txtDescricao          = (TextView) view.findViewById(R.id.txtDescricao);
			produtoPedidoViewHolder.txtProdutosEscolhidos = (TextView) view.findViewById(R.id.txtProdutosEscolhidos);
			produtoPedidoViewHolder.txtAdicionais         = (TextView) view.findViewById(R.id.txtAdicionais);
			produtoPedidoViewHolder.txtObservacao         = (TextView) view.findViewById(R.id.txtObservacao);
			produtoPedidoViewHolder.btnDiminuir           = (Button) view.findViewById(R.id.btnDiminuir);
			produtoPedidoViewHolder.txtQuantidade         = (TextView) view.findViewById(R.id.txtQuantidade);
			produtoPedidoViewHolder.btnAumentar           = (Button) view.findViewById(R.id.btnAumentar);
			produtoPedidoViewHolder.txtValorUnitario      = (TextView) view.findViewById(R.id.txtValorUnitario);
			produtoPedidoViewHolder.txtValorTotal         = (TextView) view.findViewById(R.id.txtValorTotal);

			view.setTag(produtoPedidoViewHolder);
		} else {
			produtoPedidoViewHolder = (ProdutoPedidoViewHolder) view.getTag();
		}
		
		int quantidadeUsada = produtoPedido.getProduto().getQuantidade();
		
		if (produtoPedido.getProduto().getListaTamanhos().isEmpty()) {
			produtoPedidoViewHolder.txtTamanho.setVisibility(View.GONE);
		} else {
			produtoPedidoViewHolder.txtTamanho.setVisibility(View.VISIBLE);			
			produtoPedidoViewHolder.txtTamanho.setText("Tamanho: " + produtoPedido.getProduto().getListaTamanhos().get(0).getDescricao());
			quantidadeUsada = produtoPedido.getProduto().getListaTamanhos().get(0).getQuantidade(); 
		}
		
		
		produtoPedidoViewHolder.txtDescricao.setText(produtoPedido.getProduto().getDescricao());
		
		
		produtoPedidoViewHolder.txtProdutosEscolhidos.setVisibility(View.GONE);
		produtoPedidoViewHolder.txtAdicionais.setVisibility(View.GONE);
		
		if ((!produtoPedido.getProduto().getListaProdutosEscolhidos().isEmpty()) && (produtoPedido.getProduto().isEscolheProduto())) {
			produtoPedidoViewHolder.txtProdutosEscolhidos.setVisibility(View.VISIBLE);
			
			if (!produtoPedido.getProduto().isExibirFracao()) {
				if (quantidadeUsada == 1) {
					produtoPedidoViewHolder.txtProdutosEscolhidos.setText("• " + produtoPedido.getProduto().getListaProdutosEscolhidos().get(0).getProduto().getDescricao());
				} else {
					String separador               = "";
					String textoProdutosEscolhidos = "";
					
					for (ProdutoEscolhido produtoEscolhido: produtoPedido.getProduto().getListaProdutosEscolhidos()) {
						textoProdutosEscolhidos += separador + "• " + produtoEscolhido.getQuantidade() + "x " + produtoEscolhido.getProduto().getDescricao();
						separador                = "\n";
					}
					
					produtoPedidoViewHolder.txtProdutosEscolhidos.setText(textoProdutosEscolhidos);
				}
				
			} else {
				String separador               = "";
				String textoProdutosEscolhidos = "";
				
				for (ProdutoEscolhido produtoEscolhido: produtoPedido.getProduto().getListaProdutosEscolhidos()) {
					if (produtoPedido.getOpcao() != 3) {
						textoProdutosEscolhidos += separador + "• 1";
					} else {
						if (produtoEscolhido.getOpcao() == 1) {
							textoProdutosEscolhidos += separador + "• 2";
						} else {
							textoProdutosEscolhidos += separador + "• 1";
						}
					}
					
					if (produtoPedido.getOpcao() == 2) {
						textoProdutosEscolhidos += "/2";
					} else if (produtoPedido.getOpcao() >= 3) { 
						textoProdutosEscolhidos += "/4";
					}
					
					textoProdutosEscolhidos += " " + produtoEscolhido.getProduto().getDescricao();
					separador                = "\n";
				}
				
				produtoPedidoViewHolder.txtProdutosEscolhidos.setText(textoProdutosEscolhidos);
			}
		}
		
		if (!produtoPedido.getListaAdicionais().isEmpty()) {
			produtoPedidoViewHolder.txtAdicionais.setVisibility(View.VISIBLE);
			
			String separador            = "";
			String textoAdicionais      = "";
			
			for (Adicional adicional: produtoPedido.getListaAdicionais()) {
				textoAdicionais += separador + "+ " + adicional.getDescricao();

				if (adicional.getValor() > 0)
					textoAdicionais += " (R$ " + decimalFormat.format(adicional.getValor()).replace(".", ",") + ")";

				separador        = "\n";
				valorAdicionais += adicional.getValor();
			}
			
			produtoPedidoViewHolder.txtAdicionais.setText(textoAdicionais);
		}

		if (produtoPedido.getObservacao().trim().equals(""))
			produtoPedidoViewHolder.txtObservacao.setVisibility(View.GONE);
		else {
			produtoPedidoViewHolder.txtObservacao.setVisibility(View.VISIBLE);
			produtoPedidoViewHolder.txtObservacao.setText("Observação: " + produtoPedido.getObservacao());
		}

		produtoPedidoViewHolder.txtQuantidade.setText(String.valueOf(produtoPedido.getQuantidade()));
		produtoPedidoViewHolder.txtValorUnitario.setText("R$ " + decimalFormat.format(produtoPedido.getPreco() + valorAdicionais).replace(".", ","));
		produtoPedidoViewHolder.txtValorTotal.setText("R$ " + decimalFormat.format(produtoPedido.getQuantidade() * (produtoPedido.getPreco() + valorAdicionais)).replace(".", ","));
		
		produtoPedidoViewHolder.btnDiminuir.setEnabled(produtoPedido.getQuantidade() > 0);
		produtoPedidoViewHolder.btnAumentar.setEnabled(produtoPedido.getQuantidade() < 99);
		
		produtoPedidoViewHolder.btnDiminuir.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (produtoPedido.getQuantidade() > 1) {
                    produtoPedido.setQuantidade(produtoPedido.getQuantidade() - 1);
                    notifyDataSetChanged();
                } else {
                    final ConfirmacaoDialog confirmacaoDialog = new ConfirmacaoDialog(context);
                    confirmacaoDialog.setTitle("Atenção");
                    confirmacaoDialog.setMessage("Deseja realmente remover este produto?");

                    Button btnSim = (Button) confirmacaoDialog.findViewById(R.id.btnSim);
                    btnSim.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmacaoDialog.dismiss();

                            listaProdutoPedidos.remove(produtoPedido);
                            notifyDataSetChanged();
                        }
                    });

                    confirmacaoDialog.show();
                }
            }
        });
		
		produtoPedidoViewHolder.btnAumentar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                produtoPedido.setQuantidade(produtoPedido.getQuantidade() + 1);
                notifyDataSetChanged();
            }
        });
		
		return view;
	}

	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
		((CarrinhoActivity) context).verificarListaProdutosPedido();
		((CarrinhoActivity) context).calcularTotal();
	}
	
}
