/*
 * This file is auto-generated.  DO NOT MODIFY.
 */
package com.senptec.can;
// Declare any non-default types here with import statements
// 服务端在客户端的回调

public interface ICanServerInterface extends android.os.IInterface
{
  /** Default implementation for ICanServerInterface. */
  public static class Default implements com.senptec.can.ICanServerInterface
  {
    // 客户端通过绑定服务拿到类对象，并通过此方法向服务端注册自己的方法

    @Override public void setCallback(java.lang.String packageName, com.senptec.can.ICanClientInterface callBack) throws android.os.RemoteException
    {
    }
    // 客户端向服务端发送数据

    @Override public void onSendData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException
    {
    }
    @Override
    public android.os.IBinder asBinder() {
      return null;
    }
  }
  /** Local-side IPC implementation stub class. */
  public static abstract class Stub extends android.os.Binder implements com.senptec.can.ICanServerInterface
  {
    private static final java.lang.String DESCRIPTOR = "com.senptec.can.ICanServerInterface";
    /** Construct the stub at attach it to the interface. */
    public Stub()
    {
      this.attachInterface(this, DESCRIPTOR);
    }
    /**
     * Cast an IBinder object into an com.senptec.can.ICanServerInterface interface,
     * generating a proxy if needed.
     */
    public static com.senptec.can.ICanServerInterface asInterface(android.os.IBinder obj)
    {
      if ((obj==null)) {
        return null;
      }
      android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
      if (((iin!=null)&&(iin instanceof com.senptec.can.ICanServerInterface))) {
        return ((com.senptec.can.ICanServerInterface)iin);
      }
      return new com.senptec.can.ICanServerInterface.Stub.Proxy(obj);
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
        case TRANSACTION_setCallback:
        {
          data.enforceInterface(descriptor);
          java.lang.String _arg0;
          _arg0 = data.readString();
          com.senptec.can.ICanClientInterface _arg1;
          _arg1 = com.senptec.can.ICanClientInterface.Stub.asInterface(data.readStrongBinder());
          this.setCallback(_arg0, _arg1);
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
        default:
        {
          return super.onTransact(code, data, reply, flags);
        }
      }
    }
    private static class Proxy implements com.senptec.can.ICanServerInterface
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
      // 客户端通过绑定服务拿到类对象，并通过此方法向服务端注册自己的方法

      @Override public void setCallback(java.lang.String packageName, com.senptec.can.ICanClientInterface callBack) throws android.os.RemoteException
      {
        android.os.Parcel _data = android.os.Parcel.obtain();
        android.os.Parcel _reply = android.os.Parcel.obtain();
        try {
          _data.writeInterfaceToken(DESCRIPTOR);
          _data.writeString(packageName);
          _data.writeStrongBinder((((callBack!=null))?(callBack.asBinder()):(null)));
          boolean _status = mRemote.transact(Stub.TRANSACTION_setCallback, _data, _reply, 0);
          if (!_status && getDefaultImpl() != null) {
            getDefaultImpl().setCallback(packageName, callBack);
            return;
          }
          _reply.readException();
        }
        finally {
          _reply.recycle();
          _data.recycle();
        }
      }
      // 客户端向服务端发送数据

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
      public static com.senptec.can.ICanServerInterface sDefaultImpl;
    }
    static final int TRANSACTION_setCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    static final int TRANSACTION_onSendData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
    public static boolean setDefaultImpl(com.senptec.can.ICanServerInterface impl) {
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
    public static com.senptec.can.ICanServerInterface getDefaultImpl() {
      return Stub.Proxy.sDefaultImpl;
    }
  }
  // 客户端通过绑定服务拿到类对象，并通过此方法向服务端注册自己的方法

  public void setCallback(java.lang.String packageName, com.senptec.can.ICanClientInterface callBack) throws android.os.RemoteException;
  // 客户端向服务端发送数据

  public void onSendData(java.lang.String id, java.lang.String gsonData) throws android.os.RemoteException;
}
