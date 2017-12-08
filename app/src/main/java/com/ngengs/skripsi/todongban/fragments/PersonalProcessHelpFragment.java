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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.adapters.PeopleHelpAdapter;
import com.ngengs.skripsi.todongban.data.enumerations.Values;
import com.ngengs.skripsi.todongban.data.local.PeopleHelp;
import com.ngengs.skripsi.todongban.data.local.RequestHelp;
import com.ngengs.skripsi.todongban.data.local.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.zhanghai.android.materialprogressbar.CircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PersonalProcessHelpFragment#newInstance} factory method to
 * handle an instance of this fragment.
 */
@SuppressLint("ClickableViewAccessibility")
public class PersonalProcessHelpFragment extends Fragment {
    private static final String TAG = "PersonalProcessHelp";
    private static final String ARG_PARAM_REQUEST_HELP = "args_request";
    private static final String ARG_PARAM_ID_REQUEST_HELP = "args_id";
    private static final int SECOND_FOR_CANCEL = 4;

    private RequestHelp mRequestHelp;

    private ImageView mProcessWaitingVehicleIndicator;
    private ImageView mProcessWaitingTypeIndicator;
    private TextView mProcessWaitingText;
    private MaterialProgressBar mProcessWaitingProgress;
    private MaterialProgressBar mProcessWaitingProgressCancel;
    private FloatingActionButton mProcessWaitingCancel;
    private RecyclerView mRecyclerPersonalHelper;
    private RecyclerView mRecyclerGarageHelper;
    private PeopleHelpAdapter mAdapterPersonal;
    private PeopleHelpAdapter mAdapterGarage;
    private Button mButtonCancelProcess;
    private Toolbar mToolbarPeopleHelp;
    private NestedScrollView mPeopleHelpDataLayout;

    private Context mContext;
    private String mRequestId;
    private boolean mCancelProcess;
    private SharedPreferences mSharedPreferences;
    private Handler mHandler;
    private OnFragmentInteractionListener mListener;
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            int progress = mProcessWaitingProgressCancel.getProgress();
            if (progress < 100) {
                Log.d(TAG, "run: " + progress);
                mCancelProcess = false;
                mProcessWaitingProgressCancel.incrementProgressBy(100 / SECOND_FOR_CANCEL);
                mHandler.postDelayed(this, 1000);
            } else {
                Log.d(TAG, "run: cancel");
                mCancelProcess = true;
                Toast.makeText(mContext, "Proses pencarian bantuan dibatalkan",
                               Toast.LENGTH_SHORT).show();
                mListener.onProcessCancel(mRequestId);
            }
        }
    };

    public PersonalProcessHelpFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to handle a new instance of
     * this fragment using the provided parameters.
     * *
     *
     * @param requestHelp
     *         requestHelp
     * @param requestHelpId
     *         helpId
     *
     * @return A new instance of fragment PersonalProcessHelpFragment.
     */
    public static PersonalProcessHelpFragment newInstance(RequestHelp requestHelp,
                                                          String requestHelpId) {
        PersonalProcessHelpFragment fragment = new PersonalProcessHelpFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PARAM_REQUEST_HELP, requestHelp);
        args.putString(ARG_PARAM_ID_REQUEST_HELP, requestHelpId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mRequestHelp = getArguments().getParcelable(ARG_PARAM_REQUEST_HELP);
            mRequestId = getArguments().getString(ARG_PARAM_ID_REQUEST_HELP, null);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_personal_process_help, container, false);

        mContext = getContext();

        mCancelProcess = false;
        mProcessWaitingVehicleIndicator = view.findViewById(R.id.process_waiting_vehicle_indicator);
        mProcessWaitingTypeIndicator = view.findViewById(R.id.process_waiting_type_indicator);
        mProcessWaitingText = view.findViewById(R.id.process_waiting_text);
        mProcessWaitingProgress = view.findViewById(R.id.process_waiting_progress);
        mProcessWaitingProgressCancel = view.findViewById(R.id.process_waiting_progress_cancel);

        mRecyclerPersonalHelper = view.findViewById(R.id.recycler_personal_helper);
        mRecyclerGarageHelper = view.findViewById(R.id.recycler_garage_helper);
        mButtonCancelProcess = view.findViewById(R.id.button_cancel_process);
        mPeopleHelpDataLayout = view.findViewById(R.id.people_help_data_layout);
        mToolbarPeopleHelp = view.findViewById(R.id.toolbar_people_help);

        mProcessWaitingProgressCancel.setProgressDrawable(
                new CircularProgressDrawable(0, mContext));
        mProcessWaitingCancel = view.findViewById(R.id.process_waiting_cancel);
        mProcessWaitingVehicleIndicator.setImageResource(mRequestHelp.getVehicleIcon());
        mProcessWaitingTypeIndicator.setImageResource(mRequestHelp.getHelpTypeIcon());
        mProcessWaitingCancel.setOnClickListener(v -> {

        });

        mSharedPreferences = mContext.getSharedPreferences(Values.SHARED_PREFERENCES_NAME,
                                                           Context.MODE_PRIVATE);

        if (mHandler == null) {
            mHandler = new Handler();
        }
        mProcessWaitingCancel.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.d(TAG, "onCreateView: down");
                    v.performClick();
                    mProcessWaitingProgressCancel.setVisibility(View.VISIBLE);
                    mHandler.postDelayed(mRunnable, 1000);
                    return true;
                case MotionEvent.ACTION_UP:
                    Log.d(TAG, "onCreateView: cancel");
                    if (!mCancelProcess) {
                        Toast.makeText(mContext,
                                       "Tahan tombol untuk membatalkan proses pencarian",
                                       Toast.LENGTH_SHORT).show();
                        resetProgressCancel();
                    }
                    mHandler.removeCallbacks(mRunnable);
                    return true;
            }
            return false;
        });

        mButtonCancelProcess.setOnClickListener(v -> {
            if (mListener != null && !TextUtils.isEmpty(mRequestId)) {
                mListener.onProcessCancel(mRequestId);
            }
        });

        mAdapterPersonal = new PeopleHelpAdapter(mContext);
        mAdapterGarage = new PeopleHelpAdapter(mContext);
        mRecyclerPersonalHelper.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerGarageHelper.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerPersonalHelper.setAdapter(mAdapterPersonal);
        mRecyclerGarageHelper.setAdapter(mAdapterGarage);


        if (!TextUtils.isEmpty(mRequestId)) {
            setHelpRequestId(mRequestId);
        }
        loadDataFromLocal();


//        setHelpRequestId(null);
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setHelpRequestId(String helpRequestId) {
        mRequestId = helpRequestId;
        mProcessWaitingCancel.setVisibility(View.VISIBLE);
        resetProgressCancel();
    }

    public void resetProgressCancel() {
        mProcessWaitingProgress.setVisibility(View.GONE);
        mProcessWaitingProgressCancel.setVisibility(View.GONE);
        mProcessWaitingProgressCancel.setProgress(0);
    }

    private void toggleViewPeopleHelper() {
        int showPeopleHelper;
        int showWaiting;
        if (mAdapterPersonal.getItemCount() > 0 || mAdapterGarage.getItemCount() > 0) {
            showPeopleHelper = View.VISIBLE;
            showWaiting = View.GONE;
        } else {
            showPeopleHelper = View.GONE;
            showWaiting = View.VISIBLE;
        }

        mRecyclerPersonalHelper.setVisibility(showPeopleHelper);
        mRecyclerGarageHelper.setVisibility(showPeopleHelper);
        mButtonCancelProcess.setVisibility(showPeopleHelper);
        mPeopleHelpDataLayout.setVisibility(showPeopleHelper);
        mToolbarPeopleHelp.setVisibility(showPeopleHelper);

        mProcessWaitingVehicleIndicator.setVisibility(showWaiting);
        mProcessWaitingTypeIndicator.setVisibility(showWaiting);
        mProcessWaitingText.setVisibility(showWaiting);
        mProcessWaitingProgress.setVisibility(showWaiting);
        mProcessWaitingProgressCancel.setVisibility(showWaiting);

        if (!TextUtils.isEmpty(mRequestId)) {
            mProcessWaitingCancel.setVisibility(showWaiting);
        }
    }

    public void addData(PeopleHelp data) {
        if (data.getUserType() == User.TYPE_PERSONAL) {
            mAdapterPersonal.addData(data);
        } else {
            mAdapterGarage.addData(data);
        }
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(
                mContext);
        notificationManager.cancel(Values.NOTIFICATION_TAG_PEOPLE_HELP,
                                   Values.NOTIFICATION_ID_PEOPLE_HELP);
        Vibrator vibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(1000);
        }
        toggleViewPeopleHelper();
//        saveDataToLocal(data);
    }

    private void addData(List<PeopleHelp> data) {
        for (PeopleHelp item : data) {
            if (item.getUserType() == User.TYPE_PERSONAL) {
                mAdapterPersonal.addData(item);
            } else {
                mAdapterGarage.addData(item);
            }
        }
        toggleViewPeopleHelper();
    }

    private void loadDataFromLocal() {
        Gson gson = new Gson();
        String existPeopleHelpString = mSharedPreferences.getString(
                Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP, null);
        List<PeopleHelp> existPeopleHelp = new ArrayList<>();
        if (!TextUtils.isEmpty(existPeopleHelpString)) {
            PeopleHelp[] temp = gson.fromJson(existPeopleHelpString,
                                              PeopleHelp[].class);
            existPeopleHelp.addAll(Arrays.asList(temp));
        }
        addData(existPeopleHelp);
    }

//    private void saveDataToLocal(PeopleHelp data) {
//        Gson gson = new Gson();
//        List<PeopleHelp> saveData = new ArrayList<>();
//        saveData.addAll(mAdapterPersonal.getData());
//        saveData.addAll(mAdapterGarage.getData());
//        saveData.add(data);
//        mSharedPreferences.edit()
//                          .putString(Values.SHARED_PREFERENCES_KEY_PEOPLE_HELP,
//                                     gson.toJson(saveData))
//                          .apply();
//    }


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
        void onProcessCancel(String requestId);
    }

}
