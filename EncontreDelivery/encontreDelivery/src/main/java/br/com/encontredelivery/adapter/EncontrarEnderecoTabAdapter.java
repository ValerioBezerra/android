package br.com.encontredelivery.adapter;


import br.com.encontredelivery.R;
import br.com.encontredelivery.fragment.EncontrarEnderecoCEPFragment;
import br.com.encontredelivery.fragment.EncontrarEnderecoLogradouroFragment;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class EncontrarEnderecoTabAdapter extends FragmentPagerAdapter {
	private Activity activity;
	private Fragment fragment;
	
	private EncontrarEnderecoLogradouroFragment encontrarEnderecoEnderecoFragment;
	private EncontrarEnderecoCEPFragment encontrarEnderecoEnderecoCEP;

	public EncontrarEnderecoTabAdapter(Activity activity, FragmentManager fm) {
		super(fm);
		
		this.activity                          = activity;
		this.fragment                          = null;
		this.encontrarEnderecoEnderecoFragment = new EncontrarEnderecoLogradouroFragment();
		this.encontrarEnderecoEnderecoCEP      = new EncontrarEnderecoCEPFragment();
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			fragment = encontrarEnderecoEnderecoFragment;
		} else {
			fragment = encontrarEnderecoEnderecoCEP;
		}
		
		return fragment;		
	}

	@Override
	public int getCount() {
		return 2;
	}
	
	@Override
	public CharSequence getPageTitle(int position) {
		if (position == 0) {
			return activity.getString(R.string.endereco);
		} else {
			return activity.getString(R.string.cep);
		}
    }
	


}
