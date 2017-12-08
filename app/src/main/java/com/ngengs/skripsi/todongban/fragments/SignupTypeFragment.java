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
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.SignupActivity;
import com.ngengs.skripsi.todongban.data.local.User;

import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SignupTypeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SignupTypeFragment extends Fragment {
    public static final String ARG_USER = "user";
    private User mUser;
    private OnFragmentTypeInteractionListener mListener;


    public SignupTypeFragment() {
        // Required empty public constructor
    }

    public static SignupTypeFragment newInstance(User user) {
        SignupTypeFragment fragment = new SignupTypeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SignupTypeFragment.OnFragmentTypeInteractionListener) {
            mListener = (SignupTypeFragment.OnFragmentTypeInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                                       + " must implement OnFragmentTypeInteractionListener");
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_type, container, false);
        initLayout(view);
        SignupActivity activity = (SignupActivity) getActivity();
        Timber.d("onCreateView: activity: %s", activity);
        if (activity != null) activity.setAppTitle(R.string.title_signup_as);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initLayout(View view) {
        Button mButtonSignupTypePersonal = view.findViewById(R.id.buttonSignupTypePersonal);
        Button mButtonSignupTypeGarage = view.findViewById(R.id.buttonSignupTypeGarage);

        mButtonSignupTypePersonal.setOnClickListener(v -> onButtonSignupTypePersonalClicked());
        mButtonSignupTypeGarage.setOnClickListener(v -> onButtonSignupTypeGarageClicked());
    }

    public void onButtonSignupTypePersonalClicked() {
        mUser.setType(User.TYPE_PERSONAL);
        if (mListener != null) mListener.onButtonTypeClicked(mUser);
        else throw new RuntimeException("Cant handle listener");
    }

    private void onButtonSignupTypeGarageClicked() {
        mUser.setType(User.TYPE_GARAGE);
        if (mListener != null) mListener.onButtonTypeClicked(mUser);
        else throw new RuntimeException("Cant handle listener");
    }

    public interface OnFragmentTypeInteractionListener {
        void onButtonTypeClicked(User userData);
    }

}
