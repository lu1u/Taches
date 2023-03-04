package com.lpi.taches.widget;

import android.content.Context;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.lpi.taches.R;
import com.lpi.taches.taches.Preferences;

public class TachesWidgetBlanc extends TachesWidget
{
	/***
	 * Retourne l'ID du layout
	 * @return
	 */
	@Override  protected  int getLayoutId()
	{
		return R.layout.taches_widget_blanc;
	}
	@Override protected void updateWidget(@NonNull final Context context, @NonNull final RemoteViews rv, int appWidgetId)
	{
		Preferences preference = Preferences.getInstance(context);
		try
		{
			int transparence = preference.getWidgetTransparence(appWidgetId);
			boolean bNoir = preference.getWidgetFondNoir(appWidgetId);
			rv.setInt(R.id.idLayout, "setBackgroundColor", TachesWidgetSettingsActivity.getCouleurTransparence(transparence, bNoir));
		}
		catch (Exception e)
		{
			Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
