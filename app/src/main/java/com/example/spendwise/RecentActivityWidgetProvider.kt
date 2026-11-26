package com.example.spendwise

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

class RecentActivityWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val pendingResult = goAsync()
        WidgetUpdateHelper.handleProviderUpdate(
            context = context,
            appWidgetManager = appWidgetManager,
            appWidgetIds = appWidgetIds,
            pendingResult = pendingResult,
            type = WidgetUpdateHelper.WidgetType.RECENT_ACTIVITY
        )
    }
}
