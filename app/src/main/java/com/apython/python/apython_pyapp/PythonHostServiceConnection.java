package com.apython.python.apython_pyapp;

/*
 * This connection holds the connection to the python host service.
 *
 * Created by Sebastian on 27.05.2015.
 */

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

public class PythonHostServiceConnection implements ServiceConnection {

    // Messenger for communicating with the service python host service.
    private Messenger service = null;

    // True if we have called bind on the python host service.
    private boolean bound = false;

    private ConnectionListener connectionListener;

    public interface ConnectionListener {
        void onConnected(Messenger messenger);
    }

    public PythonHostServiceConnection(ConnectionListener connectionListener) {
        super();
        this.connectionListener = connectionListener;
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        Log.d(MainActivity.TAG, "onServiceConnected");
        this.service = new Messenger(service);
        this.bound   = true;
        this.connectionListener.onConnected(this.service);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(MainActivity.TAG, "onServiceDisconnected");
        this.service = null;
        this.bound   = false;
    }

    public boolean isBound() {
        return this.bound;
    }

    public void send(Message message) throws RemoteException {
        this.service.send(message);
    }
}
