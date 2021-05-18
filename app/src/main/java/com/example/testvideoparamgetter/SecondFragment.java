package com.example.testvideoparamgetter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SecondFragment extends Fragment {

    private Camera camera;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        view.findViewById(R.id.button_second).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        Map<String, String> runtimeEvnInfo = SecondFragment.getRuntimeEnvInfo();
        TextView textview = view.findViewById(R.id.textview_second);
        runtimeEvnInfo.putAll(this.getCameraParams());
        try {
            runtimeEvnInfo.putAll(this.getCamera2Params());
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        textview.setText(mapToString(this.getActivity(), runtimeEvnInfo));
    }

    private void releaseCameraAndPreview() {
        if (this.camera != null) {
            this.camera.release();
            this.camera = null;
        }
    }

    private static Map<String, String> getRuntimeEnvInfo() {
        Map<String, String> result = new LinkedHashMap<>();

        // OSを特定するたまの上位法
        result.put("os", "Android");
        result.put("os_info1(Build.VERSION.RELEASE)", Build.VERSION.RELEASE);

        // デバイスのモデルを特定するための情報
        result.put("device_info1(Build.MODEL)", Build.MODEL);
        result.put("device_info2(Build.MANUFACTURER)", Build.MANUFACTURER);
        result.put("device_info3(Build.PRODUCT)", Build.PRODUCT);
        result.put("device_info4(Build.ID)", Build.ID);
        result.put("device_info5(Build.DEVICE)", Build.DEVICE);
        result.put("device_info6(Build.BRAND)", Build.BRAND);

        return result;
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this.getActivity(),android.Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED){
            // 許可されている時の処理
        }else{
            //許可されていない時の処理
            if (ActivityCompat.shouldShowRequestPermissionRationale(this.getActivity(), android.Manifest.permission.CAMERA)) {
                //拒否された時 Permissionが必要な理由を表示して再度許可を求めたり、機能を無効にしたりします。
            } else {
                //まだ許可を求める前の時、許可を求めるダイアログを表示します。
                ActivityCompat.requestPermissions(this.getActivity(), new String[]{android.Manifest.permission.CAMERA}, 0);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private Map<String, String> getCamera2Params() throws CameraAccessException {
        Map<String, String> result = new LinkedHashMap<>();

        this.checkCameraPermission();
        this.releaseCameraAndPreview();

        String[] cameraIds;
        CameraManager manager = (CameraManager) this.getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraIds = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            return result;
        }

        for (String cameraId : cameraIds) {
            CameraCharacteristics cameraCharacteristics = manager.getCameraCharacteristics(cameraId);

            if (cameraCharacteristics.get(CameraCharacteristics.LENS_FACING) != CameraCharacteristics.LENS_FACING_FRONT) {
                continue;
            }

            List<CameraCharacteristics.Key<?>> characteristicsKeys = cameraCharacteristics.getKeys();

            for (CameraCharacteristics.Key<?> key : characteristicsKeys) {

                String keyString = key.toString();
                String pureKeyString = keyString.replace("CameraCharacteristics.Key", "");
                pureKeyString = pureKeyString.replace("(", "");
                pureKeyString = pureKeyString.replace(")", "");
                String shortKeyString = pureKeyString.replace("android.","");
                result.put("camera2_" + shortKeyString, CameraReport.cameraConstantStringer(pureKeyString, cameraCharacteristics.get(key)) + "[" + String.valueOf(cameraCharacteristics.get(key))+ "]");
            }
        }

        return result;
    }

    private Map<String, String> getCameraParams() {
        Map<String, String> result = new LinkedHashMap<>();

        this.checkCameraPermission();

        try {
            this.releaseCameraAndPreview();
            this.camera = Camera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        } catch (Exception e) {
            Log.e(getString(R.string.app_name), "failed to open Camera");
            e.printStackTrace();
            result.put("camera_params", "error");
            return result;
        }

        Camera.Parameters parameters = this.camera.getParameters();

        // https://developer.android.com/reference/android/hardware/Camera.Parameters
//        result.put("camera_horizontal_view_angle", String.valueOf(parameters.getHorizontalViewAngle()));
//        result.put("camera_vertical_view_angle", String.valueOf(parameters.getVerticalViewAngle()));
//        result.put("camera_antibanding", String.valueOf(parameters.getAntibanding()));
//        result.put("camera_auto_exposure_lock", String.valueOf(parameters.getAutoExposureLock()));
//        result.put("camera_auto_whitebalance_lock", String.valueOf(parameters.getAutoWhiteBalanceLock()));
//        result.put("camera_auto_coloreffect", String.valueOf(parameters.getColorEffect()));
//        result.put("camera_exposure_compensation", String.valueOf(parameters.getExposureCompensation()));
//        result.put("camera_exposure_compensation_step", String.valueOf(parameters.getExposureCompensationStep()));
//        result.put("camera_flash_mode", String.valueOf(parameters.getFlashMode()));
//        result.put("camera_focus_areas", String.valueOf(parameters.getFocusAreas()));
//        result.put("camera_focal_length", String.valueOf(parameters.getFocalLength()));
//        result.put("camera_focal_mode", String.valueOf(parameters.getFocusMode()));
//
//
//        float[] focalDistances = new float[3];
//        parameters.getFocusDistances(focalDistances);
//        result.put("camera_focal_distances", Arrays.toString(focalDistances));
//
//        result.put("camera_jpeg_quality", String.valueOf(parameters.getJpegQuality()));
//        result.put("camera_jpeg_thumbnail_quality", String.valueOf(parameters.getJpegThumbnailQuality()));
//
//        Camera.Size jpegThumbnailSize = parameters.getJpegThumbnailSize();
//        result.put("camera_jpeg_thumbnail_size", jpegThumbnailSize.width + "x" + jpegThumbnailSize.height);
//
//        result.put("camera_max_exposure_compensation", String.valueOf(parameters.getMaxExposureCompensation()));
//        result.put("camera_min_exposure_compensation", String.valueOf(parameters.getMinExposureCompensation()));
//        result.put("camera_max_num_detected_faces", String.valueOf(parameters.getMaxNumDetectedFaces()));
//        result.put("camera_max_num_focus_areas", String.valueOf(parameters.getMaxNumFocusAreas()));
//        result.put("camera_max_num_metering_areas", String.valueOf(parameters.getMaxNumMeteringAreas()));
//        result.put("camera_max_zoom", String.valueOf(parameters.getMaxZoom()));
//
//        List<Camera.Area> cameraAreas = parameters.getMeteringAreas();
//        result.put("camera_metering_areas", cameraAreas == null ?  "null": cameraAreas.toString());
//        result.put("camera_picture_format", String.valueOf(parameters.getPictureFormat()));
//
//        Camera.Size pictureSize = parameters.getPictureSize();
//        result.put("camera_picture_size", pictureSize.width + "x" + pictureSize.height);
//
//        Camera.Size preferredPreviewSizeForVideo = parameters.getPreferredPreviewSizeForVideo();
//        result.put("camera_preferred_preview_size_for_video", preferredPreviewSizeForVideo.width + "x" + preferredPreviewSizeForVideo.height);
//
//        result.put("camera_preview_format", String.valueOf(parameters.getPreviewFormat()));
//
//        int[] previewFpsRange = new int[2];
//        parameters.getPreviewFpsRange(previewFpsRange);
//        result.put("camera_preview_fps_range", Arrays.toString(previewFpsRange));
//
//        result.put("camera_preview_frame_rate", String.valueOf(parameters.getPreviewFrameRate()));
//        Camera.Size previewSize = parameters.getPreviewSize();
//        result.put("camera_previewSize", previewSize.width + "x" + previewSize.height);
//
//        result.put("camera_scene_mode", parameters.getSceneMode());
//
//        List<String> supportedAntibalancing = parameters.getSupportedAntibanding();
//        result.put("camera_supported_antibalancing", supportedAntibalancing == null ?  "null": supportedAntibalancing.toString());
//
//        List<String> supportedColorEffects = parameters.getSupportedColorEffects();
//        result.put("camera_supported_color_effects", supportedColorEffects == null ?  "null": supportedColorEffects.toString());
//
//        List<String> supportedFlashModes = parameters.getSupportedFlashModes();
//        result.put("camera_supported_flash_modes", supportedFlashModes == null ?  "null": supportedFlashModes.toString());
//
//        List<String> supportedFocusModes = parameters.getSupportedFocusModes();
//        result.put("camera_supported_focus_modes", supportedFocusModes == null ?  "null": supportedFocusModes.toString());
//
//        List<Camera.Size> supportedJpegThumbnailSizes = parameters.getSupportedJpegThumbnailSizes();
//        StringBuilder supportedJpegThumbnailSizesBuilder = new StringBuilder();
//        if (supportedJpegThumbnailSizes != null) {
//            for (Camera.Size x : supportedJpegThumbnailSizes){
//                supportedJpegThumbnailSizesBuilder.append("[" + x.width + "x" + x.height + "], ");
//            }
//        }
//        result.put("camera_supported_jpeg_thumbnail_sizes", supportedJpegThumbnailSizesBuilder.toString());
//
//        List<Integer> supportedPictureFormats = parameters.getSupportedPictureFormats();
//        result.put("camera_supported_picture_formats", supportedPictureFormats == null ?  "null": supportedPictureFormats.toString());
//
//        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
//        StringBuilder supportedPictureSizesBuilder = new StringBuilder();
//        if (supportedPictureSizes != null) {
//            for (Camera.Size x : supportedPictureSizes){
//                supportedPictureSizesBuilder.append("[" + x.width + "x" + x.height + "], ");
//            }
//        }
//        result.put("camera_supported_picture_sizes", supportedPictureSizesBuilder.toString());
//
//        List<Integer> supportedPreviewFormats = parameters.getSupportedPreviewFormats();
//        result.put("camera_supported_preview_formats", supportedPreviewFormats == null ?  "null": supportedPreviewFormats.toString());
//
//        List<int[]> supportedPreviewFpsRange = parameters.getSupportedPreviewFpsRange();
//        StringBuilder supportedPreviewFpsRangeBuilder = new StringBuilder();
//        if (supportedPreviewFpsRange != null) {
//            for (int[] x : supportedPreviewFpsRange){
//                supportedPreviewFpsRangeBuilder.append("[" + Arrays.toString(x) + "], ");
//            }
//        }
//        result.put("camera_supported_preview_fps_range", supportedPreviewFpsRangeBuilder.toString());
//
//
//        result.put("camera_video_stabilization", String.valueOf(parameters.getVideoStabilization()));
//        result.put("camera_white_balance", parameters.getWhiteBalance());

        result.put("camera_params", parameters.flatten().replace(";", System.getProperty("line.separator")));
        return result;
    }

    private static String mapToString(Context context, Map<String, String> map) {
        StringBuilder stringBuilder = new StringBuilder();
        String LINE_SEPARATOR = System.getProperty("line.separator");
        for (String key : map.keySet()) {
            stringBuilder.append(key);
            stringBuilder.append(": ");
            stringBuilder.append(map.get(key));
            stringBuilder.append(", ");
            stringBuilder.append(LINE_SEPARATOR);
        }

        copyToClipboard(context, "", stringBuilder.toString());
        return stringBuilder.toString();
    }

    /**
     * Copy to clipboard.
     *
     * @param context the context to use
     * @param label user-visible label for the clip data
     * @param text the actual text in the clip
     * @return result
     */
    public static void copyToClipboard(Context context, String label, String text) {
        // copy to clipboard
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null == clipboardManager) {
            return;
        }
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
    }
}

// https://github.com/rcsumner/devCam/blob/master/app/src/main/java/com/devcam/CameraReport.java
final class CameraReport {

    static final public Map<String, Map<Integer,String>> sContextMap =
            new HashMap<>();

    // class initialization block, to populate the table of constants
    static {
        Map<Integer,String> subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        sContextMap.put("android.colorCorrection.availableAberrationModes",subMap);
        sContextMap.put("android.colorCorrection.aberrationMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"TRANSFORM_MATRIX");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        sContextMap.put("android.colorCorrection.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"50HZ");
        subMap.put(2,"60HZ");
        subMap.put(3,"AUTO");
        sContextMap.put("android.control.aeAvailableAntibandingModes",subMap);
        sContextMap.put("android.control.aeAntibandingMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"ON");
        subMap.put(2,"ON_AUTO_FLASH");
        subMap.put(3,"ON_ALWAYS_FLASH");
        subMap.put(4,"ON_AUTO_FLASH_REDEYE");
        sContextMap.put("android.control.aeAvailableModes",subMap);
        sContextMap.put("android.control.aeMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"IDLE");
        subMap.put(1,"START");
        subMap.put(2,"CANCEL");
        sContextMap.put("android.control.aePrecaptureTrigger",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"INACTIVE");
        subMap.put(1,"SEARCHING");
        subMap.put(2,"CONVERGED");
        subMap.put(3,"LOCKED");
        subMap.put(4,"FLASH_REQUIRED");
        subMap.put(5,"PRECAPTURE");
        sContextMap.put("android.control.aeState",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"AUTO");
        subMap.put(2,"MACRO");
        subMap.put(3,"CONTINUOUS_VIDEO");
        subMap.put(4,"CONTINUOUS_PICTURE");
        subMap.put(5,"EDOF");
        sContextMap.put("android.control.afAvailableModes",subMap);
        sContextMap.put("android.control.afMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"IDLE");
        subMap.put(1,"START");
        subMap.put(2,"CANCEL");
        sContextMap.put("android.control.afTrigger",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"INACTIVE");
        subMap.put(1,"PASSIVE_SCAN");
        subMap.put(2,"PASSIVE_FOCUSED");
        subMap.put(3,"ACTIVE_SCAN");
        subMap.put(4,"FOCUSED_LOCKED");
        subMap.put(5,"NOT_FOCUSED_LOCKED");
        subMap.put(6,"PASSIVE_UNFOCUSED");
        sContextMap.put("android.control.afState",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"INACTIVE");
        subMap.put(1,"SEARCHING");
        subMap.put(2,"CONVERGED");
        subMap.put(3,"LOCKED");
        sContextMap.put("android.control.awbState",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"AUTO");
        subMap.put(2,"INCANDESCENT");
        subMap.put(3,"FLOURESCENT");
        subMap.put(4,"WARM_FLOURESCENT");
        subMap.put(5,"DAYLIGHT");
        subMap.put(6,"CLOUDY_DAYLIGHT");
        subMap.put(7,"TWILIGHT");
        subMap.put(8,"SHADE");
        sContextMap.put("android.control.awbAvailableModes",subMap);
        sContextMap.put("android.control.awbMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"CUSTOM");
        subMap.put(1,"PREVIEW");
        subMap.put(2,"STILL_CAPTURE");
        subMap.put(3,"VIDEO_RECORD");
        subMap.put(4,"VIDEO_SNAPSHOT");
        subMap.put(5,"ZERO_SHUTTER_LAG");
        subMap.put(6,"MANUAL");
        sContextMap.put("android.control.captureIntent",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"MONO");
        subMap.put(2,"NEGATIVE");
        subMap.put(3,"SOLARIZE");
        subMap.put(4,"SEPIA");
        subMap.put(5,"POSTERIZE");
        subMap.put(6,"WHITEBOARD");
        subMap.put(7,"BLACKBOARD");
        subMap.put(8,"AQUA");
        sContextMap.put("android.control.availableEffects",subMap);
        sContextMap.put("android.control.effectsMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"AUTO");
        subMap.put(2,"USE_SCENE_MODE");
        subMap.put(3,"OFF_KEEP_STATE");
        sContextMap.put("android.control.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"DISABLED");
        subMap.put(1,"FACE_PRIORITY");
        subMap.put(2,"ACTION");
        subMap.put(3,"PORTRAIT");
        subMap.put(4,"LANDSCAPE");
        subMap.put(5,"NIGHT");
        subMap.put(6,"NIGHT_PORTRAIT");
        subMap.put(7,"THEATRE");
        subMap.put(8,"BEACH");
        subMap.put(9,"SNOW");
        subMap.put(10,"SUNSET");
        subMap.put(11,"STEADYPHOTO");
        subMap.put(12,"FIREWORKS");
        subMap.put(13,"SPORTS");
        subMap.put(14,"PARTY");
        subMap.put(15,"CANDLELIGHT");
        subMap.put(16,"BARCODE");
        subMap.put(17,"HIGH_SPEED_VIDEO");
        subMap.put(18,"HDR");
        sContextMap.put("android.control.availableSceneModes",subMap);
        sContextMap.put("android.control.sceneMode",subMap);


        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"ON");
        sContextMap.put("android.control.availableVideoStabilizationModes",subMap);
        sContextMap.put("android.control.videoStabilizationMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        subMap.put(3,"ZERO_SHUTTER_LAG");
        sContextMap.put("android.edge.availableEdgeModes",subMap);
        sContextMap.put("android.edge.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"SINGLE");
        subMap.put(2,"TORCH");
        sContextMap.put("android.flash.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"UNAVAILABLE");
        subMap.put(1,"CHARGING");
        subMap.put(2,"READY");
        subMap.put(3,"FIRED");
        subMap.put(4,"PARTIAL");
        sContextMap.put("android.flash.state",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        sContextMap.put("android.hotPixel.availableHotPixelModes",subMap);
        sContextMap.put("android.hotPixel.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"LIMITED");
        subMap.put(1,"FULL");
        subMap.put(2,"LEGACY");
        sContextMap.put("android.info.supportedHardwareLevel",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"FRONT");
        subMap.put(1,"BACK");
        subMap.put(2,"EXTERNAL");
        sContextMap.put("android.lens.facing",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"ON");
        sContextMap.put("android.lens.info.availableOpticalStabilization",subMap);
        sContextMap.put("android.lens.opticalStabilizationMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"UNCALIBRATED");
        subMap.put(1,"APPROXIMATE");
        subMap.put(2,"CALIBRATED");
        sContextMap.put("android.lens.info.focusDistanceCalibration",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"STATIONARY");
        subMap.put(1,"MOVING");
        sContextMap.put("android.lens.state",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        subMap.put(3,"MINIMAL");
        subMap.put(4,"ZERO_SHUTTER_LAG");
        sContextMap.put("android.noiseReduction.availableNoiseReductionModes",subMap);
        sContextMap.put("android.noiseReduction.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"BACKWARDS_COMPATIBLE");
        subMap.put(1,"MANUAL_SENSOR");
        subMap.put(2,"MANUAL_POST_PROCESSING");
        subMap.put(3,"RAW");
        subMap.put(4,"PRIVATE_REPROCESSING");
        subMap.put(5,"READ_SENSOR_SETTINGS");
        subMap.put(6,"BURST_CAPTURE");
        subMap.put(7,"YUV_REPROCESSING");
        subMap.put(8,"DEPTH_OUTPUT");
        subMap.put(9,"CONSTRAINED_HIGH_SPEED_VIDEO");
        sContextMap.put("android.request.availableCapabilities",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"CENTER_ONLY");
        subMap.put(1,"FREEFORM");
        sContextMap.put("android.scaler.croppingType",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"SOLID_COLOR");
        subMap.put(2,"COLOR_BARS");
        subMap.put(3,"COLOR_BARS_FADE_TO_GRAY");
        subMap.put(4,"PN9");
        subMap.put(5,"CUSTOM1");
        sContextMap.put("android.sensor.availableTestPatternModes",subMap);
        sContextMap.put("android.sensor.testPatternMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"RGGB");
        subMap.put(1,"GRBG");
        subMap.put(2,"GBRG");
        subMap.put(3,"BGGR");
        subMap.put(4,"RGB");
        sContextMap.put("android.sensor.info.colorFilterArrangement",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"UNKNOWN");
        subMap.put(1,"REALTIME");
        sContextMap.put("android.sensor.info.timestampSource",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(10,"CLOUDY_WEATHER");
        subMap.put(14,"COOL_WHITE_FLOURESCENT");
        subMap.put(23,"D50");
        subMap.put(20,"D55");
        subMap.put(21,"D65");
        subMap.put(22,"D75");
        subMap.put(1,"DAYLIGHT");
        subMap.put(12,"DAYLIGHT_FLOURESCENT");
        subMap.put(13,"DAY_WHITE_FLOURESCENT");
        subMap.put(9,"FINE_WEATHER");
        subMap.put(4,"FLASH");
        subMap.put(2,"FLOURESCENT");
        subMap.put(24,"ISO_STUDIO_TUNGSTEN");
        subMap.put(11,"SHADE");
        subMap.put(17,"STANDARD_A");
        subMap.put(18,"STANDARD_B");
        subMap.put(19,"STANDARD_C");
        subMap.put(3,"TUNGSTEN");
        subMap.put(15,"WHITE_FLOURESCENT");
        sContextMap.put("android.sensor.referenceIlluminant1",subMap);
        sContextMap.put("android.sensor.referenceIlluminant2",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        sContextMap.put("android.shading.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"SIMPLE");
        subMap.put(2,"FULL");
        sContextMap.put("android.statistics.info.availableFaceDetectModes",subMap);
        sContextMap.put("android.statistics.faceDetectMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"OFF");
        subMap.put(1,"ON");
        sContextMap.put("android.statistics.lensShadingMapMode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"NONE");
        subMap.put(1,"50HZ");
        subMap.put(2,"60HZ");
        sContextMap.put("android.statistics.sceneFlicker",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"CONTRAST_CURVE");
        subMap.put(1,"FAST");
        subMap.put(2,"HIGH_QUALITY");
        subMap.put(3,"GAMMA_VALUE");
        subMap.put(4,"PRESET_CURVE");
        sContextMap.put("android.tonemap.availableToneMapModes",subMap);
        sContextMap.put("android.tonemap.mode",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(0,"SRGB");
        subMap.put(1,"REC709");
        sContextMap.put("android.tonemap.preset",subMap);

        subMap = new HashMap<Integer,String>();
        subMap.put(256,"JPEG");
        subMap.put(16,"NV16");
        subMap.put(17,"NV21");
        subMap.put(37,"RAW10");
        subMap.put(32,"RAW_SENSOR");
        subMap.put(4,"RGB_565");
        subMap.put(34,"PRIVATE");
        subMap.put(35,"YUV_420_888");
        subMap.put(39,"YUV_422_888");
        subMap.put(40,"YUV_444_888");
        subMap.put(38,"RAW12");
        subMap.put(20,"YUY2");
        subMap.put(41,"FLEX_RGB_888");
        subMap.put(42,"FLEX_RGBA_8888");
        subMap.put(257,"DEPTH_POINT_CLOUD");
        subMap.put(842094169,"YV12");
        subMap.put(1144402265,"DEPTH16");

        sContextMap.put("android.graphics.ImageFormat",subMap);
    }



    // Utility function for turning a metadata constant into a meaningful string
    // based on its context. That is, turns an integer into the right label.
    // For example, cameraConstantStringer("android.lens.facing", 1) returns
    // "BACK" while cameraConstantStringer("android.control.aeMode", 1) returns
    // "ON".
    //
    // - - Parameters - -
    // String contextName : Android domain string from a CameraMetadata field
    // Object value : a value retrieved from some metadata.get(KEY) call
    //
    // Note this function is set up so that it can stringify returns from
    // any metadata returned value, including arrays of labeled values, though
    // some may not be meaningful, e.g. a TonemapCurve's toString() value.

    static public String cameraConstantStringer(
            String contextName,
            Object value){
        // Note this function tries to be extremely general. Values retrieved
        // from a metadata.get(KEY) call are often constants as in the map
        // above, but they can also have intrinsic value, like
        // android.request.pipelineMaxDepth, or may be arrays of values, like
        // android.control.aeAvailableTargetFpsRanges.

        if (sContextMap.containsKey(contextName)){
            try{
                // If contextName is in the context map, there are label values
                // defined for this value/array of values
                Map<Integer,String> context = sContextMap.get(contextName);
                //Log.v(cameraFragment.APP_TAG,"context: " + contextName);
                if (value.getClass().isArray()){
                    int len = Array.getLength(value);
                    String str = "";
                    for (int i=0; i<len; i++){
                        Integer ind = (Integer) Array.get(value,i);
                        // This case statement just for correct useage of element-separating commas
                        if(i==0) {
                            str = str + context.get(ind);
                        } else {
                            str = str + ", " + context.get(ind);
                        }
                    }
                    return str;
                } else
                    // this awkward construction is because SOME values (looking at
                    // you, Reference Illuminant 2!) are Byte instead of Integer.
                    return context.get(Integer.valueOf(value.toString()));
            } catch (RuntimeException re){
                re.printStackTrace();
                return "Unknown value " + value ;
            }
        } else if(value==null){
            return "Null";  // Catches bad behavior
        } else {
            // contextName is not in constant map, so either it is an array
            // of meaningful values, or is a meaningful value itself.
            if (value.getClass().isArray()){
                int len = Array.getLength(value);
                String str = "";
                for (int i=0; i<len; i++){
                    Object ob = (Object) Array.get(value, i);
                    // If the key generated no useful string value, i.e. some default
                    // Object.toString() response instead of a meaningful one, return '<COMPLEX_OBJECT>'
                    if (ob.toString().equals(ob.getClass().getName() + '@' + Integer.toHexString(ob.hashCode()))) {
                        // This case statement just for correct useage of element-separating commas
                        if(i==0) {
                            str = str + "<COMPLEX_OBJECT>";
                        } else {
                            str = str + ", " + "<COMPLEX_OBJECT>";
                        }
                    } else {
                        // This case statement just for correct useage of element-separating commas
                        if(i==0) {
                            str = str + ob.toString();
                        } else {
                            str = str + ", " + ob.toString();
                        }
                    }

                }
                return str;
            } else {
                // If the key generated no useful string value, i.e. some default
                // Object.toString() response instead of a meaningful one, return '<COMPLEX_OBJECT>'
                if (value.toString().equals(value.getClass().getName() + '@' + Integer.toHexString(value.hashCode()))) {
                    return "<COMPLEX_OBJECT>";
                } else {
                    return value.toString();
                }
            }
        }
    }
}
