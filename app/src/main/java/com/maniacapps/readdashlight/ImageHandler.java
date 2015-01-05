package com.maniacapps.readdashlight;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

/**
 * Created by Arijit on 1/6/2015.
 */
public class ImageHandler extends SurfaceView implements SurfaceHolder.Callback{

    private ArrayList<Bitmap> mElements;
    private CanvasThread canvasThread;

    public ImageHandler(Context context) {
        super(context);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
