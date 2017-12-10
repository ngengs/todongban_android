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

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioButton;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ngengs.skripsi.todongban.R;
import com.ngengs.skripsi.todongban.SignupActivity;
import com.ngengs.skripsi.todongban.data.local.User;
import com.ngengs.skripsi.todongban.utils.ImageUtils;
import com.ngengs.skripsi.todongban.utils.ResourceUtils;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentSignupBasicInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SignupBasicFragment#newInstance} factory method to
 * handle an instance of this fragment.
 */
public class SignupBasicFragment extends Fragment {

    private static final String ARG_PARAM = "data";
    private static final int REQUEST_CODE_CHOOSE_PROFILE = 10;
    private static final int REQUEST_CODE_CHOOSE_PROFILE_CAMERA = 11;
    private static final int REQUEST_CODE_CHOOSE_IDENTITY = 20;
    private static final int REQUEST_CODE_CHOOSE_IDENTITY_CAMERA = 21;
    private TextInputEditText mInputSignupBaseName;
    private TextInputEditText mInputSignupBaseUsername;
    private TextInputEditText mInputSignupBaseEmail;
    private TextInputEditText mInputSignupBasePassword;
    private TextInputEditText mInputSignupBaseBirthDate;
    private TextInputEditText mInputSignupBasePhone;
    private TextInputEditText mInputSignupBaseAddress;
    private CircleImageView mImageSignupIdentity;
    @SuppressWarnings("FieldCanBeLocal")
    private View mInputSignupBaseIdentityLayout;
    private TextInputEditText mInputSignupBaseIdentity;
    private CircleImageView mImageSignupProfile;
    @SuppressWarnings("FieldCanBeLocal")
    private Button mButtonSignupBasicNext;
    private RadioButton mRadioGenderMan;
    private RadioButton mRadioGenderWoman;
    private Uri mProfileImage;
    private Uri mIdentityImage;

    private User mUser;
    private Context mContext;

    private OnFragmentSignupBasicInteractionListener mListener;
    private String imagePath;
    private boolean mAlreadyLoaded;

    public SignupBasicFragment() {
        // Required empty public constructor
    }

    public static SignupBasicFragment newInstance(User user) {
        SignupBasicFragment fragment = new SignupBasicFragment();
        Bundle args = new Bundle();
        if (user != null) {
            args.putParcelable(ARG_PARAM, user);
        }
        fragment.setArguments(args);
        return fragment;
    }

    private void onButtonNextClicked() {
        //noinspection ConstantConditions
        InputMethodManager inputMethodManager
                = ((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE));
        //noinspection ConstantConditions
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(),
                                                   InputMethodManager.HIDE_NOT_ALWAYS);
        if (mListener != null) {
            String name = mInputSignupBaseName.getText().toString();
            String username = mInputSignupBaseUsername.getText().toString();
            String email = mInputSignupBaseEmail.getText().toString();
            String password = mInputSignupBasePassword.getText().toString();
            String phone = mInputSignupBasePhone.getText().toString();
            String address = mInputSignupBaseAddress.getText().toString();
            String birthDateString = mInputSignupBaseBirthDate.getText().toString();
            String identityNumber = mInputSignupBaseIdentity.getText().toString();
            Date birthDate = null;
            int gender = 0;

            if (mRadioGenderMan.isChecked()) gender = User.GENDER_MALE;
            else if (mRadioGenderWoman.isChecked()) gender = User.GENDER_FEMALE;

            // Clear error indicator
            mInputSignupBaseUsername.setError(null);
            mInputSignupBaseEmail.setError(null);
            mInputSignupBasePhone.setError(null);

            Timber.d("onButtonNextClicked: name: %s", name);
            Timber.d("onButtonNextClicked: username: %s", username);
            Timber.d("onButtonNextClicked: email: %s", email);
            Timber.d("onButtonNextClicked: password: %s", password);
            Timber.d("onButtonNextClicked: phone: %s", phone);
            Timber.d("onButtonNextClicked: address: %s", address);
            Timber.d("onButtonNextClicked: birthDate: %s", birthDateString);
            Timber.d("onButtonNextClicked: gender: %s", gender);
            Timber.d("onButtonNextClicked: identityNumber: %s", identityNumber);

            int errorCode = 0;

            boolean canRun = false;
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(username) &&
                !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                !TextUtils.isEmpty(phone) && !TextUtils.isEmpty(address) &&
                !TextUtils.isEmpty(birthDateString) && !TextUtils.isEmpty(identityNumber) &&
                gender != 0 && mProfileImage != null && mIdentityImage != null) {
                boolean validEmail = Patterns.EMAIL_ADDRESS.matcher(email).matches();
                if (!validEmail) errorCode += 1;
                Timber.d("onButtonNextClicked: Valid Email address: %s", validEmail);
                boolean validPhone = Patterns.PHONE.matcher(phone).matches();
                Timber.d("onButtonNextClicked: Valid Phone number: %s", validPhone);
                if (!validPhone) errorCode += 10;
                boolean validUsername = Pattern.compile("^[a-z0-9_-]{3,15}$")
                                               .matcher(username)
                                               .matches();
                if (!validUsername) errorCode += 100;
                Timber.d("onButtonNextClicked: Valid Username: %s", validUsername);
                boolean validPassword = (password.length() >= 6);
                if (!validPassword) errorCode += 1000;
                Timber.d("onButtonNextClicked: Valid Password: %s", validPassword);


                try {
                    DateFormat format = DateFormat.getDateInstance();
                    birthDate = format.parse(birthDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                canRun = (validEmail && validPhone && validUsername && validPassword &&
                          (birthDate != null));
            }

            if (canRun) {
                phone = phone.replace("+62", "0");
                if (phone.substring(0, 2).equalsIgnoreCase("62")) {
                    phone = "0" + phone.substring(2, phone.length() - 1);
                }
                Timber.d("onButtonNextClicked: phone: %s", phone);
                mUser.setUsername(username);
                mUser.setFullName(name);
                mUser.setPhone(phone);
                mUser.setEmail(email);
                mUser.setPasswordClean(password);
                mUser.setStatus(User.STATUS_DEACTIVE);
                mUser.setGender(gender);
                mUser.setIdentityNumber(identityNumber);
                mUser.setBirthDate(birthDate);
                mUser.setAvatarUri(mProfileImage);
                mUser.setIdentityUri(mIdentityImage);
                mUser.setAddress(address);
                mListener.onButtonBasicNextClicked(mUser);
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Tidak dapat melanjutkan: ");
                if (errorCode == 0) {
                    stringBuilder.append("Data tidak lengkap");
                } else {
                    stringBuilder.append("Terdapat kesalahan format pada ");
                    if (errorCode - 1000 >= 0) {
                        stringBuilder.append("Password ");
                        mInputSignupBasePassword.setError("Panjang password minimal 6");
                        errorCode -= 1000;
                    }
                    if (errorCode - 100 >= 0) {
                        stringBuilder.append("Username ");
                        mInputSignupBaseUsername.setError(
                                "Format username hanya menerima huruf angka _ dengan panjang 3-15");
                        errorCode -= 100;
                    }
                    if (errorCode - 10 >= 0) {
                        stringBuilder.append("Telepon ");
                        mInputSignupBasePhone.setError("Format telpon hanya berupa angka");
                        errorCode -= 10;
                    }
                    if (errorCode - 1 >= 0) {
                        stringBuilder.append("Email ");
                        mInputSignupBaseEmail.setError("Format email tidak tepat");
                        //noinspection UnusedAssignment
                        errorCode -= 1;
                    }

                }
                Timber.d("onButtonNextClicked: Cant next: %s", stringBuilder);
                Snackbar.make(mImageSignupProfile,
                              stringBuilder.toString(),
                              Snackbar.LENGTH_SHORT).show();
            }

        } else {
            throw new RuntimeException("must implement OnFragmentSignupBasicInteractionListener");
        }
    }

    private void onInputSignupBaseBirthDateClicked() {
        DatePickerDialog dpd = DatePickerDialog.newInstance(
                (DatePickerDialog viewPicker, int year, int monthOfYear, int dayOfMonth) -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, monthOfYear, dayOfMonth);
                    DateFormat format = DateFormat.getDateInstance();
                    String formatedDate = format.format(calendar.getTime());
                    Timber.d("onInputSignupBaseBirthDateClicked: %s", formatedDate);
                    mInputSignupBaseBirthDate.setText(formatedDate);
                    viewPicker.dismiss();
                });
        dpd.dismissOnPause(true);
        dpd.show(getChildFragmentManager(), "DatePickerDialogBirthDate");
    }

    private void onLayoutImageSignupIdentityClicked() {
        //noinspection ConstantConditions
        new MaterialDialog.Builder(mContext)
                .title("Gambar Untuk Kartu Identitas")
                .items("Kamera", "Gallery")
                .itemsCallback(
                        (MaterialDialog dialog, View itemView, int position, CharSequence text) -> {
                            if (position == 0) {
                                File photoFile = null;
                                try {
                                    photoFile = ImageUtils.createImageFile(mContext);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (photoFile != null) {
                                    imagePath = photoFile.getAbsolutePath();
                                    Intent cameraIntent = ImageUtils.openCamera(mContext,
                                                                                photoFile);
                                    startActivityForResult(
                                            Intent.createChooser(cameraIntent, "Pilih Gallery"),
                                            REQUEST_CODE_CHOOSE_IDENTITY_CAMERA);
                                } else {
                                    Timber.d(
                                            "onLayoutImageSignupIdentityClicked: Fail handle image for camera result");
                                    Snackbar.make(mImageSignupProfile,
                                                  "Gagal membuat file untuk hasil kamera",
                                                  Snackbar.LENGTH_SHORT).show();
                                }
                            } else if (position == 1) {
                                Intent galleryIntent = ImageUtils.openGallery();
                                if (galleryIntent.resolveActivity(
                                        mContext.getPackageManager()) != null) {
                                    startActivityForResult(
                                            Intent.createChooser(galleryIntent, "Pilih Gallery"),
                                            REQUEST_CODE_CHOOSE_IDENTITY);
                                }
                            }
                            dialog.dismiss();
                        })
                .autoDismiss(false)
                .show();
    }

    private void onImageSignupProfileClicked() {
        //noinspection ConstantConditions
        new MaterialDialog.Builder(mContext)
                .title("Gambar Untuk Profil Anda")
                .items("Kamera", "Gallery")
                .itemsCallback(
                        (MaterialDialog dialog, View itemView, int position, CharSequence text) -> {
                            if (position == 0) {
                                File photoFile = null;
                                try {
                                    photoFile = ImageUtils.createImageFile(mContext);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                if (photoFile != null) {
                                    imagePath = photoFile.getAbsolutePath();
                                    Intent cameraIntent = ImageUtils.openCamera(mContext,
                                                                                photoFile);
                                    startActivityForResult(
                                            Intent.createChooser(cameraIntent, "Pilih Gallery"),
                                            REQUEST_CODE_CHOOSE_PROFILE_CAMERA);
                                } else {
                                    Timber.d(
                                            "onLayoutImageSignupIdentityClicked: Fail handle image for camera result");
                                    Snackbar.make(mImageSignupProfile,
                                                  "Gagal membuat file untuk hasil kamera",
                                                  Snackbar.LENGTH_SHORT).show();
                                }
                            } else if (position == 1) {
                                Intent galleryIntent = ImageUtils.openGallery();
                                if (galleryIntent.resolveActivity(
                                        mContext.getPackageManager()) != null) {
                                    startActivityForResult(
                                            Intent.createChooser(galleryIntent, "Pilih Gallery"),
                                            REQUEST_CODE_CHOOSE_PROFILE);
                                }
                            }
                            dialog.dismiss();
                        })
                .autoDismiss(false)
                .show();
    }

    private void initView(View view) {
        mInputSignupBaseName = view.findViewById(R.id.inputSignupBaseName);
        mInputSignupBaseUsername = view.findViewById(R.id.inputSignupBaseUsername);
        mInputSignupBaseEmail = view.findViewById(R.id.inputSignupBaseEmail);
        mInputSignupBasePassword = view.findViewById(R.id.inputSignupBasePassword);
        mInputSignupBaseBirthDate = view.findViewById(R.id.inputSignupBaseBirthDate);
        mInputSignupBasePhone = view.findViewById(R.id.inputSignupBasePhone);
        mInputSignupBaseAddress = view.findViewById(R.id.inputSignupBaseAddress);
        mImageSignupIdentity = view.findViewById(R.id.imageSignupIdentity);
        mInputSignupBaseIdentityLayout = view.findViewById(R.id.inputLayoutSignupBaseIdentity);
        mInputSignupBaseIdentity = view.findViewById(R.id.inputSignupBaseIdentity);
        mImageSignupProfile = view.findViewById(R.id.imageSignupProfile);
        mButtonSignupBasicNext = view.findViewById(R.id.buttonSignupBasicNext);
        mRadioGenderMan = view.findViewById(R.id.radioGenderMan);
        mRadioGenderWoman = view.findViewById(R.id.radioGenderWoman);

        // Click action builder
        mImageSignupProfile.setOnClickListener(v -> onImageSignupProfileClicked());
        mImageSignupIdentity.setOnClickListener(v -> onLayoutImageSignupIdentityClicked());
        mInputSignupBaseIdentityLayout.setOnClickListener(
                v -> onLayoutImageSignupIdentityClicked());
        mInputSignupBaseBirthDate.setOnClickListener(v -> onInputSignupBaseBirthDateClicked());
        mButtonSignupBasicNext.setOnClickListener(v -> onButtonNextClicked());
        mImageSignupProfile.setImageBitmap(ResourceUtils.getBitmapFromVectorDrawable(mContext, R.drawable.ic_avatar));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_CHOOSE_IDENTITY && resultCode == Activity.RESULT_OK &&
            data != null) {
            try {
                //noinspection ConstantConditions
                Object[] result = ImageUtils.handleImageGallery(mContext, data);
                String uri = (String) result[0];
                mIdentityImage = Uri.parse(uri);
                Bitmap bitmap = (Bitmap) result[1];
                mImageSignupIdentity.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_IDENTITY_CAMERA &&
                   resultCode == Activity.RESULT_OK) {
            if (imagePath != null) {
                ImageUtils.notifyGallery(mContext, imagePath);
                mIdentityImage = Uri.parse(imagePath);
                Bitmap bitmap = ImageUtils.handleImageCamera(imagePath,
                                                             mImageSignupIdentity.getWidth());
                mImageSignupIdentity.setImageBitmap(bitmap);
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_PROFILE && resultCode == Activity.RESULT_OK &&
                   data != null) {
            try {
                //noinspection ConstantConditions
                Object[] result = ImageUtils.handleImageGallery(mContext, data);
                String uri = (String) result[0];
                mProfileImage = Uri.parse(uri);
                Bitmap bitmap = (Bitmap) result[1];
                mImageSignupProfile.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == REQUEST_CODE_CHOOSE_PROFILE_CAMERA &&
                   resultCode == Activity.RESULT_OK) {
            if (imagePath != null) {
                ImageUtils.notifyGallery(mContext, imagePath);
                mProfileImage = Uri.parse(imagePath);
                Bitmap bitmap = ImageUtils.handleImageCamera(imagePath,
                                                             mImageSignupProfile.getWidth());
                mImageSignupProfile.setImageBitmap(bitmap);
            }
        }

        if (requestCode == REQUEST_CODE_CHOOSE_IDENTITY_CAMERA ||
            requestCode == REQUEST_CODE_CHOOSE_PROFILE_CAMERA) {
            if (resultCode != Activity.RESULT_OK) {
                ImageUtils.deleteFailedImage(imagePath);
            }
        }
        imagePath = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentSignupBasicInteractionListener) {
            mListener = (OnFragmentSignupBasicInteractionListener) context;
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
            mUser = getArguments().getParcelable(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_signup_basic, container, false);
        mContext = getContext();

        initView(view);
        SignupActivity mSignupActivity = (SignupActivity) getActivity();
        if (mUser != null) bindOldData();
        else mUser = new User();
        if (mSignupActivity != null) mSignupActivity.setAppTitle(R.string.title_signup);
        Timber.d("onCreateView: alreadyLoaded: %s", mAlreadyLoaded);
        if (mAlreadyLoaded) bindOldImage();
        if (savedInstanceState == null && !mAlreadyLoaded) mAlreadyLoaded = true;
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

    private void bindOldImage() {
        if (mAlreadyLoaded && mUser != null) {
            try {
                //noinspection ConstantConditions
                Bitmap identityBitmap = ImageUtils.handleImageUri(mContext, mUser.getIdentityUri());
                Bitmap profileBitmap = ImageUtils.handleImageUri(mContext, mUser.getAvatarUri());
                mImageSignupIdentity.setImageBitmap(identityBitmap);
                mImageSignupProfile.setImageBitmap(profileBitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void bindOldData() {

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
    public interface OnFragmentSignupBasicInteractionListener {
        void onButtonBasicNextClicked(User userData);
    }
}
