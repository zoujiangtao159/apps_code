package com.appsflyer;


import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;

import java.lang.ref.WeakReference;

/**
 * Usage:
 *
 * 1. Get the Foreground Singleton, passing a Context or Application object unless you
 * are sure that the Singleton has definitely already been initialised elsewhere.
 *
 * 2.a) Perform a direct, synchronous check: Foreground.isForeground() / .isBackground()
 *
 * or
 *
 * 2.b) Register to be notified (useful in Service or other non-UI components):
 *
 *   Foreground.Listener myListener = new Foreground.Listener(){
 *       public void onBecameForeground(){
 *           // ... whatever you want to do
 *       }
 *       public void onBecameBackground(){
 *           // ... whatever you want to do
 *       }
 *   }
 *
 *   public void onCreate(){
 *      super.onCreate();
 *      Foreground.get(this).registerListener(listener);
 *   }
 *
 *   public void onDestroy(){
 *      super.onCreate();
 *      Foreground.get(this).unregisterListener(listener);
 *   }
 */
@RequiresApi(api = Build.VERSION_CODES.ICE_CREAM_SANDWICH)
class Foreground implements Application.ActivityLifecycleCallbacks {

    private static final long CHECK_DELAY = 500;

    interface Listener {

         void onBecameForeground(Activity activity);

         void onBecameBackground(WeakReference<Activity> activity);

    }

    private static Foreground instance;

    private boolean foreground = false, paused = true;
    private Listener listener = null;

    /**
     * Its not strictly necessary to use this method - _usually_ invoking
     * get with a Context gives us a path to retrieve the Application and
     * initialise, but sometimes (e.g. in test harness) the ApplicationContext
     * is != the Application, and the docs make no guarantees.
     *
     * @param application
     * @return an initialised Foreground instance
     */
    public static Foreground init(Application application){
        if (instance == null) {
            instance = new Foreground();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH ) {
                application.registerActivityLifecycleCallbacks(instance);
            }
        }
        return instance;
    }

    public static Foreground get(Application application){
        if (instance == null) {
            init(application);
        }
        return instance;
    }

    public static Foreground get(Context ctx){
        if (instance == null) {
            Context appCtx = ctx.getApplicationContext();
            if (appCtx instanceof Application) {
                init((Application)appCtx);
            }
            throw new IllegalStateException(
                    "Foreground is not initialised and " +
                            "cannot obtain the Application object");
        }
        return instance;
    }

    public static Foreground getInstance(){
        if (instance == null) {
            throw new IllegalStateException(
                    "Foreground is not initialised - invoke " +
                            "at least once with parameter init/get");
        }
        return instance;
    }

    public boolean isForeground(){
        return foreground;
    }

    public boolean isBackground(){
        return !foreground;
    }

    public void registerListener(Listener listener){
        this.listener = listener;
    }

    public void unregisterListener(){
        this.listener = null;
    }

    @Override
    public void onActivityResumed(Activity activity) {
        paused = false;
        boolean wasBackground = !foreground;
        foreground = true;

        if (wasBackground){
            try {
                listener.onBecameForeground(activity);
            } catch (Exception exc) {
                AFLogger.afLogE("Listener threw exception! ", exc);
            }
        }
    }

    @Override
    public void onActivityPaused(final Activity activity) {
        paused = true;
        AFBackgroundTask backgroundTask = new AFBackgroundTask(new WeakReference<>(activity));
        backgroundTask.executeOnExecutor(AFExecutor.getInstance().getThreadPoolExecutor());
    }

    private class AFBackgroundTask extends AsyncTask<Void, Void, Void> {

        WeakReference<Activity> weakActivity;

        public AFBackgroundTask(WeakReference<Activity> weakActivity) {
            this.weakActivity = weakActivity;
        }

        protected Void doInBackground(Void... params) {
            try {
                Thread.sleep(CHECK_DELAY);
            } catch (InterruptedException e) {
                AFLogger.afLogE("Sleeping attempt failed (essential for background state verification)\n", e);
            }
            if (foreground && paused) {
                foreground = false;
                try {
                    listener.onBecameBackground(weakActivity);
                } catch (Exception exc) {
                    AFLogger.afLogE("Listener threw exception! ", exc);
                    cancel(true);
                }
            }
            weakActivity.clear();
            return null;
        }

    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}

    @Override
    public void onActivityStopped(Activity activity) {}

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}

    @Override
    public void onActivityDestroyed(Activity activity) {}
}
