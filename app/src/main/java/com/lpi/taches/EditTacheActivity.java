package com.lpi.taches;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.taches.Tache;

public class EditTacheActivity extends AppCompatActivity
	{
	public interface Listener
		{
		void onOk();
		}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Demarre l'activity pour modifier un itineraire
	 *
	 * @param activity, l'activity qui va recevoir la notification de fin de l'edition
	 * @param tache
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public static void startForEdit(@NonNull final Activity activity, @Nullable final Tache tache, @NonNull final Listener listener)
		{
		final AlertDialog dialogBuilder = new AlertDialog.Builder(activity).create();
		LayoutInflater inflater = activity.getLayoutInflater();
		View dialogView = inflater.inflate(R.layout.activity_edit_tache, null);
		dialogView.setBackgroundColor(Color.argb(0.5f,1.0f,1.0f,1.0f));
		EditText etNom = dialogView.findViewById(R.id.etNom);
		Spinner spPriorite = dialogView.findViewById(R.id.spinnerPriorite);
		SeekBar pAchevement= dialogView.findViewById(R.id.pAchevement);
		Button bOk = dialogView.findViewById(R.id.bOk);
		Button bCancel = dialogView.findViewById(R.id.bCancel);
		TextView tvAchevement = dialogView.findViewById(R.id.tvAchevement);
		EditText etNote = dialogView.findViewById(R.id.etNote);

		// Spinner des priorites
		String[]  priorites= activity.getResources().getStringArray(R.array.priorites);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_item, priorites);
		spPriorite.setAdapter(adapter);

		// Progression
		pAchevement.setMin(Tache.PROGRESSION_MINIMUM);
		pAchevement.setMax(Tache.PROGRESSION_MAXIMUM);

		initChamps(activity, tache, etNom, spPriorite, pAchevement, tvAchevement, etNote);

		pAchevement.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
			{
			@Override public void onProgressChanged(@NonNull final SeekBar seekBar, int i, boolean b)
				{
				tvAchevement.setText(seekBar.getProgress()+"%");
				}
			@Override public void onStartTrackingTouch(SeekBar seekBar)
				{

				}
			@Override public void onStopTrackingTouch(SeekBar seekBar)
				{

				}
			});

		bOk.setOnClickListener(new View.OnClickListener()
			{
			@Override public void onClick(View view)
				{
				onOK(activity, tache, etNom,spPriorite, pAchevement, etNote );
				listener.onOk();
				dialogBuilder.dismiss();
				}
			});

		bCancel.setOnClickListener(new View.OnClickListener()
			{
			@Override public void onClick(View view)
				{
				dialogBuilder.dismiss();
				}
			});
		dialogBuilder.setView(dialogView);
		dialogBuilder.show();
		}



	private static void onOK(@NonNull final Context context, @Nullable Tache tache, EditText etNom, Spinner spPriorite, SeekBar pAchevement, @NonNull final EditText etNote)
		{
		boolean creer = false;
		if ( tache == null)
			{
			tache = new Tache();
			creer = true;
			}

		tache._nom = etNom.getText().toString();
		tache._priorite = spPriorite.getSelectedItemPosition();
		tache._achevement = pAchevement.getProgress();
		tache._note = etNote.getText().toString();

		TachesDatabase database = TachesDatabase.getInstance(context);
		if ( creer)
			database.ajoute(tache);
		else
			database.modifie(tache);
		}

	private static void initChamps(@NonNull final Context context, @Nullable Tache tache, @NonNull final EditText etNom, @NonNull final Spinner spPriorite, @NonNull final SeekBar pAchevement, @NonNull final TextView tvAchement, @NonNull final EditText etNote)
		{
		if ( tache == null)
			{
			tache = new Tache();
			tache._nom = context.getResources().getString(R.string.tache_sans_nom, TachesDatabase.getInstance(context).nbTaches()+1);
			}

		etNom.setText(tache._nom);
		spPriorite.setSelection(tache._priorite);
		pAchevement.setProgress(tache._achevement);
		tvAchement.setText(tache._achevement + "%");
		etNote.setText(tache._note);
		}
	}