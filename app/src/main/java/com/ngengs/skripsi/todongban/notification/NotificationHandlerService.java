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

package com.ngengs.skripsi.todongban.notification;

import android.content.SharedPreferences;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.utils.notifications.NotificationBuilder;
import com.ngengs.skripsi.todongban.utils.notifications.handler.PeopleHelpNotificationHandler;

import java.util.Map;

import timber.log.Timber;

public class NotificationHandlerService extends FirebaseMessagingService {
    private static final String TAG = "NotificationHandlerS";

    private SharedPreferences mSharedPreferences;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Timber.tag(TAG).d("onMessageReceived() called with: remoteMessage = [ %s ]", remoteMessage);
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);

        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Timber.tag(TAG).d("From: %s", remoteMessage.getFrom());


        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Timber.tag(TAG).d("Message data payload: %s", remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
            } else {
                // Handle message within 10 seconds
            }
            Map<String, String> payload = remoteMessage.getData();
            if (payload.containsKey("type")) {
                int type = Integer.parseInt(payload.get(Values.NOTIFICATION_DATA_GLOBAL_TYPE));
                if (type == Values.NOTIFICATION_TYPE_PEOPLE_HELP) {
                    PeopleHelpNotificationHandler.handle(this, payload);
//                    Log.d(TAG, "onMessageReceived: send broadcast");
//                    String helpId = payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_ID);
//                    String name = payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_NAME);
//                    int badge = Integer.parseInt(
//                            payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_BADGE));
//                    int userType = Integer.parseInt(
//                            payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_USER_TYPE));
//                    double distance = Double.parseDouble(
//                            payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_DISTANCE));
//                    PeopleHelp peopleHelp = new PeopleHelp(helpId, name, badge, userType, distance);
//                    Intent intent = new Intent();
//                    intent.setAction(MainActivityPersonal.ARGS_BROADCAST_FILTER);
//                    intent.putExtra(MainActivityPersonal.ARGS_BROADCAST_DATA, peopleHelp);
//
//                    NotificationBuilder.sendNotification(this, Values.NOTIFICATION_ID_PEOPLE_HELP,
//                                                         Values.NOTIFICATION_TAG_PEOPLE_HELP,
//                                                         getString(R.string.project_id),
//                                                         getString(R.string.project_id),
//                                                         NotificationBuilder.buildPendingIntentDefault(
//                                                                 this), null);
//                    sendBroadcast(intent);
                }
            }

        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Timber.tag(TAG)
                  .d("Message Notification Body: %s", remoteMessage.getNotification().getBody());
            //noinspection ConstantConditions
            NotificationBuilder.sendNotification(this, Values.NOTIFICATION_ID_DEFAULT,
                                                 Values.NOTIFICATION_TAG_DEFAULT,
                                                 remoteMessage.getNotification().getTitle(),
                                                 remoteMessage.getNotification().getBody(),
                                                 NotificationBuilder.buildPendingIntentDefault(
                                                         this), null);
        }

    }
}
