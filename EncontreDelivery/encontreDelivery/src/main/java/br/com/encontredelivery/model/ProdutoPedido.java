package br.com.encontredelivery.model;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ProdutoPedido implements Serializable {
	private Produto produto;
	private int opcao;
	private int quantidade;
	private double preco;
	private String observacao;
	private List<Adicional> listaAdicionais;
	
	public Produto getProduto() {
		return produto;
	}
	
	public void setProduto(Produto produto) {
		this.produto = produto;
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

	public double getPreco() {
		return preco;
	}

	public void setPreco(double preco) {
		this.preco = preco;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public List<Adicional> getListaAdicionais() {
		return listaAdicionais;
	}

	public void setListaAdicionais(List<Adicional> listaAdicionais) {
		this.listaAdicionais = listaAdicionais;
	}

}
