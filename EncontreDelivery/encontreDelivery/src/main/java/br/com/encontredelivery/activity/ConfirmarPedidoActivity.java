package br.com.encontredelivery.activity;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Timer;
import java.util.TimerTask;

import br.com.encontredelivery.R;
import br.com.encontredelivery.dialog.ErroAvisoDialog;
import br.com.encontredelivery.dialog.ProgressoDialog;
import br.com.encontredelivery.model.Empresa;
import br.com.encontredelivery.util.Util;
import br.com.encontredelivery.webservice.PedidoRest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class ConfirmarPedidoActivity extends ActionBarActivity {
	private long idPedido;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_confirmar_pedido);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		idPedido      = extras.getLong("idPedido");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home: finish();
				 break;
		}
		return super.onOptionsItemSelected(item);
	}

	public void clickAcompanharPedido(View view) {
		Bundle extras = new Bundle();
		extras.putLong("idPedido", idPedido);

		Intent intent = new Intent(this, DetalhesPedidoActivity.class);
		intent.putExtras(extras);
		startActivity(intent);
		finish();
	}
	
	public void clickTelaInicial(View view) {
		finish();
	}
}
