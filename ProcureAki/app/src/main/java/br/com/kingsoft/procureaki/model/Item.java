package br.com.kingsoft.procureaki.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Item implements Serializable {
	private int id;
	private String descricao;

	public Item() {
		this.id                 = 0;
		this.descricao          = "";
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
}
