package com.lpi.taches.taches;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.icu.text.DateFormat;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.lpi.taches.R;
import com.lpi.taches.database.DbHelper;
import com.lpi.taches.utils.DateUtilitaires;

import java.util.Calendar;

public class Tache
{
	public static final int PRIORITE_TRES_BASSE = 0;
	public static final int PRIORITE_BASSE = 1;
	public static final int PRIORITE_NORMALE = 2;
	public static final int PRIORITE_HAUTE = 3;
	public static final int PRIORITE_TRES_HAUTE = 4;
	public static final int INVALID_ID = -1;
	public static final int PROGRESSION_MINIMUM = 0;
	public static final int PROGRESSION_MAXIMUM = 100;

	public static String[] _nomsPriorites;
	private static TypedArray _drawablePriorites;
	private static int[] _couleursPriorites;

	public int _id;
	public String _nom = "";
	public int _priorite;
	public int _achevement; // PROGRESSION_MINIMUM..PROGRESSION_MAXIMUM
	public long _dateCreation = Calendar.getInstance().getTimeInMillis();
	public String _note = "";
	public Alarme _alarme = new Alarme();

	public Tache()
	{
		_id = INVALID_ID;
		_priorite = PRIORITE_NORMALE;
		_achevement = 0;
	}

	public Tache(@NonNull final String nom, int priorite, int achevement, long dateCreation, @NonNull final String note)
	{
		_nom = nom;
		_priorite = priorite;
		_achevement = achevement;
		_dateCreation = dateCreation;
		_note = note;
	}

	/***
	 * Creer un itineraire a partir de la base de donnee
	 * @param cursor
	 */
	public Tache(@NonNull Cursor cursor) throws IllegalArgumentException
	{
		_id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_ID));
		_nom = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_NOM));
		_dateCreation = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_CREATION));
		_priorite = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_PRIORITE));
		_achevement = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_ACHEVEMENT));
		_note = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_NOTE));
		String al = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_ALARME));
		_alarme = new Alarme(al);
	}

	/***
	 * Creer un itineraire a partir d'un bundle
	 * @param bundle
	 */
	public Tache(@NonNull Bundle bundle)
	{
		_id = bundle.getInt(DbHelper.COLONNE_TACHE_ID, _id);
		_nom = bundle.getString(DbHelper.COLONNE_TACHE_NOM, "");
		_dateCreation = bundle.getLong(DbHelper.COLONNE_TACHE_CREATION, Calendar.getInstance().getTimeInMillis());
		_priorite = bundle.getInt(DbHelper.COLONNE_TACHE_PRIORITE, PRIORITE_NORMALE);
		_achevement = bundle.getInt(DbHelper.COLONNE_TACHE_ACHEVEMENT, 0);
		_note = bundle.getString(DbHelper.COLONNE_TACHE_NOTE, "");
		String al = bundle.getString(DbHelper.COLONNE_TACHE_ALARME, null);
		_alarme = new Alarme(al);
	}

	public void toBundle(@NonNull Bundle content, boolean putId)
	{
		if (putId)
			content.putInt(DbHelper.COLONNE_TACHE_ID, _id);

		content.putString(DbHelper.COLONNE_TACHE_NOM, _nom);
		content.putLong(DbHelper.COLONNE_TACHE_CREATION, _dateCreation);
		content.putInt(DbHelper.COLONNE_TACHE_PRIORITE, _priorite);
		content.putInt(DbHelper.COLONNE_TACHE_ACHEVEMENT, _achevement);
		content.putString(DbHelper.COLONNE_TACHE_NOTE, _note);
		if (_alarme._alarmeActive)
			content.putString(DbHelper.COLONNE_TACHE_ALARME, DateUtilitaires.calendarToSqlString(_alarme._dateAlarme));
	}

	public void toContentValues(@NonNull ContentValues content, boolean putId)
	{
		if (putId)
			content.put(DbHelper.COLONNE_TACHE_ID, _id);

		content.put(DbHelper.COLONNE_TACHE_NOM, _nom);
		content.put(DbHelper.COLONNE_TACHE_CREATION, _dateCreation);
		content.put(DbHelper.COLONNE_TACHE_PRIORITE, _priorite);
		content.put(DbHelper.COLONNE_TACHE_ACHEVEMENT, _achevement);
		content.put(DbHelper.COLONNE_TACHE_NOTE, _note);
		if (_alarme._alarmeActive)
			content.put(DbHelper.COLONNE_TACHE_ALARME, DateUtilitaires.calendarToSqlString(_alarme._dateAlarme));
		else
			content.put(DbHelper.COLONNE_TACHE_ALARME, (String)null);
	}

	public String getTextPriorite(@NonNull final Context context)
	{
		if (_nomsPriorites == null)
			_nomsPriorites = context.getResources().getStringArray(R.array.priorites);

		if (_priorite < 0 || _priorite >= _nomsPriorites.length)
			_priorite = PRIORITE_NORMALE;

		return _nomsPriorites[_priorite];
	}

	public Drawable getDrawablePriorite(Context context)
	{
		if (_drawablePriorites == null)
			_drawablePriorites = context.getResources().obtainTypedArray(R.array.priorites_drawables);

		if (_priorite < 0 || _priorite >= _drawablePriorites.length())
			_priorite = PRIORITE_NORMALE;

		return _drawablePriorites.getDrawable(_priorite);
	}

	public int getCouleurPriorite(Context context)
	{
		if (_couleursPriorites == null)
			_couleursPriorites = context.getResources().getIntArray(R.array.priorites_couleurs);

		if (_priorite < 0 || _priorite >= _couleursPriorites.length)
			_priorite = PRIORITE_NORMALE;

		return _couleursPriorites[_priorite];
	}

	public Alarme getAlarme()
	{
		return _alarme;
	}

	public void setAlarme(Alarme a)
	{
		_alarme = a;
	}

	public @NonNull String toString()
	{
		Calendar creation = Calendar.getInstance();
		creation.setTimeInMillis(_dateCreation);
		String nomPriorite = _nomsPriorites==null? "pas encore initialisée" : _nomsPriorites[_priorite];
		return "ID:" + _id + "," + _nom + "," + nomPriorite + ", création:" + DateUtilitaires.getDateAndTime(creation, DateFormat.SHORT, DateFormat.SHORT) + ", alarme:" + _alarme;
	}

	/***
	 * Calcule le texte du contenu de la notification
	 * @return
	 */
	public CharSequence getNotificationContentText(@NonNull Context context)
	{
		String res = "";
		if (_note.length() > 0)
			res += _note + "\n";

		if ( _nomsPriorites!=null)
			res += getTextPriorite(context) + "\n";

		// L'alarme est forcement active, puisqu'on fait une notification
		res += "Alarme:" + DateUtilitaires.getDateAndTime(_alarme.getCalendar(), DateFormat.MEDIUM, DateFormat.SHORT);
		return res;
	}

	/***
	 * Partager (au sens Android) le contenu de cette tache
	 * @param context
	 */
	public void partage(@NonNull Context context)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(context.getString(R.string.share_header_1));
		sb.append(context.getString(R.string.share_header_2));
		sb.append(context.getString(R.string.share_header_3));

		sb.append(context.getString(R.string.share_nom)).append(_nom ).append("\n");
		sb.append(context.getString(R.string.share_note)).append(_note).append("\n");
		if ( _nomsPriorites!=null)
		{
			sb.append("Priorité:").append(_nomsPriorites[_priorite]).append("\n");
		}

		Calendar creation = Calendar.getInstance();
		creation.setTimeInMillis(_dateCreation);
		sb.append(context.getString(R.string.share_creation)).append(DateUtilitaires.getDateAndTime(creation, DateFormat.SHORT, DateFormat.SHORT)).append("\n");
		sb.append(context.getString(R.string.share_alarme)).append( _alarme.toString());

		// Lancer l'interface utilisateur pour partager les donnees
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		sendIntent.setType("text/plain");

		Intent shareIntent = Intent.createChooser(sendIntent, context.getString((R.string.app_name)));
		context.startActivity(shareIntent);
	}
}
