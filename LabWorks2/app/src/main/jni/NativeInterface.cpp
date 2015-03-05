#include "com_csc_atd_labworks_text_recogn_NativeInterface.h"
#include "DetectText.h"

#include <jni.h>
#include <stdio.h>

#include <android/log.h>

namespace {
    DetectText* toDetectTextNative(jlong detectPtr) {
        return reinterpret_cast<DetectText*>(detectPtr);
    }
}


JNIEXPORT jstring JNICALL Java_com_csc_atd_labworks_text_1recogn_NativeInterface_pingLibrary
  (JNIEnv *env, jobject jobj)
{
    __android_log_print(ANDROID_LOG_INFO, "text_recogn", "pingLibrary() invoked");
    return env->NewStringUTF("Native detect text called");
}

JNIEXPORT jint JNICALL JNI_OnLoad( JavaVM *vm, void *pvt )
{
    __android_log_print(ANDROID_LOG_INFO, "text_recogn", "jniLoad() invoked");
    fprintf( stdout, "* JNI_OnLoad called\n" );
    return JNI_VERSION_1_2;
}

JNIEXPORT jlong JNICALL Java_com_csc_atd_labworks_text_1recogn_NativeInterface_create
  (JNIEnv *env, jobject jobj)
{
    DetectText* dt = new DetectText();
    return reinterpret_cast<jlong>(dt);
}


JNIEXPORT void JNICALL Java_com_csc_atd_labworks_text_1recogn_NativeInterface_destroy
  (JNIEnv *env, jobject jobj, jlong detectPtr)
{
    delete toDetectTextNative(detectPtr);
}

JNIEXPORT jintArray JNICALL Java_com_csc_atd_labworks_text_1recogn_NativeInterface_getBoundingBoxes
  (JNIEnv *env, jobject jobj, jlong detectPtr, jlong matAddress)
{
    Mat* nativeMat =(Mat*)matAddress;
    vector<Rect> boundingBoxes = toDetectTextNative(detectPtr)->getBoundingBoxes(*nativeMat);
    jintArray result = env->NewIntArray(boundingBoxes.size() * 4);

    if (result == NULL) {
        return NULL;
    }

    jint tmp_arr[boundingBoxes.size() * 4];

    int idx = 0;
    for (int i = 0; i < boundingBoxes.size(); i++) {
        tmp_arr[idx++] = boundingBoxes[i].x;
        tmp_arr[idx++] = boundingBoxes[i].y;
        tmp_arr[idx++] = boundingBoxes[i].width;
        tmp_arr[idx++] = boundingBoxes[i].height;
    }

    env->SetIntArrayRegion(result, 0, boundingBoxes.size() * 4, tmp_arr);
    return result;
}