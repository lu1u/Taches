package com.lpi.taches;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.icu.text.DateFormat;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.lpi.taches.alarmes.NotificationsManager;
import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.listetaches.TacheRecyclerViewAdapter;
import com.lpi.taches.report.Report;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Preferences;
import com.lpi.taches.taches.Tache;
import com.lpi.taches.utils.DateUtilitaires;
import com.lpi.taches.utils.MessageBoxUtils;
import com.lpi.taches.widget.TachesWidget;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity
{
	private RecyclerView _rvTaches;
	private TacheRecyclerViewAdapter _adapter;
	private int _currentItemSelected = -1;
	private int _optionTri = OptionTri.OPTION_TRI_NOM;
	private Preferences _preferences;
	private int _optionVue = OptionVue.OPTION_VUE_TOUTES;
	private Context _context;
	private Report _report;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
	{
		if (requestCode == TacheEditActivity.RESULT_TACHE_EDIT_ACTIVITY)
		{
			setTachesAdapter();
		}
		else
		{
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		_context = this;
		_report = Report.getInstance(this);

		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> TacheEditActivity.startForEdit(MainActivity.this, null));

		_preferences = Preferences.getInstance(this);
		_optionTri = _preferences.getInt(Preferences.PREF_SORT, OptionTri.OPTION_TRI_NOM);
		_optionVue = _preferences.getInt(Preferences.PREF_VUE, OptionVue.OPTION_VUE_TOUTES);
		initActivity();
		NotificationsManager.getInstance(this).onStart();
	}


	static public void SignaleErreur(@Nullable Context context, @NonNull final String message, @NonNull Exception e)
	{
		if ( context!=null)
		{
			Toast.makeText(context, message + "\n" + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();

			Report.getInstance(context).log(Report.ERROR, message);
			Report.getInstance(context).log(Report.ERROR, e);
		}
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
			SignaleErreur(this, "Erreur dans initActivity", e);
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

			String message = getResources().getQuantityString(R.plurals.nombre_de_taches, cursor.getCount());
			message = String.format(message, cursor.getCount());
			Toast.makeText(this, message, Toast.LENGTH_LONG).show();
		}
		TachesWidget.updateAllWidgets(this);
		NotificationsManager.getInstance(this).onStart();
	}

	/***
	 * Appeler la fenetre d'édition d'une tache
	 * @param position de la tache dans la liste
	 */
	private void onEdit(int position)
	{
		//EditTacheActivity.startForEdit(this, _adapter.get(position), () -> setTachesAdapter());
		Tache tache = _adapter.get(position);
		TacheEditActivity.startForEdit(this, tache);
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
		}
		else if (id == R.id.action_tri)
		{
			OptionTri.Start(this, _optionTri, selection ->
			{
				_optionTri = selection;
				_preferences.putInt(Preferences.PREF_SORT, _optionTri);
				MainActivity.this.setTachesAdapter();
			});
		}
		else if (id == R.id.action_vue)
		{
			OptionVue.Start(this, _optionVue, selection ->
			{
				_optionVue = selection;
				_preferences.putInt(Preferences.PREF_VUE, _optionVue);
				MainActivity.this.setTachesAdapter();
			});
		}
		else if ( id == R.id.action_partager)
		{
			partagerTaches();
		}

		return super.onOptionsItemSelected(item);
	}

	/***
	 * Lancer le partage Android sur la liste des taches affichées
	 */
	private void partagerTaches()
	{
		Cursor cursor = TachesDatabase.getInstance(this).getCursor(_optionTri, _optionVue);
		if (cursor != null)
		{
			StringBuilder sb = new StringBuilder();
			sb.append(getString(R.string.share_header_1));
			sb.append(getString(R.string.share_header_2));
			sb.append(getString(R.string.share_header_3));

			// Ajouter toutes les taches de la liste
			cursor.moveToFirst();
			while ( cursor.moveToNext())
			{
				Tache t = new Tache(cursor);
				sb.append(getString(R.string.share_nom)).append(t._nom ).append("\n");
				sb.append(getString(R.string.share_note)).append(t._note).append("\n");
				String p = t.getTextPriorite(this);
				if ( p!=null)
				{
					sb.append("Priorité:").append(p).append("\n");
				}

				Calendar creation = Calendar.getInstance();
				creation.setTimeInMillis(t._dateCreation);
				sb.append(getString(R.string.share_creation)).append(DateUtilitaires.getDateAndTime(creation, DateFormat.SHORT, DateFormat.SHORT)).append("\n");
				sb.append(getString(R.string.share_alarme)).append( t.getAlarme().toString());
				sb.append("\n\n");
			}

			// Lancer l'interface utilisateur pour partager les donnees
			Intent sendIntent = new Intent();
			sendIntent.setAction(Intent.ACTION_SEND);
			sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
			sendIntent.setType("text/plain");

			Intent shareIntent = Intent.createChooser(sendIntent, getString((R.string.app_name)));
			startActivity(shareIntent);
		}
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

	/***
	 * Choix d'un item dans le menu contextuel
	 */
	@Override public boolean onContextItemSelected(@NonNull MenuItem item)
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

			case R.id.action_partager:
				partageTache();
				return true;
			default:
				return super.onContextItemSelected(item);
		}
	}

	/***
	 * Partager la tache selectionnee
	 */
	private void partageTache()
	{
		_currentItemSelected = _adapter.getSelectedItem();
		if (_currentItemSelected == -1)
		{
			_report.log(Report.WARNING, "Partage de tache: pas de tache selectionnee");
			return;
		}

		final Tache tacheAPartager = _adapter.get(_currentItemSelected);
		if (tacheAPartager != null)
		{
		tacheAPartager.partage(this);
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