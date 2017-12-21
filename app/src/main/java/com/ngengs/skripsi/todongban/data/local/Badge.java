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

package com.ngengs.skripsi.todongban.data.local;

import android.content.Context;

import com.ngengs.skripsi.todongban.R;

public class Badge {
    public static final int LEVEL_STAGE_1 = 1;
    public static final int LEVEL_STAGE_2 = 5;
    public static final int LEVEL_STAGE_3 = 10;
    public static final int LEVEL_STAGE_4 = 15;
    public static final int LEVEL_STAGE_5 = 20;

    public static final int WEIGHT_STAGE_1 = 1;
    public static final int WEIGHT_STAGE_2 = 2;
    public static final int WEIGHT_STAGE_3 = 3;
    public static final int WEIGHT_STAGE_4 = 4;
    public static final int WEIGHT_STAGE_5 = 5;


    private int level;
    private int weight;

    public Badge(int badgeNominal) {
        int dozens = badgeNominal / 10;
        int units = badgeNominal - (dozens * 10);
        level = dozens;
        weight = units;
    }

    public String getBadgeName(Context context, int userType) {
        String name;
        if (level < LEVEL_STAGE_1) {
            if (userType == User.TYPE_PERSONAL) {
                name = context.getString(R.string.badge_newbie_personal);
            } else {
                name = context.getString(R.string.badge_newbie_garage);
            }
        } else if (level < LEVEL_STAGE_2) {
            if (userType == User.TYPE_PERSONAL) {
                name = context.getString(R.string.badge_level_1_personal,
                                         getBadgeWeightName(context));
            } else {
                name = context.getString(R.string.badge_level_1_garage,
                                         getBadgeWeightName(context));
            }
        } else if (level < LEVEL_STAGE_3) {
            if (userType == User.TYPE_PERSONAL) {
                name = context.getString(R.string.badge_level_2_personal,
                                         getBadgeWeightName(context));
            } else {
                name = context.getString(R.string.badge_level_2_garage,
                                         getBadgeWeightName(context));
            }
        } else if (level < LEVEL_STAGE_4) {
            if (userType == User.TYPE_PERSONAL) {
                name = context.getString(R.string.badge_level_3_personal,
                                         getBadgeWeightName(context));
            } else {
                name = context.getString(R.string.badge_level_3_garage,
                                         getBadgeWeightName(context));
            }
        } else if (level < LEVEL_STAGE_4) {
            if (userType == User.TYPE_PERSONAL) {
                name = context.getString(R.string.badge_level_4_personal,
                                         getBadgeWeightName(context));
            } else {
                name = context.getString(R.string.badge_level_4_garage,
                                         getBadgeWeightName(context));
            }
        } else {
            if (userType == User.TYPE_PERSONAL) {
                name = context.getString(R.string.badge_level_5_personal,
                                         getBadgeWeightName(context));
            } else {
                name = context.getString(R.string.badge_level_5_garage,
                                         getBadgeWeightName(context));
            }
        }


        return name;
    }

    private String getBadgeWeightName(Context context) {
        String name;
        String[] weightName = context.getResources().getStringArray(R.array.badge_weight);
        if (weight < WEIGHT_STAGE_1) {
            name = weightName[0];
        } else if (weight < WEIGHT_STAGE_2) {
            name = weightName[1];
        } else if (weight < WEIGHT_STAGE_3) {
            name = weightName[2];
        } else if (weight < WEIGHT_STAGE_4) {
            name = weightName[3];
        } else {
            name = weightName[4];
        }
        return name;
    }
}
