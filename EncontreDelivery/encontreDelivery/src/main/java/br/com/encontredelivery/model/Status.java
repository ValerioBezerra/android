package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Status implements Serializable {
	private int id;
	private String descricao;
	private int indicador;
	private String dataHora;
	private String motivoCancelamento;
	
	public Status() {
		this.id                 = 0;
		this.descricao          = "";
		this.indicador          = 0;
		this.dataHora           = "";
		this.motivoCancelamento = "";
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

	public int getIndicador() {
		return indicador;
	}

	public void setIndicador(int indicador) {
		this.indicador = indicador;
	}

	public String getDataHora() {
		return dataHora;
	}

	public void setDataHora(String dataHora) {
		this.dataHora = dataHora;
	}

	public String getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(String motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

}
