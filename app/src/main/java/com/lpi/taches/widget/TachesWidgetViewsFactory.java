package com.lpi.taches.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.lpi.taches.R;
import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Preferences;
import com.lpi.taches.taches.Tache;

public class TachesWidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory
	{
	private final TachesDatabase _bdd;
	protected final Context mContext;
	protected Cursor _cursor;
	protected final int mAppWidgetId;
	private final Preferences _preferences;

	public TachesWidgetViewsFactory(Context context, Intent intent)
		{
		_bdd = TachesDatabase.getInstance(context);
		mContext = context;
		_preferences = Preferences.getInstance(context);
		mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
		}
	@Override public void onCreate()
		{

		}

	@Override public void onDataSetChanged()
		{
		if (_cursor != null)
			_cursor.close();

		_cursor = _bdd.getCursor(_preferences.getInt(Preferences.PREF_WIDGET_SORT, OptionTri.OPTION_TRI_NOM), _preferences.getInt(Preferences.PREF_WIDGET_VUE, OptionVue.OPTION_VUE_TOUTES));
		}

	@Override public void onDestroy()
		{
		if (_cursor != null)
			{
			_cursor.close();
			}
		}

	@Override public int getCount()
		{
		return _cursor.getCount();
		}

	@Override public RemoteViews getViewAt(int position)
		{
		Tache tache;
		if (_cursor.moveToPosition(position))
			tache = new Tache(_cursor);
		else
			tache = new Tache();

		RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.taches_widget_element_liste);
		int couleur = tache.getCouleurPriorite(mContext);
		rv.setTextViewText(R.id.tvWidgetNom, tache._nom);
		rv.setTextColor(R.id.tvWidgetNom, couleur);
		rv.setTextViewText(R.id.tvWidgetPourcent, tache._achevement + "%");
		rv.setTextColor(R.id.tvWidgetPourcent, couleur);

		return rv;
		}

	@Override public RemoteViews getLoadingView()
		{
		return null;
		}

	@Override public int getViewTypeCount()
		{
		return 1;
		}

	@Override public long getItemId(int i)
		{
		if ( _cursor !=null)
			{
			if  (_cursor.moveToPosition(i))
				{
				Tache t = new Tache(_cursor);
				return t._id;
				}
			}
		return 0;
		}

	@Override public boolean hasStableIds()
		{
		return true;
		}
	}
