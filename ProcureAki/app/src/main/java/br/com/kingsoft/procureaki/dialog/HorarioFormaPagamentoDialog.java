package br.com.kingsoft.procureaki.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.activity.EncontreMeuEnderecoActivity;
import br.com.kingsoft.procureaki.activity.SegmentosActivity;
import br.com.kingsoft.procureaki.adapter.EnderecoAdapter;
import br.com.kingsoft.procureaki.adapter.FormaPagamentoAdapter;
import br.com.kingsoft.procureaki.adapter.HorarioAdapter;
import br.com.kingsoft.procureaki.model.Endereco;
import br.com.kingsoft.procureaki.model.FormaPagamento;
import br.com.kingsoft.procureaki.model.Horario;
import br.com.kingsoft.procureaki.util.Retorno;
import br.com.kingsoft.procureaki.view.NoScrollGridView;
import br.com.kingsoft.procureaki.view.NoScrollListView;


public class HorarioFormaPagamentoDialog extends Dialog {

	private Context context;
	private NoScrollListView lvHorario;
	private NoScrollListView gvFormasPagamento;
	private Button btnOk;
	private View view;

	private View.OnClickListener clickOk = new View.OnClickListener() {
		@Override
		public void onClick(View view) {
			dismiss();
		}
	};

	public HorarioFormaPagamentoDialog(Context context) {
		super(context);
		this.context = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);    
        setContentView(R.layout.dialog_horario_forma_pagamento);
        
        view = getWindow().getDecorView();    
        view.setBackgroundResource(android.R.color.transparent);

		lvHorario         = (NoScrollListView) findViewById(R.id.lvHorario);
		gvFormasPagamento = (NoScrollListView) findViewById(R.id.gvFormasPagamento);

		btnOk = (Button) findViewById(R.id.btnOk);
		btnOk.setOnClickListener(clickOk);

        setCancelable(false);
	}
	

	 public void setListaHorarios(List<Horario> listaHorario) {
		 HorarioAdapter horarioAdapter = new HorarioAdapter(context, listaHorario);
		 lvHorario.setAdapter(horarioAdapter);
	 }

	public void setListaFormaPagamento(List<FormaPagamento> listaFormaPagamento) {
		FormaPagamentoAdapter formaPagamentoAdapter = new FormaPagamentoAdapter(context, listaFormaPagamento);
		gvFormasPagamento.setAdapter(formaPagamentoAdapter);
	}
}
