package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Horario implements Serializable {
	private int dia;
	private String horarios;
	
	public Horario() {
		this.dia      = 0;
		this.horarios = "";
	}
	
	public int getDia() {
		return dia;
	}
	public void setDia(int dia) {
		this.dia = dia;
	}

	public String getHorarios() {
		return horarios;
	}

	public void setHorarios(String horarios) {
		this.horarios = horarios;
	}
	
}
