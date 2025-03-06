package com.senptec.control.exceptions;

import com.quectel.qcarapi.util.QCarError;
import com.quectel.qcarapi.util.QCarLog;

public class ErrorHandler implements QCarError.OnErrorCB {

    private static final String TAG = "ErrorHandler";

    @Override
    public void onError(int errType, int errCode, byte[] errText, int csiNum, int channelNum) {
        QCarLog.e(QCarLog.LOG_MODULE_APP, TAG, "errType = " + errType + " errCode = " + errCode + " errText = " + new String(errText) + " csiNum = " + csiNum + " channelNum = " + channelNum);
    }
}
