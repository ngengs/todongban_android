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
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.ngengs.skripsi.todongban.MainActivityPersonal;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.PeopleHelp;
import com.ngengs.skripsi.todongban.utils.notifications.NotificationBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PeopleHelpNotificationHandler {

    public static void handle(Context context, Map<String, String> payload) {
        String helpId = payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_ID);
        String name = payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_NAME);
        int badge = Integer.parseInt(
                payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_BADGE));
        int userType = Integer.parseInt(
                payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_USER_TYPE));
        double distance = Double.parseDouble(
                payload.get(Values.NOTIFICATION_DATA_PEOPLE_HELP_DISTANCE));
        PeopleHelp peopleHelp = new PeopleHelp(helpId, name, badge, userType, distance);
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Values.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        if (PeopleHelpNotificationHandler.isCanRun(sharedPreferences)) {
            PeopleHelpNotificationHandler.saveData(sharedPreferences, peopleHelp);
            PeopleHelpNotificationHandler.sendNotification(context);
            PeopleHelpNotificationHandler.sendBroadcast(context, peopleHelp);
        }
    }

    private static void saveData(SharedPreferences sharedPreferences,
                                 PeopleHelp data) {
        Gson gson = new Gson();
        List<PeopleHelp> saveData = new ArrayList<>();
        String oldData = sharedPreferences.getString(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP,
                                                     null);
        if (!TextUtils.isEmpty(oldData)) {
            PeopleHelp[] temp = gson.fromJson(oldData, PeopleHelp[].class);
            saveData.addAll(Arrays.asList(temp));
        }
        saveData.add(data);
        sharedPreferences.edit()
                         .putString(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP,
                                    gson.toJson(saveData))
                         .apply();
    }

    private static void sendBroadcast(Context context, PeopleHelp peopleHelp) {
        Intent intent = new Intent();
        intent.setAction(MainActivityPersonal.ARGS_BROADCAST_FILTER);
        intent.putExtra(MainActivityPersonal.ARGS_BROADCAST_DATA, peopleHelp);
        context.sendBroadcast(intent);
    }

    private static void sendNotification(Context context) {
        NotificationBuilder.sendNotification(context, Values.NOTIFICATION_ID_PEOPLE_HELP,
                                             Values.NOTIFICATION_TAG_PEOPLE_HELP,
                                             context.getString(R.string.appName),
                                             context.getString(
                                                     R.string.notification_message_found_people_helper),
                                             NotificationBuilder
                                                     .buildPendingIntentDefault(context),
                                             null);

    }

    private static boolean isCanRun(SharedPreferences sharedPreferences) {
        return sharedPreferences.getBoolean(Values.SHARED_PREFERENCES_KEY_IN_HELP_PROCESS,
                                            false);
    }
}
