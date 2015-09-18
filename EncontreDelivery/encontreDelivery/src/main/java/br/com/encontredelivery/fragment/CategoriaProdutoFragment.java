package br.com.encontredelivery.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import br.com.encontredelivery.R;
import br.com.encontredelivery.model.Categoria;

public class CategoriaProdutoFragment extends Fragment {
	RestauranteCategoriasFragment restauranteCategoriasFragment;
	RestauranteProdutosFragment restauranteProdutosFragment;

	private List<Categoria> listaCategorias;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_categoria_produto, container, false);

		FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
		restauranteCategoriasFragment = new RestauranteCategoriasFragment();
		fragmentTransaction.add(R.id.layout_fragment, restauranteCategoriasFragment);
		fragmentTransaction.addToBackStack("categoria");
		fragmentTransaction.commit();

		return view;
	}

	public void carregarCategorias(List<Categoria> listaCategorias) {
		this.listaCategorias = listaCategorias;
		restauranteCategoriasFragment.carregarCategorias(listaCategorias);
	}

	public void replaceFragment(Categoria categoria) {
		Fragment fragment;
		if (categoria == null) {
			fragment = restauranteCategoriasFragment;
			restauranteCategoriasFragment.carregarCategorias(listaCategorias);
		} else {
			Bundle bundle = new Bundle();
			bundle.putSerializable("categoria", categoria);
			restauranteProdutosFragment = new RestauranteProdutosFragment();
			restauranteProdutosFragment.setArguments(bundle);
			fragment                    = restauranteProdutosFragment;
		}

		FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.layout_fragment, fragment);
		fragmentTransaction.addToBackStack("produto");
		fragmentTransaction.commit();
	}

}