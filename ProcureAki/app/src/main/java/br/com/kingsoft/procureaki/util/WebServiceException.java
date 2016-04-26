package br.com.kingsoft.procureaki.util;

@SuppressWarnings("serial")
public class WebServiceException extends Exception {
	public WebServiceException(String message) {  
//		super(message);
		super("Erro ao conectar com o servidor. Verifique sua conexão e tente novamente.");
    }  
	
}
