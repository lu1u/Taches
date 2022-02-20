package com.lpi.taches.widget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.database.ContentObserver;
import android.os.Handler;
import android.util.Log;

import com.lpi.taches.R;

public class TachesWidgetDataProviderObserver extends ContentObserver
	{
	private final AppWidgetManager mAppWidgetManager;
	private final ComponentName mComponentName;
	public final static String TAG = "FrequentDataProviderObserver" ; //$NON-NLS-1$
	public TachesWidgetDataProviderObserver(AppWidgetManager mgr, ComponentName cn, Handler h)
		{
		super(h);
		mAppWidgetManager = mgr;
		mComponentName = cn;
		}

	@Override
	public void onChange(boolean selfChange)
		{
		// The data has changed, so notify the widget that the collection view needs to be updated.
		// In response, the factory's onDataSetChanged() will be called which will requery the
		// cursor for the new data.
		Log.d(TAG, "*****Observer onchange"); //$NON-NLS-1$
		mAppWidgetManager.notifyAppWidgetViewDataChanged(mAppWidgetManager.getAppWidgetIds(mComponentName), R.id.lvTaches);
		}
	}
