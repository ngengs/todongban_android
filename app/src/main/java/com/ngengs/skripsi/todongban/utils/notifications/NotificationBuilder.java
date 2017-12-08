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

package com.ngengs.skripsi.todongban.utils.notifications;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.SplashActivity;

public class NotificationBuilder {

    public final static int REQUEST_CODE_DEFAULT = 800;


    public static void sendNotification(@NonNull Context context, int notificationId,
                                        @NonNull String notificationTag,
                                        @NonNull String title, @NonNull String messageBody,
                                        @Nullable PendingIntent contentIntent,
                                        @Nullable PendingIntent deleteIntent) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat
                .Builder(context, "TodongBan")
                .setContentTitle(title)
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setPriority(2)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_message_alert);

        if (contentIntent != null) {
            notificationBuilder.setContentIntent(contentIntent);
        }


        if (deleteIntent != null) {
            notificationBuilder.setDeleteIntent(deleteIntent);
        }

        notificationBuilder.setVibrate(new long[]{1000, 1000});
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(notificationSound);

//        NotificationManager notificationManager =
//                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(notificationTag, notificationId /* ID of notification */,
                                   notificationBuilder.build());
    }

    public static PendingIntent buildPendingIntentDefault(@NonNull Context context) {
        return NotificationBuilder.buildPendingIntent(context, SplashActivity.class,
                                                      REQUEST_CODE_DEFAULT);
    }

    public static PendingIntent buildPendingIntent(@NonNull Context context, @NonNull Class<?> clss,
                                                   int requestCode) {
        Intent intent = new Intent(context, clss);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        return NotificationBuilder.buildPendingIntent(context, intent, requestCode);
    }

    public static PendingIntent buildPendingIntent(@NonNull Context context, @NonNull Intent intent,
                                                   int requestCode) {
        return PendingIntent.getActivity(context, requestCode/* Request code */,
                                         intent,
                                         PendingIntent.FLAG_ONE_SHOT);
    }
}
