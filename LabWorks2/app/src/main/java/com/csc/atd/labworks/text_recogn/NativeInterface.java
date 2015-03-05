package com.csc.atd.labworks.text_recogn;

import android.content.res.AssetManager;

public class NativeInterface {

    public native String pingLibrary();
    private native long create();
    private native void destroy(long detectPtr);
    private native int[] getBoundingBoxes(long detectPtr, long matAddress);

    private long detectPtr = 0;

    public NativeInterface(AssetManager am) {
        detectPtr = create();
    }

    @Override
    protected void finalize() throws Throwable {
        if(detectPtr != 0) {
            destroy(detectPtr);
        }
        super.finalize();
    }

    public int[] getBoundingBoxes(long matAddress) {
        return getBoundingBoxes(detectPtr, matAddress);
    }

}
