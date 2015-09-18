package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Bairro implements Serializable {
	private int id;
	private String nome;
	private Cidade cidade;
	
	public Bairro() {
		this.id     = 0;
		this.nome   = "";
		this.cidade = null;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String toString() {
		return this.nome;
	}
	public Cidade getCidade() {
		return cidade;
	}
	public void setCidade(Cidade cidade) {
		this.cidade = cidade;
	}

}
