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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.adapters.HistoryAdapter;
import com.ngengs.skripsi.todongban.data.remote.HistoryData;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;

import retrofit2.Response;
import timber.log.Timber;

public class HistoryActivity extends AppCompatActivity {

    public static String ARG_TYPE = "history_type";
    public static int TYPE_REQUEST = 0;
    public static int TYPE_RESPONSE = 1;

    private RecyclerView mRecycler;
    private HistoryAdapter mAdapter;
    private MaterialDialog mDialog;
    private API mApi;
    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mType = getIntent().getIntExtra(ARG_TYPE, TYPE_REQUEST);
        mApi = NetworkHelpers.provideAPI(this);
        initView();
        if (mType == TYPE_REQUEST) {
            setTitle("Riwayat permintaan bantuan");
        } else {
            setTitle("Riwayat pemberian bantuan");
        }
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getHistorry();
    }

    private void initView() {
        mRecycler = findViewById(R.id.history_recycler);
        mAdapter = new HistoryAdapter(this);
        mRecycler.setHasFixedSize(true);
        mRecycler.setLayoutManager(new LinearLayoutManager(this));
        mRecycler.setAdapter(mAdapter);
        mDialog = new MaterialDialog.Builder(this)
                .title("Memproses..")
                .progress(true, 0)
                .autoDismiss(false)
                .canceledOnTouchOutside(false)
                .build();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public static void runHistoryResponse(AppCompatActivity activity) {
        Intent intent = new Intent(activity, HistoryActivity.class);
        intent.putExtra(ARG_TYPE, TYPE_RESPONSE);
        activity.startActivity(intent);
    }

    public static void runHistoryRequest(AppCompatActivity activity) {
        Intent intent = new Intent(activity, HistoryActivity.class);
        intent.putExtra(ARG_TYPE, TYPE_REQUEST);
        activity.startActivity(intent);
    }

    private void getHistorry() {
        mDialog.show();
        if (mType == TYPE_REQUEST) {
            mApi.historyRequest()
                .enqueue(new ApiResponse<>(this::historyLoadSuccess, this::historyLoadFailed));
        } else {
            mApi.historyResponse()
                .enqueue(new ApiResponse<>(this::historyLoadSuccess, this::historyLoadFailed));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void historyLoadSuccess(Response<HistoryData> response) {
        Timber.d("historyLoadSuccess() called with: response = [ %s ]", response);
        mAdapter.add(response.body().getData());
        mDialog.dismiss();
    }

    private void historyLoadFailed(Throwable t) {
        Timber.e(t, "historyLoadFailed: ");
        mDialog.dismiss();
    }
}
