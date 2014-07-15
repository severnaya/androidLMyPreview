package com.shurpo.myapplication.app.camerabasic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.*;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.*;
import android.widget.Toast;
import com.shurpo.myapplication.app.R;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Maksim on 11.07.2014.
 */
public class Camera2BasicFragment extends Fragment {

    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    private AutoFitTextureView textureView;

    private CaptureRequest.Builder previewBuilder;

    private CameraCaptureSession previewSession;

    private CameraDevice cameraDevice;

    private TextureView.SurfaceTextureListener surfaceTextureListener = new TextureView.SurfaceTextureListener() {
        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
            configureTransform(width, height);
            startPreview();
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture surfaceTexture, int width, int height) {
            configureTransform(width, height);
            startPreview();
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture surfaceTexture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture surfaceTexture) {

        }
    };

    private Size previewSize;

    private boolean openingCamera;

    private CameraDevice.StateListener stateListener = new CameraDevice.StateListener() {
        @Override
        public void onOpened(CameraDevice mCameraDevice) {
            cameraDevice = mCameraDevice;
            startPreview();
            openingCamera = false;
        }

        @Override
        public void onDisconnected(CameraDevice mCameraDevice) {
            mCameraDevice.close();
            cameraDevice = null;
            openingCamera = false;
        }

        @Override
        public void onError(CameraDevice mCameraDevice, int error) {
            mCameraDevice.close();
            cameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
            openingCamera = false;
        }
    };

    public Camera2BasicFragment() {
    }

    public static Camera2BasicFragment newInstance(){
        Camera2BasicFragment fragment = new Camera2BasicFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    @SuppressLint("Override")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camera2_basic, container, false);
    }

    @Override
    @SuppressLint("Override")
    public void onViewCreated(View view, Bundle savedInstanceState) {
        textureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        textureView.setSurfaceTextureListener(surfaceTextureListener);
        view.findViewById(R.id.picture).setOnClickListener(onClickListener());
        view.findViewById(R.id.info).setOnClickListener(onClickListener());
    }

    @Override
    @SuppressLint("Override")
    public void onResume() {
        super.onResume();
        openCamera();
    }

    @SuppressLint("Override")
    @Override
    public void onPause() {
        super.onPause();
        if (null != cameraDevice) {
            cameraDevice.close();
            cameraDevice = null;
        }
    }

    /**
     * Opens a {@link CameraDevice}. The result is listened by `stateListener`.
     */

    private void openCamera(){
        Activity activity = getActivity();
        if (activity == null || activity.isFinishing() || openingCamera){
            return;
        }
        openingCamera = true;
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = manager.getCameraIdList()[0];

            // To get a list of available sizes of camera preview, we retrieve an instance of
            // StreamConfigurationMap from CameraCharacteristics
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            previewSize = map.getOutputSizes(SurfaceTexture.class)[0];

            //We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = getResources().getConfiguration().orientation;
            if(orientation == Configuration.ORIENTATION_LANDSCAPE){
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else {
                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }

            // We are opening the camera with a listener. When it is ready, onOpened of
            // mStateListener is called.
            manager.openCamera(cameraId, stateListener, null);
        } catch (CameraAccessException e) {
            Toast.makeText(activity, "Cannot access the camera.", Toast.LENGTH_SHORT).show();
            activity.finish();
        }
    }

    /**
     * Starts the camera preview.
     */
    private void startPreview() {
        if (null == cameraDevice || !textureView.isAvailable() || null == previewSize) {
            return;
        }

        try {
            SurfaceTexture texture = textureView.getSurfaceTexture();
            assert texture != null;
            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            previewBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            previewBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            cameraDevice.createCaptureSession(Arrays.asList(surface), new CameraCaptureSession.StateListener() {
                @Override
                public void onConfigured(CameraCaptureSession cameraCaptureSession) {
                    // When the session is ready, we start displaying the preview.
                    previewSession = cameraCaptureSession;
                    updatePreview();
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
                    Activity activity = getActivity();
                    if (null != activity) {
                        Toast.makeText(activity, "Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }, null);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the camera preview. {@link #startPreview()} needs to be called in advance.
     */
    private void updatePreview() {
        if (null == cameraDevice) {
            return;
        }
        try {
            // The camera preview can be run in a background thread. This is a Handler for camera
            // preview.
            setUpCaptureRequestBuilder(previewBuilder);
            HandlerThread thread = new HandlerThread("CameraPreview");
            thread.start();
            Handler backgroundHandler = new Handler(thread.getLooper());

            // Finally, we start displaying the camera preview.
            previewSession.setRepeatingRequest(previewBuilder.build(), null, backgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fills in parameters of the {@link CaptureRequest.Builder}.
     *
     * @param builder The builder for a {@link CaptureRequest}
     */
    private void setUpCaptureRequestBuilder(CaptureRequest.Builder builder) {
        // In this sample, we just let the camera device pick the automatic settings.
        builder.set(CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
    }

    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `textureView`.
     * This method should be called after the camera preview size is determined in openCamera and
     * also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == textureView || null == previewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, previewSize.getHeight(), previewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if(Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation){
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / previewSize.getHeight(),
                    (float) viewWidth / previewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        }
        textureView.setTransform(matrix);
    }

    /**
     * Takes a picture.
     */
    private void takePicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == cameraDevice) {
                return;
            }
            CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
            // Pick the best JPEG size that can be captured with this CameraDevice.
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraDevice.getId());
            Size[] jpegSizes = null;
            if(characteristics != null){
                jpegSizes = characteristics
                        .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(ImageFormat.JPEG);
            }
            int width = 640;
            int height = 480;
            if (jpegSizes != null && 0 < jpegSizes.length) {
                width = jpegSizes[0].getWidth();
                height = jpegSizes[0].getHeight();
            }

            // We use an ImageReader to get a JPEG from CameraDevice.
            // Here, we create a new ImageReader and prepare its Surface as an output from camera.
            final ImageReader reader = ImageReader.newInstance(width, height, ImageFormat.JPEG, 1);
            List<Surface> outputSurfaces = new ArrayList<>(2);
            outputSurfaces.add(reader.getSurface());
            outputSurfaces.add(new Surface(textureView.getSurfaceTexture()));

            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(reader.getSurface());
            setUpCaptureRequestBuilder(captureBuilder);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, ORIENTATIONS.get(rotation));

            // Output file
            final File file = new File(activity.getExternalFilesDir(null), "pic.jpg");

            // This listener is called when a image is ready in ImageReader
            ImageReader.OnImageAvailableListener readerListener = new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader imageReader) {
                    Image image = null;
                    try {
                        image = reader.acquireLatestImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        save(bytes);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        if (image != null) {
                            image.close();
                        }
                    }
                }

                private void save(byte[] bytes) throws IOException {
                    OutputStream outputStream = null;
                    try {
                        outputStream = new FileOutputStream(file);
                        outputStream.write(bytes);
                    }finally {
                        if (null != outputStream) {
                            outputStream.close();
                        }
                    }

                }
            };

            // We create a Handler since we want to handle the result JPEG in a background thread
            HandlerThread thread = new HandlerThread("CameraPicture");
            thread.start();
            final Handler backgroundHandler = new Handler(thread.getLooper());
            reader.setOnImageAvailableListener(readerListener, backgroundHandler);

            // This listener is called when the capture is completed.
            // Note that the JPEG data is not available in this listener, but in the
            // ImageReader.OnImageAvailableListener we created above.
            final CameraCaptureSession.CaptureListener captureListener = new CameraCaptureSession.CaptureListener() {
                @Override
                public void onCaptureCompleted(CameraCaptureSession session, CaptureRequest request, TotalCaptureResult result) {
                    super.onCaptureCompleted(session, request, result);
                    Toast.makeText(activity, "Saved: " + file, Toast.LENGTH_SHORT).show();
                    // We restart the preview when the capture is completed
                    startPreview();
                }
            };

            // Finally, we can start a new CameraCaptureSession to take a picture.
            cameraDevice.createCaptureSession(outputSurfaces, new CameraCaptureSession.StateListener() {
                @Override
                public void onConfigured(CameraCaptureSession session) {
                    try {
                        session.capture(captureBuilder.build(), captureListener,
                                backgroundHandler);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {

                }
            }, backgroundHandler);

        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private View.OnClickListener onClickListener(){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.picture:
                        takePicture();
                        break;
                    case R.id.info:
                        Activity activity = getActivity();
                        if (null != activity) {
                            new AlertDialog.Builder(activity)
                                    .setMessage(R.string.intro_message)
                                    .setPositiveButton(android.R.string.ok, null)
                                    .show();
                        }
                        break;
                }
            }
        };
    }
}
