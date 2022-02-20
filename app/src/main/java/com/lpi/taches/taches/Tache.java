package com.lpi.taches.taches;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.taches.R;
import com.lpi.taches.database.DbHelper;

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

	public static String[] _nomsPriorites ;
	private static TypedArray _drawablePriorites;
	private static int[] _couleursPriorites;

	public int _id;
	public String _nom;
	public int _priorite;
	public int _achevement ; // PROGRESSION_MINIMUM..PROGRESSION_MAXIMUM
	public long _dateCreation = 0;
	public String _note;
	public Tache()
		{
		_id = INVALID_ID;
		_nom = "";
		_priorite = PRIORITE_NORMALE;
		_achevement = 0;
		_dateCreation = Calendar.getInstance().getTimeInMillis();
		_note = "";
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
	public Tache(@Nullable Cursor cursor) throws IllegalArgumentException
		{
		if (cursor != null)
			{
			_id = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_ID));
			_nom = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_NOM));
			_dateCreation = cursor.getLong(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_CREATION));
			_priorite = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_PRIORITE));
			_achevement = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_ACHEVEMENT));
			_note = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_TACHE_NOTE));
			}
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
		}

	public String getTextPriorite(@NonNull final Context context)
		{
		if  (_nomsPriorites == null)
			_nomsPriorites = context.getResources().getStringArray(R.array.priorites);

		if ( _priorite < 0 || _priorite >= _nomsPriorites.length)
			_priorite = PRIORITE_NORMALE;

		return _nomsPriorites[_priorite];
		}

	public Drawable getDrawablePriorite(Context context)
		{
		if  (_drawablePriorites == null)
			_drawablePriorites = context.getResources().obtainTypedArray(R.array.priorites_drawables);

		if ( _priorite < 0 || _priorite >= _drawablePriorites.length())
			_priorite = PRIORITE_NORMALE;

		return _drawablePriorites.getDrawable(_priorite);
		}

	public int getCouleurPriorite(Context context)
		{
		if  (_couleursPriorites == null)
			_couleursPriorites = context.getResources().getIntArray(R.array.priorites_couleurs);

		if ( _priorite < 0 || _priorite >= _couleursPriorites.length)
			_priorite = PRIORITE_NORMALE;

		return _couleursPriorites[_priorite];
		}
	}
