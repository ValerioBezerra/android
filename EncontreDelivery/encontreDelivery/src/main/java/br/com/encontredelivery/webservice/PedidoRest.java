package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import br.com.encontredelivery.model.Adicional;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.Pedido;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.model.ProdutoPedido;
import br.com.encontredelivery.model.Status;
import br.com.encontredelivery.model.Voucher;
import br.com.encontredelivery.util.WebServiceException;

public class PedidoRest extends GenericRest{

	public PedidoRest() {
		super("pedido_json");
	}

	public String enviar(Empresa empresa, Cliente cliente, Endereco endereco, List<ProdutoPedido> listaProdutosPedido, FormaPagamento formaPagamento, Voucher voucher, double desconto, double troco) throws Exception {
		JSONObject json = new JSONObject(); 
		
        json.put("dlv_dlvemp_ped", empresa.getId()); 
        json.put("dlv_dlvcli_ped", cliente.getId());
        json.put("dlv_gloend_ped", endereco.getId());
        json.put("dlv_numero_ped", endereco.getNumero());
        json.put("dlv_complemento_ped", endereco.getComplemento());
        json.put("dlv_taxaentrega_ped", empresa.getTaxaEntrega());
        
        if (voucher != null) {
        	 json.put("dlv_dlvvou_ped", voucher.getId());
        }
        
        json.put("dlv_desconto_ped", desconto);
        json.put("dlv_dlvfpg_ped", formaPagamento.getId());
        json.put("dlv_troco_ped", troco);
        json.put("dlv_origem_ped", "a");
        
        JSONArray jsonArrayProdutosPedido = new JSONArray();
        for (ProdutoPedido produtoPedido: listaProdutosPedido) {
        	JSONObject jsonProdutoPedido = new JSONObject(); 
        	jsonProdutoPedido.put("dlv_dlvpro_ppe", produtoPedido.getProduto().getId());
        	
        	if (!produtoPedido.getProduto().getListaTamanhos().isEmpty()) {
        		jsonProdutoPedido.put("dlv_dlvtam_ppe", produtoPedido.getProduto().getListaTamanhos().get(0).getId());
        	} else {
        		jsonProdutoPedido.put("dlv_dlvtam_ppe", null);
        	}
        	
        	jsonProdutoPedido.put("dlv_opcao_ppe", produtoPedido.getOpcao());
        	jsonProdutoPedido.put("dlv_quantidade_ppe", produtoPedido.getQuantidade());
        	jsonProdutoPedido.put("dlv_preco_ppe", produtoPedido.getPreco());
        	jsonProdutoPedido.put("dlv_observacao_ppe", produtoPedido.getObservacao());
        	jsonProdutoPedido.put("dlv_escolheproduto_ppe", (produtoPedido.getProduto().isEscolheProduto()?1:0));
        	jsonProdutoPedido.put("dlv_quantidadeproduto_ppe", produtoPedido.getProduto().getQuantidade());
        	jsonProdutoPedido.put("dlv_precomaiorproduto_ppe", (produtoPedido.getProduto().isPrecoMaiorProduto()?1:0));
        	jsonProdutoPedido.put("dlv_exibirfracaoproduto_ppe", (produtoPedido.getProduto().isExibirFracao()?1:0));
        	
        	if (!produtoPedido.getProduto().getListaProdutosEscolhidos().isEmpty()) {
        		JSONArray jsonArrayProdutosEscolhidos = new JSONArray();
        		for (ProdutoEscolhido produtoEscolhido: produtoPedido.getProduto().getListaProdutosEscolhidos()) {
        			JSONObject jsonProdutoEscolhido = new JSONObject(); 
        			jsonProdutoEscolhido.put("dlv_dlvpro_ppp", produtoEscolhido.getProduto().getId());
        			jsonProdutoEscolhido.put("dlv_opcao_ppp", produtoEscolhido.getOpcao());
        			jsonProdutoEscolhido.put("dlv_quantidade_ppp", produtoEscolhido.getQuantidade());
        			
        			jsonArrayProdutosEscolhidos.put(jsonProdutoEscolhido);
        			
        		}
        		jsonProdutoPedido.put("dlv_ppp", jsonArrayProdutosEscolhidos);
        	}
        	
        	if (!produtoPedido.getListaAdicionais().isEmpty()) {
        		JSONArray jsonArrayAdicionais = new JSONArray();
        		for (Adicional adicional: produtoPedido.getListaAdicionais()) {
        			JSONObject jsonAdicional = new JSONObject(); 
        			jsonAdicional.put("dlv_dlvadi_ppa", adicional.getId());
        			jsonAdicional.put("dlv_valor_ppa", adicional.getValor());
        			
        			jsonArrayAdicionais.put(jsonAdicional);
        			
        		}
        		jsonProdutoPedido.put("dlv_ppa", jsonArrayAdicionais);
        	}        	
        	
        	jsonArrayProdutosPedido.put(jsonProdutoPedido);
        }
        json.put("dlv_ppe", jsonArrayProdutosPedido);
        
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "enviar/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}
	
	public boolean verificarRecebimento(long idPedido) throws Exception {
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "verificar_recebimento/" + CHAVE_MD5 + "/" + idPedido);

		if (resposta[0].equals("200")) {
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				return (jsonObject.getInt("dlv_recebido_ped") == 1);
			}

			return false;
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	
	
	public String cancelarAutomatico(long idPedido) throws Exception {
		JSONObject json = new JSONObject(); 
        json.put("dlv_id_ped", idPedido); 
       
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "cancelar_automatico/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	
	
	public List<Pedido> getPedidosCliente(long idCliente, int opcao) throws Exception {
		List<Pedido> listaPedidos = new ArrayList<Pedido>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_pedidos_cliente/" + CHAVE_MD5 + "/" + idCliente + "/" + opcao);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("pedidos");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject       = jsonArray.getJSONObject(i);
				
				Pedido pedido = new Pedido();
				pedido.setId(jsonObject.getLong("dlv_id_ped"));
				
				if (jsonObject.getString("status").equals("[]")) {
					pedido.setStatus(null);
				} else {
					JSONObject jsonObjectStatus = new JSONObject(jsonObject.getString("status"));
					
					Status status = new Status();
					status.setDescricao(jsonObjectStatus.getString("dlv_descricao_sta"));
					status.setIndicador(jsonObjectStatus.getInt("dlv_indicador_sta"));
					
					pedido.setStatus(status);
				}
				
				Empresa empresa = new Empresa();
				empresa.setNome(jsonObject.getString("dlv_nome_emp"));
				empresa.setFone(jsonObject.getString("dlv_fone_emp"));
				pedido.setEmpresa(empresa);
				
				pedido.setDescricaoProdutos(jsonObject.getString("descricao_produtos"));
				pedido.setPrecoProdutos(jsonObject.getString("preco_produtos"));
				pedido.setDesconto(jsonObject.getDouble("dlv_desconto_ped"));
				pedido.setTaxaEntrega(jsonObject.getDouble("dlv_taxaentrega_ped"));
				
				FormaPagamento formaPagamento = new FormaPagamento();
				formaPagamento.setDescricao(jsonObject.getString("dlv_descricao_fpg"));
				pedido.setFormaPagamento(formaPagamento);
				
				pedido.setValorTotal(jsonObject.getDouble("valor_total"));
				
				listaPedidos.add(pedido);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaPedidos;
	}
	
	public List<Status> getStatusPedido(long idPedido) throws Exception {
		List<Status> listaStatus = new ArrayList<Status>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_status_pedido/" + CHAVE_MD5 + "/" + idPedido);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("status");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Status status = new Status();
				status.setDescricao(jsonObject.getString("dlv_descricao_sta"));
				status.setIndicador(jsonObject.getInt("dlv_indicador_sta"));
				status.setDataHora(jsonObject.getString("dlv_datahoramod_spe"));
				status.setMotivoCancelamento(jsonObject.getString("dlv_motivocanc_spe"));
				
				listaStatus.add(status);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaStatus;
	}
	
	

}
