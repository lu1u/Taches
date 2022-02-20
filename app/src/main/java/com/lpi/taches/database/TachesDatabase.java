package com.lpi.taches.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.taches.MainActivity;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Tache;

public class TachesDatabase
	{
	@Nullable protected static TachesDatabase INSTANCE = null;
	protected SQLiteDatabase _database;
	protected DbHelper _dbHelper;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull public static synchronized TachesDatabase getInstance(@NonNull Context context)
		{
		if (INSTANCE == null)
			INSTANCE = new TachesDatabase(context);
		return INSTANCE;
		}

	private TachesDatabase(@NonNull Context context)
		{
		try
			{
			_dbHelper = new DbHelper(context);
			_database = _dbHelper.getWritableDatabase();
			} catch (Exception e)
			{
			e.printStackTrace();
			}
		}


		@Override
	protected void finalize()
		{
		_dbHelper.close();
		}

	////////////////////////////////////////////////////////////////////////////////////////////////

	/***
	 * ajoute une randonnee
	 *
	 * @param Tache
	 */
	////////////////////////////////////////////////////////////////////////////////////////////////
	public void ajoute(@NonNull Tache Tache)
		{
		try
			{
			ContentValues initialValues = new ContentValues();
			Tache.toContentValues(initialValues, false);
			_database.insert(DbHelper.TABLE_TACHES, null, initialValues);
			}
		catch (Exception e)
			{
			MainActivity.SignaleErreur("Ajout de la Tache", e);
			}
		}


	public void modifie(@NonNull Tache Tache)
		{
		try
			{
			ContentValues valeurs = new ContentValues();
			Tache.toContentValues(valeurs, true);
			_database.update(DbHelper.TABLE_TACHES, valeurs, DbHelper.COLONNE_TACHE_ID + " = " + Tache._id, null);
			}
		catch (Exception e)
			{
			MainActivity.SignaleErreur("modification e la tache", e);
			}
		}

	/***
	 * Supprime une tache
	 * @return true si operation ok, false si erreur
	 */
	public boolean supprime(int Id)
		{
		try
			{
			// Operation effectuee dans une transaction sqlite pour garantir la coherence de la base
			_database.beginTransaction();
			// supprimer la tache
			_database.delete(DbHelper.TABLE_TACHES, DbHelper.COLONNE_TACHE_ID + " = " + Id, null);
			_database.setTransactionSuccessful();
			} catch (Exception e)
			{
			_database.endTransaction();
			return false;
			}

		_database.endTransaction();
		return true;
		}


	public @Nullable Cursor getCursor(int optionTri, int optionVue)
		{
		try
			{
			String orderBy;
			switch (optionTri)
				{
				case OptionTri.OPTION_TRI_NOM:
					orderBy = DbHelper.COLONNE_TACHE_NOM + " COLLATE NOCASE ASC";
					break;

				case OptionTri.OPTION_TRI_PRIORITE:
					orderBy = DbHelper.COLONNE_TACHE_PRIORITE + " DESC";
					break;

				case OptionTri.OPTION_TRI_CREATION:
					orderBy = DbHelper.COLONNE_TACHE_CREATION;
					break;
				case OptionTri.OPTION_TRI_ACHEVEMENT:
					orderBy = DbHelper.COLONNE_TACHE_ACHEVEMENT;
					break;
				default:
					orderBy = null;
					break;
				}

			String selection;
			String[] selectionArg;
			switch (optionVue)
				{
				case OptionVue.OPTION_VUE_COMPLETES:
					selection = DbHelper.COLONNE_TACHE_ACHEVEMENT + " =?";
					selectionArg = new String[]{"" + Tache.PROGRESSION_MAXIMUM};
					break;

				case OptionVue.OPTION_VUE_INCOMPLETES:
					selection = DbHelper.COLONNE_TACHE_ACHEVEMENT + " <?";
					selectionArg = new String[]{"" + Tache.PROGRESSION_MAXIMUM};
					break;
				default:
					selection = null;
					selectionArg = null;
				}

			return _database.query(DbHelper.TABLE_TACHES, DbHelper.TABLE_TACHES_COLONNES, selection, selectionArg, null, null, orderBy);
			}
		catch (Exception e)
			{
			MainActivity.SignaleErreur("Erreur dans getCursor", e);
			}

		return null;
		}

	/***
	 * Retourne le nombre de randonnees dans la base
	 * @return
	 */
	public int nbTaches()
		{
		Cursor cursor = _database.rawQuery("SELECT COUNT (*) FROM " + DbHelper.TABLE_TACHES, null);
		int count = 0;
		if (null != cursor)
			{
			if (cursor.getCount() > 0)
				{
				cursor.moveToFirst();
				count = cursor.getInt(0);
				}
			cursor.close();
			}
		return count;
		}

	}