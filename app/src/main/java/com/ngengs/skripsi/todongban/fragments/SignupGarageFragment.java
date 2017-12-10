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

package com.ngengs.skripsi.todongban.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.SelectLocationMapActivity;
import com.ngengs.skripsi.todongban.SignupActivity;
import com.ngengs.skripsi.todongban.data.local.Garage;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.utils.ResourceUtils;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import timber.log.Timber;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupGarageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupGarageFragment extends Fragment {

    private static final String ARG_USER = "user";

    private static final int REQUEST_CODE = 100;
    private static final double DEFAULT_LATITUDE = -7.951181;
    private static final double DEFAULT_LONGITUDE = 112.613348;

    private Context mContext;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker mMarker;
    private BitmapDescriptor mMarkerDescriptor;
    private User mUser;
    private double mLatitude;
    private double mLongitude;
    private boolean mAlreadySetLocation;

    private OnFragmentInteractionListener mListener;
    private View view;
    /** Nama Bengkel */
    private TextInputEditText mInputSignupBaseName;
    private TextInputLayout mInputLayoutSignupBaseName;
    /** Jam Buka */
    private TextInputEditText mInputSignupBaseOpenHour;
    private TextInputLayout mInputLayoutSignupBaseOpenHour;
    /** Jam Tutup */
    private TextInputEditText mInputSignupBaseCloseHour;
    private TextInputLayout mInputLayoutSignupBaseCloseHour;
    /** Alamat Bengkel */
    private TextInputEditText mInputSignupBaseGarageAddress;
    private TextInputLayout mInputLayoutSignupBaseGarageAddress;
    /** Daftar */
    private Button mButtonSignupGarageSubmit;

    public SignupGarageFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param user
     *         Parameter User.
     *
     * @return A new instance of fragment SignupGarageFragment.
     */
    public static SignupGarageFragment newInstance(User user) {
        SignupGarageFragment fragment = new SignupGarageFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    private void initMaps() {
        Timber.d("initMaps() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    public void onMapReady(GoogleMap googleMap) {
        Timber.d("onMapReady() called with: googleMap = [ %s ]", googleMap);
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(mContext, R.raw.maps_style));
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.setMinZoomPreference(17.0f);
        mGoogleMap.setOnMapClickListener(latLng -> mapClicked());
        LatLng latLng = new LatLng(mLatitude, mLongitude);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void mapClicked() {
        Timber.d("mapClicked() called");
        Intent intent = new Intent(mContext, SelectLocationMapActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Timber.d(
                "onActivityResult() called with: requestCode = [ %s ], resultCode = [ %s ], data = [ %s ]",
                requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mLatitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LATITUDE,
                                                DEFAULT_LATITUDE);
                mLongitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LONGITUDE,
                                                 DEFAULT_LONGITUDE);
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                Timber.d("onActivityResult: %s", latLng);
                Timber.d("onActivityResult: %s", mLatitude);
                Timber.d("onActivityResult: %s", mLongitude);
                if (mMarker != null) {
                    mMarker.remove();
                    mMarker = null;
                }
                mAlreadySetLocation = true;
                mMarker = mGoogleMap.addMarker(
                        new MarkerOptions().position(latLng).icon(mMarkerDescriptor));
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       +
                                       " must implement OnFragmentSignupBasicInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_garage, container, false);
        initView(view);
        mContext = getContext();

        SignupActivity mSignupActivity = (SignupActivity) getActivity();
        if (mSignupActivity != null) mSignupActivity.setAppTitle(R.string.title_activity_testing);
        mLatitude = DEFAULT_LATITUDE;
        mLongitude = DEFAULT_LONGITUDE;
        mAlreadySetLocation = false;

        mMarkerDescriptor = BitmapDescriptorFactory.fromBitmap(
                ResourceUtils.getBitmapFromVectorDrawable(mContext,
                                                          MapFragment.MARKER_ICON_GARAGE));
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initMaps();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void submitGarage() {
        Timber.d("submitGarage() called");
        //noinspection ConstantConditions
        InputMethodManager inputMethodManager
                = ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        //noinspection ConstantConditions
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                                                   InputMethodManager.HIDE_NOT_ALWAYS);
        if (mListener == null) {
            throw new RuntimeException("must implement OnFragmentSignupGarageInteractionListener");
        }
        Garage garage = new Garage(mUser);
        garage.setName(mInputSignupBaseName.getText().toString());
        garage.setAddress(mInputSignupBaseGarageAddress.getText().toString());
        garage.setLatitude(mLatitude);
        garage.setLongitude(mLongitude);
        garage.setOpenHour(getDateFromTextValue(mInputSignupBaseOpenHour));
        garage.setCloseHour(getDateFromTextValue(mInputSignupBaseCloseHour));
        if (!mAlreadySetLocation) {
            Snackbar.make(mButtonSignupGarageSubmit,
                          "Harus menentukan lokasi bengkel terlebih dahulu", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(garage.getName())) {
            mInputSignupBaseName.setError("Nama bengkel tidak dapat kosong");
            mInputSignupBaseName.requestFocus();
            Snackbar.make(mButtonSignupGarageSubmit,
                          "Nama bengkel tidak dapat kosong", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (TextUtils.isEmpty(garage.getAddress())) {
            mInputSignupBaseGarageAddress.setError("Alamat bengkel tidak dapat kosong");
            mInputSignupBaseGarageAddress.requestFocus();
            Snackbar.make(mButtonSignupGarageSubmit,
                          "Alamat bengkel tidak dapat kosong", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (garage.getOpenHour() == null) {
            mInputSignupBaseOpenHour.setError("Jam Buka bengkel tidak dapat kosong");
            Snackbar.make(mButtonSignupGarageSubmit,
                          "Jam Buka bengkel tidak dapat kosong", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        if (garage.getCloseHour() == null) {
            mInputSignupBaseCloseHour.setError("Jam Tutup bengkel tidak dapat kosong");
            Snackbar.make(mButtonSignupGarageSubmit,
                          "Jam Tutup bengkel tidak dapat kosong", Snackbar.LENGTH_SHORT)
                    .show();
            return;
        }
        mListener.onButtonGarageSubmitClicked(garage);
    }

    private void showTimePicker(View view) {
        Timber.d("showTimePicker() called with: view = [ %s ]", view);
        TimePickerDialog timePicker = TimePickerDialog.newInstance(
                (v, hourOfDay, minute, second) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR, hourOfDay);
                    calendar.set(Calendar.MINUTE, minute);
                    calendar.getTimeInMillis();
                    Timber.d("showTimePicker: Calendar: %s", calendar.getTime());
                    Timber.d("showTimePicker: Calendar: %s", calendar.getTimeInMillis());
                    ((TextInputEditText) view).setText(
                            ((hourOfDay < 10) ? "0" + hourOfDay : hourOfDay) + ":" +
                            ((minute < 10) ? "0" + minute : minute));
                }, true);
        timePicker.enableSeconds(false);
        timePicker.show(getChildFragmentManager(), "Select Time Garage");
    }

    private Date getDateFromTextValue(EditText view) {
        Timber.d("getDateFromTextValue() called with: view = [ %s ]", view.getText());
        if (!TextUtils.isEmpty(view.getText())) {
            Date date = null;
            String textValue = view.getText().toString();
            String[] splitValue = textValue.split(":");
            if (splitValue.length >= 2) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    Timber.d("getDateFromTextValue: HOUR: %s", splitValue[0]);
                    Timber.d("getDateFromTextValue: MINUTE: %s", splitValue[1]);
                    calendar.set(Calendar.HOUR, Integer.parseInt(splitValue[0]));
                    calendar.set(Calendar.MINUTE, Integer.parseInt(splitValue[1]));
                    calendar.set(Calendar.SECOND, 0);
                    date = calendar.getTime();
                } catch (NumberFormatException e) {
                    Timber.e(e, "getDateFromTextValue: ");
                }
            }
            return date;
        }
        return null;
    }

    private void initView(View view) {
        mInputSignupBaseName = view.findViewById(R.id.inputSignupBaseName);
        mInputLayoutSignupBaseName = view.findViewById(R.id.inputLayoutSignupBaseName);
        mInputSignupBaseOpenHour = view.findViewById(R.id.inputSignupBaseOpenHour);
        mInputLayoutSignupBaseOpenHour = view.findViewById(R.id.inputLayoutSignupBaseOpenHour);
        mInputSignupBaseCloseHour = view.findViewById(R.id.inputSignupBaseCloseHour);
        mInputLayoutSignupBaseCloseHour = view.findViewById(R.id.inputLayoutSignupBaseCloseHour);
        mInputSignupBaseGarageAddress = view.findViewById(R.id.inputSignupBaseGarageAddress);
        mInputLayoutSignupBaseGarageAddress = view.findViewById(
                R.id.inputLayoutSignupBaseGarageAddress);
        mButtonSignupGarageSubmit = view.findViewById(R.id.buttonSignupGarageSubmit);
        mButtonSignupGarageSubmit.setOnClickListener(v -> submitGarage());
        mInputSignupBaseOpenHour.setOnClickListener(this::showTimePicker);
        mInputSignupBaseCloseHour.setOnClickListener(this::showTimePicker);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onButtonGarageSubmitClicked(Garage garage);
    }
}
