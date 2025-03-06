LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional

src_dir := app/src/main/java

LOCAL_SRC_FILES := $(call all-java-files-under, $(src_dir))

LOCAL_MANIFEST_FILE:=app/src/main/AndroidManifest.xml

LOCAL_RESOURCE_DIR:=$(LOCAL_PATH)/app/src/main/res

LOCAL_CERTIFICATE := platform
        
LOCAL_PACKAGE_NAME := SpdHome_301

LOCAL_STATIC_JAVA_LIBRARIES := MediaJAR RadioJAR BluetoothJAR

LOCAL_STATIC_ANDROID_LIBRARIES := \
	  androidx.legacy_legacy-support-v4 \
	  androidx.annotation_annotation

LOCAL_PRIVATE_PLATFORM_APIS = true


LOCAL_USE_AAPT2 := true

LOCAL_PROGUARD_ENABLED := disabled

#LOCAL_PROGUARD_FLAG_FILES := app/proguard-rules.pro

include $(BUILD_PACKAGE)

include $(call all-makefiles-under,$(LOCAL_PATH)) 
