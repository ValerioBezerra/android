package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.util.WebServiceException;

public class FormaPagamentoRest extends GenericRest{

	public FormaPagamentoRest() {
		super("forma_pagamento_json");
	}
	

	public List<FormaPagamento> getFormaPagamentos(int idEmpresa) throws Exception {
		List<FormaPagamento> listaFormaPagamentos = new ArrayList<FormaPagamento>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_formas_pagamento_empresa/" + CHAVE_MD5 + "/" + idEmpresa);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("formas_pagamento");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				FormaPagamento formaPagamento = new FormaPagamento();
				formaPagamento.setId(jsonObject.getInt("dlv_id_fpg"));
				formaPagamento.setDescricao(jsonObject.getString("dlv_descricao_fpg"));
				formaPagamento.setCalculaTroco(jsonObject.getInt("dlv_calculatroco_fpg") == 1);
				formaPagamento.setUrlImagem(jsonObject.getString("url_imagem"));
				
				listaFormaPagamentos.add(formaPagamento);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaFormaPagamentos;
	}
}
