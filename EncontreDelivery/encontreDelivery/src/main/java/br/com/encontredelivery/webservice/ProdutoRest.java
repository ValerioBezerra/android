package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.Produto;
import br.com.encontredelivery.model.ProdutoEscolhido;
import br.com.encontredelivery.model.Tamanho;
import br.com.encontredelivery.util.WebServiceException;

public class ProdutoRest extends GenericRest{

	public ProdutoRest() {
		super("produto_json");
	}
	

	public List<Produto> getProdutos(int idCategoria) throws Exception {
		List<Produto> listaProdutos = new ArrayList<Produto>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_produtos_categoria/" + CHAVE_MD5 + "/" + idCategoria);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("produtos");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Produto produto = new Produto();
				produto.setId(jsonObject.getInt("dlv_id_pro"));
				produto.setDescricao(jsonObject.getString("dlv_descricao_pro"));
				produto.setPreco(jsonObject.getDouble("dlv_preco_pro"));
				produto.setPromocao(jsonObject.getInt("dlv_promocao_pro") == 1);
				produto.setDestaque(jsonObject.getInt("dlv_destaque_pro") == 1);
				produto.setPrecoPromocao(jsonObject.getDouble("dlv_precopromocional_pro"));
				produto.setEscolheProduto(jsonObject.getInt("dlv_escolheproduto_pro") == 1);
				produto.setPrecoMaiorProduto(jsonObject.getInt("dlv_precomaiorproduto_pro") == 1);
				produto.setUsaTamanho(jsonObject.getInt("usa_tamanho") == 1);
				
				listaProdutos.add(produto);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaProdutos;
	}
	
	public Produto getProduto(Produto produto) throws Exception {
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_informacoes_produto/" + CHAVE_MD5 + "/" + produto.getId());

		if (resposta[0].equals("200")) {
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				
				produto.setDetalhamento(jsonObject.getString("dlv_detalhamento_pro"));
				produto.setTempoPreparo(jsonObject.getString("dlv_tempopreparo_pro"));
				produto.setSomenteUmAdicional(jsonObject.getInt("dlv_umadicional_pro") == 1);
				produto.setQuantidade(jsonObject.getInt("dlv_quantidade_pro"));
				produto.setUnidade(jsonObject.getString("dlv_unidade_pro"));
				produto.setExibirFracao(jsonObject.getInt("dlv_exibirfracao_pro") == 1);
				produto.setMaximoProduto(jsonObject.getInt("dlv_maxproduto_pro"));
				produto.setMinimoProduto(jsonObject.getInt("dlv_minproduto_pro"));
				produto.setUrlImagem(jsonObject.getString("url_imagem"));
				
				JSONArray jsonArrayTamanho = new JSONObject(resposta[1]).getJSONArray("tamanhos");	
				List<Tamanho> listaTamanhos = new ArrayList<Tamanho>();
				for (int i = 0; i < jsonArrayTamanho.length(); i++) {
					JSONObject jsonObjectTamanho = jsonArrayTamanho.getJSONObject(i);
					
					Tamanho tamanho = new Tamanho();
					tamanho.setId(jsonObjectTamanho.getInt("dlv_id_tam"));
					tamanho.setDescricao(jsonObjectTamanho.getString("dlv_descricao_tam"));
					tamanho.setQuantidade(jsonObjectTamanho.getInt("dlv_quantidade_tam"));
					tamanho.setPreco(jsonObjectTamanho.getDouble("dlv_preco_pxt"));
					tamanho.setPromocao(jsonObjectTamanho.getInt("dlv_promocao_pxt") == 1);
					tamanho.setPrecoPromocao(jsonObjectTamanho.getDouble("dlv_precopromocional_pxt"));
					
					listaTamanhos.add(tamanho);
				}
				produto.setListaTamanhos(listaTamanhos);				
				
				JSONArray jsonArrayProduto = new JSONObject(resposta[1]).getJSONArray("produtos");				
				List<ProdutoEscolhido> listaProdutosEscolhidos = new ArrayList<ProdutoEscolhido>();				
				for (int i = 0; i < jsonArrayProduto.length(); i++){
					JSONObject jsonObjectProduto = jsonArrayProduto.getJSONObject(i);
					
					ProdutoEscolhido produtoEscolhido = new ProdutoEscolhido();
					
					Produto produtoProduto = new Produto(); 
					produtoProduto.setId(jsonObjectProduto.getInt("dlv_id_pro"));
					produtoProduto.setDescricao(jsonObjectProduto.getString("dlv_descricao_pro"));
					produtoProduto.setDetalhamento(jsonObjectProduto.getString("dlv_detalhamento_pro"));
					produtoProduto.setPreco(jsonObjectProduto.getDouble("dlv_preco_pro"));
					produtoProduto.setPromocao(jsonObjectProduto.getInt("dlv_promocao_pro") == 1);
					produtoProduto.setPrecoPromocao(jsonObjectProduto.getDouble("dlv_precopromocional_pro"));
					produtoProduto.setUrlImagem(jsonObjectProduto.getString("url_imagem"));
					
					jsonArrayTamanho = jsonObjectProduto.getJSONArray("tamanhos");	
					listaTamanhos = new ArrayList<Tamanho>();
					for (int j = 0; j < jsonArrayTamanho.length(); j++){
						JSONObject jsonObjectTamanho = jsonArrayTamanho.getJSONObject(j);
						
						Tamanho tamanho = new Tamanho();
						tamanho.setId(jsonObjectTamanho.getInt("dlv_id_tam"));
						tamanho.setDescricao(jsonObjectTamanho.getString("dlv_descricao_tam"));
						tamanho.setQuantidade(jsonObjectTamanho.getInt("dlv_quantidade_tam"));
						tamanho.setPreco(jsonObjectTamanho.getDouble("dlv_preco_pxt"));
						tamanho.setPromocao(jsonObjectTamanho.getInt("dlv_promocao_pxt") == 1);
						tamanho.setPrecoPromocao(jsonObjectTamanho.getDouble("dlv_precopromocional_pxt"));
						
						listaTamanhos.add(tamanho);
					}
					produtoProduto.setListaTamanhos(listaTamanhos);
					
					produtoEscolhido.setProduto(produtoProduto);
					
					listaProdutosEscolhidos.add(produtoEscolhido);
				}				
				produto.setListaProdutosEscolhidos(listaProdutosEscolhidos);				
			}

			return produto;
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	
	
}
