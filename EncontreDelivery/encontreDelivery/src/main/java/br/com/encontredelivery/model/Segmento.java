package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Segmento implements Serializable {
	private int id;
	private String descricao;
	private boolean escolhido; 
	
	public Segmento() {
		this.id        = 0;
		this.descricao = "";
		this.escolhido = true;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isEscolhido() {
		return escolhido;
	}

	public void setEscolhido(boolean escolhido) {
		this.escolhido = escolhido;
	}

}
