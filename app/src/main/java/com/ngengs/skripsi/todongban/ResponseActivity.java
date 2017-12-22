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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.HelpType;
import com.ngengs.skripsi.todongban.data.local.ResponseHelp;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.remote.ResponseSearch;
import com.ngengs.skripsi.todongban.fragments.MapFragment;
import com.ngengs.skripsi.todongban.utils.ResourceUtils;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;
import timber.log.Timber;

public class ResponseActivity extends AppCompatActivity {
    private CircleImageView mResponseAvatar;
    /** Nama */
    private TextView mResponseFullName;
    /** Permasalahan */
    private TextView mResponseHelpType;
    /** Keterangan */
    private TextView mResponseMessage;
    /** Lokasi */
    private TextView mResponseAddress;
    private Button mResponseButtonAccept;
    private Button mResponseButtonReject;
    private MaterialDialog mDialog;


    private SharedPreferences mSharedPreferences;
    private String responseId;
    private int mUserType;
    private API mApi;
    private ResponseHelp mData;
    private GoogleMap mGoogleMap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Timber.d("onCreate() called with: savedInstanceState = [ %s ]", savedInstanceState);
        super.onCreate(savedInstanceState);
        boolean reject = false;
        if (getIntent().getAction() != null) {
            if (getIntent().getAction().equalsIgnoreCase("reject")) {
                reject = true;
            }
        }
        mSharedPreferences = getSharedPreferences(Values.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        responseId = mSharedPreferences.getString(Values.SHARED_PREFERENCES_KEY_ID_RESPONSE_SEARCH,
                                                  null);
        mUserType = mSharedPreferences.getInt(Values.SHARED_PREFERENCES_KEY_USER_TYPE,
                                              User.TYPE_PERSONAL);
        if (TextUtils.isEmpty(responseId)) {
            throw new RuntimeException("Empty Id");
        }
        mApi = NetworkHelpers.provideAPI(this);
        ResponseActionActivity.cancelNotification(this);
        ResponseActionActivity.stopAutoCancelNotification(this);
        Timber.d("onCreate: %s: %s", "REJECT", reject);

        if (reject) {
            Timber.d("onCreate: %s", "REJECT");
            Toast.makeText(this, "Reject help", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            setContentView(R.layout.activity_response);
            mDialog = new MaterialDialog.Builder(this)
                    .progress(true, 0)
                    .autoDismiss(false)
                    .title("Memprosess")
                    .canceledOnTouchOutside(false)
                    .build();
            initView();
            mDialog.show();
            mApi.responseSearchDetail(responseId)
                .enqueue(new ApiResponse<>(this::getDataSuccess, this::getDataFailure));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void getDataSuccess(Response<ResponseSearch> response) {
        Timber.d("getDataSuccess() called with: response = [ %s ]", response);
        ResponseSearch body = response.body();
        mData = body.getData();
        mResponseFullName.setText(mData.getName());
        mResponseAddress.setText(mData.getAddress());
        mResponseMessage.setText(mData.getMessage());
        mResponseHelpType.setText(
                getString(HelpType.getVehicleNameFromHelpType(mData.getHelpType())) + " " +
                getString(HelpType.getNameFromHelpType(mData.getHelpType())));
        Picasso.with(this)
               .load(mData.getAvatar())
               .resize(100, 100)
               .centerCrop()
               .into(mResponseAvatar);
        initMaps();
    }

    private void initMaps() {
        Timber.d("initMaps() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void onMapReady(GoogleMap googleMap) {
        Timber.d("onMapReady() called with: googleMap = [ %s ]", googleMap);
        mGoogleMap = googleMap;

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.maps_style));
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.setMinZoomPreference(17.0f);
        mGoogleMap.setOnMapClickListener(latLng -> mapClicked());
        LatLng latLng = new LatLng(mData.getLatitude(), mData.getLongitude());
        mGoogleMap.addMarker(
                new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(
                        ResourceUtils.getBitmapFromVectorDrawable(this,
                                                                  MapFragment.MARKER_ICON_HELP))));
        mGoogleMap.setOnMarkerClickListener(marker -> mapClicked());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        if (mDialog != null) mDialog.dismiss();
    }

    private boolean mapClicked() {
        Timber.d("mapClicked() called");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String url = "https://www.google.com/maps/dir/?api=1&destination=" + mData.getLatitude() +
                     "," + mData.getLongitude();
        intent.setData(Uri.parse(url));
        startActivity(intent);
        return true;
    }

    private void getDataFailure(Throwable throwable) {
        Timber.e(throwable, "getDataFailure: ");
        if (mDialog != null) mDialog.dismiss();
        Toast.makeText(this, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
    }

    private void rejectHelp() {
        Intent intent = new Intent(this, ResponseActionActivity.class);
        intent.setAction(ResponseActionActivity.ACTION_REJECT);
        intent.putExtra(ResponseActionActivity.PARAMS_RESPONSE_ID, responseId);
        startActivityForResult(intent, 100);
    }


    private void acceptHelp() {
        Intent intent = new Intent(this, ResponseActionActivity.class);
        intent.setAction(ResponseActionActivity.ACTION_ACCEPT);
        intent.putExtra(ResponseActionActivity.PARAMS_RESPONSE_ID, responseId);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
//            if(resultCode == RESULT_OK){
//
//            } else {
//
//            }
            if (mDialog != null) {
                mDialog.dismiss();
            }
            finish();
        }
    }

    private void initView() {
        mResponseAvatar = findViewById(R.id.response_avatar);
        mResponseFullName = findViewById(R.id.response_full_name);
        mResponseHelpType = findViewById(R.id.response_help_type);
        mResponseMessage = findViewById(R.id.response_message);
        mResponseAddress = findViewById(R.id.response_address);
        mResponseButtonAccept = findViewById(R.id.response_button_accept);
        mResponseButtonReject = findViewById(R.id.response_button_reject);
        if (mUserType == User.TYPE_PERSONAL) {
            mResponseButtonReject.setText("Abaikan");
            mResponseButtonAccept.setText("Bantu");
        } else {
            mResponseButtonReject.setText("Tidak Menghampiri");
            mResponseButtonAccept.setText("Dapat Menghampiri");
        }
        mResponseButtonAccept.setOnClickListener(view -> acceptHelp());
        mResponseButtonReject.setOnClickListener(view -> rejectHelp());
    }
}
