package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Configuracao implements Serializable {
	private int versao;
	private String mensagemInicial;
	private boolean bloquear;
	
	public Configuracao() {
		this.versao       	= 0;
		this.mensagemInicial = "";
		this.bloquear      	= false;
	}

	public int getVersao() {
		return versao;
	}

	public void setVersao(int versao) {
		this.versao = versao;
	}

	public String getMensagemInicial() {
		return mensagemInicial;
	}

	public void setMensagemInicial(String mensagemInicial) {
		this.mensagemInicial = mensagemInicial;
	}

	public boolean isBloquear() {
		return bloquear;
	}

	public void setBloquear(boolean bloquear) {
		this.bloquear = bloquear;
	}
	
}
