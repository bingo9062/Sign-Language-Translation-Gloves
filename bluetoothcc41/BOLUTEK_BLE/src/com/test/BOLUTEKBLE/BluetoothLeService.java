/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.BOLUTEKBLE;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * ���ڹ������ӵķ���������������ͨ���й��ڹ�ó��Э��
 * ������LEװ�á�
 */
@SuppressLint("NewApi")
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class BluetoothLeService extends Service {
//    private final static String TAG = BluetoothLeService.class.getSimpleName();
  private final static String TAG ="a";
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;

    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
//    public static final UUID SERVIE_UUID = UUID
//			.fromString("0000FFF0-0000-1000-8000-00805f9b34fb");
    public static final UUID SERVIE_UUID = UUID
		.fromString("00001801-0000-1000-8000-00805f9b34fb");
    
//	public static final UUID RED_LIGHT_CONTROL_UUID = UUID
//			.fromString("0000FFF4-0000-1000-8000-00805f9b34fb");
    
    public final static String ACTION_DATA_RSSI =
            "com.example.bluetooth.le.ACTION_DATA_RSSI";
    
    public final static String ACTION_RSSI =
            "com.example.bluetooth.le.ACTION_RSSI";
    
    
//    public static final UUID RED_LIGHT_CONTROL_UUID = UUID
//			.fromString("0000FFF4-0000-1000-8000-00805f9b34fb");
//	public static final UUID RED_LIGHT_CONTROL_UUID_TWO = UUID
//			.fromString("0000FFF1-0000-1000-8000-00805f9b34fb");
    
    public static final UUID RED_LIGHT_CONTROL_UUID = UUID
			.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f");
	public static final UUID RED_LIGHT_CONTROL_UUID_TWO = UUID
			.fromString("bef8d6c9-9c21-4c9e-b632-bd58c1009f9f");
    public final static UUID UUID_HEART_RATE_MEASUREMENT =
            UUID.fromString(SampleGattAttributes.HEART_RATE_MEASUREMENT);
    
    public static final UUID ACC_MEASUREMENT_CHARAC = UUID.fromString("F000AA51-0451-4000-B000-000000000000");
    public static final UUID ACC_MEASUREMENT_CHARAC2 = UUID.fromString("F000AA52-0451-4000-B000-000000000000");

    // ���ӵı仯�ͷ����֡�
    @SuppressLint("NewApi")
	private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @SuppressLint("NewApi")
		@Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) 
            	{
            	 Log.i(TAG, "onConnectionStateChange");
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.e(TAG, "������������.");
                // ��ͼ���ַ������ӳɹ���
                Log.e(TAG, "����������:" +
                        mBluetoothGatt.discoverServices());

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.e(TAG, "�������Ͽ�.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	 Log.i(TAG, "onServicesDiscovered");
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
            	 Log.e(TAG, "onservicesdiscovered�յ�: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	 Log.i(TAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                //broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }
        /**
         * �������ݡ�
         */
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	 Log.i(TAG, "onCharacteristicChanged");
        	/**
        	 * �����ͺ���������
        	 */
           broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        	/**
        	 * bolutek ------>>> DM-710-A 
        	 * ��ʽUInt8
        	 */
            /*byte[] data = characteristic.getValue();
            //数据
            String str2 = new String(data);
            Log.e(TAG, "���ض�����ֵ:"+characteristic.getValue().toString());
            Log.e(TAG, "���ض�����ֵdata:"+Arrays.toString(data));
            Log.e(TAG, "���ض�����ֵdata2:"+str2);
            
            Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(EXTRA_DATA, String.valueOf(str2));
            sendBroadcast(intent);*/
        }
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
        	broadcastUpdate(ACTION_RSSI,rssi+"");
        	 Log.e("a", "���ض�����ֵ:"+rssi);
        };
    };
    
    //���¹㲥
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    //���¹㲥
    private void broadcastUpdate(final String action,final String rssi) {
        final Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra(ACTION_DATA_RSSI, rssi);
        sendBroadcast(intent);
    }

    //���¹㲥
	private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        //�������ʲ����������⴦�����ݷ���
        //���ͲĹ��
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            int flag = characteristic.getProperties();
            int format = -1;
            if ((flag & 0x01) != 0) {
                format = BluetoothGattCharacteristic.FORMAT_UINT16;
                Log.d(TAG, "��ʽUInt16");
            } else {
                format = BluetoothGattCharacteristic.FORMAT_UINT8;
                Log.d(TAG, "��ʽUInt8--");
            }
            final int heartRate = characteristic.getIntValue(format, 1);
            Log.d(TAG, String.format("���յ�����: %d", heartRate));
            intent.putExtra(EXTRA_DATA, String.valueOf(heartRate));
        } else {
        	//�������е������ļ��������ݸ�ʽ��Ϊʮ�����ơ�
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data));// + "\n" + stringBuilder.toString());
                Log.i(TAG, "onCharacteristicRead:"+new String(data));
            }
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
        BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
    	// ��ʹ��һ���������豸����Ӧ��ȷ��bluetoothgatt�������á�
    	// ����Դ�Ĵ�ɨ�ɾ������������������У�������
    	// ����ʱ���û������Ǵӷ���Ͽ���
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * ����ת����ͷ��
     * �����Ƿ�ɹ�
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
            	  Log.e(TAG, "bluetoothmanager�޷���ʼ��");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
        	   Log.e(TAG, "�޷���� ��mBluetoothAdapter");
            return false;
        }

        return true;
    }

    /**
     *����������LEװ�ù�ó��Э���ķ�������
     *
     * @param��ַ���豸��ַ��Ŀ�ĵ��豸��
     *
     * @���ط���true��������������ɹ������ӽ��
     *ͨ���첽�ı���
     *
     *�ص���
     */
	public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            Log.e(TAG, "��û�г�ʼ����δָ���ĵ�ַ��");
            return false;
        }

        //��ǰ������װ�á������������ӡ�
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
        	   Log.e(TAG, "��ͼʹ��һ�����е�mbluetoothgatt����.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "û���ҵ��豸���޷����ӡ�");
            return false;
        }
        // ����Ҫֱ�����ӵ��豸���������������Զ�����
        // ��������
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.e(TAG, "��ͼ����һ���µ����ӡ�");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        System.out.println("device.getBondState=="+device.getBondState());
        return true;
    }

	  /**
	    *�Ͽ�һ���������ӻ�ȡ����������ӡ��Ͽ��Ľ��
	    *ͨ���첽�ı���
	    *
	    *�ص���
	    */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
        	  Log.e(TAG, "��û�г�ʼ��");
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     *ʹ�ø�����BLEװ�ú�Ӧ�ó������������������ȷ����Դ
     *��ȷ�ͷš�
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     *Ҫ����һ��������{������bluetoothgattcharacteristic��ȡ}�������Ķ��Ľ����
     *�첽ͨ��
     *�ص���
     *
     * @param������ȡ��
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
        	  Log.e(TAG, "��û�г�ʼ��");
            return;
        }
        Log.e("a", "���ڶ�");
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     *���û����֪ͨ�����ԡ�
     *
     * @param��������Ϊ��
     * @param�������Ϊ�棬����֪ͨ������Ϊfalse��
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "��û�г�ʼ��");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        
        // �����ض��������ʲ�����
        if (UUID_HEART_RATE_MEASUREMENT.equals(characteristic.getUuid())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(SampleGattAttributes.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }


    /**
    *�����������豸֧�ֵĹ�ó��Э���ķ����б���Ӧ����
    *����ֻ����{������bluetoothgatt # discoverservices() }�ɹ���ɡ�
    *
    *�����صĴ����б�֧�ֵķ���{ }��
    */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    
    /**
     * ��������
     * @param characteristic
     * @param bb
     * @return
     */
    public Boolean write(BluetoothGattCharacteristic characteristic ,String bb){
    	if(mBluetoothGatt==null){
    		Log.e(TAG, "mBluetoothGatt==��");
			return false;
    	}
    	if(characteristic==null){
    		Log.e(TAG, "characteristic==��");
			return false;
    	}
    	 // �����ض��������ʲ�����

        	Log.e("a", "�����ˡ���������");

        	characteristic.setValue(bb);

            return   mBluetoothGatt.writeCharacteristic(characteristic);

    	
    }
    
    public boolean readrssi(){
    	if(mBluetoothGatt==null){
    		return false;
    	}
    	return mBluetoothGatt.readRemoteRssi();
    }
    public Boolean writeLlsAlertLevel(int iAlertLevel, byte[] bb) {

		// Log.i("iDevice", iDevice);
    	if(mBluetoothGatt==null){
    		Log.e(TAG, "mBluetoothGatt==��");
			return false;
    	}
    	
		BluetoothGattService linkLossService = mBluetoothGatt
				.getService(SERVIE_UUID);
		if (linkLossService == null) {
			Log.e(TAG, "����û�з��֣�");
			return false;
		}
	
		// enableBattNoti(iDevice);
		BluetoothGattCharacteristic alertLevel = null;
		switch (iAlertLevel) {
		case 1: // red
			alertLevel = linkLossService.getCharacteristic(RED_LIGHT_CONTROL_UUID);
			break;
		case 2:
			alertLevel = linkLossService.getCharacteristic(RED_LIGHT_CONTROL_UUID_TWO);
			break;
		}
		if (alertLevel == null) {
			Log.e(TAG, "����û�з��֣�");
			return false;
		}
		boolean status = false;
		int storedLevel = alertLevel.getWriteType();
		Log.e(TAG, "storedLevel() - storedLevel=" + storedLevel);

		alertLevel.setValue(bb);

		alertLevel.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
		status = mBluetoothGatt.writeCharacteristic(alertLevel);
		Log.e(TAG, "writeLlsAlertLevel() - status=" + status);
		return status;
	}
    
    private void showMessage(String msg) {
		Log.e(TAG, msg);
	}
}
