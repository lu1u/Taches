package com.lpi.taches;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

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
import com.lpi.taches.utils.MessageBoxUtils;
import com.lpi.taches.widget.TachesWidget;

public class MainActivity extends AppCompatActivity
	{
	private RecyclerView _rvTaches;
	private TacheRecyclerViewAdapter _adapter;
	private int _currentItemSelected = -1;
	private int _optionTri = OptionTri.OPTION_TRI_NOM;
	private Preferences _preferences;
	private int _optionVue = OptionVue.OPTION_VUE_TOUTES;
	private static Context _context;


	@Override
	protected void onCreate(Bundle savedInstanceState)
		{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		_context = this;
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> EditTacheActivity.startForEdit(MainActivity.this, null, () -> setTachesAdapter()));

		_preferences = Preferences.getInstance(this);
		_optionTri = _preferences.getInt(Preferences.PREF_SORT, OptionTri.OPTION_TRI_NOM);
		_optionVue = _preferences.getInt(Preferences.PREF_VUE, OptionVue.OPTION_VUE_TOUTES);
		initActivity();
		}


	static public void SignaleErreur(@NonNull final String message, @NonNull Exception e)
		{
			Toast.makeText(_context, message + "\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
		}


	/***
	 * Initialisation de l'Activity
	 */
	private void initActivity()
		{
			try
			{
				_rvTaches = findViewById(R.id.rvListeTaches);
				_rvTaches.setLayoutManager(new LinearLayoutManager(this));
				registerForContextMenu(_rvTaches);

				_rvTaches.setOnClickListener(view ->
												 {
												 view.setSelected(true);
												 _currentItemSelected = _rvTaches.getChildAdapterPosition(_rvTaches.getFocusedChild());
												 MainActivity.this.openContextMenu(view);
												 }
											);

				setTachesAdapter();
			} catch (Exception e)
			{
				SignaleErreur("Erreur dans initActivity", e);
			}
		}

	/***
	 * Configurer l'adapteur de taches de la RecyclerView (a faire après chaque modification dans la table des Taches),
	 * avertir les Widgets du changement
	 */
	private void setTachesAdapter()
		{
		Cursor cursor = TachesDatabase.getInstance(this).getCursor(_optionTri, _optionVue);
		if (cursor != null)
			{
			_adapter = new TacheRecyclerViewAdapter(this, cursor, position -> onEdit(position));
			_rvTaches.setAdapter(_adapter);
			}
		TachesWidget.updateAllWidgets(this);
		}

	/***
	 * Appeler la fenetre d'édition d'une tache
	 * @param position de la tache dans la liste
	 */
	private void onEdit(int position)
		{
		EditTacheActivity.startForEdit(this, _adapter.get(position), () -> setTachesAdapter());
		}

	/***
	 * Creer le menu de l'activity
	 * @param menu
	 * @return
	 */
	@Override public boolean onCreateOptionsMenu(Menu menu)
		{
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
		}

	/***
	 * Option de menu selectionnee
	 * @param item
	 * @return
	 */
	@Override public boolean onOptionsItemSelected(@NonNull MenuItem item)
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
			MessageBoxUtils.messageBox(this, getResources().getString(R.string.supprimer_titre), getResources().getString(R.string.supprimer_tache, objetASupprimer._nom),
			                           MessageBoxUtils.BOUTON_OK | MessageBoxUtils.BOUTON_CANCEL,
			                           new MessageBoxUtils.Listener()
				                           {
				                           @Override public void onOk()
					                           {
					                           // Supprimer
					                           final TachesDatabase database = TachesDatabase.getInstance(MainActivity.this);

					                           if (database.supprime(objetASupprimer._id))
						                           messageNotification(_rvTaches, "Tâche " + objetASupprimer._nom + " supprimée");
					                           else
						                           messageNotification(_rvTaches, "Erreur lors de la suppression de  " + objetASupprimer._nom + ", tache non supprimée");
					                           setTachesAdapter();
					                           _currentItemSelected = -1;
					                           }

				                           @Override public void onCancel()
					                           {
					                           }
				                           });
			}
		}

	public static void messageNotification(@NonNull View v, @NonNull String message)
		{
		Snackbar.make(v, message, Snackbar.LENGTH_LONG).show();
		}
	}