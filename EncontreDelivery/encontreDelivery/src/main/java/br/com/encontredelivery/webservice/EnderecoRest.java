package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import br.com.encontredelivery.model.Bairro;
import br.com.encontredelivery.model.Cidade;
import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.util.WebServiceException;

public class EnderecoRest extends GenericRest{

	public EnderecoRest() {
		super("endereco_json");
	}
	
	public List<Endereco> getEnderecosGPS(double latitude, double longitude) throws Exception {
		List<Endereco> listaEnderecos = new ArrayList<Endereco>();
	
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_enderecos_gps/" + CHAVE_MD5 + "/" + latitude + "/" + longitude);

		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("enderecos");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Endereco endereco = new Endereco();
				endereco.setId(jsonObject.getInt("glo_id_end"));
				endereco.setCep(jsonObject.getString("glo_cep_end"));
				endereco.setLogradouro(jsonObject.getString("glo_logradouro_end"));
				
				Cidade cidade = new Cidade();
				cidade.setId(jsonObject.getInt("glo_id_cid"));
				cidade.setNome(jsonObject.getString("glo_nome_cid"));
				cidade.setUf(jsonObject.getString("glo_uf_est"));
				
				Bairro bairro = new Bairro();
				bairro.setId(jsonObject.getInt("glo_id_bai"));
				bairro.setNome(jsonObject.getString("glo_nome_bai"));
				bairro.setCidade(cidade);
				
				endereco.setBairro(bairro);

				listaEnderecos.add(endereco);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaEnderecos;
	}

	public List<Endereco> getEnderecos(int idCidade, int idBairro, String logradouro) throws Exception {
		List<Endereco> listaEnderecos = new ArrayList<Endereco>();

		logradouro        = logradouro.trim().replace(" ", "+");		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_enderecos_logradouro/" + CHAVE_MD5 + "/" + 
		                                                                    idCidade + "/" + idBairro + "/" + logradouro);

		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("enderecos");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Endereco endereco = new Endereco();
				endereco.setId(jsonObject.getInt("glo_id_end"));
				endereco.setCep(jsonObject.getString("glo_cep_end"));
				endereco.setLogradouro(jsonObject.getString("glo_logradouro_end"));
				
				Cidade cidade = new Cidade();
				cidade.setId(jsonObject.getInt("glo_id_cid"));
				cidade.setNome(jsonObject.getString("glo_nome_cid"));
				cidade.setUf(jsonObject.getString("glo_uf_est"));
				
				Bairro bairro = new Bairro();
				bairro.setId(jsonObject.getInt("glo_id_bai"));
				bairro.setNome(jsonObject.getString("glo_nome_bai"));
				bairro.setCidade(cidade);
				
				endereco.setBairro(bairro);

				listaEnderecos.add(endereco);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaEnderecos;
	}
	
	public Endereco getEndereco(String cep) throws Exception {
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_endereco_cep/" + CHAVE_MD5 + "/" + cep);

		if (resposta[0].equals("200")) {
			Endereco endereco = null;
			
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				
				endereco = new Endereco();
				endereco.setId(jsonObject.getInt("glo_id_end"));
				endereco.setCep(jsonObject.getString("glo_cep_end"));
				endereco.setLogradouro(jsonObject.getString("glo_logradouro_end"));
				
				Cidade cidade = new Cidade();
				cidade.setId(jsonObject.getInt("glo_id_cid"));
				cidade.setNome(jsonObject.getString("glo_nome_cid"));
				cidade.setUf(jsonObject.getString("glo_uf_est"));
				
				Bairro bairro = new Bairro();
				bairro.setId(jsonObject.getInt("glo_id_bai"));
				bairro.setNome(jsonObject.getString("glo_nome_bai"));
				bairro.setCidade(cidade);
				
				endereco.setBairro(bairro);
			}

			return endereco;
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	
	
	public List<Endereco> getEnderecosCliente(long idCliente) throws Exception {
		List<Endereco> listaEnderecos = new ArrayList<Endereco>();

		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_enderecos_cliente/" + CHAVE_MD5 + "/" + idCliente);

		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("enderecos");
			
			for(int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Endereco endereco = new Endereco();
				endereco.setId(jsonObject.getInt("glo_id_end"));
				endereco.setCep(jsonObject.getString("glo_cep_end"));
				endereco.setLogradouro(jsonObject.getString("glo_logradouro_end"));				
				endereco.setNumero(jsonObject.getString("dlv_numero_ecl"));
				endereco.setComplemento(jsonObject.getString("dlv_complemento_ecl"));
				endereco.setCadastrado(true);
				endereco.setIdEnderecoCliente(jsonObject.getLong("dlv_id_ecl"));
				
				Cidade cidade = new Cidade();
				cidade.setId(jsonObject.getInt("glo_id_cid"));
				cidade.setNome(jsonObject.getString("glo_nome_cid"));
				cidade.setUf(jsonObject.getString("glo_uf_est"));
				
				Bairro bairro = new Bairro();
				bairro.setId(jsonObject.getInt("glo_id_bai"));
				bairro.setNome(jsonObject.getString("glo_nome_bai"));
				bairro.setCidade(cidade);
				
				endereco.setBairro(bairro);

				listaEnderecos.add(endereco);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaEnderecos;
	}
	
	public String cadastrarEnderecoCliente(Cliente cliente, Endereco endereco) throws Exception {
		JSONObject json = new JSONObject(); 
		
        json.put("dlv_dlvcli_ecl", cliente.getId()); 
        json.put("dlv_gloend_ecl", endereco.getId()); 
        json.put("dlv_numero_ecl", endereco.getNumero()); 
        json.put("dlv_complemento_ecl", endereco.getComplemento());
        
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "cadastrar_endereco_cliente/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}

	public String alterarEnderecoCliente(Cliente cliente, Endereco endereco) throws Exception {
		JSONObject json = new JSONObject();

		json.put("dlv_id_ecl", endereco.getIdEnderecoCliente());
		json.put("dlv_dlvcli_ecl", cliente.getId());
		json.put("dlv_gloend_ecl", endereco.getId());
		json.put("dlv_numero_ecl", endereco.getNumero());
		json.put("dlv_complemento_ecl", endereco.getComplemento());

		String[] resposta = new WebServiceClient().post(getUrlWebService() + "alterar_endereco_cliente/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}


	public String apagarEnderecoCliente(Endereco endereco) throws Exception {
		JSONObject json = new JSONObject(); 
		
        json.put("dlv_id_ecl", endereco.getIdEnderecoCliente()); 
        
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "apagar_endereco_cliente/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	

}
