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

package com.ngengs.skripsi.todongban;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;

import timber.log.Timber;

public class TestingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> clicked());
    }

    private void clicked() {
        Intent intent = new Intent(this, SelectLocationMapActivity.class);
        intent.putExtra(SelectLocationMapActivity.PARAM_WITH_ADDRESS, true);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d(
                "onActivityResult() called with: requestCode = [ %s ], resultCode = [ %s ], data = [ %s ]",
                requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Timber.d("onActivityResult: RESULT OK");
                double latitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LATITUDE, 0);
                double longitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LONGITUDE,
                                                       0);
                Timber.d("onActivityResult: latitude: %s , longitude: %s", latitude, longitude);
                String address = data.getStringExtra(SelectLocationMapActivity.RESULT_ADDRESS);
                if (address != null) {
                    Timber.d("onActivityResult: address: %s", address);
                } else {
                    Timber.d("onActivityResult: address empty");
                }
            } else {
                Timber.d("onActivityResult: RESULT NOT OK");
            }
        }
    }
}
