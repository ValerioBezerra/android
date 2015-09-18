package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Voucher implements Serializable {
	private int id;
	private String codigo;
	private String descricao;
	private String tipo;
	private double valor;
	
	public Voucher() {
		this.id        = 0;
		this.codigo    = "";
		this.descricao = "";
		this.tipo      = "";
		this.valor     = 0;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public double getValor() {
		return valor;
	}

	public void setValor(double valor) {
		this.valor = valor;
	}
}
