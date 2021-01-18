package com.example.fcm_tour.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fcm_tour.Controllers.Preferences;
import com.example.fcm_tour.Controllers.Users;
import com.example.fcm_tour.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.w3c.dom.Text;

import java.util.Set;

public class ChangePwPage extends Fragment {
    static View v;
    TextInputEditText currentPassword, newPassword, confirmPassword;
    TextInputLayout currentPasswordTxt, newPasswordTxt, confirmPasswordTxt;
    Button changePwBtn, addPwBtn;
    String userType;
    static FragmentTransaction ft;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Preferences.init(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_change_pw_page, container, false);
        ft = getParentFragmentManager().beginTransaction();
        userType = Preferences.readUserType();
        currentPasswordTxt = v.findViewById(R.id.currentPWTxt);
        currentPassword = v.findViewById(R.id.currentPW);
        newPasswordTxt = v.findViewById(R.id.newPWTxt);
        newPassword = v.findViewById(R.id.newPW);
        confirmPasswordTxt = v.findViewById(R.id.confirmPWTxt);
        confirmPassword = v.findViewById(R.id.confirmPW);
        changePwBtn = v.findViewById(R.id.changePW);
        addPwBtn = v.findViewById(R.id.addPW);
        displayLayout();

        newPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comparePasswords();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        confirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                comparePasswords();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        changePwBtn.setOnClickListener(v -> {
            if (Users.isPasswordValid(newPassword.getText().toString())) {
                Users.changePw(Preferences.readUserEmail(), currentPassword.getText().toString(), newPassword.getText().toString(), getContext());
            } else {
                confirmPassword.setText("");
                Toast.makeText(getContext(), R.string.passwordErrorMsg, Toast.LENGTH_SHORT).show();
            }
        });

        addPwBtn.setOnClickListener(v -> {
            if (Users.isPasswordValid(newPassword.getText().toString())) {
                Users.addPw(Preferences.readUserEmail(), newPassword.getText().toString(), getContext());
            } else {
                confirmPassword.setText("");
                Toast.makeText(getContext(), R.string.passwordErrorMsg, Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    public void displayLayout() {
        if (userType.equals("2") || userType.equals("3") || userType.equals("7")) {
            addPwBtn.setVisibility(View.VISIBLE);
            changePwBtn.setVisibility(View.GONE);
            currentPasswordTxt.setVisibility(View.GONE);
            currentPassword.setVisibility(View.GONE);
        } else {
            changePwBtn.setVisibility(View.VISIBLE);
            addPwBtn.setVisibility(View.GONE);
            currentPasswordTxt.setVisibility(View.VISIBLE);
            currentPassword.setVisibility(View.VISIBLE);
        }
    }

    public void comparePasswords() {
        if (newPassword.getText().toString().equals(confirmPassword.getText().toString()) && !(newPassword.getText().toString().equals(""))) {
            changePwBtn.setEnabled(true);
            addPwBtn.setEnabled(true);
        } else {
            changePwBtn.setEnabled(false);
            addPwBtn.setEnabled(false);
        }
    }

    public static void pwChangedSuccess() {
        final int homeContainer = R.id.fullpage;
        SettingsPage settingsPage = new SettingsPage();
        openFragment(settingsPage, homeContainer);
    }

    private static void openFragment(SettingsPage settingsPage, int homeContainer) {
        ft.replace(homeContainer, settingsPage);
        ft.commit();
    }
}