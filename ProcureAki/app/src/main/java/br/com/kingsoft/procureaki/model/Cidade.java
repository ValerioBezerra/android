package br.com.kingsoft.procureaki.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Cidade implements Serializable {
	private int id;
	private String nome;
	private String uf;
	
	public Cidade() {
		this.id   = 0;
		this.nome = "";
		this.uf   = "";
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
	public String getUf() {
		return uf;
	}
	public void setUf(String uf) {
		this.uf = uf;
	}
	public String toString() {
		if (uf.equals("")) {
			return this.nome;
		} else {
			return this.nome + " - " + this.uf;
		}
	}

}
