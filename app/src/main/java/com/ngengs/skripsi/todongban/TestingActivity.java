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
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class TestingActivity extends AppCompatActivity {
    private static final String TAG = "TestingActivity";

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
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode +
                   "], resultCode = [" + resultCode + "], data = [" + data + "]");
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                Log.d(TAG, "onActivityResult: RESULT OK");
                double latitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LATITUDE, 0);
                double longitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LONGITUDE,
                                                       0);
                Log.d(TAG, "onActivityResult: latitude: " + latitude + ", longitude: " + longitude);
                String address = data.getStringExtra(SelectLocationMapActivity.RESULT_ADDRESS);
                if (address != null) {
                    Log.d(TAG, "onActivityResult: address: "+address);
                } else {
                    Log.d(TAG, "onActivityResult: address empty");
                }
            } else {
                Log.d(TAG, "onActivityResult: RESULT NOT OK");
            }
        }
    }
}
