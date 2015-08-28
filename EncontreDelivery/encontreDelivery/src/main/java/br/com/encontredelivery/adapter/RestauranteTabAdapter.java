package br.com.encontredelivery.adapter;


import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.fragment.InformacoesFragment;
import br.com.encontredelivery.fragment.RestauranteCategoriaFragment;
import br.com.encontredelivery.model.Categoria;
import br.com.encontredelivery.model.FormaPagamento;
import br.com.encontredelivery.model.Horario;

public class RestauranteTabAdapter extends FragmentPagerAdapter {
	private Activity activity;
	private Fragment fragment;

	private Fragment cardapioFragment;
	private RestauranteCategoriaFragment restauranteCategoriaFragment;
	private InformacoesFragment informacoesFragment;

	private FragmentManager fragmentManager;

	public RestauranteTabAdapter(Activity activity, FragmentManager fm) {
		super(fm);
		
		this.activity                     = activity;
		this.fragment                     = null;
		this.restauranteCategoriaFragment = new RestauranteCategoriaFragment();
		this.informacoesFragment          = new InformacoesFragment();
		this.cardapioFragment             = restauranteCategoriaFragment;
		this.fragmentManager              = fm;
	}

	@Override
	public Fragment getItem(int position) {
		if (position == 0) {
			fragment = cardapioFragment;
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
		restauranteCategoriaFragment.carregarCategorias(listaCategorias);
	}

	public void carregarHorarios(List<Horario> listaHorarios) {
		informacoesFragment.carregarHorarios(listaHorarios);
	}

	public void carregarFormasPagamento(List<FormaPagamento> listaFormasPagamento) {
		informacoesFragment.carregarFormasPagamento(listaFormasPagamento);
	}

	public void trocarCardapioFragment(Categoria categoria) {
		fragmentManager.beginTransaction().remove(cardapioFragment).commit();
		if (categoria == null) {
			cardapioFragment  = restauranteCategoriaFragment;
		} else {
			cardapioFragment = new InformacoesFragment();
		}
		fragmentManager.beginTransaction().add(cardapioFragment, toString()).commit();
		notifyDataSetChanged();
//		getItem(0);
//		fragment = cardapioFragment;
//		fragment.on
	}
}
