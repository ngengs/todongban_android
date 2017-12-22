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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.data.local.Garage;
import com.ngengs.skripsi.todongban.data.remote.GarageData;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Response;
import timber.log.Timber;

public class EditGarageActivity extends AppCompatActivity {

    private API mApi;
    private MaterialDialog mDialog;
    /** Jam Buka */
    private TextInputEditText mInputEditGarageOpen;
    /** Jam Tutup */
    private TextInputEditText mInputEditGarageClose;
    private Switch mSwitchGarageForceClose;
    /** Bengkel buka sesuai dengan jadwal operasional bengkel */
    private TextView mDescGarageForceClose;
    private RelativeLayout mParentItemGarageForceClose;
    private FloatingActionButton mEditGarageSave;

    private Garage mData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_garage);
        initView();
        mApi = NetworkHelpers.provideAPI(this);
        setTitle("Pengaturan Bengkel");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        loadGarage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void runSetting(AppCompatActivity activity) {
        Intent intent = new Intent(activity, EditGarageActivity.class);
        activity.startActivity(intent);

    }

    private void loadGarage() {
        mDialog.show();
        mApi.getGarage()
            .enqueue(new ApiResponse<>(this::garageLoadSuccess, this::garageLoadFailed));
    }

    @SuppressWarnings("ConstantConditions")
    private void garageLoadSuccess(Response<GarageData> response) {
        Timber.d("garageLoadSuccess() called with: response = [ %s ]", response);
        Timber.d("garageLoadSuccess: %s", response.body());
        mData = response.body().getData();
        mSwitchGarageForceClose.setChecked((mData.getForceClose() == Garage.FORCE_CLOSE_TRUE));
        mInputEditGarageOpen.setText(timeFromDate(mData.getOpenHour()));
        mInputEditGarageClose.setText(timeFromDate(mData.getCloseHour()));
        mDialog.dismiss();
    }

    private void garageLoadFailed(Throwable t) {
        Timber.e(t, "garageLoadFailed: ");
        mDialog.dismiss();
        Snackbar.make(mParentItemGarageForceClose, "Terjadi kesalahan pada server",
                      Snackbar.LENGTH_SHORT).show();
    }

    private String timeFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return ((hour < 10) ? "0" + hour : hour) + ":" + ((minute < 10) ? "0" + minute : minute);
    }


    private void showTimePicker(View view) {
        Timber.d("showTimePicker() called with: view = [ %s ]", view);
        TimePickerDialog timePicker = TimePickerDialog.newInstance(
                (v, hourOfDay, minute, second) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.getTimeInMillis();
                    Timber.d("showTimePicker: Calendar: %s", calendar.getTime());
                    Timber.d("showTimePicker: Calendar: %s", calendar.getTimeInMillis());
                    if (view.getId() == R.id.inputEditGarageOpen) {
                        mData.setOpenHour(calendar.getTime());
                    } else {
                        mData.setCloseHour(calendar.getTime());
                    }
                    ((TextInputEditText) view).setText(
                            ((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) + ":" +
                            ((minute < 10) ? "0" + minute : minute));
                }, true);
        timePicker.enableSeconds(false);
        timePicker.show(getSupportFragmentManager(), "Select Time Garage");
    }

    private void saveGarage() {
        Timber.d("saveGarage() called");
        Timber.d("saveGarage: %s", mData);
        mDialog.show();
        Map<String, RequestBody> garageData = new HashMap<>(NetworkHelpers.prepareMapPart(mData));
        mApi.updateGarage(garageData)
            .enqueue(new ApiResponse<>(this::saveSuccess, this::saveFailed));
    }

    private void saveSuccess(Response<SingleStringData> response) {
        Timber.d("saveSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
        Snackbar.make(mParentItemGarageForceClose, "Data bengkel berhasil dirubah",
                      Snackbar.LENGTH_SHORT).show();
    }

    private void saveFailed(Throwable t) {
        Timber.e(t, "saveFailed: ");
        mDialog.dismiss();
        Snackbar.make(mParentItemGarageForceClose, "Data bengkel gagal dirubah",
                      Snackbar.LENGTH_SHORT).show();
    }

    private void initView() {
        mInputEditGarageOpen = findViewById(R.id.inputEditGarageOpen);
        mInputEditGarageClose = findViewById(R.id.inputEditGarageClose);
        mSwitchGarageForceClose = findViewById(R.id.switch_garage_force_close);
        mSwitchGarageForceClose.setOnCheckedChangeListener((compoundButton, b) -> {
            if (mData != null) {
                mData.setForceClose((b) ? Garage.FORCE_CLOSE_TRUE : Garage.FORCE_CLOSE_NOT);
            }
            if (b) {
                mDescGarageForceClose.setText(R.string.garage_setting_force_close_on);
            } else {
                mDescGarageForceClose.setText(R.string.garage_setting_force_close_off);
            }
        });
        mDescGarageForceClose = findViewById(R.id.desc_garage_force_close);
        mParentItemGarageForceClose = findViewById(R.id.parent_item_garage_force_close);
        mEditGarageSave = findViewById(R.id.editGarageSave);
        mInputEditGarageOpen.setOnClickListener(this::showTimePicker);
        mInputEditGarageClose.setOnClickListener(this::showTimePicker);
        mEditGarageSave.setOnClickListener(view -> saveGarage());
        mParentItemGarageForceClose.setOnClickListener(view -> mSwitchGarageForceClose.toggle());

        mDialog = new MaterialDialog.Builder(this)
                .title("Memproses..")
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .build();
    }
}
