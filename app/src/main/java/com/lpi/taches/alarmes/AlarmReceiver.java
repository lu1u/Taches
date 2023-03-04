
package com.lpi.taches.alarmes;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.report.Report;
import com.lpi.taches.taches.Tache;

/***
 * Recepteur des alarmes Carillon, alarme Android programmée dans NotificationManager
 */
public class AlarmReceiver extends BroadcastReceiver
{
	private static final long DELAI_PROCHAINE_ALARME = 60L * 1000L;        // 1 minute

	/***
	 * Reception d'une alarme Android
	 * @param context
	 * @param intent
	 */
	@Override public void onReceive(@NonNull Context context, @NonNull Intent intent)
	{
		Report report = Report.getInstance(context);
		try
		{
			String action = intent.getAction();
			report.log(Report.DEBUG, "AlarmeReceiver.onReceive!, action = " + action);

			if (NotificationsManager.ACTION_ALARME.equals(action))
			{
				int id = intent.getIntExtra(NotificationsManager.EXTRA_ID_TACHE, -1);
				Tache tache = TachesDatabase.getInstance(context).getTache(id);

				if (tache == null)
				{
					report.log(Report.ERROR, "Impossible de charger la tache, id=" + id);
				}
				else
				{
					report.log(Report.DEBUG,  "Tache = " + tache);
					NotificationsManager.getInstance(context).creerNotification( tache);
					programmerProchaineNotification(context);
				}
			}

		} catch (Exception e)
		{
			report.log(Report.ERROR, e);
			e.printStackTrace();
		}
	}

	/***
	 * Creer la prochaine notification, apres un délai pour éviter de réafficher la meme notification
	 * @param context
	 */
	private void programmerProchaineNotification(@NonNull Context context)
	{
		Handler handler = new Handler();
		handler.postDelayed(new Runnable()
		{
			@Override
			public void run()
			{
				NotificationsManager.getInstance(context).onStart();
			}
		}, DELAI_PROCHAINE_ALARME);
	}




}