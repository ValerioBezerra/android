package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class ProdutoEscolhido implements Serializable {
	private int opcao;
	private int quantidade;
	private boolean escolhido;
	private Produto produto;
	
	public ProdutoEscolhido() {
		this.opcao      = 0;
		this.quantidade = 0;
		this.escolhido  = false;
		this.produto    = new Produto();
	}
	
	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public boolean isEscolhido() {
		return escolhido;
	}

	public void setEscolhido(boolean escolhido) {
		this.escolhido = escolhido;
	}

	public Produto getProduto() {
		return produto;
	}

	public void setProduto(Produto produto) {
		this.produto = produto;
	}

}
