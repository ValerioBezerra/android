package br.com.encontredelivery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Empresa implements Serializable {
	private int id;
	private String nome;
	private String detalhamento;
	private double taxaEntrega;
	private double valorMinimo;
	private String tempoMedio;
	private List<String> listaFones;
	private boolean aberto;
	private Endereco endereco;
	private String distanciaEnderecos;
	private String tempoEnderecos;
	private List<Segmento> listaSegmentos;
	private int curtidas;
	private String urlImagem;
	
	public Empresa() {
		this.id           		= 0;
		this.nome         		= "";
		this.detalhamento 		= "";
		this.taxaEntrega  		= 0;
		this.valorMinimo  		= 0;
		this.tempoMedio   		= "";
		this.listaFones         = new ArrayList<String>();
		this.aberto       		= false;
		this.endereco     		= null;
		this.distanciaEnderecos = "";
		this.tempoEnderecos     = "";
		this.listaSegmentos     = new ArrayList<Segmento>();
		this.curtidas           = 0;
		this.urlImagem          = "";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDetalhamento() {
		return detalhamento;
	}

	public void setDetalhamento(String detalhamento) {
		this.detalhamento = detalhamento;
	}

	public double getTaxaEntrega() {
		return taxaEntrega;
	}

	public void setTaxaEntrega(double taxaEntrega) {
		this.taxaEntrega = taxaEntrega;
	}

	public double getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(double valorMinimo) {
		this.valorMinimo = valorMinimo;
	}

	public String getTempoMedio() {
		return tempoMedio;
	}

	public void setTempoMedio(String tempoMedio) {
		this.tempoMedio = tempoMedio;
	}

	public List<String> getListaFones() {
		return listaFones;
	}

	public void setListaFones(List<String> listaFones) {
		this.listaFones = listaFones;
	}

	public boolean isAberto() {
		return aberto;
	}

	public void setAberto(boolean aberto) {
		this.aberto = aberto;
	}

	public Endereco getEndereco() {
		return endereco;
	}

	public void setEndereco(Endereco endereco) {
		this.endereco = endereco;
	}

	public String getDistanciaEnderecos() {
		return distanciaEnderecos;
	}

	public void setDistanciaEnderecos(String distanciaEnderecos) {
		this.distanciaEnderecos = distanciaEnderecos;
	}

	public String getTempoEnderecos() {
		return tempoEnderecos;
	}

	public void setTempoEnderecos(String tempoEnderecos) {
		this.tempoEnderecos = tempoEnderecos;
	}

	public List<Segmento> getListaSegmentos() {
		return listaSegmentos;
	}

	public void setListaSegmentos(List<Segmento> listaSegmentos) {
		this.listaSegmentos = listaSegmentos;
	}

	public int getCurtidas() {
		return curtidas;
	}

	public void setCurtidas(int curtidas) {
		this.curtidas = curtidas;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

}
