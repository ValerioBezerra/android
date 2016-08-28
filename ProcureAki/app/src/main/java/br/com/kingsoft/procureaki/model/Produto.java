package br.com.kingsoft.procureaki.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Produto implements Serializable {
	private int id;
	private String descricao;
	private double preco;
	private String urlImagemEmpresa;

	public Produto() {
		this.id               = 0;
		this.descricao        = "";
		this.preco            = 0;
		this.urlImagemEmpresa = "";
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

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public String getUrlImagemEmpresa() {
		return urlImagemEmpresa;
	}

	public void setUrlImagemEmpresa(String urlImagemEmpresa) {
		this.urlImagemEmpresa = urlImagemEmpresa;
	}
}
