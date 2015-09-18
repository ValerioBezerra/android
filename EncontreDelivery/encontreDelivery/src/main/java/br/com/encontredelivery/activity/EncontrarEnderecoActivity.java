package br.com.encontredelivery.activity;


import br.com.encontredelivery.R;
import br.com.encontredelivery.adapter.EncontrarEnderecoTabAdapter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

public class EncontrarEnderecoActivity extends AppCompatActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_encontrar_endereco);

		if (getIntent().getExtras().getBoolean("adicionar"))
			setTitle(R.string.adicionar_endereco);
		else
			setTitle(R.string.encontrar_endereco);

		Toolbar toolbar     = (Toolbar) findViewById(R.id.toolbar);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

		EncontrarEnderecoTabAdapter encontrarEnderecoTabAdapter = new EncontrarEnderecoTabAdapter(this, getSupportFragmentManager());
		setSupportActionBar(toolbar);
		viewPager.setAdapter(encontrarEnderecoTabAdapter);
		tabLayout.setupWithViewPager(viewPager);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
