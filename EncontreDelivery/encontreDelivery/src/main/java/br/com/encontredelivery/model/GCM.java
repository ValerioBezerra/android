package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class GCM implements Serializable {
	private long id;
	private String regId;
	private Cliente cliente;
	
	public GCM() {
		this.id       = 0;
		this.regId   = "";
		this.cliente = new Cliente();
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getRegId() {
		return regId;
	}

	public void setRegId(String regId) {
		this.regId = regId;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

}
