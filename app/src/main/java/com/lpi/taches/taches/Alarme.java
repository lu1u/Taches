package com.lpi.taches.taches;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lpi.taches.R;
import com.lpi.taches.utils.DateUtilitaires;

import java.text.DateFormat;
import java.util.Calendar;

/***
 * Contient les informations pour programmer une alarme
 */
public class Alarme
{
	@NonNull Calendar _dateAlarme;
	boolean _alarmeActive;

	public Alarme()
	{
		_alarmeActive = false;
		_dateAlarme = Calendar.getInstance();
	}

	public Alarme(@Nullable String s)
	{
		Calendar c = DateUtilitaires.sqlStringToCalendar(s);
		_alarmeActive = c !=null;
		_dateAlarme = c;
	}

	/***
	 * Retourne la representation texte de la date de l'alarme, ou ""
	 * @return
	 */
	public @NonNull  String getDate(@NonNull Context context)
	{
		if ( ! _alarmeActive)
			return context.getString(R.string.alarme_non_active);

		return DateUtilitaires.getDateAndTime(_dateAlarme, DateFormat.MEDIUM, DateFormat.SHORT);
	}

	/***
	 * Retourne la representation texte de la date de l'alarme
	 * @return
	 */
	public @NonNull  String toString()
	{
		if ( ! _alarmeActive)
			return "Non active";

		if ( _dateAlarme == null)
			return "Alarme nulle";

		return DateUtilitaires.getDateAndTime(_dateAlarme, DateFormat.MEDIUM, DateFormat.DEFAULT);
	}
	public boolean isActive()
	{
		return _alarmeActive;
	}

	public void setActive(boolean val)
	{
		_alarmeActive = val;
		if ( _alarmeActive && _dateAlarme == null)
			_dateAlarme = Calendar.getInstance();
	}

	public Calendar getCalendar()
	{
		if( _dateAlarme==null || !_alarmeActive)
			return Calendar.getInstance();

		return _dateAlarme;
	}

	public void setAlarme(@NonNull Calendar cal)
	{
		_dateAlarme = (Calendar)cal.clone();
	}
}
