package br.com.kingsoft.procureaki.webservice;

public class GenericRest {
	
	private String urlWebService;
	
	protected static final String CHAVE_MD5 = "08787a804e2a4f7ba145a553e4eab7cb";
	
	public GenericRest(String classe) {
//		this.urlWebService = "http://192.168.56.1/encontredelivery/administrador/json/" + classe + "/";
//		this.urlWebService = "http://192.168.100.11/web/encontredelivery/administrador/json/" + classe + "/";
//		this.urlWebService = "http://encontredelivery.com.br/demo/administrador/json/" + classe + "/";
//		this.urlWebService = "http://192.168.100.101/web/procureaki/json/" + classe + "/";
		this.urlWebService = "http://192.168.2.109/web/procureaki/json/" + classe + "/";
	}

	public String getUrlWebService() {
		return urlWebService;
	}
	
}
