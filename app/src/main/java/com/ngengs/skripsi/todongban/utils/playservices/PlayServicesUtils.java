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

package com.ngengs.skripsi.todongban.utils.playservices;

import com.google.android.gms.common.ConnectionResult;

/**
 * Created by ngengs on 10/26/2017.
 */

public final class PlayServicesUtils {

    public static void checkStatus(int playServiceStatus, PlayServicesAvailableResponse callback) {
        switch (playServiceStatus) {
            case ConnectionResult.API_UNAVAILABLE:
                callback.onError(playServiceStatus);
                //API is not available
                break;
            case ConnectionResult.NETWORK_ERROR:
                callback.onError(playServiceStatus);
                //Network error while connection
                break;
            case ConnectionResult.SERVICE_MISSING:
                callback.onError(playServiceStatus);
                //service is missing
                break;
            case ConnectionResult.RESTRICTED_PROFILE:
                callback.onError(playServiceStatus);
                //Profile is restricted by google so can not be used for play services
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                callback.onError(playServiceStatus);
//                mTextProcess.setText("Butuh update dari Google Play Sarvice");
                break;
            case ConnectionResult.SIGN_IN_REQUIRED:
                callback.onError(playServiceStatus);
                //service available but user not signed in
                break;
            case ConnectionResult.SUCCESS:
                callback.onSuccess();
                break;
        }
    }

}
