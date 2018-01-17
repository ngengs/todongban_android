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
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RatingBar;
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
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.data.local.UserWithLocation;
import com.ngengs.skripsi.todongban.data.remote.ResponseDetail;
import com.ngengs.skripsi.todongban.utils.ResourceUtils;
import com.ngengs.skripsi.todongban.utils.networks.API;
import com.ngengs.skripsi.todongban.utils.networks.ApiResponse;
import com.ngengs.skripsi.todongban.utils.networks.NetworkHelpers;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Response;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalResponseHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalResponseHelpFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_REQUEST = "id_request";

    private Context mContext;
    private MaterialDialog mDialog;
    private String mRequestId;
    private API mApi;
    private UserWithLocation mData;
    private GoogleMap mGoogleMap;

    private OnFragmentInteractionListener mListener;
    private CircleImageView mResponseDetailAvatar;
    private TextView mResponseDetailFullName;
    private TextView mResponseDetailTextPhone;
    private TextView mResponseDetailTextEmail;
    private ImageButton mResponseDetailButtonCall;
    private ImageButton mResponseDetailButtonEmail;
    private Button mResponseDetailButtonFinish;
    private Button mResponseDetailButtonSubmit;
    private RatingBar mResponseDetailRating;
    private BottomSheetDialog mRatingDialog;

    public PersonalResponseHelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param requestId
     *         Parameter 1.
     *
     * @return A new instance of fragment PersonalResponseHelpFragment.
     */
    public static PersonalResponseHelpFragment newInstance(String requestId) {
        PersonalResponseHelpFragment fragment = new PersonalResponseHelpFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM_REQUEST, requestId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequestId = getArguments().getString(ARG_PARAM_REQUEST);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_personal_response_help, container, false);
        View sheetView = inflater.inflate(R.layout.bottom_sheet_rating, container, false);
        mContext = getContext();
        mDialog = new MaterialDialog.Builder(mContext)
                .title("Memprosess")
                .canceledOnTouchOutside(false)
                .progress(true, 0)
                .autoDismiss(false)
                .build();
        mApi = NetworkHelpers.provideAPI(mContext);
        // Inflate the layout for this fragment
        initView(view, sheetView);
        getDetailHelper();
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getDetailHelper() {
        mDialog.show();
        mApi.responseDetail(mRequestId)
            .enqueue(new ApiResponse<>(this::getDetailSuccess, this::getDetailFailure));
    }

    @SuppressWarnings("ConstantConditions")
    private void getDetailSuccess(Response<ResponseDetail> response) {
        Timber.d("getDetailSuccess() called with: response = [ %s ]", response);
        mData = response.body().getData();
        if (mData.getType() == User.TYPE_GARAGE) {
            mResponseDetailFullName.setText(mData.getGarageName());
        } else {
            mResponseDetailFullName.setText(mData.getFullName());
        }
        mResponseDetailTextPhone.setText(mData.getPhone());
        mResponseDetailTextEmail.setText(mData.getEmail());
        Picasso.with(mContext)
               .load(mData.getAvatar())
               .resize(100, 100)
               .centerCrop()
               .into(mResponseDetailAvatar);
        mDialog.dismiss();
        initMaps();
    }

    private void getDetailFailure(Throwable t) {
        Timber.e(t, "getDetailFailure: ");
        Toast.makeText(mContext, "Terjadi kesalahan pada server", Toast.LENGTH_SHORT).show();
    }

    private void initMaps() {
        Timber.d("initMaps() called");
        mDialog.show();
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this::onMapReady);
    }

    private void onMapReady(GoogleMap googleMap) {
        Timber.d("onMapReady() called with: googleMap = [ %s ]", googleMap);
        mGoogleMap = googleMap;

        mGoogleMap = googleMap;
        mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mGoogleMap.setTrafficEnabled(false);
        mGoogleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(mContext, R.raw.maps_style));
        mGoogleMap.getUiSettings().setCompassEnabled(false);
        mGoogleMap.getUiSettings().setTiltGesturesEnabled(false);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap.getUiSettings().setAllGesturesEnabled(false);
        mGoogleMap.setMinZoomPreference(17.0f);
        mGoogleMap.setOnMapClickListener(latLng -> mapClicked());
        LatLng latLng = new LatLng(mData.getLatitude(), mData.getLongitude());
        int markerIcon = (mData.getType() == User.TYPE_PERSONAL)
                         ? MapFragment.MARKER_ICON_HELP : MapFragment.MARKER_ICON_GARAGE;
        mGoogleMap.addMarker(
                new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromBitmap(
                        ResourceUtils.getBitmapFromVectorDrawable(mContext, markerIcon))));
        mGoogleMap.setOnMarkerClickListener(marker -> mapClicked());
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mDialog.dismiss();
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

    private void callTheHelper() {
        Timber.d("callTheHelper() called");
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mData.getPhone()));
        startActivity(Intent.createChooser(intent, "Telepon dengan..."));
    }

    private void emailTheHelper() {
        Timber.d("emailTheHelper() called");
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", mData.getEmail(), null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Permintaan Bantuan");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{mData.getEmail()});
        startActivity(Intent.createChooser(emailIntent, "Kirim Email dengan..."));
    }

    private void submit(int rating){
        if (mListener != null) {
            mListener.onFinishHelpProcess(mRequestId, rating);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void initView(View view, View sheetView) {
        mResponseDetailAvatar = view.findViewById(R.id.response_detail_avatar);
        mResponseDetailFullName = view.findViewById(R.id.response_detail_full_name);
        mResponseDetailTextPhone = view.findViewById(R.id.response_detail_text_phone);
        mResponseDetailTextEmail = view.findViewById(R.id.response_detail_text_email);
        mResponseDetailButtonCall = view.findViewById(R.id.response_detail_button_call);
        mResponseDetailButtonEmail = view.findViewById(R.id.response_detail_button_email);
        mResponseDetailButtonFinish = view.findViewById(R.id.response_detail_button_finish);

        mResponseDetailButtonFinish.setOnClickListener(v -> mRatingDialog.show());
        mResponseDetailButtonEmail.setOnClickListener(v -> emailTheHelper());
        mResponseDetailButtonCall.setOnClickListener(v -> callTheHelper());

        mRatingDialog = new BottomSheetDialog(mContext);
        mRatingDialog.setContentView(sheetView);
        mResponseDetailButtonSubmit = mRatingDialog.findViewById(R.id.bottom_sheet_button_submit);
        mResponseDetailRating = mRatingDialog.findViewById(R.id.bottom_sheet_rating);
        mResponseDetailRating.setMax(5);
        mResponseDetailButtonSubmit.setOnClickListener(v -> {
            int rating = (int) mResponseDetailRating.getRating();
            if (rating <= 0) {
                Toast.makeText(mContext, "Berikan rating terlebih dahulu", Toast.LENGTH_SHORT)
                     .show();
                return;
            }
            mResponseDetailRating.setRating(0);
            mRatingDialog.dismiss();
            submit(rating);
        });
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
        void onFinishHelpProcess(String requestId, int rating);
    }
}
