// Generated by view binder compiler. Do not edit!
package com.example.carswitcherapp.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.carswitcherapp.R;
import com.example.carswitcherapp.common.view.MyCheckImageView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityMainBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final MyCheckImageView ivAcceleratorInterlock;

  @NonNull
  public final MyCheckImageView ivAdas;

  @NonNull
  public final MyCheckImageView ivAebs;

  @NonNull
  public final MyCheckImageView ivDomeLamp;

  @NonNull
  public final MyCheckImageView ivEvModel;

  @NonNull
  public final MyCheckImageView ivExAmbient;

  @NonNull
  public final MyCheckImageView ivFcuDegas;

  @NonNull
  public final MyCheckImageView ivIcmButtonBack;

  @NonNull
  public final MyCheckImageView ivIcmButtonDown;

  @NonNull
  public final MyCheckImageView ivIcmButtonMenu;

  @NonNull
  public final MyCheckImageView ivIcmButtonUp;

  @NonNull
  public final MyCheckImageView ivInAmbient;

  @NonNull
  public final MyCheckImageView ivSteeringWheelAdjust;

  @NonNull
  public final MyCheckImageView ivVolumeDown;

  @NonNull
  public final MyCheckImageView ivVolumeUp;

  private ActivityMainBinding(@NonNull LinearLayout rootView,
      @NonNull MyCheckImageView ivAcceleratorInterlock, @NonNull MyCheckImageView ivAdas,
      @NonNull MyCheckImageView ivAebs, @NonNull MyCheckImageView ivDomeLamp,
      @NonNull MyCheckImageView ivEvModel, @NonNull MyCheckImageView ivExAmbient,
      @NonNull MyCheckImageView ivFcuDegas, @NonNull MyCheckImageView ivIcmButtonBack,
      @NonNull MyCheckImageView ivIcmButtonDown, @NonNull MyCheckImageView ivIcmButtonMenu,
      @NonNull MyCheckImageView ivIcmButtonUp, @NonNull MyCheckImageView ivInAmbient,
      @NonNull MyCheckImageView ivSteeringWheelAdjust, @NonNull MyCheckImageView ivVolumeDown,
      @NonNull MyCheckImageView ivVolumeUp) {
    this.rootView = rootView;
    this.ivAcceleratorInterlock = ivAcceleratorInterlock;
    this.ivAdas = ivAdas;
    this.ivAebs = ivAebs;
    this.ivDomeLamp = ivDomeLamp;
    this.ivEvModel = ivEvModel;
    this.ivExAmbient = ivExAmbient;
    this.ivFcuDegas = ivFcuDegas;
    this.ivIcmButtonBack = ivIcmButtonBack;
    this.ivIcmButtonDown = ivIcmButtonDown;
    this.ivIcmButtonMenu = ivIcmButtonMenu;
    this.ivIcmButtonUp = ivIcmButtonUp;
    this.ivInAmbient = ivInAmbient;
    this.ivSteeringWheelAdjust = ivSteeringWheelAdjust;
    this.ivVolumeDown = ivVolumeDown;
    this.ivVolumeUp = ivVolumeUp;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.iv_accelerator_interlock;
      MyCheckImageView ivAcceleratorInterlock = ViewBindings.findChildViewById(rootView, id);
      if (ivAcceleratorInterlock == null) {
        break missingId;
      }

      id = R.id.iv_adas;
      MyCheckImageView ivAdas = ViewBindings.findChildViewById(rootView, id);
      if (ivAdas == null) {
        break missingId;
      }

      id = R.id.iv_aebs;
      MyCheckImageView ivAebs = ViewBindings.findChildViewById(rootView, id);
      if (ivAebs == null) {
        break missingId;
      }

      id = R.id.iv_dome_lamp;
      MyCheckImageView ivDomeLamp = ViewBindings.findChildViewById(rootView, id);
      if (ivDomeLamp == null) {
        break missingId;
      }

      id = R.id.iv_ev_model;
      MyCheckImageView ivEvModel = ViewBindings.findChildViewById(rootView, id);
      if (ivEvModel == null) {
        break missingId;
      }

      id = R.id.iv_ex_ambient;
      MyCheckImageView ivExAmbient = ViewBindings.findChildViewById(rootView, id);
      if (ivExAmbient == null) {
        break missingId;
      }

      id = R.id.iv_fcu_degas;
      MyCheckImageView ivFcuDegas = ViewBindings.findChildViewById(rootView, id);
      if (ivFcuDegas == null) {
        break missingId;
      }

      id = R.id.iv_icm_button_back;
      MyCheckImageView ivIcmButtonBack = ViewBindings.findChildViewById(rootView, id);
      if (ivIcmButtonBack == null) {
        break missingId;
      }

      id = R.id.iv_icm_button_down;
      MyCheckImageView ivIcmButtonDown = ViewBindings.findChildViewById(rootView, id);
      if (ivIcmButtonDown == null) {
        break missingId;
      }

      id = R.id.iv_icm_button_menu;
      MyCheckImageView ivIcmButtonMenu = ViewBindings.findChildViewById(rootView, id);
      if (ivIcmButtonMenu == null) {
        break missingId;
      }

      id = R.id.iv_icm_button_up;
      MyCheckImageView ivIcmButtonUp = ViewBindings.findChildViewById(rootView, id);
      if (ivIcmButtonUp == null) {
        break missingId;
      }

      id = R.id.iv_in_ambient;
      MyCheckImageView ivInAmbient = ViewBindings.findChildViewById(rootView, id);
      if (ivInAmbient == null) {
        break missingId;
      }

      id = R.id.iv_steering_wheel_adjust;
      MyCheckImageView ivSteeringWheelAdjust = ViewBindings.findChildViewById(rootView, id);
      if (ivSteeringWheelAdjust == null) {
        break missingId;
      }

      id = R.id.iv_volume_down;
      MyCheckImageView ivVolumeDown = ViewBindings.findChildViewById(rootView, id);
      if (ivVolumeDown == null) {
        break missingId;
      }

      id = R.id.iv_volume_up;
      MyCheckImageView ivVolumeUp = ViewBindings.findChildViewById(rootView, id);
      if (ivVolumeUp == null) {
        break missingId;
      }

      return new ActivityMainBinding((LinearLayout) rootView, ivAcceleratorInterlock, ivAdas,
          ivAebs, ivDomeLamp, ivEvModel, ivExAmbient, ivFcuDegas, ivIcmButtonBack, ivIcmButtonDown,
          ivIcmButtonMenu, ivIcmButtonUp, ivInAmbient, ivSteeringWheelAdjust, ivVolumeDown,
          ivVolumeUp);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
