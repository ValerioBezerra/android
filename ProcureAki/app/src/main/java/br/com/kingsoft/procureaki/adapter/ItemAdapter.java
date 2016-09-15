package br.com.kingsoft.procureaki.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import br.com.kingsoft.procureaki.R;
import br.com.kingsoft.procureaki.model.Item;


public class ItemAdapter extends BaseAdapter {
	private Context context;
	private LayoutInflater inflater;
	private List<Item> listaItems;
	
	static class ItemViewHolder {
		TextView txtDescricao;
	}
	
	public ItemAdapter(Context context, List<Item> listaItems) {
		this.context        = context;
		this.inflater       = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.listaItems = listaItems;
	}

	@Override
	public int getCount() {
		return listaItems.size();
	}

	@Override
	public Object getItem(int position) {
		return listaItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return listaItems.get(position).getId();
	}
	
	@Override
	public View getView(int position, View view, ViewGroup parent) {
		final Item item = listaItems.get(position);
		ItemViewHolder itemViewHolder;
		
		if (view == null) {
			view = inflater.inflate(R.layout.adapter_item, parent, false);
			
			itemViewHolder = new ItemViewHolder();

			itemViewHolder.txtDescricao = (TextView) view.findViewById(R.id.txtDescricao);

			view.setTag(itemViewHolder);
		} else {
			itemViewHolder = (ItemViewHolder) view.getTag();
		}

		itemViewHolder.txtDescricao.setText(item.getDescricao());

		return view;
	}
	
}
