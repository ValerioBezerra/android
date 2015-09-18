package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.Adicional;
import br.com.encontredelivery.util.WebServiceException;

public class AdicionalRest extends GenericRest{

	public AdicionalRest() {
		super("adicional_json");
	}
	

	public List<Adicional> getAdicionais(int idProduto) throws Exception {
		List<Adicional> listaAdicionais = new ArrayList<Adicional>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_adicionais_produto/" + CHAVE_MD5 + "/" + idProduto);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("adicionais");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Adicional adicional = new Adicional();
				adicional.setId(jsonObject.getInt("dlv_id_adi"));
				adicional.setDescricao(jsonObject.getString("dlv_descricao_adi"));
				adicional.setValor(jsonObject.getDouble("dlv_valor_adi"));
				
				listaAdicionais.add(adicional);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaAdicionais;
	}
}
