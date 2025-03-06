package com.spd.custom.view;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlbumArtsBuilder {
    private static final String TAG = "AlbumArtsBuilder";
    private static AlbumArtsBuilder m_builder;
    private ExecutorService m_es;
    public static AlbumArtsBuilder get()
    {
        if (m_builder == null)
        {
            m_builder = new AlbumArtsBuilder();
        }
        return m_builder;
    }
    private HashMap<Long, ArtsUser> m_album_art_buffer = new HashMap<>();
    private HashMap<Long, ArtsUser> m_song_art_buffer = new HashMap<>();
    private static final int ARTS_TYPE_SONG = 0;
    private static final int ARTS_TYPE_ALBUM = 1;

    private class ArtsUser extends Handler implements Runnable
    {
        long m_id;
        int m_art_type;
        private int m_pic_width,m_pic_height;
        WeakReference<Bitmap> m_album_art;
        List<AlbumArtsBuilderCallBack> m_album_user_list = new ArrayList<>();
        //Thread m_load_thread;
        private Context m_con;
        public ArtsUser(Context c_context , long c_id,int c_type , int c_pic_width , int c_pic_height)
        {
            m_con = c_context;
            m_id = c_id;
            m_art_type = c_type;
            m_pic_width = c_pic_width;
            m_pic_height = c_pic_height;
        }
        public Bitmap getUserAlbumArts(AlbumArtsBuilderCallBack c_call_back)
        {
            Log.d(TAG, "getUserAlbumArts: "+m_id+" == 0 =="+m_art_type);
            Bitmap c_result = null;
            synchronized (ArtsUser.this) {
                if (m_album_art != null && m_album_art.get() != null && !m_album_art.get().isRecycled()) {
                    c_result = m_album_art.get();
                    Log.d(TAG, "getUserAlbumArts: "+m_id+" == 1 =="+m_art_type);
                }
            }
            if (c_result == null)
            {
                if (m_album_user_list.indexOf(c_call_back)<0)
                {
                    m_album_user_list.add(c_call_back);
                    Log.d(TAG, "getUserAlbumArts: "+m_id+" == 2 =="+m_art_type);
                }
                if (!this.hasMessages(0))
                {
                    this.sendEmptyMessage(0);
                    Log.d(TAG, "getUserAlbumArts: "+m_id+" == 3 =="+m_art_type);
                }
            }
            return c_result;
        }

        @Override
        public void run() {
            Log.d(TAG, "getUserAlbumArts: "+m_id+" == 4 =="+m_art_type);
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Bitmap c_bitmap = null;
            if (m_art_type == ARTS_TYPE_SONG)
            {
                c_bitmap = getSongArtwork(m_con,m_id,m_pic_width,m_pic_height);
            }
            else if (m_art_type == ARTS_TYPE_ALBUM)
            {
                Log.d(TAG, "getUserAlbumArts: "+m_id+" == 5 =="+m_art_type);
                c_bitmap = getAlbumArtwork(m_con,m_id,m_pic_width,m_pic_height);
                Log.d(TAG, "getUserAlbumArts: "+m_id+" == 6 =="+m_art_type+" "+c_bitmap);
            }

            synchronized (ArtsUser.this) {
                m_album_art = new WeakReference<>(c_bitmap);
            }
            if (!this.hasMessages(1))
            {
                this.sendEmptyMessage(1);
                Log.d(TAG, "getUserAlbumArts: "+m_id+" == 4 =="+m_art_type);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bitmap c_bmp = null;
                if (m_album_art != null && m_album_art.get() != null && !m_album_art.get().isRecycled())
                {
                    c_bmp = m_album_art.get();
                }
                for (AlbumArtsBuilderCallBack c_call_back : m_album_user_list) {
                    if (m_art_type == ARTS_TYPE_ALBUM)
                    {
                        c_call_back.onAlbumArtsLoaded(m_id, c_bmp);
                    }
                    if (m_art_type == ARTS_TYPE_SONG)
                    {
                        c_call_back.onSongArtsLoaded(m_id, c_bmp);
                    }
                }
                m_album_user_list.clear();
                Log.d(TAG, "handleMessage: ==send== "+m_id+" "+m_art_type+" "+c_bmp);
            }
            else if (msg.what == 0)
            {
                if (m_es == null || m_es.isShutdown())
                {
                    m_es = Executors.newFixedThreadPool(5);
                }
                m_es.submit(this);
            }
        }
    }
    public Bitmap requestLoadAlbumArtWork(Context c_context ,long c_id,AlbumArtsBuilderCallBack c_callback, int c_pic_width , int c_pic_height)
    {
        Log.d(TAG, "requestLoadAlbumArtWork: "+c_id);
        ArtsUser c_user = null;
        if (m_album_art_buffer.containsKey(c_id))
        {
            c_user = m_album_art_buffer.get(c_id);
        }
        else
        {
            c_user = new ArtsUser(c_context,c_id,ARTS_TYPE_ALBUM,c_pic_width,c_pic_height);
            m_album_art_buffer.put(c_id,c_user);
        }
        return c_user.getUserAlbumArts(c_callback);
    }
    public Bitmap requestLoadSongArtWork(Context c_context ,long c_id ,AlbumArtsBuilderCallBack c_callback, int c_pic_width , int c_pic_height)
    {
        Log.d(TAG, "requestLoadSongArtWork: "+c_id);
        ArtsUser c_user = null;
        if (m_song_art_buffer.containsKey(c_id))
        {
            c_user = m_song_art_buffer.get(c_id);
        }
        else
        {
            c_user = new ArtsUser(c_context,c_id,ARTS_TYPE_SONG,c_pic_width,c_pic_height);
            m_song_art_buffer.put(c_id,c_user);
        }
        return c_user.getUserAlbumArts(c_callback);
    }
    public interface AlbumArtsBuilderCallBack
    {
        public void onAlbumArtsLoaded(long c_id, Bitmap c_bmp);
        public void onSongArtsLoaded(long c_id, Bitmap c_bmp);
    }

    //private static final BitmapFactory.Options sBitmapOptionsCache = new BitmapFactory.Options();
    private static final BitmapFactory.Options sBitmapOptions = new BitmapFactory.Options();
    static {
        // for the cache,
        // 565 is faster to decode and display
        // and we don't want to dither here because the image will be scaled
        // down later
        //sBitmapOptionsCache.inPreferredConfig = Bitmap.Config.RGB_565;
        //sBitmapOptionsCache.inDither = false;
        sBitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        sBitmapOptions.inDither = false;
    }

    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private static Bitmap getSongArtwork(Context context, long song_id , int c_width , int c_height) {
        Bitmap bm = null;
        ParcelFileDescriptor pfd = null;
        try {
            Uri uri = Uri.parse("content://media/external/audio/media/" + song_id + "/albumart");

            pfd = context.getContentResolver().openFileDescriptor(uri, "r");
            if (pfd != null) {
                FileDescriptor fd = pfd.getFileDescriptor();
                sBitmapOptions.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd,null,sBitmapOptions);
                float c_width_scale = sBitmapOptions.outWidth / c_width;
                float c_height_scale = sBitmapOptions.outHeight / c_height;
                int c_org_width = (int) Math.ceil(c_width_scale);
                int c_org_height = (int) Math.ceil(c_height_scale);
                if (c_org_width > 1 || c_org_height > 1) {
                    // 该属性接收值必须要 >1  可以实现按照 1/opts.inSampleSize 的大小来解析该图片
                    sBitmapOptions.inSampleSize = c_org_width > c_org_height ? c_org_width : c_org_height;
                }
                sBitmapOptions.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFileDescriptor(fd,null,sBitmapOptions);
                if (bm!= null)
                {
                    c_width_scale = (float)bm.getWidth() / (float)c_width;
                    c_height_scale = (float)bm.getHeight() / (float)c_height;
                    Bitmap c_draw_bitmap = Bitmap.createBitmap(c_width,c_height, Bitmap.Config.ARGB_8888);
                    Canvas c_canvas = new Canvas(c_draw_bitmap);
                    c_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                    Matrix c_matrix = new Matrix();
                    c_matrix.setScale(1f/c_width_scale,1f/c_height_scale);
                    Path c_path = new Path();
                    c_path.addArc(0,0,c_width,c_height,0,360);
                    c_path.close();
                    c_canvas.clipPath(c_path);
                    c_canvas.drawBitmap(bm,c_matrix,null);
                    bm.recycle();
                    bm = c_draw_bitmap;
                }
            }
        } catch (IllegalStateException ex) {
        } catch (FileNotFoundException ex) {
        } finally {
            try {
                if (pfd != null) {
                    pfd.close();
                }
            } catch (IOException e) {
            }
        }
        return bm;
    }
    private static Bitmap getAlbumArtwork(Context context,long album_id,int c_width , int c_height) {
        Log.d(TAG, "getAlbumArtwork: ==0== "+context +" "+album_id+" ");
        ContentResolver res = context.getContentResolver();
        Uri uri = ContentUris.withAppendedId(sArtworkUri, album_id);
        if (uri != null) {
            Log.d(TAG, "getAlbumArtwork: ==1== "+uri);
            InputStream in = null;
            try {
                Log.d(TAG, "getAlbumArtwork: ==2== ");

                Log.d(TAG, "getAlbumArtwork: ==3== ");
                in = res.openInputStream(uri);
                sBitmapOptions.inJustDecodeBounds = true;
                sBitmapOptions.inSampleSize = 1;
                Log.d(TAG, "getAlbumArtwork: ==4== ");
                Bitmap c_bmp = BitmapFactory.decodeStream(in, null, sBitmapOptions);
                Log.d(TAG, "getAlbumArtwork: ==5== "+c_bmp+" "+sBitmapOptions.outWidth+" "+sBitmapOptions.outHeight);
                float c_width_scale = sBitmapOptions.outWidth / c_width;
                float c_height_scale = sBitmapOptions.outHeight / c_height;
                int c_org_width = (int) Math.ceil(c_width_scale);
                int c_org_height = (int) Math.ceil(c_height_scale);
                if (c_org_width > 1 || c_org_height > 1) {
                    // 该属性接收值必须要 >1  可以实现按照 1/opts.inSampleSize 的大小来解析该图片
                    sBitmapOptions.inSampleSize = c_org_width > c_org_height ? c_org_width : c_org_height;
                }
                sBitmapOptions.inJustDecodeBounds = false;
                Log.d(TAG, "getAlbumArtwork: ==6== "+sBitmapOptions.inSampleSize);
                in.close();
                in = res.openInputStream(uri);
                c_bmp = BitmapFactory.decodeStream(in, null, sBitmapOptions);
                c_bmp = ThumbnailUtils.extractThumbnail(c_bmp, c_width, c_height,
                        ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
                in.close();
                if (c_bmp != null)
                {
                    c_width_scale = (float)c_bmp.getWidth() / (float)c_width;
                    c_height_scale = (float)c_bmp.getHeight() / (float)c_height;
                    Bitmap c_draw_bitmap = Bitmap.createBitmap(c_width,c_height, Bitmap.Config.ARGB_8888);
                    Canvas c_canvas = new Canvas(c_draw_bitmap);
                    c_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
                    Matrix c_matrix = new Matrix();
                    c_matrix.setScale(1f/c_width_scale,1f/c_height_scale);
                    Log.d(TAG, "getAlbumArtwork: ==72=="+c_bmp+" "+c_width_scale+" "+c_height_scale);
                    Path c_path = new Path();
                    c_path.addArc(0,0,c_width,c_height,0,360);
                    c_path.close();
                    c_canvas.clipPath(c_path);
                    c_canvas.drawBitmap(c_bmp,c_matrix,null);
                    c_bmp.recycle();
                    return c_draw_bitmap;
                }
                else
                {
                    return c_bmp;
                }
            } catch (FileNotFoundException ex) {
                // The album art thumbnail does not actually exist. Maybe the
                // user deleted it, or
                // maybe it never existed to begin with.
                //============================
                Bitmap bm = null;
                ParcelFileDescriptor pfd = null;
                try {
                    Uri uri_album = ContentUris.withAppendedId(sArtworkUri, album_id);

                    pfd = context.getContentResolver().openFileDescriptor(uri_album, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        bm = BitmapFactory.decodeFileDescriptor(fd);
                    }
                } catch (IllegalStateException ex_sub) {
                } catch (FileNotFoundException ex_sub) {
                } finally {
                    try {
                        if (pfd != null) {
                            pfd.close();
                        }
                    } catch (IOException e) {
                    }
                }
                //============================
                if (bm != null) {
                    if (bm.getConfig() == null) {
                        bm = bm.copy(Bitmap.Config.RGB_565, false);
                    }
                }
                return bm;
            } catch (OutOfMemoryError ex) {
                return null;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                }
            }
        }
        return null;
    }
}
