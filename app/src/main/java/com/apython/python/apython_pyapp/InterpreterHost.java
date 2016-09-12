package com.apython.python.apython_pyapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.ViewGroup;

/**
 * A wrapper of the defined interface of any Python Host.
 * One should be able to just copy this class into ones
 * Python App project and let it handle all communication.
 * 
 * Created by Sebastian on 20.08.2016.
 */
@SuppressWarnings("JniMissingFunction") // They are all provided by the Python host
public class InterpreterHost {
    // The protocol version used by this app
    public static final int PROTOCOL_VERSION = 0;
    
    // The apps activity which hosts the UI for the Python app.
    private final Activity hostingAppActivity;
    
    private String logTag = "PythonApp";
    
    public InterpreterHost(Activity hostingAppActivity, String logTag) {
        this.hostingAppActivity = hostingAppActivity;
        if (logTag != null) this.logTag = logTag;
    }

    public boolean connectToHost(int requestId, String minPyVersion, String requirements, Class verifyActivity) {
        Intent startIntent = new Intent("com.python.pythonhost.PYTHON_APP_GET_EXECUTION_INFO");
        startIntent.putExtra("protocolVersion", PROTOCOL_VERSION);
        startIntent.putExtra("minPythonVersion", minPyVersion);
        if (requirements.length() > 0) startIntent.putExtra("requirements", requirements);
        startIntent.putExtra("securityIntent", new Intent(hostingAppActivity, verifyActivity));
        try {
            hostingAppActivity.startActivityForResult(startIntent, requestId);
            return true;
        } catch (ActivityNotFoundException unused) {
            return false;
        }
    }
    
    @SuppressLint("UnsafeDynamicallyLoadedCode") // We trust our permission
    public boolean loadInterpreterFromHostData(Intent data) {
        System.setProperty("python.android.app.wrapper.class",
                           this.getClass().getCanonicalName().replace('.', '/'));
        String libPath = data.getStringExtra("libPath");
        if (libPath == null) return false;
        try {
            System.load(libPath);
            setLogTag(logTag);
        } catch (UnsatisfiedLinkError e) {
            Log.e(logTag, "Failed to load the native library provided py the interpreter host!", e);
            return false;
        }
        return loadPythonHost(hostingAppActivity, data.getStringExtra("pythonVersion"));
    }
    
    private native boolean loadPythonHost(Activity activity, String pythonVersion);
    private native void    setLogTag(String tag);
    public  native Object  setWindow(int windowType, ViewGroup parent);
    public  native int     startInterpreter(String[] args);
}
