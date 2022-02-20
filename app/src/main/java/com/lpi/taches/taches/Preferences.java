package com.lpi.taches.taches;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.taches.database.DbHelper;

/**
 * Gestionnaire des preferences de l'application
 */
public class Preferences
	{
	public static final String PREF_SORT = "Sort";
	public static final String PREF_VUE = "Vue";
	public static final String PREF_WIDGET_SORT = "WidgetSort";
	public static final String PREF_WIDGET_VUE = "WidgetVue";

	@Nullable private static Preferences INSTANCE = null;
	private final SQLiteDatabase database;

	private Preferences(Context context)
		{
		DbHelper dbHelper = new DbHelper(context);
		database = dbHelper.getWritableDatabase();
		}

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull public static synchronized Preferences getInstance(@NonNull Context context)
		{
		if (INSTANCE == null)
			{
			INSTANCE = new Preferences(context);
			}
		return INSTANCE;
		}


//	public void putString(String name, String s)
//		{
//		ContentValues values = new ContentValues();
//		values.put(DbHelper.COLONNE_PREF_STRING_NAME, name);
//		values.put(DbHelper.COLONNE_PREF_STRING_VALEUR, s);
//
//		database.beginTransaction();
//		boolean present = trouveId(DbHelper.TABLE_PREFERENCES_STRING, DbHelper.COLONNE_PREF_STRING_NAME, name);
//		try
//			{
//			if (present)
//				database.update(DbHelper.TABLE_PREFERENCES_STRING, values, DbHelper.COLONNE_PREF_STRING_NAME + "=?", new String[]{name});
//			else
//				database.insert(DbHelper.TABLE_PREFERENCES_STRING, null, values);
//			database.setTransactionSuccessful();
//			} catch (Exception e)
//			{
//			Log.e("SAMBA", e.getMessage());
//			} finally
//			{
//			database.endTransaction();
//			}
//		}

	private boolean trouveId(String tableName, String colonneID, String name)
		{
		Cursor c = database.query(tableName, new String[]{colonneID}, colonneID + " =?", new String[]{name}, null, null, null, null);
		boolean result = false;

		if (c != null)
			{
			if (c.moveToFirst()) //if the row exist then return the id
				result = true;
			c.close();
			}
		return result;
		}

	public String getString(String name, String defaut)
		{
		String result = defaut;
		try
			{
			String where = DbHelper.COLONNE_PREF_STRING_NAME + " = \"" + name + "\"";
			Cursor cursor = database.query(DbHelper.TABLE_PREFERENCES_STRING, null, where, null, null, null, null);
			if (cursor != null)
				{
				if (cursor.moveToFirst())
					result = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_PREF_STRING_VALEUR));
				cursor.close();
				}
			} catch (SQLException e)
			{
			e.printStackTrace();
			}

		return result;
		}

	public void putInt(String name, int i)
		{
		ContentValues values = new ContentValues();
		values.put(DbHelper.COLONNE_PREF_INT_NAME, name);
		values.put(DbHelper.COLONNE_PREF_INT_VALEUR, i);

		database.beginTransaction();
		boolean present = trouveId(DbHelper.TABLE_PREFERENCES_INT, DbHelper.COLONNE_PREF_INT_NAME, name);
		try
			{
			if (present)
				database.update(DbHelper.TABLE_PREFERENCES_INT, values, DbHelper.COLONNE_PREF_INT_NAME + "=?", new String[]{name});
			else
				database.insert(DbHelper.TABLE_PREFERENCES_INT, null, values);
			database.setTransactionSuccessful();
			} catch (Exception e)
			{
			Log.e("SAMBA", e.getMessage());
			} finally
			{
			database.endTransaction();
			}
		}

	public int getInt(String name, int defaut)
		{
		int result = defaut;
		try
			{
			String where = DbHelper.COLONNE_PREF_INT_NAME + " = \"" + name + "\"";
			Cursor cursor = database.query(DbHelper.TABLE_PREFERENCES_INT, null, where, null, null, null, null);
			if (cursor != null)
				{
				if (cursor.moveToFirst())
					result = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_PREF_INT_VALEUR));
				cursor.close();
				}
			} catch (SQLException e)
			{
			e.printStackTrace();
			}

		return result;
		}

//	public void putBool(String name, boolean b)
//		{
//		putInt(name, b ? 1 : 0);
//		}
//
//	public boolean getBool(String name, boolean defaut)
//		{
//		int res = getInt(name, defaut ? 1 : 0);
//		return (res != 0);
//		}
//
//	public float getFloat(String name, float defaut)
//		{
//		try
//			{
//			String s = getString(name, Float.toString(defaut));
//			return Float.parseFloat(s);
//			} catch (Exception e)
//			{
//			return defaut;
//			}
//		}


	}
