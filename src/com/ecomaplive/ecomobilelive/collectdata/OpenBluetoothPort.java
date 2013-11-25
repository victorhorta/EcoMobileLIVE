package com.ecomaplive.ecomobilelive.collectdata;

import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;
import android.os.Build;

import java.io.IOException;
import java.util.UUID;

public abstract class OpenBluetoothPort implements OpenBluetoothPortInterface {
    public abstract BluetoothSocket open(BluetoothDevice device, UUID uuid) throws IOException;

    private static class OpenBluetoothPortEclair extends OpenBluetoothPort {
        @Override
        public BluetoothSocket open(BluetoothDevice device, UUID uuid) throws IOException {
            return device.createRfcommSocketToServiceRecord(uuid);
        }
    }

    private static class OpenBluetoothPortGingerbread extends OpenBluetoothPort {
        @Override
        public BluetoothSocket open(BluetoothDevice device, UUID uuid) throws IOException {
            return device.createInsecureRfcommSocketToServiceRecord(uuid);
        }
    }

    public static OpenBluetoothPortInterface newInstance() {
        final int sdkVersion = Integer.parseInt(Build.VERSION.SDK);
        if (sdkVersion > Build.VERSION_CODES.GINGERBREAD)
            return new OpenBluetoothPortGingerbread();
        return new OpenBluetoothPortEclair();
    }
}
