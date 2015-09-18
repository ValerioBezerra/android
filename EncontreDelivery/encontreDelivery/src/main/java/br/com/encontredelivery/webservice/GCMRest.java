package br.com.encontredelivery.webservice;

import org.json.JSONObject;

import br.com.encontredelivery.model.GCM;
import br.com.encontredelivery.util.WebServiceException;

public class GCMRest extends GenericRest{

	public GCMRest() {
		super("gcm_json");
	}

	public String cadastrarAlterar(GCM gcm, String registroIDAntigo) throws Exception {
		JSONObject json = new JSONObject(); 
		
        json.put("registroIDAntigo", registroIDAntigo); 
        json.put("dlv_regid_gcm", gcm.getRegId()); 
        if (gcm.getCliente() != null) {
        	json.put("dlv_dlvcli_gcm", gcm.getCliente().getId());
        } else {
        	json.put("dlv_dlvcli_gcm", null);
        }
        
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "cadastrarAlterar/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}
	
}
