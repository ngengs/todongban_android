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

package com.ngengs.skripsi.todongban.services;

import android.content.Intent;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.google.android.gms.common.api.GoogleApiClient;
import com.ngengs.skripsi.todongban.ResponseActionActivity;

import timber.log.Timber;

public class ClearRequestSearchNotificationService extends JobService {
    public final static String TAG = "ClearRequestSearch";

    public final static String PARAM_TOKEN = "token";
    public final static String PARAM_TYPE = "type";


    private JobParameters givenParams;
    private GoogleApiClient mGoogleApiClient;


    @Override
    public boolean onStartJob(JobParameters job) {
        Timber.tag(TAG).d("onStartJob() called with: job = [ %s ]", job);
        Intent intent = new Intent(this, ResponseActionActivity.class);
        intent.setAction(ResponseActionActivity.ACTION_REJECT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        return false;
    }


    @Override
    public boolean onStopJob(JobParameters job) {
        Timber.tag(TAG).d("onStopJob() called with: job = [ %s ]", job);
        return false;
    }

}
