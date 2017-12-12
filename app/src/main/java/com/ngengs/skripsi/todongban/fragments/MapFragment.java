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

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.utils.ResourceUtils;
import com.ngengs.skripsi.todongban.utils.customviews.GoogleMapResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment can implement the
 * {@link OnFragmentMinimalInteractionListener} interface
 * to handle minimal interaction events.
 * Activities that contain this fragment can implement the
 * {@link OnFragmentCompleteInteractionListener} interface
 * to handle all interaction events.
 * Use the {@link MapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String ARG_PADDING_VERTICAL = "padding_vertical";
    private static final String ARG_PADDING_HORIZONTAL = "padding_horizontal";
    private static final String ARG_MARKER_ICON = "marker_icon";

    @DrawableRes
    public static final int MARKER_ICON_HELP = R.drawable.ic_marker_people_request_help;
    @DrawableRes
    public static final int MARKER_ICON_GARAGE = R.drawable.ic_marker_garage_location;

    private int mPaddingVertical;
    private int mPaddingHorizontal;

    private ImageView mMapMarkerChangerImage;
    private TextView mMapMarkerChangerIndicator;

    private Context mContext;
    private Location mLocation;
    private GoogleApiClient mGoogleApiClient;
    private GoogleMap mGoogleMap;
    private Marker mMarker;
    private boolean mEditLocation;
    private boolean mProcessMovingCamera;
    private int mMarkerIcon;
    private BitmapDescriptor mMarkerDescriptor;

    private OnFragmentMinimalInteractionListener mMinimalListener;
    private OnFragmentCompleteInteractionListener mCompleteListener;

    public MapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paddingVertical
     *         Parameter 1.
     * @param paddingHorizontal
     *         Parameter 2.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(int paddingVertical, int paddingHorizontal) {
        return newInstance(paddingVertical, paddingHorizontal, MARKER_ICON_HELP);
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paddingVertical
     *         Parameter 1.
     * @param paddingHorizontal
     *         Parameter 2.
     * @param mapMarkerIcon
     *         Parameter 3.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance(int paddingVertical, int paddingHorizontal,
                                          int mapMarkerIcon) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PADDING_VERTICAL, paddingVertical);
        args.putInt(ARG_PADDING_HORIZONTAL, paddingHorizontal);
        args.putInt(ARG_MARKER_ICON, mapMarkerIcon);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment MapFragment.
     */
    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onAttach(Context context) {
        Timber.d("onAttach: ");
        super.onAttach(context);
        if (context instanceof OnFragmentCompleteInteractionListener) {
            mCompleteListener = (OnFragmentCompleteInteractionListener) context;
            mMinimalListener = (OnFragmentMinimalInteractionListener) context;
        } else if (getParentFragment() instanceof OnFragmentCompleteInteractionListener) {
            mCompleteListener = (OnFragmentCompleteInteractionListener) getParentFragment();
            mMinimalListener = (OnFragmentMinimalInteractionListener) getParentFragment();
        }

        if (context instanceof OnFragmentMinimalInteractionListener) {
            mMinimalListener = (OnFragmentMinimalInteractionListener) context;
        } else if (getParentFragment() instanceof OnFragmentMinimalInteractionListener) {
            mMinimalListener = (OnFragmentMinimalInteractionListener) getParentFragment();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPaddingVertical = getArguments().getInt(ARG_PADDING_VERTICAL, 0);
            mPaddingHorizontal = getArguments().getInt(ARG_PADDING_HORIZONTAL, 0);
            mMarkerIcon = getArguments().getInt(ARG_MARKER_ICON, MARKER_ICON_HELP);
        } else {
            mPaddingVertical = 0;
            mPaddingHorizontal = 0;
            mMarkerIcon = MARKER_ICON_HELP;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        mContext = getContext();
        initView(view);
        mProcessMovingCamera = false;
        mEditLocation = false;

        mMarkerDescriptor = BitmapDescriptorFactory.fromBitmap(
                ResourceUtils.getBitmapFromVectorDrawable(mContext, mMarkerIcon));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        initPlayServices();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mMinimalListener = null;
        mCompleteListener = null;
    }

    public Location getSelectedLocation() {
        return mLocation;
    }

    public double getLatitude() {
        if (mLocation != null) return mLocation.getLatitude();
        else return 0;
    }

    public double getLongitude() {
        if (mLocation != null) return mLocation.getLongitude();
        else return 0;
    }

    public String getAddress() {
        Address address = getAddress(getLatitude(), getLongitude());
        String stringAddress = null;
        if (address != null) {
            List<String> addressLine = new ArrayList<>();
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                addressLine.add(address.getAddressLine(i));
            }
            stringAddress = TextUtils.join(System.getProperty("line.separator"), addressLine);
        }
        return stringAddress;
    }

    public void setToMyLocation() {
        mEditLocation = false;
        locationChangeMarker();
        detectLocation();
    }

    public void enableGesture() {
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setScrollGesturesEnabled(true);
        }
    }

    public void disableGesture() {
        if (mGoogleMap != null) {
            mGoogleMap.getUiSettings().setScrollGesturesEnabled(false);
        }
    }

    private void initView(View view) {
        mMapMarkerChangerImage = view.findViewById(R.id.map_marker_changer_image);
        mMapMarkerChangerIndicator = view.findViewById(R.id.map_marker_changer_indicator);
        mMapMarkerChangerImage.setOnClickListener(v -> selectedMapMarker());
        mMapMarkerChangerIndicator.setOnClickListener(v -> selectedMapMarker());
        mMapMarkerChangerImage.setImageResource(mMarkerIcon);
    }

    private void initPlayServices() {
        Timber.d("initPlayServices() called");
        mGoogleApiClient = new GoogleApiClient.Builder(mContext, this, this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void initMaps() {
        Timber.d("initMaps() called");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void selectedMapMarker() {
        Timber.d("selectedMapMarker() called");
        if (mGoogleMap != null) {
            LatLng latLng = mGoogleMap.getCameraPosition().target;
            mLocation.setLatitude(latLng.latitude);
            mLocation.setLongitude(latLng.longitude);
            mEditLocation = false;
            locationChangeMarker();
            changeLocationOnMap();
        }
    }

    public void onMapReady(GoogleMap googleMap) {
        Timber.d("onMapReady() called with: googleMap = [ %s ]", googleMap);
        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setTrafficEnabled(false);
        if (mPaddingVertical > 0 && mPaddingHorizontal > 0) {
            mGoogleMap.setPadding(mPaddingHorizontal, mPaddingVertical, mPaddingHorizontal,
                                  mPaddingVertical);
        }
        mGoogleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(mContext, R.raw.maps_style));
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.setMinZoomPreference(17.0f);
        mGoogleMap.setOnCameraMoveStartedListener(i -> mapMovement());
        changeLocationOnMap();
    }

    @SuppressLint({"MissingPermission", "SetTextI18n"})
    private void changeLocationOnMap() {
        Timber.d("changeLocationOnMap() called");
        if (mGoogleMap == null) {
            initMaps();
            return;
        }
        if (mLocation == null) {
            detectLocation();
            return;
        }
        if (mMinimalListener != null) {
            mMinimalListener.onMyLocationReady();
        }
        mEditLocation = false;
        LatLng position = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setBuildingsEnabled(false);
        if (mMarker != null) {
            mMarker.remove();
        }
        mMarker = mGoogleMap.addMarker(
                new MarkerOptions().position(position).flat(true).icon(mMarkerDescriptor));
        mProcessMovingCamera = true;
        Timber.d("changeLocationOnMap: Location: %s", position);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(position),
                                 new GoogleMapResponse(this::mapsFinishChangeLocation,
                                                       this::mapsCancelChangeLocation));
        if (mCompleteListener != null) {
            mCompleteListener.onAddressChanged(getAddress());
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Timber.d("onConnected() called with: bundle = [ %s ]", bundle);
        detectLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Timber.d("onConnectionSuspended() called with: i = [ %s ]", i);
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.d("onConnectionFailed() called with: connectionResult = [ %s ]",
                 connectionResult.getErrorCode());
    }

    @SuppressLint("MissingPermission")
    private void detectLocation() {
        Timber.d("detectLocation() called");
        Task<Location> resultLocation = LocationServices
                .getFusedLocationProviderClient(mContext)
                .getLastLocation();
        resultLocation.addOnCompleteListener(task -> {
            Timber.d("detectLocation: Finishing get location");
            if (task.isSuccessful()) {
                Timber.d("detectLocation: success get location");
                mLocation = task.getResult();
                changeLocationOnMap();
            } else {
                Timber.e("detectLocation: kesalahan saat mendapatkan lokasi");
                Toast.makeText(mContext,
                               "Gagal mendapatkan lokasi anda sekarang, silahkan mengatur lokasi anda secara manual",
                               Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mapMovement() {
        Timber.d("mapMovement() called");
        if (!mEditLocation && !mProcessMovingCamera) {
            mEditLocation = true;
            if (mMarker != null) {
                mMarker.remove();
            }
            locationChangeMarker();
        }
    }

    public void cancelEditLocation() {
        mEditLocation = false;
        locationChangeMarker();
        changeLocationOnMap();
    }

    private void locationChangeMarker() {
        Timber.d("locationChangeMarker() called");
        if (mEditLocation) {
            mMapMarkerChangerIndicator.setVisibility(View.VISIBLE);
            mMapMarkerChangerImage.setVisibility(View.VISIBLE);
            if (mMinimalListener != null) {
                mMinimalListener.onProcessEdit();
            }
        } else {
            mMapMarkerChangerIndicator.setVisibility(View.GONE);
            mMapMarkerChangerImage.setVisibility(View.GONE);
            if (mMinimalListener != null) {
                mMinimalListener.onFinishEdit();
            }
        }
    }

    private Address getAddress(double latitude, double longitude) {
        Timber.d("getAddress() called with: latitude = [ %s ], longitude = [ %s ]", latitude,
                 longitude);
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(mContext, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0);
        } catch (IOException e) {
            Timber.e(e, "getAddress: ");
            Toast.makeText(mContext, "Gagal mendapatkan nama alamat dari lokasi yang dipilih",
                           Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    private void mapsFinishChangeLocation() {
        Timber.d("mapsFinishChangeLocation() called");
        Timber.d("animateCamera:onFinish(): Location: %s", mGoogleMap.getCameraPosition().target);
        mProcessMovingCamera = false;
        if (mCompleteListener != null) {
            mCompleteListener.onMapFinishMove();
        }
    }

    private void mapsCancelChangeLocation() {
        Timber.d("mapsCancelChangeLocation() called");
        mProcessMovingCamera = false;
        mapMovement();
    }

    /**
     * This interface can be implemented by activities or other fragment that contain this
     * fragment to allow minimal interaction in this fragment to be communicated
     * to the activity or parent fragment and potentially other fragments contained in that
     * activity or parent fragment.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentMinimalInteractionListener {
        void onMyLocationReady();

        void onProcessEdit();

        void onFinishEdit();
    }

    /**
     * This interface can be implemented by activities or other fragment that contain this
     * fragment to allow complete interaction in this fragment to be communicated
     * to the activity or parent fragment and potentially other fragments contained in that
     * activity or parent fragment.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentCompleteInteractionListener
            extends OnFragmentMinimalInteractionListener {
        void onAddressChanged(String address);

        void onMapFinishMove();
    }
}
