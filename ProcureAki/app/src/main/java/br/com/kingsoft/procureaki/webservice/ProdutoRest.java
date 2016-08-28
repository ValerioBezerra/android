package br.com.kingsoft.procureaki.webservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.model.Produto;
import br.com.kingsoft.procureaki.util.WebServiceException;

public class ProdutoRest extends GenericRest{

	public ProdutoRest() {
		super("produto_json");
	}

	public List<Produto> getProdutos(String descricao) throws Exception {
		
		List<Produto> listaProdutos = new ArrayList<Produto>();
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_produtos_descricao/" + CHAVE_MD5 + "/" + descricao);

		if (resposta[0].equals("200")) {

			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("produtos");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Produto produto = new Produto();
				produto.setId(jsonObject.getInt("bus_id_pro"));
				produto.setDescricao(jsonObject.getString("bus_descricao_pro"));
				produto.setPreco(jsonObject.getDouble("bus_preco_pro"));
				produto.setUrlImagemEmpresa(jsonObject.getString("urlImagemEmpresa"));

				listaProdutos.add(produto);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaProdutos;
	}
	

}
