package br.com.kingsoft.procureaki.webservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.model.Tipo;
import br.com.kingsoft.procureaki.util.WebServiceException;

public class TipoRest extends GenericRest{

	public TipoRest() {
		super("tipo_json");
	}
	

	public List<Tipo> getTipos(int idSegmento, String latitude, String longitude, int distanciaKm) throws Exception {
		List<Tipo> listaTipos = new ArrayList<Tipo>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_tipos" + "/" + CHAVE_MD5 + "/" + idSegmento + "/" + latitude + "/" + longitude + "/" + distanciaKm);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("tipos");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Tipo tipo = new Tipo();
				tipo.setId(jsonObject.getInt("bus_id_tip"));
				tipo.setDescricao(jsonObject.getString("bus_descricao_tip"));
				tipo.setQuantidadeEmpresas(jsonObject.getInt("quantidade_empresa"));

				listaTipos.add(tipo);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaTipos;
	}
}
