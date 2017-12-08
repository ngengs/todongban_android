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

package com.ngengs.skripsi.todongban.data.enumerations;

public final class Values {
    public static final String SHARED_PREFERENCES_NAME = "TodongBanPreference";
    public static final String SHARED_PREFERENCES_KEY_TOKEN = "token";
    public static final String SHARED_PREFERENCES_KEY_USER_TYPE = "user_type";
    public static final String SHARED_PREFERENCES_KEY_IN_HELP_PROCESS = "in_help_process";
    public static final String SHARED_PREFERENCES_KEY_ID_HELP_PROCESS = "id_help_process";
    public static final String SHARED_PREFERENCES_KEY_TYPE_HELP_PROCESS = "type_help_process";
    public static final String SHARED_PREFERENCES_KEY_PEOPLE_HELP = "people_help";

    public static final int NOTIFICATION_TYPE_PEOPLE_HELP = 201;

    public static final int NOTIFICATION_ID_DEFAULT = 0;
    public static final int NOTIFICATION_ID_PEOPLE_HELP = 1;

    public static final String NOTIFICATION_TAG_DEFAULT = "TDB_NOTIFICATION";
    public static final String NOTIFICATION_TAG_PEOPLE_HELP = "TDB_FOUND_PEOPLE";

    public static final String NOTIFICATION_DATA_GLOBAL_TYPE = "type";
    public static final String NOTIFICATION_DATA_PEOPLE_HELP_ID = "id";
    public static final String NOTIFICATION_DATA_PEOPLE_HELP_NAME = "name";
    public static final String NOTIFICATION_DATA_PEOPLE_HELP_BADGE = "badge";
    public static final String NOTIFICATION_DATA_PEOPLE_HELP_DISTANCE = "distance";
    public static final String NOTIFICATION_DATA_PEOPLE_HELP_USER_TYPE = "user_type";
}
