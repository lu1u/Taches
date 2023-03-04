package com.lpi.taches.alarmes;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.lpi.taches.MainActivity;
import com.lpi.taches.R;
import com.lpi.taches.database.TachesDatabase;
import com.lpi.taches.report.Report;
import com.lpi.taches.taches.Tache;
import com.lpi.taches.utils.DateUtilitaires;

import java.util.Calendar;

/***
 * Classe pour regrouper la gestion des alarmes
 */
public class NotificationsManager
{
	@NonNull public static final String ACTION_ALARME = NotificationsManager.class.getName() + ".action";
	@NonNull public  static final String EXTRA_ID_TACHE = NotificationsManager.class.getName() + ".tacheId";
	private static final String CHANNEL_ID = "Taches";
	private static final int NOTIFICATION_ID = 1225;	private static final int REQUEST_CODE = 12;
	private static final String TAG = "Notifications";


	private static NotificationsManager INSTANCE = null;
	private Context _context;
	private PendingIntent _pendingIntent;

	/**
	 * Point d'accès pour l'instance unique du singleton
	 */
	@NonNull
	public static synchronized NotificationsManager getInstance(@NonNull Context context)
	{
		if (INSTANCE == null)
			INSTANCE = new NotificationsManager(context);
		return INSTANCE;
	}

	private NotificationsManager(@NonNull Context context)
	{
		try
		{
		_context = context;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	/***
	 * Au redémarrage du telephone: reprogrammer une notification
	 */
	public void onBoot()
	{
		Report.getInstance(_context).log(Report.HISTORIQUE, "Boot détecté");
		Tache tache = getProchaineNotificationAlarme();
		if (tache !=null)
		{
			// Programmer une notification pour cette tache
			programmerAlarme( tache);
		}
	}

	/***
	 * Redémarrage de l'application : reprogrammer une notification
	 */
	public void onStart()
	{
		Tache tache = getProchaineNotificationAlarme();
		if (tache !=null)
		{
			// Programmer une notification pour cette tache
			programmerAlarme( tache);
		}

	}

	/***
	 * Programmer une notification et alarme Android
	 * @param tache
	 */
	public void programmerAlarme(@NonNull Tache tache)
	{
		Report report = Report.getInstance(_context);
		try
		{
			AlarmManager alarmManager = (AlarmManager) _context.getSystemService(Context.ALARM_SERVICE);
			if ( alarmManager!=null)
			{
				Intent intent = new Intent(_context, AlarmReceiver.class);
				intent.setAction(ACTION_ALARME);
				intent.putExtra(EXTRA_ID_TACHE, tache._id);

				if ( _pendingIntent!=null)
					alarmManager.cancel(_pendingIntent);

				_pendingIntent = PendingIntent.getBroadcast(_context, REQUEST_CODE, intent, PendingIntent.FLAG_CANCEL_CURRENT|PendingIntent.FLAG_MUTABLE);
				report.log(Report.DEBUG, "Programmer alarme: " + tache.getAlarme().getDate(_context));
				alarmManager.setExact(AlarmManager.RTC_WAKEUP, tache._alarme.getCalendar().getTimeInMillis(), _pendingIntent);
			}
		} catch (Exception e)
		{
			report.log(Report.ERROR,e);
		}
	}

	/***
	 * Retourne la tache dont l'alarme est la plus proche dans l'avenir, ou null
	 * @return
	 */
	private @Nullable Tache getProchaineNotificationAlarme()
	{
		TachesDatabase database = TachesDatabase.getInstance(_context);
		Calendar maintenant = Calendar.getInstance();
		String dateMaintenant = DateUtilitaires.calendarToSqlString(maintenant);
		return database.getProchaineTache(dateMaintenant);
	}

	/***
	 * Creer une notification Android avec son et texte
	 * @param tache
	 */
	public void creerNotification(@NonNull Tache tache)
	{
		try
		{
			NotificationManagerCompat notificationManager = NotificationManagerCompat.from(_context);
			notificationManager.cancel(NOTIFICATION_ID);

			createNotificationChannel();

			// Pour afficher l'activity de l'application quand on clique sur la notification
			Intent resultIntent = new Intent(_context, MainActivity.class);// Create an Intent for the activity you want to start
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(_context);// Create the TaskStackBuilder and add the intent, which inflates the back stack
			stackBuilder.addNextIntentWithParentStack(resultIntent);        // Get the PendingIntent containing the entire back stack
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

			// Creer la notification
			NotificationCompat.Builder builder = new NotificationCompat.Builder(_context, CHANNEL_ID)
					.setContentIntent(resultPendingIntent)
					.setSmallIcon(R.drawable.ic_launcher_foreground)
					.setContentTitle(tache._nom)
					.setContentText(tache.getNotificationContentText(_context))
					//.addAction(R.drawable.snooze, _context.getString(R.string.snooze_5_minutes),getSnoozeIntent(tache, 5))
					//.addAction(R.drawable.snooze, _context.getString(R.string.snooze_1_hour),getSnoozeIntent(tache, 60))
					.setAutoCancel(true)
					.setPriority(NotificationCompat.PRIORITY_DEFAULT);
			notificationManager.notify(NOTIFICATION_ID, builder.build());
		} catch (Exception e)
		{
			Log.e(TAG, e.toString());
			e.printStackTrace();
		}
	}

//	/***
//	 * Calcule un PendingIntent qui sera envoyé lors d'une action sur la notification
//	 * @param nbMinutes
//	 * @return
//	 */
//	private @NonNull PendingIntent getSnoozeIntent(@NonNull Tache tache,  int nbMinutes)
//	{
//		Intent snoozeIntent = new Intent(_context, NotificationActionReceiver.class);
//		snoozeIntent.setAction(NotificationActionReceiver.ACTION_SNOOZE);
//		snoozeIntent.putExtra(NotificationActionReceiver.EXTRA_NB_MINUTES, nbMinutes);
//		Bundle extras = new Bundle();
//		tache.toBundle(extras, true);
//		snoozeIntent.putExtras(extras);
//		return	PendingIntent.getBroadcast(_context, 0, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
//	}


	public void createNotificationChannel()
	{
		// Create the NotificationChannel, but only on API 26+ because
		// the NotificationChannel class is new and not in the support library
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
		{
			CharSequence name = _context.getString(R.string.channel_name);
			String description = _context.getString(R.string.channel_description);
			int importance = NotificationManager.IMPORTANCE_DEFAULT;
			NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
			channel.setDescription(description);
			// Register the channel with the system; you can't change the importance
			// or other notification behaviors after this
			NotificationManager notificationManager = _context.getSystemService(NotificationManager.class);
			notificationManager.createNotificationChannel(channel);
		}
	}

	/***
	 * Supprime la notification, s'il y en a une d'affichée
	 */
	public void supprimeNotification()
	{
		NotificationManager notificationManager = _context.getSystemService(NotificationManager.class);
		if ( notificationManager!=null)
			notificationManager.cancel(NOTIFICATION_ID);
	}
}
