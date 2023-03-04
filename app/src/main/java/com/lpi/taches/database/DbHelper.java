package com.lpi.taches.database;

/*
 * Utilitaire de gestion de la base de donnees
 */

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.NonNull;

public class DbHelper extends SQLiteOpenHelper
{
	public static final int DATABASE_VERSION = 4;
	public static final @NonNull String DATABASE_NAME = "taches.db";
	////////////////////////////////////////////////////////////////////////////////////////////////////
	// Table des randonnees
	public static final @NonNull String TABLE_TACHES = "TACHES";
	public static final @NonNull String COLONNE_TACHE_ID = "_id";
	public static final @NonNull String COLONNE_TACHE_NOM = "NOM";
	public static final @NonNull String COLONNE_TACHE_CREATION = "CREATION";
	public static final @NonNull String COLONNE_TACHE_PRIORITE = "PRIORITE";
	public static final @NonNull String COLONNE_TACHE_ACHEVEMENT = "ACHEVEMENT";
	public static final @NonNull String COLONNE_TACHE_NOTE = "NOTE";
	public static final @NonNull String COLONNE_TACHE_ALARME = "ALARME";
	public static final @NonNull
	String[] TABLE_TACHES_COLONNES = {COLONNE_TACHE_ID, COLONNE_TACHE_NOM, COLONNE_TACHE_CREATION, COLONNE_TACHE_PRIORITE, COLONNE_TACHE_ACHEVEMENT, COLONNE_TACHE_NOTE, COLONNE_TACHE_ALARME};

	// Table preferences bool et int
	public static final @NonNull String TABLE_PREFERENCES_INT = "PREFERENCES_INT";
	public static final @NonNull String COLONNE_PREF_INT_NAME = "NAME";
	public static final @NonNull String COLONNE_PREF_INT_VALEUR = "VALEUR";
	// Table preferences string
	public static final @NonNull String TABLE_PREFERENCES_STRING = "PREFERENCES_STRING";
	public static final @NonNull String COLONNE_PREF_STRING_NAME = "NAME";
	public static final @NonNull String COLONNE_PREF_STRING_VALEUR = "VALEUR";

	public static final int INVALID_ID = -1;

	private static final String DATABASE_TACHES_CREATE = "create table "
			+ TABLE_TACHES + "("
			+ COLONNE_TACHE_ID + " integer primary key autoincrement, "
			+ COLONNE_TACHE_NOM + " text not null,"
			+ COLONNE_TACHE_CREATION + " integer, "
			+ COLONNE_TACHE_PRIORITE + " integer, "
			+ COLONNE_TACHE_ACHEVEMENT + " integer, "
			+ COLONNE_TACHE_NOTE + " text not null, "
			+ COLONNE_TACHE_ALARME + " text"
			+ ");";


	private static final String DATABASE_PREF_INT_CREATE = "create table "
			+ TABLE_PREFERENCES_INT + "("
			+ COLONNE_PREF_INT_NAME + " text primary key not null, "
			+ COLONNE_PREF_INT_VALEUR + " integer "
			+ ");";
	private static final String DATABASE_PREF_STRING_CREATE = "create table "
			+ TABLE_PREFERENCES_STRING + "("
			+ COLONNE_PREF_INT_NAME + " text primary key not null, "
			+ COLONNE_PREF_INT_VALEUR + " text "
			+ ");";

	public DbHelper(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(@NonNull SQLiteDatabase database)
	{
		try
		{
			database.execSQL(DATABASE_TACHES_CREATE);
			database.execSQL(DATABASE_PREF_INT_CREATE);
			database.execSQL(DATABASE_PREF_STRING_CREATE);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion)
	{
		try
		{
			Log.w(DbHelper.class.getName(),
					"Upgrading database from version " + oldVersion + " to "
							+ newVersion + ", which will destroy all old data");
			db.execSQL("ALTER TABLE " + TABLE_TACHES + " ADD COLUMN " + COLONNE_TACHE_ALARME + " TEXT DEFAULT NULL");
			//db.execSQL("DROP TABLE IF EXISTS " + TABLE_TACHES);
			//db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES_INT);
			//db.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES_STRING);

			onCreate(db);
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}


}
