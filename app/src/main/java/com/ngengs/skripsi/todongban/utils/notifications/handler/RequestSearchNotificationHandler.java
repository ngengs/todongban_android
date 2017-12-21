/*==============================================================================
 Copyright (c) 2017 Rizky Kharisma (@ngengs)


 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 =============================================================================*/

package com.ngengs.skripsi.todongban.utils.notifications.handler;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.ResponseActionActivity;
import com.ngengs.skripsi.todongban.ResponseActivity;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.HelpType;
import com.ngengs.skripsi.todongban.services.ClearRequestSearchNotificationService;
import com.ngengs.skripsi.todongban.utils.notifications.NotificationBuilder;

import java.util.Map;

import timber.log.Timber;

public class RequestSearchNotificationHandler {
    public static void handle(Context context, Map<String, String> payload) {
        Timber.d("handle() called with: context = [ %s ], payload = [ %s ]", context, payload);
        String id = payload.get(Values.NOTIFICATION_DATA_REQUEST_SEARCH_ID);
        String name = payload.get(Values.NOTIFICATION_DATA_REQUEST_SEARCH_NAME);
        String helpType = payload.get(Values.NOTIFICATION_DATA_REQUEST_SEARCH_TYPE);
        float distance = Float.parseFloat(
                payload.get(Values.NOTIFICATION_DATA_REQUEST_SEARCH_DISTANCE));

        saveData(context, id);
        sendNotification(context, id, name, helpType, distance);
        autoCancelNotification(context);
    }

    private static void sendNotification(Context context, String responseId, String name,
                                         String helpType,
                                         float distance) {
        Timber.d(
                "sendNotification() called with: context = [ %s ], name = [ %s ], helpType = [ %s ], distance = [ %s ]",
                context, name, helpType, distance);
        PendingIntent defaultPendingIntent = NotificationBuilder.buildPendingIntent(context,
                                                                                    ResponseActivity.class,
                                                                                    NotificationBuilder.REQUEST_CODE_DEFAULT);
        Intent rejectIntent = new Intent(context, ResponseActionActivity.class);
        rejectIntent.putExtra(ResponseActionActivity.ACTION_REJECT, true);
        rejectIntent.putExtra(ResponseActionActivity.PARAMS_RESPONSE_ID, responseId);
        rejectIntent.setAction(ResponseActionActivity.ACTION_REJECT);
        String helpTypeName = context.getString(HelpType.getNameFromHelpType(helpType));
        String vehicleTypeName = context.getString(HelpType.getVehicleNameFromHelpType(helpType));
        String distanceText;
        if (distance < 1) {
            distance = distance * 1000;
            if (distance < 500) {
                distanceText = "dekat";
            } else {
                distanceText = distance + " meter";
            }
        } else {
            distanceText = distance + " kilometer";
        }
        PendingIntent rejectPendingIntent = NotificationBuilder.buildPendingIntent(context,
                                                                                   rejectIntent,
                                                                                   NotificationBuilder.REQUEST_CODE_DEFAULT);

        NotificationBuilder.sendNotification(context, Values.NOTIFICATION_ID_REQUEST_SEARCH,
                                             Values.NOTIFICATION_TAG_REQUEST_SEARCH,
                                             context.getString(R.string.appName),
                                             context.getString(
                                                     R.string.notification_message_need_help, name,
                                                     vehicleTypeName, helpTypeName, distanceText),
                                             defaultPendingIntent,
                                             rejectPendingIntent);

    }

    private static void autoCancelNotification(Context context) {
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(new GooglePlayDriver(context));
        Bundle jobBundle = new Bundle();
        Job job = dispatcher
                .newJobBuilder()
                .setService(ClearRequestSearchNotificationService.class)
                .setTag(Values.NOTIFICATION_TAG_REQUEST_SEARCH)
                .setConstraints(Constraint.ON_ANY_NETWORK)
                .setLifetime(Lifetime.FOREVER)
                .setRecurring(false)
                .setReplaceCurrent(true)
                .setTrigger(Trigger.executionWindow(60, 90))
                .setRetryStrategy(RetryStrategy.DEFAULT_LINEAR)
                .setExtras(jobBundle)
                .build();
        dispatcher.mustSchedule(job);
    }

    private static void saveData(Context context, String idResponse) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Values.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit()
                         .putString(Values.SHARED_PREFERENCES_KEY_ID_RESPONSE_SEARCH, idResponse)
                         .apply();
    }
}
