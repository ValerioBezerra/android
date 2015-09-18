package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Tamanho implements Serializable {
	private boolean escolhido;
	private int id;
	private String descricao;
	private int quantidade;
	private double preco;
	private boolean promocao;
	private double precoPromocao;
	
	
	public Tamanho() {
		this.escolhido     = false;
		this.id            = 0;
		this.descricao     = "";
		this.quantidade    = 0;
		this.preco         = 0;
		this.promocao      = false;
		this.precoPromocao = 0;
	}
	
	public boolean isEscolhido() {
		return escolhido;
	}

	public void setEscolhido(boolean escolhido) {
		this.escolhido = escolhido;
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

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public boolean isPromocao() {
		return promocao;
	}

	public void setPromocao(boolean promocao) {
		this.promocao = promocao;
	}

	public double getPrecoPromocao() {
		return precoPromocao;
	}

	public void setPrecoPromocao(double precoPromocao) {
		this.precoPromocao = precoPromocao;
	}
}
