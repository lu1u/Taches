/**
 * Enregistre les traces du programme dans une base de donnees, consultable avec ReportActivity
 * Les traces ne sont enregistrees que si le fichier build.gradle contient la definition suivante:
 *   buildConfigField "boolean", "REPORT", "true"
 *
 *   exemple:
 *       defaultConfig {
 *         applicationId "com.lpi.compagnonderoute"
 *         minSdkVersion 27
 *         targetSdkVersion 28
 *         versionCode 1
 *         versionName "1.0"
 *         buildConfigField "boolean", "REPORT", "true"
 *         testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
 *     }
 */
package com.lpi.taches.report;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.taches.BuildConfig;

import java.util.Objects;


/**
 * @author lucien
 */
@SuppressWarnings("nls")
public class Report
{
	@NonNull final private static String TAG = "Report";
	// Niveaux de trace
	public static final int DEBUG = 0;
	public static final int WARNING = 1;
	public static final int ERROR = 2;
	public static final int HISTORIQUE = 3;

	public static final boolean GENERER_TRACES = BuildConfig.REPORT;

	private static final int MAX_BACKTRACE = 10;
	@Nullable
	private static Report INSTANCE = null;
	final ReportDatabase _reportDatabase;

	private Report(Context context)
	{

		if (GENERER_TRACES)
			_reportDatabase = ReportDatabase.getInstance(context);
		else
			_reportDatabase = null;
	}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 * @param context: le context habituel d'Android, peut être null si l'objet a deja ete utilise
	 */
	@NonNull
	public static synchronized Report getInstance(@Nullable Context context)
	{
		if (INSTANCE == null)
		{
			INSTANCE = new Report(context);
		}
		return INSTANCE;
	}

	public void log(int niv, @NonNull Exception e)
	{
		if (_reportDatabase != null)
		{
			log(niv, Objects.requireNonNull(e.getLocalizedMessage()));
			for (int i = 0; i < e.getStackTrace().length && i < MAX_BACKTRACE; i++)
				log(niv, e.getStackTrace()[i].getClassName() + '/' + e.getStackTrace()[i].getMethodName() + ':' + e.getStackTrace()[i].getLineNumber());
		}
	}

	public void log(int niv, @NonNull String message)
	{
		if (_reportDatabase != null)
		{
			Log.d(TAG, message);
			_reportDatabase.Ajoute(ReportDatabaseHelper.CalendarToSQLiteDate(null), niv, message);
		}
	}

	/***
	 * Construire un texte contenant toutes les lignes du rapport
	 * @param niveau
	 * @return
	 */
	public @NonNull  String getText(@NonNull Context context, int niveau)
	{
		ReportDatabase database = ReportDatabase.getInstance(context);
		StringBuilder stringBuilder = new StringBuilder();
		try
		{
			Cursor cursor = database.getCursor(niveau);
			if ( cursor!=null)
			{
				cursor.moveToFirst();
				while (cursor.moveToNext())
				{
					int date = cursor.getInt(cursor.getColumnIndexOrThrow(ReportDatabaseHelper.COLONNE_TRACES_DATE));
					String ligne = cursor.getString(cursor.getColumnIndexOrThrow(ReportDatabaseHelper.COLONNE_TRACES_LIGNE));
					int n = cursor.getInt(cursor.getColumnIndexOrThrow(ReportDatabaseHelper.COLONNE_TRACES_NIVEAU));
					stringBuilder.append(ReportAdapter.formatDate(context, date)).append(":").append(ligne).append("\n");
				}
				cursor.close();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return stringBuilder.toString();
	}
}
