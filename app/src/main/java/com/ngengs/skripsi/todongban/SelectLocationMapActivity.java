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
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.ngengs.skripsi.todongban.fragments.MapFragment;

public class SelectLocationMapActivity extends AppCompatActivity
        implements MapFragment.OnFragmentMinimalInteractionListener {
    private final static String TAG = "SelectLocationMap";

    public final static String PARAM_WITH_ADDRESS = "need_address";

    public final static String RESULT_LATITUDE = "latitude";
    public final static String RESULT_LONGITUDE = "longitude";
    public final static String RESULT_ADDRESS = "address";

    private FloatingActionButton mMapsSelectButton;
    private FloatingActionButton mMapsMyLocationButton;

    private boolean mNeedAddress;
    private MapFragment mMap;
    private boolean mEditMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_map);
        initView();
        setTitle(getString(R.string.title_select_location));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        mNeedAddress = false;
        mEditMode = false;
        if (getIntent().getExtras() != null) {
            mNeedAddress = getIntent().getBooleanExtra(PARAM_WITH_ADDRESS, false);
        }
        mMap = MapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                                   .add(R.id.fragment_map_wrapper, mMap)
                                   .commit();
    }

    @Override
    public void onBackPressed() {
        if (mEditMode) {
            mMap.cancelEditLocation();
        } else {
            setResult(RESULT_CANCELED);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        mMapsSelectButton = findViewById(R.id.maps_select_button);
        mMapsSelectButton.setOnClickListener(v -> selectLocation());
        mMapsMyLocationButton = findViewById(R.id.maps_my_location_button);
        mMapsMyLocationButton.setOnClickListener(v -> selectMyLocation());
    }

    private void selectLocation() {
        Location mLocation = mMap.getSelectedLocation();
        if (mLocation != null) {
            Intent intent = new Intent();
            intent.putExtra(RESULT_LATITUDE, mLocation.getLatitude());
            intent.putExtra(RESULT_LONGITUDE, mLocation.getLongitude());
            if (mNeedAddress) {
                intent.putExtra(RESULT_ADDRESS, mMap.getAddress());
            }
            setResult(RESULT_OK, intent);
            finish();
        } else {
            Toast.makeText(this, "Belum menentukan lokasi", Toast.LENGTH_SHORT).show();
        }
    }

    private void selectMyLocation() {
        Log.d(TAG, "selectMyLocation() called");
        mMap.setToMyLocation();
    }

    @Override
    public void onMyLocationReady() {
        Log.d(TAG, "onMyLocationReady() called");
        if (mMapsMyLocationButton.getVisibility() == View.GONE) {
            mMapsMyLocationButton.setVisibility(View.VISIBLE);
        }
        if (mMapsSelectButton.getVisibility() == View.GONE) {
            mMapsSelectButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProcessEdit() {
        Log.d(TAG, "onProcessEdit() called");
        mEditMode = true;
        mMapsSelectButton.hide();
    }

    @Override
    public void onFinishEdit() {
        Log.d(TAG, "onFinishEdit() called");
        mEditMode = false;
        mMapsSelectButton.show();
    }

}
