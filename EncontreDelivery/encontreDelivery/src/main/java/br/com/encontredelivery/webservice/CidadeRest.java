package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.Cidade;
import br.com.encontredelivery.util.WebServiceException;

public class CidadeRest extends GenericRest{

	public CidadeRest() {
		super("endereco_json");
	}

	public List<Cidade> getCidades() throws Exception {
		
		List<Cidade> listaCidades = new ArrayList<Cidade>();
		Cidade cidade = new Cidade();
		cidade.setNome("Cidade");
		listaCidades.add(cidade);
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_cidades_visiveis/" + CHAVE_MD5);

		if (resposta[0].equals("200")) {
			cidade = null;
			
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("cidades");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				cidade = new Cidade();
				cidade.setId(jsonObject.getInt("glo_id_cid"));
				cidade.setNome(jsonObject.getString("glo_nome_cid"));
				cidade.setUf(jsonObject.getString("glo_uf_est"));

				listaCidades.add(cidade);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaCidades;
	}
	

}
