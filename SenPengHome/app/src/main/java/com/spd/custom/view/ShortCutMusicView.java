package com.spd.custom.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;

import com.spd.bluetooth.entity.aidl.CallLog;
import com.spd.bluetooth.entity.aidl.MusicInfo;
import com.spd.home.FullscreenActivity;
import com.spd.home.R;
import com.spd.media.entity.aidl.DeviceListItem;
import com.spd.media.entity.aidl.DevicesList;
import com.spd.media.entity.aidl.NowPlaying;
import com.spd.media.util.MediaUtilDef;

public class ShortCutMusicView extends ShortCutBaseView implements MediaHelper.MediaHelperCallBack,BtHelper.BtHelperPhoneCallBack,AlbumArtsBuilder.AlbumArtsBuilderCallBack{
    public static String TAG = "ShortCutMusicView";
    private Bitmap m_bmp_part_sub;
    private String m_current_media_text = null;
    private Bitmap m_current_album_art = null;
    public ShortCutMusicView(Context context) {
        super(context);
        Log.d(TAG, this.hashCode()+"ShortCutMusicView: ");
        m_bmp_bg = BitmapFactory.decodeResource(context.getResources(), R.drawable.icon_bg_02);
        m_bmp_part = BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon_part_music_01);
        m_bmp_part_sub = BitmapFactory.decodeResource(context.getResources(),R.drawable.app_icon_part_music_02);
        m_bmp_shadow = ShadowBuilder.buildBitmapShadow(context,m_bmp_part,8);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Log.d(TAG, this.hashCode()+"onAttachedToWindow: ");
        MediaHelper.get().regsiterCallBack(this);
        BtHelper.get().regsiterCallBack(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.d(TAG, this.hashCode()+"onDetachedFromWindow: ");
        MediaHelper.get().unregsiterCallBack(this);
        BtHelper.get().unregsiterCallBack(this);
    }

/*    @Override
    protected String getDrawTitle()
    {
        //Log.d(TAG, this.hashCode()+"getDrawTitle: ");
        String c_result = getShortCutInfo().getAppInfo().mAppName;
        if (m_current_media_text != null)
        {
            c_result = m_current_media_text;
            m_paint.setShadowLayer(3,1,1, 0xff000000);
            int c_end = m_current_media_text.length();
            m_paint.getTextBounds(c_result,0,c_end,m_test_text_rect);
            while (m_test_text_rect.width()>300)
            {
                c_end --;
                m_paint.getTextBounds(c_result,0,c_end,m_test_text_rect);
            }
            c_result = m_current_media_text.substring(0,c_end);
        }
        return c_result;
    }*/
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Log.d(TAG, this.hashCode()+"onDraw: ");
        ShortCut c_app_info = getShortCutInfo();
        synchronized (this) {
            if (m_current_album_art != null && !m_current_album_art.isRecycled()) {
                m_camera.save();
                m_camera.translate(-m_current_album_art.getWidth() / 2f, m_current_album_art.getHeight() / 2f + m_icon_y_offset, FullscreenActivity.m_view_draw_scale - m_init_value_part * 300 - m_drag_value * m_drag_length + m_down_value * m_down_length);
                m_camera.getMatrix(m_matrix);
                m_camera.restore();
                m_matrix.postTranslate(canvas.getWidth() / 2f + c_app_info.getShortRealPostionX(), canvas.getHeight() / 2f + c_app_info.getShortRealPostionY());
                m_paint.setColor(0xffffffff);
                m_paint.setAlpha((int) (m_base_alpha * Math.min(255, (1f - m_init_value_part) * 255 * (1f - m_drag_value * 0.5f))));
                canvas.drawBitmap(m_current_album_art, m_matrix, m_paint);
            }
        }
        m_camera.save();
        m_camera.translate(-m_bmp_part_sub.getWidth()/2f,m_bmp_part_sub.getHeight()/2f+m_icon_y_offset,FullscreenActivity.m_view_draw_scale-m_init_value_part*300-m_drag_value*m_drag_length+m_down_value*m_down_length);
        m_camera.getMatrix(m_matrix);
        m_camera.restore();
        m_matrix.postTranslate(canvas.getWidth()/2f+c_app_info.getShortRealPostionX(),canvas.getHeight()/2f+c_app_info.getShortRealPostionY());
        m_paint.setColor(0xffffffff);
        m_paint.setAlpha((int)(m_base_alpha*Math.min(255,(1f-m_init_value_part)*255*(1f-m_drag_value*0.5f))));
        canvas.drawBitmap(m_bmp_part_sub,m_matrix,m_paint);

    }


    @Override
    public void onMediaServiceConnected() {
        Log.d(TAG, this.hashCode()+"onMediaServiceConnected: ");
        NowPlaying c_music = MediaHelper.get().getNowPlaying();
        if (c_music != null && c_music.playIndex >=0)
        {
            onMediaNowPlayingChanged(c_music);
        }
    }

    @Override
    public void onMediaServiceDisconnected() {
        Log.d(TAG, this.hashCode()+"onMediaServiceDisconnected: ");
    }

    @Override
    public void onMediaDevicesListChanged(DevicesList devicelist) {

    }

    @Override
    public void onMediaDevicesChanged(int device, DeviceListItem deviceItem) {

    }

    @Override
    public void onMediaPlayStatusChanged(int status, int speed) {

    }

    @Override
    public void onMediaPlayTimeChanged(int timeMs, int durationMs) {

    }

    @Override
    public void onMediaPlayModeChanged(int repeatMode, int shuffleMode) {

    }
    private NowPlaying m_current_playing_info;
    @Override
    public void onMediaNowPlayingChanged(NowPlaying musicInfo) {
        Log.d(TAG, this.hashCode()+"onMediaNowPlayingChanged: "+musicInfo);
        if (musicInfo != null && musicInfo.playIndex != -1 && musicInfo.fileType == MediaUtilDef.MEDIA_TYPE_AUDIO)
        {
            //Log.d(TAG, this.hashCode()+"onMediaNowPlayingChanged: "+musicInfo.playIndex +" "+musicInfo.fileType);
            m_current_playing_info = musicInfo;
            m_current_media_text = musicInfo.songTitle;
            if (musicInfo.file_id > 0)
            {
                synchronized (this)
                {
                    m_current_album_art = AlbumArtsBuilder.get().requestLoadSongArtWork(getContext(),musicInfo.file_id,this,90,90);
                    if (m_current_album_art != null && !m_current_album_art.isRecycled())
                    {
                        if (m_init_anim == null || !m_init_anim.isRunning())
                        {
                            this.invalidate();
                        }
                    }
                }
            }
            else if (musicInfo.album_id > 0)
            {
                synchronized (this)
                {
                    m_current_album_art = AlbumArtsBuilder.get().requestLoadAlbumArtWork(getContext(), m_current_playing_info.album_id, this, 90, 90);
                    if (m_current_album_art != null && !m_current_album_art.isRecycled()) {
                        if (m_init_anim == null || !m_init_anim.isRunning())
                        {
                            this.invalidate();
                        }
                    }
                }
            }
        }
        else
        {
            m_current_playing_info = null;
            synchronized (this)
            {
                if (m_current_album_art != null)
                {
                    m_current_album_art.recycle();
                }
                m_current_album_art = null;
            }
            if (m_init_anim == null || !m_init_anim.isRunning())
            {
                this.invalidate();
            }
        }
    }

    @Override
    public void onSongArtsLoaded(long c_id, Bitmap c_bmp) {
        Log.d(TAG, this.hashCode()+"onSongArtsLoaded: "+c_id+" "+c_bmp);
        if (m_current_playing_info != null && m_current_playing_info.file_id == c_id)
        {
            if (c_bmp != null)
            {
                synchronized (this)
                {
                    if (m_current_album_art != null)
                    {
                        m_current_album_art.recycle();
                    }
                    m_current_album_art = c_bmp;
                }
                if (m_init_anim == null || !m_init_anim.isRunning())
                {
                    this.invalidate();
                }
            }
            else if (m_current_playing_info.album_id > 0)
            {
                synchronized (this)
                {
                    m_current_album_art = AlbumArtsBuilder.get().requestLoadAlbumArtWork(getContext(),m_current_playing_info.album_id,this,90,90);
                    if (m_current_album_art != null && !m_current_album_art.isRecycled())
                    {
                        if (m_init_anim == null || !m_init_anim.isRunning())
                        {
                            this.invalidate();
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onAlbumArtsLoaded(long c_id, Bitmap c_bmp) {
        Log.d(TAG, this.hashCode()+"onAlbumArtsLoaded: "+c_id+" "+c_bmp);
        if (m_current_playing_info != null && m_current_playing_info.file_id == c_id)
        {
            if (c_bmp != null)
            {
                synchronized (this)
                {
                    if (m_current_album_art != null)
                    {
                        m_current_album_art.recycle();
                    }
                    m_current_album_art = c_bmp;
                }
                if (m_init_anim == null || !m_init_anim.isRunning())
                {
                    this.invalidate();
                }
            }
        }
    }


    @Override
    public void onMediaPlayerInfoChanged(int codec, int status) {

    }

    @Override
    public void onMediaPlayingListChanged(int count) {

    }

    @Override
    public void onMediaFavoriteListChanged(int count) {

    }

    @Override
    public void sendMediaCommonControl(int msg, int arg1) {

    }
//////////////BT//////////////
    @Override
    public boolean onServiceConnected() {
        return false;
    }

    @Override
    public boolean onServiceDisconnected() {
        return false;
    }

    @Override
    public boolean onConnectStatusChanged(int status) {
        return false;
    }
    private int m_bt_media_status = -1;
    @Override
    public boolean onPlayStatusChanged(int status, long postion) {
        Log.d(TAG, this.hashCode()+"onPlayStatusChanged: "+status+" "+postion);
        m_bt_media_status = status;
        chechBtMediaInfo();
        return false;
    }
    private MusicInfo m_current_music_info;
    @Override
    public boolean onTrackInfoChanged(MusicInfo musicInfo) {
        Log.d(TAG, this.hashCode()+"onTrackInfoChanged: "+musicInfo);
        if (musicInfo != null)
        {
            m_current_music_info = musicInfo;
        }
        else
        {
            m_current_music_info = null;
        }
        chechBtMediaInfo();
        return false;
    }
    private void chechBtMediaInfo()
    {
        Log.d(TAG, this.hashCode()+"chechBtMediaInfo: ");
        if (m_bt_media_status >= 1 && m_current_music_info != null && m_current_playing_info == null)
        {
            synchronized (this)
            {
                if (m_current_album_art != null)
                {
                    m_current_album_art.recycle();
                }
                m_current_album_art = null;
            }
            m_current_media_text = m_current_music_info.getSongTitle();
        }
    }

    @Override
    public boolean onCallStatusChanged(CallLog callLog) {
        return false;
    }

    @Override
    public boolean onMicStatusChanged(int status) {
        return false;
    }

    @Override
    public boolean onTransferStatusChanged(int status) {
        return false;
    }

    @Override
    public boolean onPhonebookCountChanged(int type, int count) {
        return false;
    }

    @Override
    public boolean sendCommonControl(int msg, int arg1, int arg2) {
        return false;
    }

    @Override
    public boolean onDialogListChanged(int i) {
        return false;
    }


}
