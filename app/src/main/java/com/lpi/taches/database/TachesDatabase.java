package com.lpi.taches.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.lpi.taches.MainActivity;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Tache;

public class TachesDatabase
{
	private static final String TAG = "TachesDatabase";
	@Nullable
	protected static TachesDatabase INSTANCE = null;
	protected SQLiteDatabase _database;
	protected DbHelper _dbHelper;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized TachesDatabase getInstance(@NonNull Context context)
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
		} catch (Exception e)
		{
			MainActivity.SignaleErreur(null,"Ajout de la Tache", e);
		}
	}


	public void modifie(@NonNull Tache Tache)
	{
		try
		{
			ContentValues valeurs = new ContentValues();
			Tache.toContentValues(valeurs, true);
			_database.update(DbHelper.TABLE_TACHES, valeurs, DbHelper.COLONNE_TACHE_ID + " = " + Tache._id, null);
		} catch (Exception e)
		{
			MainActivity.SignaleErreur(null,"modification e la tache", e);
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


	public @Nullable
	Cursor getCursor(int optionTri, int optionVue)
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
				case OptionTri.OPTION_TRI_ALARME:
					orderBy = DbHelper.COLONNE_TACHE_ALARME + " ASC";
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
		} catch (Exception e)
		{
			MainActivity.SignaleErreur(null,"Erreur dans getCursor", e);
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

	/***
	 * Calcule un nom de tache unique dans la base de donnees
	 * @param context
	 * @param idFormat
	 * @return
	 */
	public @NonNull
	String getNomUnique(@NonNull final Context context, @StringRes int idFormat)
	{
		int indice = nbTaches();
		String res;
		do
		{
			indice++;
			res = context.getString(idFormat, indice);
		}
		while (existe(res));
		return res;
	}

	/***
	 * Return TRUE si une tache existe avec ce nom
	 * @param nom
	 * @return
	 */
	private boolean existe(@NonNull final String nom)
	{
		boolean existe = false;
		try
		{
			Cursor cursor = _database.rawQuery("SELECT COUNT(*) FROM " + DbHelper.TABLE_TACHES + " WHERE " + DbHelper.COLONNE_TACHE_NOM + "=?", new String[]{nom});
			if (null != cursor)
			{
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					existe = cursor.getInt(0) > 0;
				}
				cursor.close();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return existe;
	}

	/***
	 * Charge une tache a partir de son Id dans la base
	 * @param id
	 * @return
	 */
	public @Nullable  Tache getTache(int id)
	{
		Tache result = null;
		String selection = DbHelper.COLONNE_TACHE_ID + " =?";
		String[] selectionArg = new String[]{"" + id};
		try
		{
			Cursor cursor = _database.query(DbHelper.TABLE_TACHES, DbHelper.TABLE_TACHES_COLONNES, selection, selectionArg, null, null, null);
			if (null != cursor)
			{
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					result  = new Tache(cursor);
					Log.d(TAG, "Tache trouvée " + result);
				}
				cursor.close();
			}
		} catch (Exception e)
		{
			MainActivity.SignaleErreur(null,"Erreur dans getCursor", e);
		}
		return result;
	}

	/***
	 * Retourne la tache dont l'alarme est la plus proche dans l'avenir
	 * @param dateMaintenant
	 * @return
	 */
	public Tache getProchaineTache(String dateMaintenant)
	{
		Tache result = null;
		try
		{
			final String query = "SELECT * FROM " + DbHelper.TABLE_TACHES + " WHERE " + DbHelper.COLONNE_TACHE_ALARME + " >= ?"
					+ " ORDER BY " + DbHelper.COLONNE_TACHE_ALARME + " ASC, " + DbHelper.COLONNE_TACHE_PRIORITE + " DESC"
					+ " LIMIT 1";

			Log.d(TAG, "getProchaineTache: " + dateMaintenant);
			Log.d(TAG, query);

			Cursor cursor = _database.rawQuery(query, new String[]{dateMaintenant});
			if (null != cursor)
			{
				if (cursor.getCount() > 0)
				{
					cursor.moveToFirst();
					result  = new Tache(cursor);
					Log.d(TAG, "Tache trouvée " + result);
				}
				cursor.close();
			}
		} catch (Exception e)
		{
			MainActivity.SignaleErreur(null,"Erreur dans getCursor", e);
		}

		return result;
	}


}