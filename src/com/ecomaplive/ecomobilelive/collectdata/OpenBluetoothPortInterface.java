package com.ecomaplive.ecomobilelive.collectdata;

import android.bluetooth.BluetoothSocket;
import android.bluetooth.BluetoothDevice;

import java.io.IOException;

import java.util.UUID;

public interface OpenBluetoothPortInterface
{
	public BluetoothSocket open(BluetoothDevice device, UUID uuid) throws IOException;
}
