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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.SelectLocationMapActivity;
import com.ngengs.skripsi.todongban.SignupActivity;
import com.ngengs.skripsi.todongban.data.local.Garage;
import com.ngengs.skripsi.todongban.data.local.User;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SignupGarageFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupGarageFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupGarageFragment extends Fragment {
    private static final String TAG = "SignupGarageFragment";

    private static final String ARG_USER = "user";

    private static final int REQUEST_CODE = 100;
    private static final double DEFAULT_LATITUDE = -7.951181;
    private static final double DEFAULT_LONGITUDE = 112.613348;

    private Context mContext;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private Marker mMarker;
    private User mUser;
    private double mLatitude;
    private double mLongitude;

    private OnFragmentInteractionListener mListener;

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
        Log.d(TAG, "initMaps() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady() called with: googleMap = [" + googleMap + "]");
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
        LatLng latLng = new LatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    private void mapClicked() {
        Log.d(TAG, "mapClicked() called");
        Intent intent = new Intent(mContext, SelectLocationMapActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                mLatitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LATITUDE,
                                                DEFAULT_LATITUDE);
                mLongitude = data.getDoubleExtra(SelectLocationMapActivity.RESULT_LONGITUDE,
                                                 DEFAULT_LONGITUDE);
                LatLng latLng = new LatLng(mLatitude, mLongitude);
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                if (mMarker != null) {
                    mMarker.remove();
                    mMarker = null;
                }
                mMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng));
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup_garage, container, false);
        mContext = getContext();

        SignupActivity mSignupActivity = (SignupActivity) getActivity();
        if (mSignupActivity != null) mSignupActivity.setAppTitle(R.string.title_activity_testing);
        initMaps();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void buttonSubmit() {

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
