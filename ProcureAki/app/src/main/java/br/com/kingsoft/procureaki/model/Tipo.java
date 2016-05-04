package br.com.kingsoft.procureaki.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Tipo implements Serializable {
	private int id;
	private String descricao;
	private int quantidadeEmpresas;

	public Tipo() {
		this.id                 = 0;
		this.descricao          = "";
		this.quantidadeEmpresas = 0;
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

	public int getQuantidadeEmpresas() {
		return quantidadeEmpresas;
	}

	public void setQuantidadeEmpresas(int quantidadeEmpresas) {
		this.quantidadeEmpresas = quantidadeEmpresas;
	}
}
