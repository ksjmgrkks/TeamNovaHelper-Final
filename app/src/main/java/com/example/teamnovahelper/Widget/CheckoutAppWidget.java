package com.example.teamnovahelper.Widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.example.teamnovahelper.MainActivity.LoadingActivity;
import com.example.teamnovahelper.R;
//참고 : https://onepinetwopine.tistory.com/345
/**
 * Implementation of App Widget functionality.
 */
public class CheckoutAppWidget extends AppWidgetProvider {
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.checkout_app_widget);

        //이미지 뷰를 클릭
        Intent intent = new Intent(context, LoadingActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setOnClickPendingIntent(R.id.imageView_marker, pendingIntent);

        Intent intentPublic = new Intent(Intent.ACTION_VIEW, Uri.parse("https://teamnova.co.kr/index2.php"));
        PendingIntent pendingIntentPublic = PendingIntent.getActivity(context, 0, intentPublic, 0);
        views.setOnClickPendingIntent(R.id.button1, pendingIntentPublic);

        Intent intentMember = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.teamnovamember.co.kr/index.php"));
        PendingIntent pendingIntentMember = PendingIntent.getActivity(context, 0, intentMember, 0);
        views.setOnClickPendingIntent(R.id.button2, pendingIntentMember);

        // Get the layout for the App Widget and attach an on-click listener
        // to the button
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}