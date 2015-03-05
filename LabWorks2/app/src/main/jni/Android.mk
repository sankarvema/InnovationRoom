LOCAL_PATH          := $(call my-dir)

include $(CLEAR_VARS)

OPENCV_CAMERA_MODULES:=off

include C:\bin\OpenCV-2.4.9-android-sdk\sdk\native\jni\OpenCV.mk

LOCAL_MODULE            := text_recogn
LOCAL_SRC_FILES         := NativeInterface.cpp DetectText.cpp
LOCAL_LDLIBS    += -llog -ldl

include $(BUILD_SHARED_LIBRARY)

