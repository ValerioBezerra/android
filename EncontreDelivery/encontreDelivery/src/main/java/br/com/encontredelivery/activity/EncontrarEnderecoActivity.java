package br.com.encontredelivery.activity;


import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EncontrarEnderecoTabAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class EncontrarEnderecoActivity extends ActionBarActivity {
	
	private EncontrarEnderecoTabAdapter encontrarEnderecoTabAdapter;
	private ViewPager mViewPager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encontrar_endereco);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (getIntent().getExtras().getBoolean("adicionar"))
			setTitle(R.string.adicionar_endereco);
		else
			setTitle(R.string.encontrar_endereco);

		encontrarEnderecoTabAdapter = new EncontrarEnderecoTabAdapter(this, getSupportFragmentManager());	
		
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(encontrarEnderecoTabAdapter);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    	case android.R.id.home: finish();
	    		 break;
	    }
	    return super.onOptionsItemSelected(item);
	}

}
