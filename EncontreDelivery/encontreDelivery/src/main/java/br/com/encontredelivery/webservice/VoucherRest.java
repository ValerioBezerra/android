package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import br.com.encontredelivery.model.Voucher;
import br.com.encontredelivery.util.WebServiceException;

public class VoucherRest extends GenericRest{

	public VoucherRest() {
		super("voucher_json");
	}
	

	public List<Voucher> getVouchers(long idCliente) throws Exception {
		List<Voucher> listaVouchers = new ArrayList<Voucher>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_vouchers_clientes/" + CHAVE_MD5 + "/" + idCliente);
		
		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("vouchers");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Voucher voucher = new Voucher();
				voucher.setId(jsonObject.getInt("dlv_id_vou"));
				voucher.setCodigo(jsonObject.getString("dlv_codigo_vou"));
				voucher.setDescricao(jsonObject.getString("dlv_descricao_vou"));
				voucher.setTipo(jsonObject.getString("dlv_tipo_vou"));
				voucher.setValor(jsonObject.getDouble("dlv_valor_vou"));
				
				listaVouchers.add(voucher);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaVouchers;
	}
	
	public Voucher getVoucher(String codigo, long idCliente, int idEmpresa) throws Exception {
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_voucher_codigo/" + CHAVE_MD5 + "/" + codigo + "/" + idCliente + "/" + idEmpresa);

		if (resposta[0].equals("200")) {
			Voucher voucher = null;
			
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				
				voucher = new Voucher();
				voucher.setId(jsonObject.getInt("dlv_id_vou"));
				voucher.setTipo(jsonObject.getString("dlv_tipo_vou"));
				voucher.setValor(jsonObject.getDouble("dlv_valor_vou"));
			}

			return voucher;
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}
}
