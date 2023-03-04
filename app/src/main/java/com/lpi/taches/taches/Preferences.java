package com.lpi.taches.taches;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Gestionnaire des preferences de l'application
 */
public class Preferences
{
	public static final String PREF_SORT = "Sort";
	public static final String PREF_VUE = "Vue";
	private static final String PREF_WIDGET_SORT = "WidgetSort";
	private static final String PREF_WIDGET_VUE = "WidgetVue";
	private static final String PREF_WIDGET_TRANSPARENCE = "TransparenceWidget";
	private static final String PREF_WIDGET_FOND_NOIR = "FondWidgetNoir";

//	@Nullable private static Preferences INSTANCE = null;
//	private final SQLiteDatabase database;
//
//	private Preferences(Context context)
//	{
//		DbHelper dbHelper = new DbHelper(context);
//		database = dbHelper.getWritableDatabase();
//	}
//
//	/**
//	 * Point d'accès pour l'instance unique du singleton
//	 */
//	@NonNull public static synchronized Preferences getInstance(@NonNull Context context)
//	{
//		if (INSTANCE == null)
//		{
//			INSTANCE = new Preferences(context);
//		}
//		return INSTANCE;
//	}
//
//
////	public void putString(String name, String s)
////		{
////		ContentValues values = new ContentValues();
////		values.put(DbHelper.COLONNE_PREF_STRING_NAME, name);
////		values.put(DbHelper.COLONNE_PREF_STRING_VALEUR, s);
////
////		database.beginTransaction();
////		boolean present = trouveId(DbHelper.TABLE_PREFERENCES_STRING, DbHelper.COLONNE_PREF_STRING_NAME, name);
////		try
////			{
////			if (present)
////				database.update(DbHelper.TABLE_PREFERENCES_STRING, values, DbHelper.COLONNE_PREF_STRING_NAME + "=?", new String[]{name});
////			else
////				database.insert(DbHelper.TABLE_PREFERENCES_STRING, null, values);
////			database.setTransactionSuccessful();
////			} catch (Exception e)
////			{
////			Log.e("SAMBA", e.getMessage());
////			} finally
////			{
////			database.endTransaction();
////			}
////		}
//
//	private boolean trouveId(String tableName, String colonneID, String name)
//	{
//		Cursor c = database.query(tableName, new String[]{colonneID}, colonneID + " =?", new String[]{name}, null, null, null, null);
//		boolean result = false;
//
//		if (c != null)
//		{
//			if (c.moveToFirst()) //if the row exist then return the id
//				result = true;
//			c.close();
//		}
//		return result;
//	}
//
//	public String getString(String name, String defaut)
//	{
//		String result = defaut;
//		try
//		{
//			String where = DbHelper.COLONNE_PREF_STRING_NAME + " = \"" + name + "\"";
//			Cursor cursor = database.query(DbHelper.TABLE_PREFERENCES_STRING, null, where, null, null, null, null);
//			if (cursor != null)
//			{
//				if (cursor.moveToFirst())
//					result = cursor.getString(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_PREF_STRING_VALEUR));
//				cursor.close();
//			}
//		} catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	public void putInt(String name, int i)
//	{
//		ContentValues values = new ContentValues();
//		values.put(DbHelper.COLONNE_PREF_INT_NAME, name);
//		values.put(DbHelper.COLONNE_PREF_INT_VALEUR, i);
//
//		database.beginTransaction();
//		boolean present = trouveId(DbHelper.TABLE_PREFERENCES_INT, DbHelper.COLONNE_PREF_INT_NAME, name);
//		try
//		{
//			if (present)
//				database.update(DbHelper.TABLE_PREFERENCES_INT, values, DbHelper.COLONNE_PREF_INT_NAME + "=?", new String[]{name});
//			else
//				database.insert(DbHelper.TABLE_PREFERENCES_INT, null, values);
//			database.setTransactionSuccessful();
//		} catch (Exception e)
//		{
//			Log.e("SAMBA", e.getMessage());
//		} finally
//		{
//			database.endTransaction();
//		}
//	}
//
//	public int getInt(String name, int defaut)
//	{
//		int result = defaut;
//		try
//		{
//			String where = DbHelper.COLONNE_PREF_INT_NAME + " = \"" + name + "\"";
//			Cursor cursor = database.query(DbHelper.TABLE_PREFERENCES_INT, null, where, null, null, null, null);
//			if (cursor != null)
//			{
//				if (cursor.moveToFirst())
//					result = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.COLONNE_PREF_INT_VALEUR));
//				cursor.close();
//			}
//		} catch (SQLException e)
//		{
//			e.printStackTrace();
//		}
//
//		return result;
//	}
//
//	public void putBool(String name, boolean b)
//	{
//		putInt(name, b ? 1 : 0);
//	}
//
//	public boolean getBool(String name, boolean defaut)
//	{
//		int res = getInt(name, defaut ? 1 : 0);
//		return (res != 0);
//	}
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

	@NonNull private static final String PREFERENCES = Preferences.class.getName();

	private @Nullable static Preferences _instance;
	private @NonNull final SharedPreferences settings;
	private @NonNull final SharedPreferences.Editor editor;

	/***
	 * Obtenir l'instance (unique) de Preferences
	 * On peut donner un Context null si l'objet a deja ete initialisé
	 */
	public static synchronized Preferences getInstance(@Nullable final Context context)
	{
		if (_instance == null)
			if (context != null)
				_instance = new Preferences(context);

		return _instance;
	}

	/***
	 * Constructeur privé, utilisable uniquement dans getInstance
	 */
	private Preferences(final @NonNull Context context)
	{
		settings = context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
		editor = settings.edit();

	}

	public int getInt(@NonNull final String nom, int defaut)
	{
		return settings.getInt(nom, defaut);
	}

	public void putInt(@NonNull final String nom, int val)
	{
		editor.putInt(nom, val);
		editor.apply();
	}

	public int getWidgetVue(final int widgetId, int defaut)
	{
		return settings.getInt(PREF_WIDGET_VUE + widgetId, defaut);
	}

	public void putWidgetVue(final int widgetId, int val)
	{
		editor.putInt(PREF_WIDGET_VUE + widgetId, val);
		editor.apply();
	}


	public int getWidgetSort(final int widgetId, int defaut)
	{
		return settings.getInt(PREF_WIDGET_SORT + widgetId, defaut);
	}

	public void putWidgetSort(final int widgetId, int val)
	{
		editor.putInt(PREF_WIDGET_SORT + widgetId, val);
		editor.apply();
	}

	public int getWidgetTransparence(final int widgetId)
	{
		return settings.getInt(PREF_WIDGET_TRANSPARENCE + widgetId, 128);
	}

	public void putWidgetTransparence(final int widgetId, int val)
	{
		editor.putInt(PREF_WIDGET_TRANSPARENCE + widgetId, val);
		editor.apply();
	}

	public boolean getWidgetFondNoir(final int widgetId)
	{
		return settings.getBoolean(PREF_WIDGET_FOND_NOIR + widgetId, true);
	}

	public void putWidgetFondNoir(final int widgetId, boolean val)
	{
		editor.putBoolean(PREF_WIDGET_FOND_NOIR + widgetId, val);
		editor.apply();
	}
//	public float getFloat(@NonNull final String nom, float defaut)
//	{
//		return settings.getFloat(nom, defaut);
//	}
//
//
//	public void setFloat(@NonNull final String nom, float val)
//	{
//		editor.putFloat(nom, val);
//		editor.apply();
//	}


	public void putBool(@NonNull final String nom, boolean val)
	{
		editor.putBoolean(nom, val);
		editor.apply();
	}


	public boolean getBool(@NonNull final String nom, boolean defaut)
	{
		return settings.getBoolean(nom, defaut);
	}


	/*


		public void setChar(@NonNull final String nom, char val)
		{
			editor.putString(nom, "" + val);
			editor.apply();
		}

		public char getChar(@NonNull final String nom, char defaut)
		{
			return settings.getString(nom, defaut + " ").charAt(0);
		}

	public String getString(@NonNull final String nom, final String defaut)
	{
		return settings.getString(nom, defaut);
	}

	public void setString(@NonNull final String nom, String val)
	{
		editor.putString(nom, val);
		editor.apply();
	}	*/
}
