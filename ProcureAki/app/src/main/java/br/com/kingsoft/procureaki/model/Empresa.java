package br.com.kingsoft.procureaki.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("serial")
public class Empresa implements Serializable {
	private int id;
	private String nome;
	private String detalhamento;
	private List<String> listaFones;
	private boolean aberto;
	private Endereco endereco;
	private int quantidadeDiasAtualizacao;
	private double distanciaKm;
	private String urlImagem;
	
	public Empresa() {
		this.id           		       = 0;
		this.nome         		       = "";
		this.detalhamento 		       = "";
		this.listaFones                = new ArrayList<String>();
		this.aberto       		       = false;
		this.endereco     		       = null;
		this.quantidadeDiasAtualizacao = 0;
		this.distanciaKm               = 0;
		this.urlImagem                 = "";
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

	public int getQuantidadeDiasAtualizacao() {
		return quantidadeDiasAtualizacao;
	}

	public void setQuantidadeDiasAtualizacao(int quantidadeDiasAtualizacao) {
		this.quantidadeDiasAtualizacao = quantidadeDiasAtualizacao;
	}

	public double getDistanciaKm() {
		return distanciaKm;
	}

	public void setDistanciaKm(double distanciaKm) {
		this.distanciaKm = distanciaKm;
	}

	public String getUrlImagem() {
		return urlImagem;
	}

	public void setUrlImagem(String urlImagem) {
		this.urlImagem = urlImagem;
	}

}
