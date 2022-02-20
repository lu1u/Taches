package com.lpi.taches.widget;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Preferences;

public class TachesWidgetContentProvider extends ContentProvider
	{
	@SuppressWarnings("nls")
	public static final Uri CONTENT_URI = Uri.parse("content://com.lpi.taches.widget.TachesWidgetContentProvider");
	protected TachesDatabase _database;
	@Override
	public boolean onCreate()
		{
		if (_database == null)
			_database = TachesDatabase.getInstance(getContext());
		return true;
		}

	@Override
	@SuppressWarnings("nls")
	public synchronized Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder)
		{
		Preferences preferences = Preferences.getInstance(getContext());
		return _database.getCursor(preferences.getInt(Preferences.PREF_WIDGET_SORT, OptionTri.OPTION_TRI_NOM), preferences.getInt(Preferences.PREF_WIDGET_VUE, OptionVue.OPTION_VUE_TOUTES));
		}

	@Override
	@SuppressWarnings("nls")
	public String getType(Uri uri)
		{
		return "vnd.android.cursor.dir/vnd.taches.componentname";
		}

	@Override
	public Uri insert(Uri uri, ContentValues values)
		{
		return null;
		}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
		{
		return 0;
		}

	@Override
	public synchronized int update(Uri uri, ContentValues values, String selection, String[] selectionArgs)
		{
		getContext().getContentResolver().notifyChange(uri, null);
		return 1;
		}
	}