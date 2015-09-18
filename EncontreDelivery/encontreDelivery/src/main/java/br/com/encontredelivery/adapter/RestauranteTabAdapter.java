package br.com.encontredelivery.adapter;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.fragment.CategoriaProdutoFragment;
import br.com.encontredelivery.fragment.InformacoesFragment;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.Horario;

public class RestauranteTabAdapter extends FragmentPagerAdapter {
	private Activity activity;
	private Fragment fragment;

	private CategoriaProdutoFragment categoriaProdutoFragment;
	private InformacoesFragment informacoesFragment;

	public RestauranteTabAdapter(Activity activity, FragmentManager fm) {
		super(fm);
		
		this.activity                     = activity;
		this.fragment                     = null;
		this.categoriaProdutoFragment     = new CategoriaProdutoFragment();
		this.informacoesFragment          = new InformacoesFragment();
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			fragment = categoriaProdutoFragment;
		} else {
			fragment = informacoesFragment;
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
			return activity.getString(R.string.cardapio);
		} else {
			return activity.getString(R.string.informacoes);
		}
    }

	public void carregarCategorias(List<Categoria> listaCategorias) {
		categoriaProdutoFragment.carregarCategorias(listaCategorias);
	}

	public void carregarHorarios(List<Horario> listaHorarios) {
		informacoesFragment.carregarHorarios(listaHorarios);
	}

	public void carregarFormasPagamento(List<FormaPagamento> listaFormasPagamento) {
		informacoesFragment.carregarFormasPagamento(listaFormasPagamento);
	}

	public void replaceFragment(Categoria categoria) {
		categoriaProdutoFragment.replaceFragment(categoria);
	}
}