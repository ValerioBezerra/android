package br.com.encontredelivery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Produto implements Serializable {
	
	private int id;
	private String descricao;
	private String detalhamento;
	private double preco;
	private boolean promocao;
	private double precoPromocao;
	private String tempoPreparo;
	private boolean destaque;
	private boolean somenteUmAdicional;
	private boolean escolheProduto;
	private int quantidade;
	private String unidade;
	private boolean precoMaiorProduto;
	private boolean exibirFracao;
	private int minimoProduto;
	private int maximoProduto;
	private boolean usaTamanho;
	private String urlImagem;
	private List<Tamanho> listaTamanhos;
	private List<ProdutoEscolhido> listaProdutosEscolhidos;
	
	public Produto() {
		this.id                 	 = 0;
		this.descricao          	 = "";
		this.detalhamento       	 = "";
		this.preco              	 = 0;
		this.promocao           	 = false;
		this.precoPromocao      	 = 0;
		this.tempoPreparo       	 = "";
		this.destaque           	 = false;
		this.somenteUmAdicional 	 = false;
		this.escolheProduto     	 = false;
		this.quantidade         	 = 0;
		this.unidade            	 = "";
		this.precoMaiorProduto  	 = false;
		this.exibirFracao       	 = false;
		this.minimoProduto           = 0;
		this.maximoProduto           = 0;
		this.usaTamanho       	     = false;
		this.urlImagem               = "";   
		this.listaProdutosEscolhidos = new ArrayList<ProdutoEscolhido>();
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

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
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

	public String getTempoPreparo() {
		return tempoPreparo;
	}

	public void setTempoPreparo(String tempoPreparo) {
		this.tempoPreparo = tempoPreparo;
	}

	public boolean isDestaque() {
		return destaque;
	}

	public void setDestaque(boolean destaque) {
		this.destaque = destaque;
	}

	public boolean isSomenteUmAdicional() {
		return somenteUmAdicional;
	}

	public void setSomenteUmAdicional(boolean somenteUmAdicional) {
		this.somenteUmAdicional = somenteUmAdicional;
	}

	public boolean isEscolheProduto() {
		return escolheProduto;
	}

	public void setEscolheProduto(boolean escolheProduto) {
		this.escolheProduto = escolheProduto;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public boolean isPrecoMaiorProduto() {
		return precoMaiorProduto;
	}

	public void setPrecoMaiorProduto(boolean precoMaiorProduto) {
		this.precoMaiorProduto = precoMaiorProduto;
	}

	public boolean isExibirFracao() {
		return exibirFracao;
	}

	public void setExibirFracao(boolean exibirFracao) {
		this.exibirFracao = exibirFracao;
	}

	public int getMinimoProduto() {
		return minimoProduto;
	}

	public void setMinimoProduto(int minimoProduto) {
		this.minimoProduto = minimoProduto;
	}

	public int getMaximoProduto() {
		return maximoProduto;
	}

	public void setMaximoProduto(int maximoProduto) {
		this.maximoProduto = maximoProduto;
	}

	public boolean isUsaTamanho() {
		return usaTamanho;
	}

	public void setUsaTamanho(boolean usaTamanho) {
		this.usaTamanho = usaTamanho;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}
	
	public List<Tamanho> getListaTamanhos() {
		return listaTamanhos;
	}

	public void setListaTamanhos(List<Tamanho> listaTamanhos) {
		this.listaTamanhos = listaTamanhos;
	}

	public List<ProdutoEscolhido> getListaProdutosEscolhidos() {
		return listaProdutosEscolhidos;
	}

	public void setListaProdutosEscolhidos(
			List<ProdutoEscolhido> listaProdutosEscolhidos) {
		this.listaProdutosEscolhidos = listaProdutosEscolhidos;
	}

}
