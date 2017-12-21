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
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.data.remote.SingleStringData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import retrofit2.Response;
import timber.log.Timber;

public class EditProfileActivity extends AppCompatActivity {

    /** Password Lama */
    private TextInputEditText mInputEditProfilePasswordOld;
    /** Password Lama */
    private TextInputEditText mInputEditProfilePasswordNew;
    private FloatingActionButton mEditProfileSave;

    private API mApi;
    private MaterialDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initView();
        mApi = NetworkHelpers.provideAPI(this);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProfile() {
        Timber.d("saveProfile() called");
        String oldPassword = mInputEditProfilePasswordOld.getText().toString();
        String newPassword = mInputEditProfilePasswordNew.getText().toString();
        if (TextUtils.isEmpty(oldPassword) || TextUtils.isEmpty(newPassword)) {
            Snackbar.make(mEditProfileSave, "Isi password lama dan baru terlebih dahulu",
                          Snackbar.LENGTH_SHORT).show();
            return;
        }
        if (oldPassword.length() < 6 || newPassword.length() < 6) {
            Snackbar.make(mEditProfileSave, "Panjang password minimal 6",
                          Snackbar.LENGTH_SHORT).show();
            return;
        }
        mDialog.show();
        mApi.updatePassword(oldPassword, newPassword)
            .enqueue(new ApiResponse<>(this::updateSuccess, this::updateFailure));
    }


    public static void runSetting(AppCompatActivity activity) {
        Intent intent = new Intent(activity, EditProfileActivity.class);
        activity.startActivity(intent);
    }

    private void updateSuccess(Response<SingleStringData> response) {
        Timber.d("updateSuccess() called with: response = [ %s ]", response);
        mDialog.dismiss();
        if (response.body() != null) {
            Snackbar.make(mEditProfileSave, "Password berhasil dirubah", Snackbar.LENGTH_SHORT)
                    .show();
            mInputEditProfilePasswordOld.setText("");
            mInputEditProfilePasswordNew.setText("");
        } else {
            updateFailure(new Exception("Gagal mengubah password"));
        }
    }

    private void updateFailure(Throwable t) {
        Timber.e(t, "updateFailure: ");
        mDialog.dismiss();
        Snackbar.make(mEditProfileSave, "Password gagal dirubah", Snackbar.LENGTH_SHORT).show();
    }

    private void initView() {
        mInputEditProfilePasswordOld = findViewById(R.id.inputEditProfilePasswordOld);
        mInputEditProfilePasswordNew = findViewById(R.id.inputEditProfilePasswordNew);
        mEditProfileSave = findViewById(R.id.editProfileSave);
        mInputEditProfilePasswordOld.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveProfile();
                return true;
            }
            return false;
        });
        mInputEditProfilePasswordNew.setOnKeyListener((view, keyCode, keyEvent) -> {
            if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
                saveProfile();
                return true;
            }
            return false;
        });
        mEditProfileSave.setOnClickListener(view -> saveProfile());
        mDialog = new MaterialDialog.Builder(this)
                .progress(true, 0)
                .title("Memproses")
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .build();
    }
}
