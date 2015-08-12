package br.com.encontredelivery.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Pedido implements Serializable {
	private long id;
	private Status status;
	private Empresa empresa;
	private String descricaoProdutos;
	private String precoProdutos;
	private double taxaEntrega;
	private FormaPagamento formaPagamento;
	private Voucher voucher;
	private double desconto;
	private double valorTotal;
	
	public Pedido() {
		this.id                  = 0;
		this.status              = new Status();
		this.empresa             = new Empresa();
		this.descricaoProdutos   = "";
		this.precoProdutos       = "";
		this.taxaEntrega         = 0;
		this.formaPagamento      = new FormaPagamento();
		this.voucher             = null;
		this.desconto            = 0;
		this.valorTotal          = 0;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public String getDescricaoProdutos() {
		return descricaoProdutos;
	}

	public void setDescricaoProdutos(String descricaoProdutos) {
		this.descricaoProdutos = descricaoProdutos;
	}

	public String getPrecoProdutos() {
		return precoProdutos;
	}

	public void setPrecoProdutos(String precoProdutos) {
		this.precoProdutos = precoProdutos;
	}

	public double getTaxaEntrega() {
		return taxaEntrega;
	}

	public void setTaxaEntrega(double taxaEntrega) {
		this.taxaEntrega = taxaEntrega;
	}

	public FormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(FormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public Voucher getVoucher() {
		return voucher;
	}

	public void setVoucher(Voucher voucher) {
		this.voucher = voucher;
	}

	public double getDesconto() {
		return desconto;
	}

	public void setDesconto(double desconto) {
		this.desconto = desconto;
	}

	public double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(double valorTotal) {
		this.valorTotal = valorTotal;
	}
}
