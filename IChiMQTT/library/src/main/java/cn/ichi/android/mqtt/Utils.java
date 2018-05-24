package cn.ichi.android.mqtt;

import android.app.Activity;
import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by mozj on 2018/2/26.
 */

public class Utils {

    private static String TAG = "Utils";

    /**
     *
     * @param filePath
     * @throws IOException
     */
    public static Bitmap readImage(String filePath) {
        try {
            File imgFile = new File(filePath);
            if (!imgFile.exists()) {
                return null;
            }
            return BitmapFactory.decodeFile(filePath);
        } catch (Exception e) {
            Log.e(TAG, "read Image Exception: " + e.getMessage());
        }
        return null;
    }

    private static Application m_Application = null;

    public static Application getApplication()  {
        if (m_Application == null) {
            try {
                Class<?> mClass = Class.forName("android.app.ActivityThread");
                Method currentActivityThread = mClass.getMethod("currentActivityThread");
                Method getApplication = mClass.getMethod("getApplication");

                Object activityThread = currentActivityThread.invoke(null);

                m_Application = (Application) getApplication.invoke(activityThread);
            } catch (Exception e) {
                Log.e(TAG, "read Image Exception: " + e.getMessage());
            }
        }

        return m_Application;
    }

    public static Activity getActivity() {
        Class activityThreadClass = null;
        try {
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Object activityThread = activityThreadClass.getMethod("currentActivityThread").invoke(null);
            Field activitiesField = activityThreadClass.getDeclaredField("mActivities");
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(activityThread);
            for (Object activityRecord : activities.values()) {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if (!pausedField.getBoolean(activityRecord)) {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity activity = (Activity) activityField.get(activityRecord);
                    return activity;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getActivity Exception: " + e.getMessage());
        }
        return null;
    }

}
