package br.com.encontredelivery.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Retorno {
	
	public static String getMD5(String texto) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(texto.getBytes());
			byte[] hashMd5 = md.digest();
			
			return getHexa(hashMd5);
		} catch (NoSuchAlgorithmException e) {
			return null;
		}
	}
	
	private static String getHexa(byte[] bytes) {
		   StringBuilder s = new StringBuilder();
		   
		   for (int i = 0; i < bytes.length; i++) {
		       int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
		       int parteBaixa = bytes[i] & 0xf;
		       
		       if (parteAlta == 0) { 
		    	   s.append('0');
		       }
		       
		       s.append(Integer.toHexString(parteAlta | parteBaixa));
		   }
		   
		   return s.toString();
	}	
	
	public static String getMascaraCep(String cep){  
		return cep.substring(0, 5) + "-" + cep.substring(5);  
	}
	
	public static String getMascaraCpf(String cpf){  
		return cpf.substring(0, 3) + "." + cpf.substring(3, 6) + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);   
	}

	public static String getSomenteNumeros(String valor) {
		return valor.replaceAll("[^0-9]", "");
	}
	
}
