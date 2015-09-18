package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.Horario;
import br.com.encontredelivery.util.WebServiceException;

public class HorarioRest extends GenericRest{

	public HorarioRest() {
		super("horario_json");
	}
	

	public List<Horario> getHorarios(int idEmpresa) throws Exception {
		List<Horario> listaHorarios = new ArrayList<Horario>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_horarios_empresa/" + CHAVE_MD5 + "/" + idEmpresa);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("horarios");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Horario horario = new Horario();
				horario.setDia(jsonObject.getInt("dlv_dia_exh"));
				horario.setHorarios(jsonObject.getString("horarios"));
				
				listaHorarios.add(horario);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaHorarios;
	}
}
