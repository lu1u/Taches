package com.lpi.taches.listetaches;

import android.content.Context;
import android.database.Cursor;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lpi.taches.R;
import com.lpi.taches.customviews.CustomJaugeView;
import com.lpi.taches.taches.Tache;

public class TacheRecyclerViewAdapter extends RecyclerView.Adapter<TacheRecyclerViewAdapter.ViewHolder>
	{
	public interface ItemClicListener
		{
		void onClic(int position);
		}

	private final LayoutInflater _inflater;
	private final Cursor _cursor;
	private int _selectedItem = 0;
	private final ItemClicListener _itemClicListener;

	// data is passed into the constructor
	public TacheRecyclerViewAdapter(@NonNull final Context context, @NonNull final Cursor c, @NonNull final ItemClicListener listener)
		{
		_inflater = LayoutInflater.from(context);
		_cursor = c;
		_itemClicListener = listener;
		}

	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
		{
		View view = _inflater.inflate(R.layout.element_liste, parent, false);
		return new ViewHolder(view);
		}

	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int position)
		{
		_cursor.moveToPosition(position);
		Tache tache = new Tache(_cursor);
		holder.tvNom.setText(tache._nom);
		holder.tvPriorite.setText(tache.getTextPriorite(_inflater.getContext()));
		holder.cjJauge.setValeur(tache._achevement);
		holder.tvBack.setBackground(tache.getDrawablePriorite(_inflater.getContext()));
		}

	@Override
	public int getItemCount()
		{
		if (_cursor == null)
			return 0;
		else
			return _cursor.getCount();
		}

	public Tache get(int position)
		{
		if (_cursor == null)
			return null;

		if (position < 0 || position >= _cursor.getCount())
			return null;

		_cursor.moveToPosition(position);
		return new Tache(_cursor);
		}

	public int getSelectedItem()
		{
		return _selectedItem;
		}


	// stores and recycles views as they are scrolled off screen
	public class ViewHolder extends RecyclerView.ViewHolder
		{
		public final TextView tvNom;
		public final TextView tvPriorite;
		public final CustomJaugeView cjJauge;
		public final TextView tvBack;

		ViewHolder(View itemView)
			{
			super(itemView);
			tvNom = itemView.findViewById(R.id.tvNom);
			tvPriorite = itemView.findViewById(R.id.tvPriorite);
			cjJauge = itemView.findViewById(R.id.cjJauge);
			//imPriorite = itemView.findViewById(R.id.ivPriorite);
			tvBack = itemView.findViewById(R.id.tvBack);
			cjJauge.setMinimum(Tache.PROGRESSION_MINIMUM);
			cjJauge.setMaximum(Tache.PROGRESSION_MAXIMUM);

			itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener()
				{
				@Override
				public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo)
					{
					contextMenu.setHeaderTitle(tvNom.getText().toString());
					contextMenu.add(0, R.id.action_modifier, 0, "Modifier...");
					contextMenu.add(0, R.id.action_supprimer, 0, "Supprimer...");
					}
				});

			itemView.setOnClickListener(new View.OnClickListener()
				{
				@Override public void onClick(View view)
					{
					if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

					// Updating old as well as new positions
					notifyItemChanged(_selectedItem);
					_selectedItem = getAdapterPosition();
					notifyItemChanged(_selectedItem);
					_itemClicListener.onClic(_selectedItem);
					}
				});

			itemView.setOnLongClickListener(new View.OnLongClickListener()
				{
				@Override public boolean onLongClick(View view)
					{
					notifyItemChanged(_selectedItem);
					_selectedItem = getAdapterPosition();
					notifyItemChanged(_selectedItem);
					return false;
					}
				});
			}

		}
	}
