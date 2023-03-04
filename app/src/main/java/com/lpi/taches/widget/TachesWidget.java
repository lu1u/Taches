package com.lpi.taches.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.lpi.taches.R;
import com.lpi.taches.taches.OptionTri;
import com.lpi.taches.taches.OptionVue;
import com.lpi.taches.taches.Preferences;

/**
 * Implementation of App Widget functionality.
 */
public class TachesWidget extends AppWidgetProvider
{
	private static final String ACTION_VUE = "com.lpi.taches.widget.VUE";
	private static final String ACTION_SORT = "com.lpi.taches.widget.SORT";
	private static final String ACTION_APPLICATION = "com.lpi.taches.widget.APPLICATION";
	public static final String EXTRA_WIDGETID = "com.lpi.taches.widget.id";

	TachesWidgetDataProviderObserver sDataObserver;
	private static Handler sWorkerQueue;


	/***
	 * Forcer la mise a jour des widgets
	 * @param context
	 */
	public static void updateAllWidgets(final Context context)
	{
		// Forcer la mise a jour du DataProvider
		final Uri uri = ContentUris.withAppendedId(TachesWidgetContentProvider.CONTENT_URI, 0);
		ContentValues values = new ContentValues();
		final ContentResolver r = context.getContentResolver();
		r.update(uri, values, null, null);

		// Avertir les widgets
		final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, TachesWidget.class);
		mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lvTaches);

		cn = new ComponentName(context, TachesWidgetBlanc.class);
		mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lvTaches);
	}

	/***
	 * Constructeur par defaut
	 */
	public TachesWidget()
	{
		// Start the worker thread
		HandlerThread sWorkerThread = new HandlerThread("TachesWidgetContentProvider-worker"); //$NON-NLS-1$
		sWorkerThread.start();
		sWorkerQueue = new Handler(sWorkerThread.getLooper());
	}

	/***
	 * Mise a jour de toutes les widgets
	 * @param context
	 * @param appWidgetManager
	 * @param appWidgetIds
	 */
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
	{
		try
		{
			//Toast.makeText(context, "onUpdate", Toast.LENGTH_LONG).show();
			for (int appWidgetId : appWidgetIds)
			{
				final RemoteViews rv = new RemoteViews(context.getPackageName(), getLayoutId());
				updateWidget(context, rv, appWidgetId);
				setDataProviderService(rv, context, appWidgetId);
				setOnClickListeners(rv, context, appWidgetId);

				if (sDataObserver != null)
					sDataObserver.dispatchChange(true, TachesWidgetContentProvider.CONTENT_URI);
				appWidgetManager.updateAppWidget(appWidgetId, rv);
			}
			super.onUpdate(context, appWidgetManager, appWidgetIds);
		} catch (Exception e)
		{
			e.printStackTrace();
			Erreur(context, e);
		}
	}

	/***
	 * Permet aux classes derivees de faire qq chose lors de l'update
	 * @param rv
	 * @param appWidgetId
	 */
	protected void updateWidget(@NonNull final Context context, @NonNull final RemoteViews rv, int appWidgetId)
	{

	}

	/***
	 * Retourne l'ID du layout
	 * @return
	 */
	protected int getLayoutId()
	{
		return R.layout.taches_widget;
	}

	/**
	 * Installe les recepteurs de clic sur les boutons
	 *
	 * @param rv
	 * @param context
	 * @param appWidgetId
	 */
	protected void setOnClickListeners(RemoteViews rv, Context context, int appWidgetId)
	{
		// Bouton Vue
		{
			final Intent refreshIntent = new Intent(context, this.getClass());
			refreshIntent.setAction(TachesWidget.ACTION_VUE);
			refreshIntent.putExtra(EXTRA_WIDGETID, appWidgetId);
			final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
			rv.setOnClickPendingIntent(R.id.ibWidgetVue, refreshPendingIntent);
		}

		// Bouton Sort
		{
			final Intent refreshIntent = new Intent(context, this.getClass());
			refreshIntent.setAction(TachesWidget.ACTION_SORT);
			refreshIntent.putExtra(EXTRA_WIDGETID, appWidgetId);
			final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
			rv.setOnClickPendingIntent(R.id.ibWidgetSort, refreshPendingIntent);
		}

		// Bouton Application
		{
			final Intent refreshIntent = new Intent(context, this.getClass());
			refreshIntent.setAction(TachesWidget.ACTION_APPLICATION);
			refreshIntent.putExtra(EXTRA_WIDGETID, appWidgetId);
			final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
			rv.setOnClickPendingIntent(R.id.ibWidgetApplication, refreshPendingIntent);
		}
	}

	/***
	 * Specify the service to provide data for the collection widget. Note that we need to
	 * embed the appWidgetId via the data otherwise it will be ignored.
	 * Set the empty view to be displayed if the collection is empty. It must be a sibling
	 * view of the collection view.
	 * @param rv
	 * @param context
	 * @param appWidgetId
	 */
	private void setDataProviderService(RemoteViews rv, Context context, int appWidgetId)
	{
		final Intent intent = new Intent(context, TachesWidgetContentService.class);
		intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
		intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
		rv.setRemoteAdapter(R.id.lvTaches, intent);
		rv.setEmptyView(R.id.lvTaches, R.id.tvEmpty);
	}

	@Override
	public void onEnabled(Context context)
	{
		try
		{
			// Log.d(TAG, "OnEnabled") ;
			final ContentResolver r = context.getContentResolver();
			if (sDataObserver == null)
			{
				final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
				final ComponentName cn = new ComponentName(context, TachesWidget.class);
				sDataObserver = new TachesWidgetDataProviderObserver(mgr, cn, sWorkerQueue);
				r.registerContentObserver(TachesWidgetContentProvider.CONTENT_URI, true, sDataObserver);
			}
		} catch (Exception e)
		{
			Erreur(context, e);
			e.printStackTrace();
		}
	}


	/***
	 * Rapporte une erreur detectee par le programme
	 *
	 * @param context
	 * @param e
	 */
	@SuppressWarnings("nls")
	public static void Erreur(Context context, Exception e)
	{
		String message;
		if (e == null)
		{
			message = "null exception ?!?"; //$NON-NLS-1$
		}
		else
		{
			StringBuilder stack = new StringBuilder();

			StackTraceElement[] st = e.getStackTrace();
			for (StackTraceElement stackTraceElement : st)
			{
				String line = stackTraceElement.getMethodName() + ":" + stackTraceElement.getLineNumber();
				stack.append(line);
				stack.append("\n");
			}
			message = "Erreur " + e.getLocalizedMessage() + "\n" + stack;
		}
		Toast t = Toast.makeText(context, message, Toast.LENGTH_LONG);
		t.show();
	}

	@Override
	public void onReceive(Context context, Intent intent)
	{
		final String action = intent.getAction();
		final int widgetId = intent.getIntExtra(EXTRA_WIDGETID, -1);
		try
		{
			switch (action)
			{
				case ACTION_VUE:
					handleRefreshVue(context, intent, widgetId);
					break;
				case ACTION_SORT:
					handleRefreshSort(context, intent, widgetId);
					break;
				case ACTION_APPLICATION:
					handleApplication(context, intent, widgetId);
					break;

			}
			super.onReceive(context, intent);
		} catch (Exception e)
		{
			Erreur(context, e);
			e.printStackTrace();
		}
	}

	private void handleApplication(final Context context, final Intent i, int widgetId)
	{
		PackageManager manager = context.getPackageManager();
		Intent intent = manager.getLaunchIntentForPackage(context.getPackageName());
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		context.startActivity(intent);
	}


	private void handleRefreshSort(final Context context, final Intent intent, int widgetId)
	{
		Preferences preferences = Preferences.getInstance(context);
		int optionSort = preferences.getWidgetSort(widgetId, OptionTri.OPTION_TRI_NOM);
		@StringRes int message;
		switch (optionSort)
		{
			case OptionTri.OPTION_TRI_NOM:
				optionSort = OptionTri.OPTION_TRI_PRIORITE;
				message = R.string.widget_sort_priorite;
				break;
			case OptionTri.OPTION_TRI_PRIORITE:
				optionSort = OptionTri.OPTION_TRI_CREATION;
				message = R.string.widget_sort_creation;
				break;
			case OptionTri.OPTION_TRI_CREATION:
				optionSort = OptionTri.OPTION_TRI_ACHEVEMENT;
				message = R.string.widget_sort_achevement;
				break;
			case OptionTri.OPTION_TRI_ACHEVEMENT:
				optionSort = OptionTri.OPTION_TRI_ALARME;
				message = R.string.widget_sort_alarme;
				break;
			default:
				optionSort = OptionTri.OPTION_TRI_NOM;
				message = R.string.widget_sort_nom;
		}

		preferences.putWidgetSort(widgetId, optionSort);

		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		updateAllWidgets(context);
	}

	private void handleRefreshVue(final Context context, final Intent intent, int widgetId)
	{
		Preferences preferences = Preferences.getInstance(context);
		int option = preferences.getWidgetVue(widgetId, OptionVue.OPTION_VUE_TOUTES);
		@StringRes int message;

		switch (option)
		{
			case OptionVue.OPTION_VUE_TOUTES:
				option = OptionVue.OPTION_VUE_INCOMPLETES;
				message = R.string.widget_vue_incompletes;
				break;
			case OptionVue.OPTION_VUE_INCOMPLETES:
				option = OptionVue.OPTION_VUE_COMPLETES;
				message = R.string.widget_vue_completes;
				break;
			default:
				option = OptionVue.OPTION_VUE_TOUTES;
				message = R.string.widget_vue_toutes;
		}

		preferences.putWidgetVue(widgetId, option);
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

		updateAllWidgets(context);
	}

	/***
	 * Forcer la mise a jour des Widget
	 * @param context
	 */
	private void handleRefresh(final Context context)
	{
		final ContentResolver r = context.getContentResolver();
		// Forcer la mise a jour du DataProvider
		final Uri uri = ContentUris.withAppendedId(TachesWidgetContentProvider.CONTENT_URI, 0);
		ContentValues values = new ContentValues();
		r.update(uri, values, null, null);

		// Avertir les widgets
		final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
		ComponentName cn = new ComponentName(context, TachesWidget.class);
		mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.lvTaches);
	}
}