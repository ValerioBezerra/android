package br.com.encontredelivery.webservice;

import org.json.JSONObject;

import br.com.encontredelivery.model.Configuracao;
import br.com.encontredelivery.util.WebServiceException;

public class ConfiguracaoRest extends GenericRest{

	public ConfiguracaoRest() {
		super("configuracao_json");
	}
	
	public Configuracao getConfiguracao() throws Exception {
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_versao/" + CHAVE_MD5);
		
		if (resposta[0].equals("200")) {
			Configuracao configuracao = null;
			
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				
				configuracao = new Configuracao();
				configuracao.setVersao(jsonObject.getInt("dlv_versao_cfg"));
				configuracao.setMensagemInicial(jsonObject.getString("dlv_msginicial_cfg"));
				configuracao.setBloquear(jsonObject.getInt("dlv_bloquearapp_cfg") == 1);
			}
			
			return configuracao;
		} else {
			throw new WebServiceException(resposta[1]);
		}

	}
}
