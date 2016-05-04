package br.com.kingsoft.procureaki.webservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.model.Segmento;
import br.com.kingsoft.procureaki.util.WebServiceException;

public class SegmentoRest extends GenericRest{

	public SegmentoRest() {
		super("segmento_json");
	}
	

	public List<Segmento> getSegmentos(String latitude, String longitude, int distanciaKm) throws Exception {
		List<Segmento> listaSegmentos = new ArrayList<Segmento>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_segmentos" + "/" + CHAVE_MD5 + "/" + latitude + "/" + longitude + "/" + distanciaKm);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("segmentos");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Segmento segmento = new Segmento();
				segmento.setId(jsonObject.getInt("bus_id_seg"));
				segmento.setDescricao(jsonObject.getString("bus_descricao_seg"));
				segmento.setQuantidadeEmpresas(jsonObject.getInt("quantidade_empresa"));

				listaSegmentos.add(segmento);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaSegmentos;
	}
}
