package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FormaPagamento implements Serializable {
	private int id;
	private String descricao;
	private boolean calculaTroco;
	private String urlImagem;
	private boolean escolhido;
	
	public FormaPagamento() {
		this.id        = 0;
		this.descricao = "";
		this.escolhido = false;
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
	
	public boolean isCalculaTroco() {
		return calculaTroco;
	}

	public void setCalculaTroco(boolean calculaTroco) {
		this.calculaTroco = calculaTroco;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

	public boolean isEscolhido() {
		return escolhido;
	}

	public void setEscolhido(boolean escolhido) {
		this.escolhido = escolhido;
	}

}
