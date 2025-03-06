/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.senptec.can;
// Declare any non-default types here with import statements
// 被设置到服务端

public interface ICanClientInterface extends android.os.IInterface
{
  /** Default implementation for ICanClientInterface. */
  public static class Default implements com.senptec.can.ICanClientInterface
  {
    // 服务端向客户端转发数据

    @Override public void onReceiveData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException
    {
    }
    // 服务端向客户端返回最后发送的数据

    @Override public void onSendData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException
    {
    }
    // 服务端向客户端返回录像列表
    // gsonData <=> RecordResponse

    @Override public void onRecordFileList(java.lang.String tag, java.lang.String gsonData) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.senptec.can.ICanClientInterface
  {
    private static final java.lang.String DESCRIPTOR = "com.senptec.can.ICanClientInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.senptec.can.ICanClientInterface interface,
     * generating a proxy if needed.
     */
    public static com.senptec.can.ICanClientInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.senptec.can.ICanClientInterface))) {
        return ((com.senptec.can.ICanClientInterface)iin);
      }
      return new com.senptec.can.ICanClientInterface.Stub.Proxy(obj);
    }
    @Override public android.os.IBinder asBinder()
    {
      return this;
    }
    @Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
    {
      java.lang.String descriptor = DESCRIPTOR;
      switch (code)
      {
        case INTERFACE_TRANSACTION:
        {
          reply.writeString(descriptor);
          return true;
        }
        case TRANSACTION_onReceiveData:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onReceiveData(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onSendData:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onSendData(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        case TRANSACTION_onRecordFileList:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          java.lang.String _arg1;
          _arg1 = data.readString();
          this.onRecordFileList(_arg0, _arg1);
          reply.writeNoException();
          return true;
        }
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.senptec.can.ICanClientInterface
    {
      private android.os.IBinder mRemote;
      Proxy(android.os.IBinder remote)
      {
        mRemote = remote;
      }
      @Override public android.os.IBinder asBinder()
      {
        return mRemote;
      }
      public java.lang.String getInterfaceDescriptor()
      {
        return DESCRIPTOR;
      }
      // 服务端向客户端转发数据

      @Override public void onReceiveData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeString(gsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onReceiveData, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onReceiveData(id, gsonData);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // 服务端向客户端返回最后发送的数据

      @Override public void onSendData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(id);
          _data.writeString(gsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onSendData, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onSendData(id, gsonData);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // 服务端向客户端返回录像列表
      // gsonData <=> RecordResponse

      @Override public void onRecordFileList(java.lang.String tag, java.lang.String gsonData) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(tag);
          _data.writeString(gsonData);
          boolean _status = mRemote.transact(Stub.TRANSACTION_onRecordFileList, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().onRecordFileList(tag, gsonData);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      public static com.senptec.can.ICanClientInterface sDefaultImpl;
    }
    static final int TRANSACTION_onReceiveData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onSendData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    static final int TRANSACTION_onRecordFileList = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
    public static boolean setDefaultImpl(com.senptec.can.ICanClientInterface impl) {
      // Only one user of this interface can use this function
      // at a time. This is a heuristic to detect if two different
      // users in the same process use this function.
      if (Stub.Proxy.sDefaultImpl != null) {
        throw new IllegalStateException("setDefaultImpl() called twice");
      }
      if (impl != null) {
        Stub.Proxy.sDefaultImpl = impl;
        return true;
      }
      return false;
    }
    public static com.senptec.can.ICanClientInterface getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  // 服务端向客户端转发数据

  public void onReceiveData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException;
  // 服务端向客户端返回最后发送的数据

  public void onSendData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException;
  // 服务端向客户端返回录像列表
  // gsonData <=> RecordResponse

  public void onRecordFileList(java.lang.String tag, java.lang.String gsonData) throws android.os.RemoteException;
}
