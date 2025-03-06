package com.spd.Scene;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.spd.custom.view.CustomDesktop;
import com.spd.home.R;

import java.util.ArrayList;
import java.util.List;


public abstract class SceneManager {
    public static final int SCENE_STATE_TAG = 0xf1100001;
    public static final int SCENE_ANIN_TAG = 0xf1300001;

    public static final int SCENE_STATE_REQUEST_CATCH = 1;
    public static final int SCENE_STATE_REQUEST_RELEASE = 2;

    private int m_scene_state;
    public boolean m_attach_flag = false;
    private List<View> m_motion_view_list = new ArrayList<>();
    protected ViewGroup m_root_view;
    private String m_manage_name;
    private boolean mBound = false;
    protected Context m_context;

    public abstract boolean getChildSceneMotionFlag(ViewGroup c_root_view , View c_child_view);
    public abstract void onSceneChildChange(int c_count , ViewGroup c_root_view , int c_index , View c_child_view , int state);
    public abstract void reOrderChildMotion(ViewGroup c_root_view , List<View> c_motion_view_list);


    public int getExitSceneDelayTime()
    {
        return 700;
    }
    public SceneManager(Context context , String c_manage_name , ViewGroup c_root_view)
    {
        m_manage_name = c_manage_name;
        m_root_view = c_root_view;
        m_root_view.setVisibility(View.INVISIBLE);
        m_context = context;
        m_scene_state = 0;
        m_attach_flag = false;
    }
    public void enterScene()
    {
        Log.d("SimonCheck002", "enterScene: ");
        if (m_scene_state != SCENE_STATE_REQUEST_CATCH)
        {
            m_scene_state = SCENE_STATE_REQUEST_CATCH;
            m_attach_flag = true;
            m_root_view.post(new Runnable() {
                @Override
                public void run() {
                    m_root_view.setVisibility(View.VISIBLE);
                    changeSceneState(m_root_view,SCENE_STATE_REQUEST_CATCH);
                }
            });
        }
    }
    public void exitScene()
    {
        if (m_scene_state != SCENE_STATE_REQUEST_RELEASE)
        {
            m_scene_state = SCENE_STATE_REQUEST_RELEASE;
            m_root_view.setVisibility(View.VISIBLE);
            changeSceneState(m_root_view,SCENE_STATE_REQUEST_RELEASE);
        }
    }
    private void changeSceneState(ViewGroup c_root_view , int c_state)
    {

        m_motion_view_list.clear();
        checkChildView(c_root_view,m_motion_view_list);
        reOrderChildMotion(c_root_view,m_motion_view_list);
        Log.d("SimonCheck002", "changeSceneState: "+c_state+" "+c_root_view+" "+m_motion_view_list.size());
        CustomDesktop c_destop = c_root_view.findViewById(R.id.id_app_desktop);
        Log.d("SimonCheck002", "c_destop: "+c_destop.getChildCount());
        if (m_motion_view_list.size() > 0)
        {
            for (int i = 0 ; i < m_motion_view_list.size() ; i++)
            {
                View c_child = m_motion_view_list.get(i);
                onSceneChildChange(m_motion_view_list.size(),c_root_view,i,c_child,c_state);
            }
        }
    }

    private boolean isVisible(View v) {
        return v.getLocalVisibleRect(new Rect());
    }
    private void checkChildView(ViewGroup c_root_view , List<View> c_motion_view_list)
    {
        int c_child_count = c_root_view.getChildCount();
        for (int i = 0 ; i < c_child_count ; i++)
        {
            View c_child = c_root_view.getChildAt(i);
            Object c_scene_tag = c_child.getTag(SCENE_STATE_TAG);
            if (isVisible(c_child))
            {
                if (c_scene_tag != null && (boolean)c_scene_tag)
                {
                    c_motion_view_list.add(c_child);
                }
                else if (getChildSceneMotionFlag(c_root_view,c_child))
                {
                    c_motion_view_list.add(c_child);
                }
                else if (c_child instanceof ViewGroup)
                {
                    ViewGroup c_child_root_view = (ViewGroup)c_child;
                    checkChildView (c_child_root_view , c_motion_view_list);
                }
            }
        }
    }

}

/*
    private HashMap<Class,Method> m_class_method = new HashMap<Class, Method>();
    private List<Class> m_invalid_class = new ArrayList<>();
            Class c_class = c_child.getClass();
            try {
                Log.d("Test01", "checkChildView: "+c_child.toString()+"  start ");
                int c_result = 0;
                if (m_invalid_class.indexOf(c_class)>=0)
                {

                }
                else
                {
                    if (m_class_method.containsKey(c_class))
                    {
                        Method getPackageSizeInfo = m_class_method.get(c_class);
                        c_result = (int)getPackageSizeInfo.invoke(c_child);
                    }
                    else
                    {
                        Method getPackageSizeInfo = c_child.getClass().getMethod("getSceneState");
                        m_class_method.put(c_class,getPackageSizeInfo);
                        c_result = (int)getPackageSizeInfo.invoke(c_child);
                    }
                }

                Log.d("Test01", "checkChildView: "+c_child.toString()+"   "+c_result);
            } catch (NoSuchMethodException e) {
                m_invalid_class.add(c_class);
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
 */