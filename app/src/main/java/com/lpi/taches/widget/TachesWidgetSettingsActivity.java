package com.lpi.taches.widget;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RemoteViews;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lpi.taches.R;
import com.lpi.taches.taches.Preferences;
import com.lpi.taches.utils.MessageBoxUtils;

public class TachesWidgetSettingsActivity extends AppCompatActivity
{
	private static final String[] PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE};
	public static final int REQUEST_CODE = 2335;
	int appWidgetId;
	LinearLayout _layout;
	private SeekBar _seekbarTransparence;
	private RadioButton _rbNoir;

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
	{
		if ( requestCode == REQUEST_CODE)
		{
			if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
			{
				initWallpaper();
			}
		}
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_taches_widget_settings);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null)
		{
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
			//If the intent doesn’t have a widget ID, then call finish()//
			if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID)
			{
				finish();
			}
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
			{
				MessageBoxUtils.messageBox(this, "Permission demandée",
						"Le système Android exige que vous accordiez la permission à cette application d'accéder au stockage de l'appareil. Voulez-vous accorder cette permission?",
						MessageBoxUtils.BOUTON_OK | MessageBoxUtils.BOUTON_CANCEL, new MessageBoxUtils.Listener()
						{
							@Override public void onOk()
							{
								requestPermissions(PERMISSIONS, REQUEST_CODE);
							}

							@Override public void onCancel()
							{
							}
						}
				);
			}
		else
			initWallpaper();

		// Fond noir/blanc
		_seekbarTransparence = findViewById(R.id.seekBar);
		RadioGroup rg = findViewById(R.id.idRadioGroup);
		_rbNoir = findViewById(R.id.radioButtonNoir);
		RadioButton rbBlanc = findViewById(R.id.radioButtonBlanc);
		boolean bNoir = Preferences.getInstance(this).getWidgetFondNoir(appWidgetId);
		rg.check( bNoir ? R.id.radioButtonNoir : R.id.radioButtonBlanc);

		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
		{
			@Override public void onCheckedChanged(RadioGroup radioGroup, int i)
			{
				switch( i )
				{
					case R.id.radioButtonNoir:
					{
						_layout.setBackgroundColor(getCouleurTransparence(_seekbarTransparence.getProgress(), true));
						break;
					}
					case R.id.radioButtonBlanc:
					{
						_layout.setBackgroundColor(getCouleurTransparence(_seekbarTransparence.getProgress(), false));
						break;
					}
				}
			}
		});

		// Seekbar transparence
		_layout = findViewById(R.id.idLayoutFond);
		int transparence = Preferences.getInstance(this).getWidgetTransparence(appWidgetId);

		_layout.setBackgroundColor(getCouleurTransparence(transparence, bNoir));
		_seekbarTransparence.setProgress(transparence);
		_seekbarTransparence.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{
			@Override public void onProgressChanged(SeekBar seekBar, int i, boolean b)
			{
				_layout.setBackgroundColor(getCouleurTransparence(i, _rbNoir.isChecked()));
			}

			@Override public void onStartTrackingTouch(SeekBar seekBar)
			{

			}

			@Override public void onStopTrackingTouch(SeekBar seekBar)
			{

			}
		});
	}

	public static int getCouleurTransparence(int transparence, boolean noir)
	{
		if ( transparence<0)
			transparence = 0;
		if (transparence>255)
			transparence = 255;

		if ( noir)
			return Color.argb(transparence, 0, 0, 0);
		else
			return Color.argb(transparence, 255, 255, 255);
	}

	private void initWallpaper()
	{
		ImageView ivWallpaper = findViewById(R.id.imageViewWallpaper);
		if (ivWallpaper != null)
		{
			Drawable d = getWallpaperDrawable();
			if (d != null)
				ivWallpaper.setImageDrawable(d);
		}
	}

	/***
	 * Retourne le Papier peint du téléphone
	 * @return
	 */
	@SuppressLint("MissingPermission") private @Nullable Drawable getWallpaperDrawable()
	{
		WallpaperManager manager = WallpaperManager.getInstance(getApplicationContext());
		try
		{
			return manager.getDrawable();
		} catch (Exception e)
		{
			Toast.makeText(this, "Erreur getWallpaper " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
			return null;
		}
	}

	public void onOk(View v)
	{
		Preferences prefs = Preferences.getInstance(this);
		prefs.putWidgetTransparence(appWidgetId, _seekbarTransparence.getProgress());
		prefs.putWidgetFondNoir(appWidgetId, _rbNoir.isChecked());

		AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
		RemoteViews views = new RemoteViews(getPackageName(), R.layout.taches_widget);
		appWidgetManager.updateAppWidget(appWidgetId, views);
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); //Pass the original appWidgetId
		setResult(RESULT_OK, resultValue);//Set the results from the configuration Activity
		finish();        //Finish the Activity
	}

	public void onAnnuler(View v)
	{
		Intent resultValue = new Intent();
		resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId); //Pass the original appWidgetId
		setResult(RESULT_CANCELED, resultValue);//Set the results from the configuration Activity
		finish();        //Finish the Activity
	}
}