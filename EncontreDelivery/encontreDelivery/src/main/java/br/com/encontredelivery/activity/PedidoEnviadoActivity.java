package br.com.encontredelivery.activity;

import br.com.encontredelivery.R;
import br.com.encontredelivery.service.VerificarPedidoService;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;

public class PedidoEnviadoActivity extends ActionBarActivity {
	private long idPedido;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pedido_enviando);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		idPedido      = extras.getLong("idPedido");
	}

	@Override
	protected void onStart() {
		super.onStart();
		Bundle bundle = new Bundle();
		bundle.putLong("idPedido", idPedido);
		Intent intent = new Intent(this, VerificarPedidoService.class);
		intent.putExtras(bundle);
		startService(intent);
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
