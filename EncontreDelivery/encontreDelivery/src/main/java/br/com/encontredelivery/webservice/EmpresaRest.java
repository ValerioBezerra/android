package br.com.encontredelivery.webservice;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


import br.com.encontredelivery.model.Bairro;
import br.com.encontredelivery.model.Cidade;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.model.Endereco;
import br.com.encontredelivery.model.Segmento;
import br.com.encontredelivery.util.WebServiceException;

public class EmpresaRest extends GenericRest{

	public EmpresaRest() {
		super("empresa_json");
	}
	

	public List<Empresa> getEmpresas(String nome, String cepCliente, int idCidade, int idBairro, String whereSegmentos) throws Exception {
		List<Empresa> listaEmpresas = new ArrayList<Empresa>();
		
		nome              = nome.trim().replace(" ", "+");
		String[] resposta = null;
		
		if (nome.trim().equals("")) {
			resposta = new WebServiceClient().get(getUrlWebService() + "retornar_empresas_endereco/" + CHAVE_MD5 + "/" + 
																		cepCliente + "/" + idCidade + "/" + idBairro + "/" + whereSegmentos); 
		} else {
			resposta = new WebServiceClient().get(getUrlWebService() + "retornar_empresas_endereco/" + CHAVE_MD5 + "/" +
																		cepCliente + "/" + idCidade + "/" + idBairro + "/" + whereSegmentos + "/" + nome); 
		}		

		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("empresas");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Empresa empresa = new Empresa();
				empresa.setId(jsonObject.getInt("dlv_id_emp"));
				empresa.setNome(jsonObject.getString("dlv_nome_emp"));
				empresa.setDetalhamento(jsonObject.getString("dlv_detalhamento_emp"));
				empresa.setTaxaEntrega(jsonObject.getDouble("dlv_taxaentrega_emp"));
				empresa.setValorMinimo(jsonObject.getDouble("dlv_valorminimo_emp"));
				empresa.setTempoMedio(jsonObject.getString("dlv_tempomedio_emp"));

				JSONArray jsonArrayFones = jsonObject.getJSONArray("fones");
				List<String> listaFones  = new ArrayList<String>();
				for (int j = 0; j < jsonArrayFones.length(); j++){
					JSONObject jsonObjectFone = jsonArrayFones.getJSONObject(j);

					listaFones.add(jsonObjectFone.getString("dlv_fone_ext"));
				}
				empresa.setListaFones(listaFones);

				empresa.setAberto(jsonObject.getInt("dlv_aberto_emp") == 1);
				
				Endereco endereco = new Endereco();
				endereco.setId(jsonObject.getInt("glo_id_end"));
				endereco.setCep(jsonObject.getString("glo_cep_end"));
				endereco.setLogradouro(jsonObject.getString("glo_logradouro_end"));
				endereco.setNumero(jsonObject.getString("dlv_numero_emp"));
				endereco.setComplemento(jsonObject.getString("dlv_complemento_emp"));
				
				Cidade cidade = new Cidade();
				cidade.setId(jsonObject.getInt("glo_id_cid"));
				cidade.setNome(jsonObject.getString("glo_nome_cid"));
				cidade.setUf(jsonObject.getString("glo_uf_est"));
				
				Bairro bairro = new Bairro();
				bairro.setId(jsonObject.getInt("glo_id_bai"));
				bairro.setNome(jsonObject.getString("glo_nome_bai"));
				bairro.setCidade(cidade);
				
				endereco.setBairro(bairro);
				empresa.setEndereco(endereco);
				
				empresa.setDistanciaEnderecos(jsonObject.getString("distancia_enderecos"));
				empresa.setTempoEnderecos(jsonObject.getString("tempo_enderecos"));
				
				
				List<Segmento> listaSegmentos = new ArrayList<Segmento>();
				JSONArray segmentosArray     = new JSONObject(jsonObject.getString("segmentos")).getJSONArray("lista");
				for (int j = 0; j < segmentosArray.length(); j++) {
					JSONObject segmentoObject = segmentosArray.getJSONObject(j);
					
					Segmento segmento = new Segmento();
					segmento.setId(segmentoObject.getInt("dlv_id_seg"));
					segmento.setDescricao(segmentoObject.getString("dlv_descricao_seg"));
					
					listaSegmentos.add(segmento);
				}
				
				empresa.setListaSegmentos(listaSegmentos);
				empresa.setCurtidas(jsonObject.getInt("quantidade_curtidas"));
				empresa.setUrlImagem(jsonObject.getString("url_imagem"));
				
				listaEmpresas.add(empresa);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaEmpresas;
	}
}
