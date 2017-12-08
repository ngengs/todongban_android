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
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.adapters.HelpTypeAdapter;
import com.ngengs.skripsi.todongban.data.local.RequestHelp;
import com.ngengs.skripsi.todongban.utils.customviews.BottomSheetResponse;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PersonalRequestHelpFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalRequestHelpFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PersonalRequestHelpFragment extends Fragment
        implements MapFragment.OnFragmentCompleteInteractionListener {
    private static final String TAG = "PersonalRequestHelp";

    //    private GoogleMap mGoogleMap;
//    private GoogleApiClient mGoogleApiClient;
//    private Location mLocationNow;
    private TextView mLocationName;
    //    private Marker mMarker;
    private FloatingActionButton mMyLocationFab;
    //    private View mMarkerChangeLocationRoot;
//    private View mMarkerChangeLocationImage;
//    private View mMarkerChangeLocationIndicator;
    private MapFragment mMap;
    private Toolbar mToolbar;
    @SuppressWarnings("FieldCanBeLocal")
    private View mBottomSheetBehaviorView;
    private BottomSheetBehavior mBottomSheetBehavior;
    private ImageView mSelectableTypeMotorcycle;
    private ImageView mSelectableTypeCar;
    @SuppressWarnings("FieldCanBeLocal")
    private RecyclerView mRecyclerView;
    private HelpTypeAdapter mAdapter;
    private boolean mEditLocation;
    //    private boolean mProcessMovingCamera;
    private int mSelectedVehicle;
    private RequestHelp mRequestHelp;
    private Context mContext;

    private OnFragmentInteractionListener mListener;

    public PersonalRequestHelpFragment() {
        // Required empty public constructor
    }

    public static PersonalRequestHelpFragment newInstance() {
        return new PersonalRequestHelpFragment();
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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_personal_request_help, container, false);
        mContext = getContext();
        mLocationName = view.findViewById(R.id.maps_location_name);
        mMyLocationFab = view.findViewById(R.id.maps_my_location_button);
        mBottomSheetBehaviorView = view.findViewById(R.id.bottom_sheet_vehicle_wrapper);
        mBottomSheetBehavior = BottomSheetBehavior.from(mBottomSheetBehaviorView);
        mMyLocationFab.setOnClickListener(v -> {
            if (mMap != null) {
                mMap.setToMyLocation();
            }
        });
        mToolbar = view.findViewById(R.id.maps_location_toolbar);
        mSelectableTypeMotorcycle = view.findViewById(R.id.selectable_type_motorcycle);
        mSelectableTypeCar = view.findViewById(R.id.selectable_type_car);
        mSelectableTypeMotorcycle.setOnClickListener(this::selectableVehicleType);
        mSelectableTypeCar.setOnClickListener(this::selectableVehicleType);
        mBottomSheetBehavior.setBottomSheetCallback(
                new BottomSheetResponse(this::bottomSheetStateChanged, null));
        mMyLocationFab.setTranslationY(getResources().getDimension(
                R.dimen.bottom_sheet_vehicle_collapse));
        mMyLocationFab.setVisibility(View.GONE);
        mRecyclerView = view.findViewById(R.id.recycler_help_type);
        mAdapter = new HelpTypeAdapter();
        mAdapter.setOnClickListener(this::selectedHelpType);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setHasFixedSize(true);
        mRequestHelp = null;

        grayScaleVehicleImage();

//        mProcessMovingCamera = false;
        mEditLocation = false;
        mSelectedVehicle = 0;
//        locationChangeMarker();
        showBottomSheet(false);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mListener != null) {
            DrawerLayout mDrawerLayout = mListener.prepareDrawerLayoutForHelpRequest();
            ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(getActivity(),
                                                                            mDrawerLayout,
                                                                            mToolbar,
                                                                            R.string.navigation_drawer_open,
                                                                            R.string.navigation_drawer_close);
            mListener.syncDrawerLayoutForHelpRequest(mDrawerToggle);
//            mDrawerToggle.setToolbarNavigationClickListener(view -> {
//                if(mDrawerLayout.isDrawerOpen(GravityCompat.START)){
//                    mDrawerLayout.closeDrawer(GravityCompat.START);
//                } else {
//                    mDrawerLayout.openDrawer(GravityCompat.START);
//                }
//            });
            mDrawerToggle.syncState();
        }

        int paddingVertical = getResources().getDimensionPixelSize(
                R.dimen.bottom_sheet_vehicle_collapse);
        int paddingHorizontal = getResources().getDimensionPixelSize(
                R.dimen.margin_padding_default);
        mMap = MapFragment.newInstance(paddingVertical, paddingHorizontal);
        getChildFragmentManager().beginTransaction()
                                 .add(R.id.fragment_map_wrapper, mMap)
                                 .commit();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void selectedHelpType(String helpTypeId) {
        Log.d(TAG, "selectedHelpType() called with: helpTypeId = [" + helpTypeId + "]");
        showBottomSheet(false);
        mRequestHelp = new RequestHelp(helpTypeId);
        String helpConfirmationMessage = getString(
                R.string.help_confirmation_message, getString(mRequestHelp.getVehicleName()),
                getString(mRequestHelp.getHelpTypeName()));
        new MaterialDialog.Builder(mContext)
                .title(R.string.help_confirmation_title)
                .content(helpConfirmationMessage)
                .input(R.string.help_confirmation_message_hint, 0, true, (dialog, input) -> {

                })
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .onNegative((dialog, which) -> {
                    mRequestHelp = null;
                    showBottomSheet(true);
                    dialog.dismiss();
                })
                .onPositive((dialog, which) -> {
                    Log.d(TAG, "selectedHelpType: Select positive");
                    //noinspection ConstantConditions
                    mRequestHelp.setMessage(dialog.getInputEditText().getText().toString());
                    mRequestHelp.setLocationLatitude(mMap.getLatitude());
                    mRequestHelp.setLocationLongitude(mMap.getLongitude());
                    mRequestHelp.setLocationName(mLocationName.getText().toString());
                    dialog.dismiss();
                    if (mListener != null) {
                        Log.d(TAG, "selectedHelpType: " + mRequestHelp);
                        mListener.onHelpRequested(mRequestHelp);
                    }
                })
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .show();
    }

    private void grayScaleVehicleImage() {
        Log.d(TAG, "grayScaleVehicleImage() called");
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        mSelectableTypeCar.setColorFilter(filter);
        mSelectableTypeMotorcycle.setColorFilter(filter);
    }

    private void showMyLocationButton(boolean show) {
        Log.d(TAG, "showMyLocationButton() called with: show = [" + show + "]");
        if (show) {
            mMyLocationFab.animate().scaleX(1).scaleY(1).translationY(0).setDuration(150).start();
        } else {
            mMyLocationFab.animate().scaleX(0).scaleY(0).setDuration(150).start();
        }
    }

    private void selectableVehicleType(View view) {
        Log.d(TAG, "selectableVehicleType() called with: view = [" + view + "]");
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            showMyLocationButton(false);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }

        if (view.getId() == R.id.selectable_type_motorcycle) {
            mSelectedVehicle = RequestHelp.VEHICLE_TYPE_MOTORCYCLE;
        } else if (view.getId() == R.id.selectable_type_car) {
            mSelectedVehicle = RequestHelp.VEHICLE_TYPE_CAR;
        }

        grayScaleVehicleImage();
        if (view.getId() == mSelectableTypeMotorcycle.getId()) {
            mSelectableTypeMotorcycle.clearColorFilter();
        } else if (view.getId() == mSelectableTypeCar.getId()) {
            mSelectableTypeCar.clearColorFilter();
        }

        mAdapter.setVehicleType(mSelectedVehicle);
    }

    private void showBottomSheet(boolean show) {
        Log.d(TAG, "showBottomSheet() called with: show = [" + show + "]");
        if (mBottomSheetBehavior.isHideable() == show) {
            mBottomSheetBehavior.setHideable(!show);
        }
        if (show) {
            if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        } else {
            if (mBottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

    public boolean isEditLocation() { return mEditLocation; }

    public void cancelEditLocation() {
        this.mEditLocation = false;
        if (mMap != null) {
            mMap.cancelEditLocation();
        }
    }

    public boolean isBottomSheetNotCollapsed() {
        return mBottomSheetBehavior != null &&
               (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN);
    }

    public void setBottomSheetCollapsed() {
        if (mBottomSheetBehavior != null) {
            showBottomSheet(true);
        }
    }

    private void bottomSheetStateChanged(View bottomSheet, int newState) {
        Log.d(TAG, "bottomSheetStateChanged() called with: bottomSheet = [" + bottomSheet +
                   "], newState = [" + newState + "]");
        if (newState == BottomSheetBehavior.STATE_DRAGGING) {
            showMyLocationButton(false);
        } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
            if (mMap != null) {
                mMap.disableGesture();
            }
        } else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
            if (mMap != null) {
                mMap.enableGesture();
            }
            grayScaleVehicleImage();
            showMyLocationButton(true);
            mSelectedVehicle = 0;
            if (mAdapter != null) {
                mAdapter.setVehicleType(mSelectedVehicle);
            }
        } else if (newState == BottomSheetBehavior.STATE_HIDDEN) {
            mMyLocationFab.animate()
                          .translationY(getResources().getDimension(
                                  R.dimen.bottom_sheet_vehicle_collapse))
                          .setDuration(150)
                          .start();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onAddressChanged(String address) {
        Log.d(TAG, "onAddressChanged() called with: address = [" + address + "]");
        if (!TextUtils.isEmpty(address)) {
            mLocationName.setText(address);
        } else {
            mLocationName.setText("(" + mMap.getLatitude() + ", " + mMap.getLongitude() + ")");
        }
    }

    @Override
    public void onMapFinishMove() {
        Log.d(TAG, "onMapFinishMove() called");
        showBottomSheet(true);
    }

    @Override
    public void onMyLocationReady() {
        Log.d(TAG, "onMyLocationReady() called");
        if (mMyLocationFab.getVisibility() == View.GONE) {
            mMyLocationFab.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onProcessEdit() {
        Log.d(TAG, "onProcessEdit() called");
        mEditLocation = true;
        showBottomSheet(false);
    }

    @Override
    public void onFinishEdit() {
        Log.d(TAG, "onFinishEdit() called");
        mEditLocation = false;
        showBottomSheet(true);
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
        void onHelpRequested(RequestHelp requestHelp);

        DrawerLayout prepareDrawerLayoutForHelpRequest();

        void syncDrawerLayoutForHelpRequest(ActionBarDrawerToggle toggle);
    }
}
