package br.com.kingsoft.procureaki.webservice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import br.com.kingsoft.procureaki.model.Bairro;
import br.com.kingsoft.procureaki.model.Cidade;
import br.com.kingsoft.procureaki.model.Empresa;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.util.WebServiceException;

public class EmpresaRest extends GenericRest{

	public EmpresaRest() {
		super("empresa_json");
	}
	

	public List<Empresa> getEmpresas(int idSegmento, int idTipo, String latitude, String longitude, int distanciaKm) throws Exception {
		List<Empresa> listaEmpresas = new ArrayList<Empresa>();
		
		String[] resposta = new WebServiceClient().get(getUrlWebService() + "retornar_empresas/" + CHAVE_MD5 + "/" + idSegmento + "/" + idTipo + "/" + latitude + "/" + longitude + "/" + distanciaKm);

		if (resposta[0].equals("200")) {
			JSONArray jsonArray = new JSONObject(resposta[1]).getJSONArray("empresas");
			
			for (int i = 0; i < jsonArray.length(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				
				Empresa empresa = new Empresa();
				empresa.setId(jsonObject.getInt("bus_id_emp"));
				empresa.setNome(jsonObject.getString("bus_nome_emp"));
				empresa.setDetalhamento(jsonObject.getString("bus_detalhamento_emp"));

				empresa.setAberto(jsonObject.getInt("bus_aberto_emp") == 1);
				
				Endereco endereco = new Endereco();
				endereco.setId(jsonObject.getInt("glo_id_end"));
				endereco.setCep(jsonObject.getString("glo_cep_end"));
				endereco.setLogradouro(jsonObject.getString("glo_logradouro_end"));
				endereco.setNumero(jsonObject.getString("bus_numero_emp"));
				endereco.setComplemento(jsonObject.getString("bus_complemento_emp"));
				endereco.setLatitude(jsonObject.getString("glo_latitude_end"));
				endereco.setLongitude(jsonObject.getString("glo_longitude_end"));

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
				
				empresa.setUrlImagem(jsonObject.getString("url_imagem"));
				empresa.setQuantidadeDiasAtualizacao(jsonObject.getInt("quantidade_dias_atualizacao"));
				empresa.setDistanciaKm(jsonObject.getDouble("distancia_km"));

				listaEmpresas.add(empresa);
			}
				
		} else {
			throw new WebServiceException(resposta[1]);
		}

		return listaEmpresas;
	}
}
