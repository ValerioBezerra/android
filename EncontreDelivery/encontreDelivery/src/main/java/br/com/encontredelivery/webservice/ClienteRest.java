package br.com.encontredelivery.webservice;

import org.json.JSONObject;

import br.com.encontredelivery.model.Cliente;
import br.com.encontredelivery.util.WebServiceException;

public class ClienteRest extends GenericRest{

	public ClienteRest() {
		super("cliente_json");
	}

	public String cadastrar(Cliente cliente) throws Exception {
		JSONObject json = new JSONObject(); 
		
        json.put("dlv_nome_cli", cliente.getNome()); 
        json.put("dlv_fone_cli", cliente.getFone());
        
        if (!cliente.getIdFacebook().equals("")) {
            json.put("dlv_idfacebook_cli", cliente.getIdFacebook()); 
        } else {
            json.put("dlv_email_cli", cliente.getEmail()); 
            json.put("dlv_senha_cli", cliente.getSenha()); 
        }
        
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "cadastrar/" + CHAVE_MD5, json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}
	
	public String alterar(Cliente cliente) throws Exception {
		JSONObject json = new JSONObject(); 
		
        json.put("dlv_nome_cli", cliente.getNome()); 
        json.put("dlv_fone_cli", cliente.getFone());
        
        if (!cliente.getSenha().equals("")) {
            json.put("dlv_senha_cli", cliente.getSenha()); 
        }
        
		String[] resposta = new WebServiceClient().post(getUrlWebService() + "alterar/" + CHAVE_MD5 + "/" + cliente.getId(), json);
		if (resposta[0].equals("200")) {
			return resposta[1];
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	
	
	
	public Cliente getCliente(String email, String senha) throws Exception {
		email             = email.trim().replace(" ", "+");		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_login_email/" + CHAVE_MD5 + "/" + email + "/" + senha);

		if (resposta[0].equals("200")) {
			Cliente cliente = null;
			
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				
				cliente = new Cliente();
				cliente.setId(jsonObject.getLong("dlv_id_cli"));
				cliente.setNome(jsonObject.getString("dlv_nome_cli"));
				cliente.setEmail(jsonObject.getString("dlv_email_cli"));
				cliente.setSenha(jsonObject.getString("dlv_senha_cli"));
				cliente.setIdFacebook("");
				cliente.setFone(jsonObject.getString("dlv_fone_cli"));
			}

			return cliente;
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}
	
	public Cliente getClienteFacebook(String idFacebook) throws Exception {
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "verificar_facebook/" + CHAVE_MD5 + "/" + idFacebook);

		if (resposta[0].equals("200")) {
			Cliente cliente = null;
			
			if (!resposta[1].equals("[]")) {
				JSONObject jsonObject = new JSONObject(resposta[1]);
				
				cliente = new Cliente();
				cliente.setId(jsonObject.getLong("dlv_id_cli"));
				cliente.setNome(jsonObject.getString("dlv_nome_cli"));
				cliente.setIdFacebook(jsonObject.getString("dlv_idfacebook_cli"));
				cliente.setFone(jsonObject.getString("dlv_fone_cli"));
			}

			return cliente;
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}
	
	public String recuperarSenha(String email) throws Exception {
		email             = email.trim().replace(" ", "+");		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "recuperar_senha/" + CHAVE_MD5 + "/" + email);

		if (resposta[0].equals("200")) {
			JSONObject jsonObject = new JSONObject(resposta[1]);
			
			if (jsonObject.getString("status").equals("sucesso")) {
				return "";
			} else {
				return jsonObject.getString("mensagem");
			}
		} else {
			throw new WebServiceException(resposta[1]);
		}
	}	
	
	

}
