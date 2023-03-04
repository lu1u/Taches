package com.lpi.taches.alarmes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lpi.taches.taches.Tache;

import java.util.Calendar;

/***
 * Reception des actions des boutons de la notification Tache
 */
public class NotificationActionReceiver extends BroadcastReceiver
{

	private static final String TAG = "NotificationActionReceiver";
	public static final String EXTRA_NB_MINUTES = "ExtraNbMinutes";
	public static final String ACTION_SNOOZE = NotificationActionReceiver.class.getName() + "snooze";

	@Override
	public void onReceive(Context context, Intent intent)
	{
		try
		{
			String action = intent.getAction();
			Log.d(TAG, "AlarmeReceiver.onReceive!, action = " + action);

			if (ACTION_SNOOZE.equals(action))
			{
				Bundle bundle = intent.getExtras();
				if ( bundle !=null)
				{
					int nbMinutes = intent.getIntExtra(EXTRA_NB_MINUTES, 1);
					Tache tache = new Tache(bundle);
					creerNotificationDans5Minutes(context, tache, nbMinutes);
				}
			}

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/***
	 * Repeter la notification dans 5 minutes
	 * @param context
	 * @param tache
	 */
	private void creerNotificationDans5Minutes(@NonNull Context context, @NonNull Tache tache, int nbMinutes)
	{
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, nbMinutes);
		tache._alarme.setAlarme(cal);
		NotificationsManager notificationsManager =  NotificationsManager.getInstance(context);
		notificationsManager.supprimeNotification();
		notificationsManager.programmerAlarme(tache);
	}
}