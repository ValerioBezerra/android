package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Adicional implements Serializable {
	private int id;
	private String descricao;
	private boolean escolhido;
	private double valor;
	
	public Adicional() {
		this.id         = 0;
		this.descricao  = "";
		this.escolhido  = false;
		this.valor      = 0;
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

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}

}
