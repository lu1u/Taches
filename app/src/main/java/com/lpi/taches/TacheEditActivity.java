package com.lpi.taches;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NavUtils;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.report.Report;
import com.lpi.taches.taches.Alarme;
import com.lpi.taches.taches.Tache;

import java.util.Calendar;

public class TacheEditActivity extends AppCompatActivity
{
	public static final int RESULT_TACHE_EDIT_ACTIVITY = 324544;
	private Tache _tache;
	private EditText _etNom;
	private Spinner _spPriorite;
	private SeekBar _pAchevement;
	private TextView _tvAchevement;
	private EditText _etNote;
	private EditAlarmeFragment _fEditAlarme;

	private static final int MODE_CREER = 0;
	private static final int MODE_MODIFIER = 1;
	private static int _mode;

	/***
	 * Demarrage de l'activity, qui repondra par RESULT_TACHE_EDIT_ACTIVITY, a intercepter
	 * par onActivityResult
	 * @param activity
	 * @param tache
	 */
	public static void startForEdit(@NonNull Activity activity, @Nullable Tache tache)
	{
		Intent intent = new Intent(activity, TacheEditActivity.class);
		if (tache != null)
		{
			Bundle b = new Bundle();
			tache.toBundle(b, true);
			intent.putExtras(b);
		}
		activity.startActivityForResult(intent, RESULT_TACHE_EDIT_ACTIVITY);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tache_edit);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		ActionBar action = getSupportActionBar();
		if (action != null)
			action.setDisplayHomeAsUpEnabled(true);
		FloatingActionButton fab = findViewById(R.id.fab);
		fab.setOnClickListener(view -> onOk());

		// Recuperation de la tache qu'on a fourni en entree dans le cas ou on modifie
		// une tache existante
		Report r = Report.getInstance(this);
		Bundle b = getIntent().getExtras();
		if (b != null)
		{
			try
			{
				_tache = new Tache(b);
				_mode = MODE_MODIFIER;
				r.log(Report.HISTORIQUE, "Modification de la tache " + _tache._nom);

			} catch (Exception e)
			{
				_tache = null;
			}
		}

		if (_tache == null)
		{
			r.log(Report.HISTORIQUE, "Création nouvelle tache");
			_tache = new Tache();
			_tache._nom = TachesDatabase.getInstance(this).getNomUnique(this, R.string.tache_sans_nom);
			_mode = MODE_CREER;
		}

		getControles();
		initUI();
	}

	/***
	 * Initialisation des champs de l'interface utilisateur
	 */
	private void initUI()
	{
		// Spinner priorités
		String[] priorites = getResources().getStringArray(R.array.priorites);
		ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorites);
		_spPriorite.setAdapter(adapter);


		_etNom.setText(_tache._nom);
		_spPriorite.setSelection(_tache._priorite);
		_pAchevement.setProgress(_tache._achevement);
		_tvAchevement.setText(_tache._achevement + "%");
		_etNote.setText(_tache._note);

		// Progression
		_pAchevement.setMin(Tache.PROGRESSION_MINIMUM);
		_pAchevement.setMax(Tache.PROGRESSION_MAXIMUM);
		_pAchevement.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override
			public void onProgressChanged(@NonNull final SeekBar seekBar, int i, boolean b)
			{
				_tvAchevement.setText(seekBar.getProgress() + "%");
			}

			@Override public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override public void onStopTrackingTouch(SeekBar seekBar)
			{

			}
		});

		// Alarme
		final Alarme a = _tache.getAlarme();
		_fEditAlarme.setAlarmeActive(a.isActive());
		_fEditAlarme.setAlarme(a.getCalendar());
		_fEditAlarme.setListener(new EditAlarmeFragment.Listener()
		{
			@Override public void changeActive(boolean active)
			{
				a.setActive(active);
			}

			@Override public void changeDate(int y, int m, int j)
			{
				Calendar c = a.getCalendar();
				c.set(Calendar.YEAR, y);
				c.set(Calendar.MONTH, m);
				c.set(Calendar.DAY_OF_MONTH, j);
			}

			@Override public void changeHeure(int h, int m)
			{
				Calendar c = a.getCalendar();
				c.set(Calendar.HOUR_OF_DAY, h);
				c.set(Calendar.MINUTE, m);
			}
		});
	}

	private void getControles()
	{
		_etNom = findViewById(R.id.etNom);
		_spPriorite = findViewById(R.id.spinnerPriorite);
		_pAchevement = findViewById(R.id.pAchevement);
		_tvAchevement = findViewById(R.id.tvAchevement);
		_etNote = findViewById(R.id.etNote);
		_fEditAlarme = (EditAlarmeFragment) getFragmentManager().findFragmentById(R.id.fEditAlarme);

		View.OnFocusChangeListener listener = new View.OnFocusChangeListener()
		{
			@Override public void onFocusChange(View view, boolean b)
			{
				if (b)
					_fEditAlarme.updateUI(false);
			}
		};
		_etNom.setOnFocusChangeListener(listener);
		_spPriorite.setOnFocusChangeListener(listener);
		_pAchevement.setOnFocusChangeListener(listener);
		_tvAchevement.setOnFocusChangeListener(listener);
		_etNote.setOnFocusChangeListener(listener);
	}

	/***
	 * Validation de la fenetre
	 */
	private void onOk()
	{
		// Modifier la tache
		_tache._nom = _etNom.getText().toString();
		_tache._priorite = _spPriorite.getSelectedItemPosition();
		_tache._achevement = _pAchevement.getProgress();
		_tache._note = _etNote.getText().toString();
		TachesDatabase database = TachesDatabase.getInstance(this);

		Report r = Report.getInstance(this);

		if (_mode == MODE_CREER)
		{
			r.log(Report.HISTORIQUE, "Ajout de la tache: " + _tache);
			database.ajoute(_tache);
		}
		else
		{
			r.log(Report.HISTORIQUE, "Modification de la tache: " + _tache);
			database.modifie(_tache);
		}

		// Avertir l'activite appelante
		setResult(RESULT_TACHE_EDIT_ACTIVITY, null);
		finish();//finishing activity
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			// Respond to the action bar's Up/Home button
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;

			case R.id.action_ok:
			{
				onOk();
				return true;
			}

			case R.id.action_annuler:
			{
				NavUtils.navigateUpFromSameTask(this);
				return true;
			}
		}
		return super.onOptionsItemSelected(item);
	}

	/***
	 * Creer le menu de l'activity
	 * @param menu
	 * @return
	 */
	@Override public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_edit, menu);
		return true;
	}


}