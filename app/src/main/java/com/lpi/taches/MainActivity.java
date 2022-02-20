package com.lpi.taches;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.listetaches.TacheRecyclerViewAdapter;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Preferences;
import com.lpi.taches.taches.Tache;
import com.lpi.taches.widget.TachesWidget;

public class MainActivity extends AppCompatActivity
	{

	private RecyclerView _rvTaches;
	private TacheRecyclerViewAdapter _adapter;
	private int _currentItemSelected = -1;
	private int _optionTri = OptionTri.OPTION_TRI_NOM;
	private Preferences _preferences;
	private int _optionVue = OptionVue.OPTION_VUE_TOUTES;

	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(new View.OnClickListener()
			{
			@Override
			public void onClick(View view)
				{
				EditTacheActivity.startForEdit(MainActivity.this, null, new EditTacheActivity.Listener()
					{
					@Override public void onOk()
						{
						setTachesAdapter();
						}
					});
				}
			});

		_preferences = Preferences.getInstance(this);
		_optionTri = _preferences.getInt(Preferences.PREF_SORT, OptionTri.OPTION_TRI_NOM);
		_optionVue = _preferences.getInt(Preferences.PREF_VUE, OptionVue.OPTION_VUE_TOUTES);
		initActivity();
		}


	static public void SignaleErreur(String message, @NonNull Exception e)
		{

		}


	private void initActivity()
		{
		_rvTaches = findViewById(R.id.rvListeTaches);
		_rvTaches.setLayoutManager(new LinearLayoutManager(this));
		registerForContextMenu(_rvTaches);

		_rvTaches.setOnClickListener(new View.OnClickListener()
			                             {
			                             @Override public void onClick(@NonNull View view)
				                             {
				                             view.setSelected(true);
				                             _currentItemSelected = _rvTaches.getChildAdapterPosition(_rvTaches.getFocusedChild());
				                             MainActivity.this.openContextMenu(view);
				                             }
			                             }
		                            );


		setTachesAdapter();
		}

	private void setTachesAdapter()
		{
		Cursor cursor = TachesDatabase.getInstance(this).getCursor(_optionTri, _optionVue);
		if (cursor != null)
			{
			_adapter = new TacheRecyclerViewAdapter(this, cursor, new TacheRecyclerViewAdapter.ItemClicListener()
				{
				@Override public void onClic(int position)
					{
					onEdit(position);
					}
				});
			_rvTaches.setAdapter(_adapter);
			}
		TachesWidget.updateWidgets(this, getApplication());
		}

	private void onEdit(int position)
		{
		EditTacheActivity.startForEdit(this, _adapter.get(position), () -> setTachesAdapter());
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
		{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
		}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
		{
		int id = item.getItemId();

		if (id == R.id.action_apropos)
			{
			DialogAPropos.start(this);
			return true;
			} else if (id == R.id.action_tri)
			{
			OptionTri.Start(this, _optionTri, selection ->
				{
				_optionTri = selection;
				_preferences.putInt(Preferences.PREF_SORT, _optionTri);
				MainActivity.this.setTachesAdapter();
				});
			} else if (id == R.id.action_vue)
			{
			OptionVue.Start(this, _optionVue, selection ->
				{
				_optionVue = selection;
				_preferences.putInt(Preferences.PREF_VUE, _optionVue);
				MainActivity.this.setTachesAdapter();
				});
			}
		return super.onOptionsItemSelected(item);
		}

	@Override
	public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo)
		{
		super.onCreateContextMenu(menu, v, menuInfo);
		if (v.getId() == R.id.rvListeTaches)
			{
			if (_currentItemSelected != -1)
				{
				Tache selectedItem = _adapter.get(_currentItemSelected);
				if (selectedItem != null)
					{
					MenuInflater inflater = getMenuInflater();
					inflater.inflate(R.menu.menu_liste, menu);
					menu.setHeaderTitle(selectedItem._nom);
					}
				}
			}
		}

	@Override
	/***
	 * Choix d'un item dans le menu contextuel
	 */
	public boolean onContextItemSelected(@NonNull MenuItem item)
		{
		switch (item.getItemId())
			{
			case R.id.action_modifier:
				_currentItemSelected = _adapter.getSelectedItem();
				onEdit(_currentItemSelected);
				return true;
			case R.id.action_supprimer:
				supprime();
				return true;

			default:
				return super.onContextItemSelected(item);
			}
		}

	private void supprime()
		{
		_currentItemSelected = _adapter.getSelectedItem();
		if (_currentItemSelected == -1)
			return;

		final Tache objetASupprimer = _adapter.get(_currentItemSelected);
		if (objetASupprimer != null)
			{
			final TachesDatabase database = TachesDatabase.getInstance(MainActivity.this);
			AlertDialog dialog = new AlertDialog.Builder(this).create();
			dialog.setTitle("Supprimer");
			dialog.setMessage(getResources().getString(R.string.supprimer_tache, objetASupprimer._nom));
			dialog.setCancelable(false);
			dialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(android.R.string.ok),
			                 new DialogInterface.OnClickListener()
				                 {
				                 public void onClick(DialogInterface dialog, int buttonId)
					                 {
					                 // Supprimer
					                 if (database.supprime(objetASupprimer._id))
						                 messageNotification(_rvTaches, "Tâche " + objetASupprimer._nom + " supprimée");
					                 else
						                 messageNotification(_rvTaches, "Erreur lors de la suppression de  " + objetASupprimer._nom + ", tache non supprimée");
					                 setTachesAdapter();
					                 _currentItemSelected = -1;
					                 }
				                 });
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(android.R.string.cancel),
			                 (dialog1, buttonId) ->
				                 {
				                 // Ne rien faire
				                 });
			dialog.setIcon(android.R.drawable.ic_dialog_alert);
			dialog.show();
			}
		}

	public static void messageNotification(@NonNull View v, @NonNull String message)
		{
		Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
		}
	}