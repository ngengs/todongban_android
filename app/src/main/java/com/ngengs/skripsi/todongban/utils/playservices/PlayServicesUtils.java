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

import android.support.annotation.NonNull;

import com.google.android.gms.common.ConnectionResult;
import com.ngengs.skripsi.todongban.utils.playservices.interfaces.PlayServiceResponseError;
import com.ngengs.skripsi.todongban.utils.playservices.interfaces.PlayServiceResponseSuccess;


public final class PlayServicesUtils {

    public static void checkStatus(int playServiceStatus,
                                   @NonNull PlayServiceResponseSuccess success,
                                   @NonNull PlayServiceResponseError error) {
        switch (playServiceStatus) {
            case ConnectionResult.API_UNAVAILABLE:
                error.onError(playServiceStatus);
                //API is not available
                break;
            case ConnectionResult.NETWORK_ERROR:
                error.onError(playServiceStatus);
                //Network error while connection
                break;
            case ConnectionResult.SERVICE_MISSING:
                error.onError(playServiceStatus);
                //service is missing
                break;
            case ConnectionResult.RESTRICTED_PROFILE:
                error.onError(playServiceStatus);
                //Profile is restricted by google so can not be used for play services
                break;
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                error.onError(playServiceStatus);
//                mTextProcess.setText("Butuh update dari Google Play Sarvice");
                break;
            case ConnectionResult.SIGN_IN_REQUIRED:
                error.onError(playServiceStatus);
                //service available but user not signed in
                break;
            case ConnectionResult.SUCCESS:
                success.onSuccess();
                break;
        }
    }

}
