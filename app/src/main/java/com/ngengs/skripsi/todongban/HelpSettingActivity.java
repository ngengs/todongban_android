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
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.adapters.HelpSettingAdapter;
import com.ngengs.skripsi.todongban.data.local.HelpType;
import com.ngengs.skripsi.todongban.data.remote.HelpConfig;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Response;
import timber.log.Timber;

public class HelpSettingActivity extends AppCompatActivity {

    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRecyclerMotorCycle;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRecyclerCar;
    private HelpSettingAdapter mAdapterMotorCycle;
    private HelpSettingAdapter mAdapterCar;
    private API mApi;
    private MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_setting);
        initView();

        setTitle("Pengaturan Jenis Bantuan");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDialog = new MaterialDialog.Builder(this)
                .title("Memprosess")
                .content("Mohon Tunggu")
                .progress(true, 0)
                .build();
        mDialog.show();
        mApi = NetworkHelpers.provideAPI(this);
        mApi.getHelpConfig()
            .enqueue(new ApiResponse<>(this::getDataSuccess, this::getDataFailure));
    }

    private void getDataSuccess(Response<HelpConfig> response) {
        Timber.d("getDataSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
        HelpConfig data = response.body();
        if (data != null) {
            for (HelpType item : data.getData()) {
                switch (item.getVehicle()) {
                    case HelpType.VEHICLE_MOTORCYCLE:
                        Timber.d("getDataSuccess: %s", "Add Motor");
                        mAdapterMotorCycle.add(item);
                        break;
                    case HelpType.VEHICLE_CAR:
                        Timber.d("getDataSuccess: %s", "Add Car");
                        mAdapterCar.add(item);
                        break;
                }
            }
            mAdapterMotorCycle.notifyDataSetChanged();
            mAdapterCar.notifyDataSetChanged();
        }
    }

    private void getDataFailure(Throwable t) {
        Timber.e(t, "getDataFailure: ");
        mDialog.dismiss();
    }

    private void updateDataSuccess(Response<HelpConfig> response) {
        Timber.d("updateDataSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
    }

    private void updateDataFailure(Throwable t) {
        Timber.e(t, "updateDataFailure: ");
        mDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.help_setting_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initView() {
        mRecyclerMotorCycle = findViewById(R.id.recycler_help_setting_motor_cycle);
        mRecyclerCar = findViewById(R.id.recycler_help_setting_car);

        mAdapterMotorCycle = new HelpSettingAdapter();
        mRecyclerMotorCycle.setAdapter(mAdapterMotorCycle);
        mRecyclerMotorCycle.setHasFixedSize(true);
        mRecyclerMotorCycle.setLayoutManager(new LinearLayoutManager(this));
        mAdapterCar = new HelpSettingAdapter();
        mRecyclerCar.setAdapter(mAdapterCar);
        mRecyclerCar.setHasFixedSize(true);
        mRecyclerCar.setLayoutManager(new LinearLayoutManager(this));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ViewCompat.setNestedScrollingEnabled(mRecyclerMotorCycle, false);
            ViewCompat.setNestedScrollingEnabled(mRecyclerCar, false);
        } else {
            mRecyclerMotorCycle.setNestedScrollingEnabled(false);
            mRecyclerCar.setNestedScrollingEnabled(false);
        }
    }

    public static void runSetting(AppCompatActivity activity) {
        Intent intent = new Intent(activity, HelpSettingActivity.class);
        activity.startActivity(intent);
    }

    private void save() {
        List<HelpType> data = new ArrayList<>();
        data.addAll(mAdapterMotorCycle.get());
        data.addAll(mAdapterCar.get());
        List<String> saveHelpType = new ArrayList<>();
        List<Integer> saveStatus = new ArrayList<>();
        for (HelpType item : data) {
            saveHelpType.add(item.getId());
            saveStatus.add(item.getStatus());
        }
        Timber.d("save: %s", saveHelpType);
        mDialog.show();
        mApi.updateHelpConfig(saveHelpType, saveStatus)
            .enqueue(new ApiResponse<>(this::updateDataSuccess, this::updateDataFailure));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_save:
                save();
                break;
            case android.R.id.home:
                onBackPressed();
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
