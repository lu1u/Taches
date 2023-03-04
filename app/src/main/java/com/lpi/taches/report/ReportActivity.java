package com.lpi.taches.report;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.taches.R;
import com.lpi.taches.utils.ConfirmBox;

import java.util.Objects;


public class ReportActivity extends AppCompatActivity
{
	int _niveau = Report.DEBUG;
	ReportAdapter _adapter;

	/***
	 * Lancer l'activity Report
	 * @param mainActivity
	 */
	public static void start(Activity mainActivity)
	{
		mainActivity.startActivity(new Intent(mainActivity, ReportActivity.class));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_report);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

		// Listview qui contient les messages
		ListView lv = findViewById(R.id.idListView);
		lv.setEmptyView(findViewById(R.id.textViewEmpty));
		_adapter = new ReportAdapter(this, ReportDatabase.getInstance(this).getCursor(_niveau));
		lv.setAdapter(_adapter);

		/// Spinner pour le niveau de traces affichées (DEBUG, WARNING, ERROR)
		Spinner spinner = findViewById(R.id.spinner);
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.niveauxRapport, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
				_niveau = position;
				_adapter.changeCursor(ReportDatabase.getInstance(ReportActivity.this).getCursor(_niveau));
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{

			}
		});
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onSupportNavigateUp()
	{
		finish();
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.menu_report, menu);
		return true;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * Menu principal
	 * @param item
	 * @return
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item)
	{
		int id = item.getItemId();

		switch (id)
		{
			// Vider les traces
			case R.id.menu_report_delete:
			{
				ConfirmBox.show(this, "Effacer les traces?", new ConfirmBox.ConfirmBoxListener()
				{
					@Override public void onPositive()
					{
						ReportDatabase db = ReportDatabase.getInstance(ReportActivity.this);
						db.Vide();
						_adapter.changeCursor(db.getCursor(_niveau));
					}

					@Override public void onNegative()
					{

					}
				});
			}
			break;
			case R.id.menu_report_partager:
				partager();
				break;
			default:
				return super.onOptionsItemSelected(item);
		}
		return true;
	}

	/***
	 * Partager le contenu du rapport
	 */
	private void partager()
	{
		Report report = Report.getInstance(this);
		String texte = report.getText(this, _niveau);
		report.log(Report.HISTORIQUE, "Partage:");
		report.log(Report.HISTORIQUE, texte);
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, texte);
		sendIntent.setType("text/plain");

		Intent shareIntent = Intent.createChooser(sendIntent, getString(R.string.app_name));
		startActivity(shareIntent);
	}
}
