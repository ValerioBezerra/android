package br.com.kingsoft.procureaki.webservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.model.Bairro;
import br.com.kingsoft.procureaki.util.WebServiceException;

public class BairroRest extends GenericRest{

	public BairroRest() {
		super("endereco_json");
	}

	public List<Bairro> getBairros(int idCidade) throws Exception {
		
		List<Bairro> listaBairros = new ArrayList<Bairro>();
		Bairro bairro = new Bairro();
		bairro.setNome("Bairro");
		listaBairros.add(bairro);
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_bairros_visiveis/" + CHAVE_MD5 + "/" + idCidade);

		if (resposta[0].equals("200")) {
			bairro = null;
			
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("bairros");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				bairro = new Bairro();
				bairro.setId(jsonObject.getInt("glo_id_bai"));
				bairro.setNome(jsonObject.getString("glo_nome_bai"));

				listaBairros.add(bairro);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaBairros;
	}
	

}
