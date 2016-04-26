package br.com.kingsoft.procureaki.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Endereco implements Serializable {
	private int id;
	private String cep;
	private String logradouro;
	private Bairro bairro;
	private String numero;
	private String complemento;
	private boolean cadastrado;
	private long idEnderecoCliente;
	
	public Endereco() {
		this.id                = 0;
		this.cep               = "";
		this.logradouro        = "";
		this.bairro            = null;
		this.numero            = "";
		this.complemento       = "";
		this.cadastrado        = false;
		this.idEnderecoCliente = 0;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getCep() {
		return cep;
	}

	public void setCep(String cep) {
		this.cep = cep;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public Bairro getBairro() {
		return bairro;
	}

	public void setBairro(Bairro bairro) {
		this.bairro = bairro;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public boolean isCadastrado() {
		return cadastrado;
	}

	public void setCadastrado(boolean cadastrado) {
		this.cadastrado = cadastrado;
	}

	public long getIdEnderecoCliente() {
		return idEnderecoCliente;
	}

	public void setIdEnderecoCliente(long idEnderecoCliente) {
		this.idEnderecoCliente = idEnderecoCliente;
	}

}
