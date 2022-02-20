package com.lpi.taches.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class TachesWidgetContentService extends RemoteViewsService
	{
	@Override public RemoteViewsFactory onGetViewFactory(Intent intent)
		{
		return new TachesWidgetViewsFactory(this.getApplicationContext(), intent);
		}
	}
