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

import android.content.Context;
import android.content.Intent;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.WaitVerificationActivity;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.utils.notifications.NotificationBuilder;

import java.util.Map;

public class SignupNotificationHandler {

    public static void handle(Context context, Map<String, String> payload) {
        int code = Integer.valueOf(payload.get(Values.NOTIFICATION_DATA_GLOBAL_CODE));
        if (code == Values.NOTIFICATION_CODE_SIGNUP_SUCCESS) {
            SignupNotificationHandler.sendNotification(context,
                                                       R.string.notification_message_signup_complete);
        }
        SignupNotificationHandler.sendBroadcast(context, code);
    }

    private static void sendNotification(Context context, int resourceMessage) {
        NotificationBuilder.sendNotification(context, Values.NOTIFICATION_ID_SIGNUP,
                                             Values.NOTIFICATION_TAG_SIGNUP,
                                             context.getString(R.string.appName),
                                             context.getString(resourceMessage),
                                             NotificationBuilder
                                                     .buildPendingIntentDefault(context),
                                             null);
    }

    private static void sendBroadcast(Context context, int code) {
        Intent intent = new Intent();
        intent.setAction(WaitVerificationActivity.ARGS_BROADCAST_FILTER);
        intent.putExtra(WaitVerificationActivity.ARGS_BROADCAST_DATA, code);
        context.sendBroadcast(intent);
    }

}
