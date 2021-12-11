// Generated by view binder compiler. Do not edit!
package com.gachon.ask.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import com.gachon.ask.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivitySignupBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button btnGoLogin;

  @NonNull
  public final Button btnSignup;

  @NonNull
  public final EditText etEmail;

  @NonNull
  public final EditText etNickname;

  @NonNull
  public final EditText etPassword;

  @NonNull
  public final EditText etPasswordCheck;

  private ActivitySignupBinding(@NonNull LinearLayout rootView, @NonNull Button btnGoLogin,
      @NonNull Button btnSignup, @NonNull EditText etEmail, @NonNull EditText etNickname,
      @NonNull EditText etPassword, @NonNull EditText etPasswordCheck) {
    this.rootView = rootView;
    this.btnGoLogin = btnGoLogin;
    this.btnSignup = btnSignup;
    this.etEmail = etEmail;
    this.etNickname = etNickname;
    this.etPassword = etPassword;
    this.etPasswordCheck = etPasswordCheck;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivitySignupBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivitySignupBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_signup, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivitySignupBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.btn_go_login;
      Button btnGoLogin = rootView.findViewById(id);
      if (btnGoLogin == null) {
        break missingId;
      }

      id = R.id.btn_signup;
      Button btnSignup = rootView.findViewById(id);
      if (btnSignup == null) {
        break missingId;
      }

      id = R.id.et_email;
      EditText etEmail = rootView.findViewById(id);
      if (etEmail == null) {
        break missingId;
      }

      id = R.id.et_nickname;
      EditText etNickname = rootView.findViewById(id);
      if (etNickname == null) {
        break missingId;
      }

      id = R.id.et_password;
      EditText etPassword = rootView.findViewById(id);
      if (etPassword == null) {
        break missingId;
      }

      id = R.id.et_password_check;
      EditText etPasswordCheck = rootView.findViewById(id);
      if (etPasswordCheck == null) {
        break missingId;
      }

      return new ActivitySignupBinding((LinearLayout) rootView, btnGoLogin, btnSignup, etEmail,
          etNickname, etPassword, etPasswordCheck);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}