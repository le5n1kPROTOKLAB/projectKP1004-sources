package com.spd.custom.view;

public interface ShortCutClickListener {
    public void onAppLongClicked(AppInfo c_app_info);
    public void onAppClicked(AppInfo c_app_info);
    public void onDragShortCutStart();
    public void onDragShortCutFinished();
}
