package com.lpi.taches.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lpi.taches.R;

import java.util.Calendar;

/**
 * Adapter pour afficher les traces
 * Created by lucien on 06/02/2016.
 */
public class ReportAdapter extends CursorAdapter
{

	private static final String TAG = "ReportAdapter";

	public ReportAdapter(Context context, Cursor cursor)
	{
		super(context, cursor, 0);
	}

	/**
	 * Bind an existing view to the data pointed to by cursor
	 * @param view    Existing view, returned earlier by newView
	 * @param context Interface to application's global information
	 * @param cursor  The cursor from which to get the data. The cursor is already
	 */
	@Override
	public void bindView(@NonNull View view, Context context, @NonNull Cursor cursor)
	{
		try
		{
			int date = cursor.getInt(cursor.getColumnIndexOrThrow(ReportDatabaseHelper.COLONNE_TRACES_DATE));
			String ligne = cursor.getString(cursor.getColumnIndexOrThrow(ReportDatabaseHelper.COLONNE_TRACES_LIGNE));
			int n = cursor.getInt(cursor.getColumnIndexOrThrow(ReportDatabaseHelper.COLONNE_TRACES_NIVEAU));
			TextView tv = view.findViewById(R.id.textView);
			switch (n)
			{
				case Report.DEBUG:
					tv.setTextColor(Color.argb(255, 0, 128, 0));
					break;
				case Report.WARNING:
					tv.setTextColor(Color.argb(255, 128, 128, 0));
					break;
				case Report.ERROR:
					tv.setTextColor(Color.argb(255, 128, 0, 0));
					break;
				default:
					tv.setTextColor(Color.BLACK);
			}
			tv.setText(formatDate(context, date) + ":" + ligne);
		} catch (Exception e)
		{
			Log.e(TAG, e.getLocalizedMessage());
		}
	}

	@SuppressLint("DefaultLocale") public static String formatDate(Context context, int date)
	{
		Calendar c = ReportDatabaseHelper.SQLiteDateToCalendar(date);

		//return android.text.format.DateFormat.getDateFormat(context).format(c.getTime()) + ' '
		//		+ android.text.format.DateFormat.getTimeFormat(context).format(c.getTime());

		return String.format("%02d/%02d %02d:%02d:%02d", c.get(Calendar.MONTH) + 1, c.get(Calendar.DAY_OF_MONTH),
				c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
	}

	/**
	 * Makes a new view to hold the data pointed to by cursor.
	 * @param context Interface to application's global information
	 * @param cursor  The cursor from which to get the data. The cursor is already
	 *                moved to the correct position.
	 * @param parent  The parent to which the new view is attached to
	 * @return the newly created view.
	 */
	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent)
	{
		return LayoutInflater.from(context).inflate(R.layout.ligne_rapport, parent, false);
	}
}
