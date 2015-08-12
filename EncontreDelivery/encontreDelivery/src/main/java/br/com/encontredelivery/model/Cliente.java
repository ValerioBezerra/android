package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Cliente implements Serializable {
	private long id;
	private String nome;
	private String email;
	private String senha;
	private String idFacebook;
	private String fone;
	private String dataAniversario;
	
	public Cliente() {
		this.id         	 = 0;
		this.nome       	 = "";
		this.email      	 = "";
		this.senha      	 = "";
		this.idFacebook 	 = "";
		this.fone       	 = "";
		this.dataAniversario = "";
	}
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public String getIdFacebook() {
		return idFacebook;
	}
	
	public void setIdFacebook(String idFacebook) {
		this.idFacebook = idFacebook;
	}
	
	public String getFone() {
		return fone;
	}
	
	public void setFone(String fone) {
		this.fone = fone;
	}

	public String getDataAniversario() {
		return dataAniversario;
	}

	public void setDataAniversario(String dataAniversario) {
		this.dataAniversario = dataAniversario;
	}
	
}
