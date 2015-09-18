package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.util.WebServiceException;

public class CategoriaRest extends GenericRest{

	public CategoriaRest() {
		super("categoria_json");
	}
	

	public List<Categoria> getCategorias(int idEmpresa) throws Exception {
		List<Categoria> listaCategorias = new ArrayList<Categoria>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_categorias_empresa/" + CHAVE_MD5 + "/" + idEmpresa);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("categorias");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Categoria categoria = new Categoria();
				categoria.setId(jsonObject.getInt("dlv_id_cat"));
				categoria.setDescricao(jsonObject.getString("dlv_descricao_cat"));
				
				listaCategorias.add(categoria);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaCategorias;
	}
}
