package com.spd.WallpaperChooser;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PhotoThumbBuilder {
    private static final String TAG = "PhotoThumbBuilder";
    private static PhotoThumbBuilder m_builder;
    private ExecutorService m_es;
    public static PhotoThumbBuilder get()
    {
        if (m_builder == null)
        {
            m_builder = new PhotoThumbBuilder();
        }
        return m_builder;
    }
    private HashMap<String, PhotoUser> m_photo_image_buffer = new HashMap<>();
    private HashMap<String, PhotoUser> m_photo_thumb_buffer = new HashMap<>();

    private class PhotoUser extends Handler implements Runnable
    {
        String m_file_path;
        boolean m_thumb_flag = false;
        private int m_pic_width,m_pic_height;
        WeakReference<Bitmap> m_image_org;
        List<PhotoBuilderCallBack> m_image_user_list = new ArrayList<>();
        //Thread m_image_load_thread ;
        boolean m_image_load_flag = false;
        public PhotoUser(String c_path)
        {
            m_file_path = c_path;
        }
        public Bitmap getPhotoImageThumb(PhotoBuilderCallBack c_call_back , int c_pic_width , int c_pic_height)
        {
            Log.d(TAG, "getPhotoImageThumb: "+m_file_path+" "+c_call_back);
            m_thumb_flag = true;
            m_pic_width = c_pic_width;
            m_pic_height = c_pic_height;
            Bitmap c_result = null;
            synchronized (PhotoUser.this) {
                if (m_image_org != null && m_image_org.get() != null && !m_image_org.get().isRecycled()) {
                    c_result = m_image_org.get();
                }
            }
            if (c_result == null)
            {
                if (m_image_user_list.indexOf(c_call_back)<0)
                {
                    m_image_user_list.add(c_call_back);
                }
                if (!this.hasMessages(0))
                {
                    m_image_load_flag = true;
                    this.sendEmptyMessage(0);
                }
            }
            return c_result;
        }
        public Bitmap getPhotoImage(PhotoBuilderCallBack c_call_back)
        {
            m_thumb_flag = false;
            Log.d(TAG, "getUserAlbumArts: "+m_file_path+" == 0 ==");
            Bitmap c_result = null;
            synchronized (PhotoUser.this) {
                if (m_image_org != null && m_image_org.get() != null && !m_image_org.get().isRecycled()) {
                    c_result = m_image_org.get();
                }
            }
            if (c_result == null)
            {
                if (m_image_user_list.indexOf(c_call_back)<0)
                {
                    m_image_user_list.add(c_call_back);
                }
                if (!this.hasMessages(0))
                {
                    m_image_load_flag = true;
                    this.sendEmptyMessage(0);
                }
            }
            return c_result;
        }

        @Override
        public void run() {
            Log.d(TAG, "run: Start");
            android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
            Bitmap c_bitmap = null;
            Log.d(TAG, "run: =0= "+m_thumb_flag);
            if (m_thumb_flag)
            {
                c_bitmap = getImageThumbnail(m_file_path,m_pic_width,m_pic_height);
            }
            else
            {
                c_bitmap = BitmapFactory.decodeFile(m_file_path);
            }
            Log.d(TAG, "run: =1= "+c_bitmap);
            synchronized (PhotoUser.this) {
                m_image_org = new WeakReference<>(c_bitmap);
            }
            if (!PhotoUser.this.hasMessages(1))
            {
                PhotoUser.this.sendEmptyMessage(1);
            }
            Log.d(TAG, "run: end");
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Bitmap c_bmp = null;
                synchronized (PhotoUser.this) {
                    if (m_image_org != null && m_image_org.get() != null && !m_image_org.get().isRecycled()) {
                        c_bmp = m_image_org.get();
                    }
                }
                for (PhotoBuilderCallBack c_call_back : m_image_user_list) {
                    c_call_back.onImageLoaded(m_file_path, c_bmp);
                }
                m_image_user_list.clear();
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
    public interface PhotoBuilderCallBack
    {
        public void onImageLoaded(String c_path, Bitmap c_bmp);
    }
    public void clearBitmapBuffer()
    {
        m_photo_image_buffer.clear();
        m_photo_thumb_buffer.clear();
        if (m_es != null && !m_es.isShutdown())
        {
            m_es.shutdown();
        }
    }
    public Bitmap requestLoadPhotoImage(String c_path, PhotoBuilderCallBack c_callback, int c_pic_width , int c_pic_height)
    {
        Log.d(TAG, "requestLoadPhotoImage: "+c_path);
        PhotoUser c_user = null;
        if (m_photo_image_buffer.containsKey(c_path))
        {
            c_user = m_photo_image_buffer.get(c_path);
        }
        else
        {
            c_user = new PhotoUser(c_path);
            m_photo_image_buffer.put(c_path,c_user);
        }
        return c_user.getPhotoImageThumb(c_callback,c_pic_width,c_pic_height);
    }
    public Bitmap requestLoadPhotoThumb(String c_path, PhotoBuilderCallBack c_callback, int c_pic_width , int c_pic_height)
    {
        Log.d(TAG, "requestLoadPhotoThumb: "+c_path);
        PhotoUser c_user = null;
        if (m_photo_thumb_buffer.containsKey(c_path))
        {
            c_user = m_photo_thumb_buffer.get(c_path);
        }
        else
        {
            c_user = new PhotoUser(c_path);
            m_photo_thumb_buffer.put(c_path,c_user);
        }
        return c_user.getPhotoImageThumb(c_callback,c_pic_width,c_pic_height);
    }

    private static Bitmap getImageThumbnail(String imagePath, int width, int height) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        options.inJustDecodeBounds = false; // 设为 false
        int h = options.outHeight;
        int w = options.outWidth;
        int beWidth = w / width;
        int beHeight = h / height;
        int be = 1;
        if (beWidth < beHeight) {
            be = beWidth;
        } else {
            be = beHeight;
        }
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        Log.d(TAG, "getImageThumbnail: = 0 = "+imagePath+" "+bitmap);
        if(bitmap != null && (bitmap.getWidth() > width || bitmap.getHeight()>height))
        {
/*            Log.d(TAG, "getImageThumbnail: = 1 = "+imagePath+" "+bitmap.getWidth()+" "+bitmap.getHeight());
            bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
            Log.d(TAG, "getImageThumbnail: out = "+imagePath+" "+bitmap.getWidth()+" "+bitmap.getHeight());*/
            float c_scale_width = (float)width/(float)bitmap.getWidth();
            float c_scale_height = (float)height/(float)bitmap.getHeight();
            float c_scall = c_scale_width;
            if (c_scale_height>c_scale_width)
            {
                c_scall = c_scale_height;
            }
            float c_offset_x = (width - bitmap.getWidth()*c_scall)/2f;
            float c_offset_y = (height - bitmap.getHeight()*c_scall)/2f;
            Bitmap c_swap_bmp = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
            Paint c_scale_paint = new Paint();
            c_scale_paint.setAntiAlias(true);
            Canvas c_scale_canvas = new Canvas(c_swap_bmp);
            c_scale_canvas.setDrawFilter(new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG));
            Matrix c_scale_matrix = new Matrix();
            c_scale_matrix.postScale(c_scall,c_scall);
            c_scale_matrix.postTranslate(c_offset_x,c_offset_y);
            c_scale_canvas.drawBitmap(bitmap,c_scale_matrix,c_scale_paint);
            c_scale_canvas.setBitmap(null);
            bitmap.recycle();
            bitmap = c_swap_bmp;
        }
        return bitmap;
    }
}
