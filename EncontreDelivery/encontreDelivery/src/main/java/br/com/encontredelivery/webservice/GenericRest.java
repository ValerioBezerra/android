package br.com.encontredelivery.webservice;

public class GenericRest {
	
	private String urlWebService;
	
	protected static final String CHAVE_MD5 = "08787a804e2a4f7ba145a553e4eab7cb";
	
	public GenericRest(String classe) {
//		this.urlWebService = "http://192.168.56.1/encontredelivery/administrador/json/" + classe + "/";
//		this.urlWebService = "http://192.168.100.2/encontredelivery/administrador/json/" + classe + "/";
		this.urlWebService = "http://encontredelivery.com.br/administrador/json/" + classe + "/";
	}

	public String getUrlWebService() {
		return urlWebService;
	}
	
}
