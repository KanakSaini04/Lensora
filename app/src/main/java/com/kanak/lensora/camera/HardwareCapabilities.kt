package com.kanak.lensora.camera

import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.util.Range
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo

/**
 * Adaptive pixel-binning modes supported by the 200MP Samsung ISOCELL HP3 sensor.
 */
enum class PixelBinningMode(val displayName: String, val pixelSizeMicrons: Float) {
    NIGHT_12_5MP("12.5MP Night (16-in-1 Tetra²pixel)", 2.24f),
    PORTRAIT_50MP("50MP Portrait (4-in-1 Tetra pixel)", 1.12f),
    ULTRA_HD_200MP("200MP Ultra-HD (Full Resolution)", 0.56f)
}

/**
 * In-sensor crop region representation for lossless digital zoom.
 */
data class SensorCropRegion(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int
) {
    val width: Int get() = right - left
    val height: Int get() = bottom - top

    fun toAndroidRect(): Rect = Rect(left, top, right, bottom)
}

/**
 * Encapsulates physical hardware capabilities for a camera sensor lens.
 */
data class LensCapabilities(
    val cameraId: String = "",
    val lensFacing: Int = CameraCharacteristics.LENS_FACING_BACK,
    val isoMin: Int? = null,
    val isoMax: Int? = null,
    val exposureTimeMinNs: Long? = null,
    val exposureTimeMaxNs: Long? = null,
    val minFocusDistanceDiopters: Float? = null,
    val isFixedFocus: Boolean = true,
    val supportsManualFocus: Boolean = false,
    val hasOis: Boolean = false,
    val activeArraySize: Rect? = null,
    val availableApertures: FloatArray = floatArrayOf(),
    val focalLengths: FloatArray = floatArrayOf(),
    val isRawSupported: Boolean = false,
    val hardwareLevel: Int = CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED,
    val activeBinningMode: PixelBinningMode = PixelBinningMode.ULTRA_HD_200MP
) {
    val isoRange: Range<Int>?
        get() = if (isoMin != null && isoMax != null) Range(isoMin, isoMax) else null

    val exposureTimeRangeNs: Range<Long>?
        get() = if (exposureTimeMinNs != null && exposureTimeMaxNs != null) Range(exposureTimeMinNs, exposureTimeMaxNs) else null

    /**
     * Safely checks if manual focus override is supported on this lens.
     * Prevents manual diopter override crashes on fixed-focus auxiliary lenses.
     */
    fun canOverrideFocusManually(): Boolean {
        return !isFixedFocus && supportsManualFocus && (minFocusDistanceDiopters != null && minFocusDistanceDiopters > 0.0f)
    }

    /**
     * Calculates the in-sensor crop region [SCALER_CROP_REGION] for lossless zoom.
     */
    fun calculateInSensorCropRegion(zoomRatio: Float, fullWidth: Int = 16384, fullHeight: Int = 12288): SensorCropRegion {
        if (zoomRatio <= 1.0f) return SensorCropRegion(0, 0, fullWidth, fullHeight)
        val cropWidth = (fullWidth / zoomRatio).toInt().coerceAtLeast(1)
        val cropHeight = (fullHeight / zoomRatio).toInt().coerceAtLeast(1)
        val left = (fullWidth - cropWidth) / 2
        val top = (fullHeight - cropHeight) / 2
        return SensorCropRegion(left, top, left + cropWidth, top + cropHeight)
    }

    /**
     * Formats exposure time range in nanoseconds into human-readable seconds format (e.g. "1/8000s - 2s").
     */
    fun formattedExposureTimeRange(): String {
        val minNs = exposureTimeMinNs ?: return "N/A"
        val maxNs = exposureTimeMaxNs ?: return "N/A"
        val minSec = minNs / 1_000_000_000.0
        val maxSec = maxNs / 1_000_000_000.0
        return "${formatSeconds(minSec)} - ${formatSeconds(maxSec)}"
    }

    private fun formatSeconds(sec: Double): String {
        return if (sec <= 0.0) {
            "0s"
        } else if (sec < 1.0) {
            val denom = (1.0 / sec).toInt()
            "1/${denom}s"
        } else if (sec % 1.0 == 0.0) {
            "${sec.toLong()}s"
        } else {
            "${sec}s"
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LensCapabilities

        if (cameraId != other.cameraId) return false
        if (lensFacing != other.lensFacing) return false
        if (isoMin != other.isoMin) return false
        if (isoMax != other.isoMax) return false
        if (exposureTimeMinNs != other.exposureTimeMinNs) return false
        if (exposureTimeMaxNs != other.exposureTimeMaxNs) return false
        if (minFocusDistanceDiopters != other.minFocusDistanceDiopters) return false
        if (isFixedFocus != other.isFixedFocus) return false
        if (supportsManualFocus != other.supportsManualFocus) return false
        if (hasOis != other.hasOis) return false
        if (activeArraySize != other.activeArraySize) return false
        if (!availableApertures.contentEquals(other.availableApertures)) return false
        if (!focalLengths.contentEquals(other.focalLengths)) return false
        if (isRawSupported != other.isRawSupported) return false
        if (hardwareLevel != other.hardwareLevel) return false
        if (activeBinningMode != other.activeBinningMode) return false

        return true
    }

    override fun hashCode(): Int {
        var result = cameraId.hashCode()
        result = 31 * result + lensFacing
        result = 31 * result + (isoMin ?: 0)
        result = 31 * result + (isoMax ?: 0)
        result = 31 * result + (exposureTimeMinNs?.hashCode() ?: 0)
        result = 31 * result + (exposureTimeMaxNs?.hashCode() ?: 0)
        result = 31 * result + (minFocusDistanceDiopters?.hashCode() ?: 0)
        result = 31 * result + isFixedFocus.hashCode()
        result = 31 * result + supportsManualFocus.hashCode()
        result = 31 * result + hasOis.hashCode()
        result = 31 * result + (activeArraySize?.hashCode() ?: 0)
        result = 31 * result + availableApertures.contentHashCode()
        result = 31 * result + focalLengths.contentHashCode()
        result = 31 * result + isRawSupported.hashCode()
        result = 31 * result + hardwareLevel
        result = 31 * result + activeBinningMode.hashCode()
        return result
    }
}

/**
 * Inspector component leveraging Camera2Interop to extract exact physical camera hardware capabilities
 * across 0.6x Ultrawide, 1x Main (200MP ISOCELL HP3), and Telephoto/Macro lenses.
 */
object HardwareCapabilitiesInspector {

    /**
     * Inspects physical capabilities from a CameraX [CameraInfo] instance.
     */
    @OptIn(ExperimentalCamera2Interop::class)
    fun inspect(cameraInfo: CameraInfo): LensCapabilities {
        val camera2Info = Camera2CameraInfo.from(cameraInfo)
        val cameraId = camera2Info.cameraId ?: ""

        val isoRange = camera2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
        val exposureRange = camera2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
        val minFocusDistance = camera2Info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
        val activeArray = camera2Info.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val apertures = camera2Info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES) ?: floatArrayOf()
        val focalLengths = camera2Info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) ?: floatArrayOf()
        val lensFacing = camera2Info.getCameraCharacteristic(CameraCharacteristics.LENS_FACING) ?: CameraCharacteristics.LENS_FACING_BACK
        val hwLevel = camera2Info.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
            ?: CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED

        val oisModes = camera2Info.getCameraCharacteristic(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
        val hasOis = oisModes?.contains(CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON) == true

        val capabilitiesKeys = camera2Info.getCameraCharacteristic(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        val isRawSupported = capabilitiesKeys?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) == true

        // Fixed-focus safety checks for auxiliary lenses
        val isFixedFocus = minFocusDistance == null || minFocusDistance == 0.0f
        val afModes = camera2Info.getCameraCharacteristic(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
        val supportsAfOff = afModes?.contains(CameraMetadata.CONTROL_AF_MODE_OFF) == true
        val supportsManualFocus = !isFixedFocus && supportsAfOff

        return LensCapabilities(
            cameraId = cameraId,
            lensFacing = lensFacing,
            isoMin = isoRange?.lower,
            isoMax = isoRange?.upper,
            exposureTimeMinNs = exposureRange?.lower,
            exposureTimeMaxNs = exposureRange?.upper,
            minFocusDistanceDiopters = minFocusDistance,
            isFixedFocus = isFixedFocus,
            supportsManualFocus = supportsManualFocus,
            hasOis = hasOis,
            activeArraySize = activeArray,
            availableApertures = apertures,
            focalLengths = focalLengths,
            isRawSupported = isRawSupported,
            hardwareLevel = hwLevel
        )
    }

    /**
     * Inspects physical capabilities directly from Android [CameraCharacteristics].
     */
    fun inspectCharacteristics(characteristics: CameraCharacteristics, cameraId: String = ""): LensCapabilities {
        val isoRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
        val exposureRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
        val minFocusDistance = characteristics.get(CameraCharacteristics.LENS_INFO_MINIMUM_FOCUS_DISTANCE)
        val activeArray = characteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)
        val apertures = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES) ?: floatArrayOf()
        val focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS) ?: floatArrayOf()
        val lensFacing = characteristics.get(CameraCharacteristics.LENS_FACING) ?: CameraCharacteristics.LENS_FACING_BACK
        val hwLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
            ?: CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED

        val oisModes = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
        val hasOis = oisModes?.contains(CameraMetadata.LENS_OPTICAL_STABILIZATION_MODE_ON) == true

        val capabilitiesKeys = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
        val isRawSupported = capabilitiesKeys?.contains(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW) == true

        // Fixed-focus safety checks for auxiliary lenses
        val isFixedFocus = minFocusDistance == null || minFocusDistance == 0.0f
        val afModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
        val supportsAfOff = afModes?.contains(CameraMetadata.CONTROL_AF_MODE_OFF) == true
        val supportsManualFocus = !isFixedFocus && supportsAfOff

        return LensCapabilities(
            cameraId = cameraId,
            lensFacing = lensFacing,
            isoMin = isoRange?.lower,
            isoMax = isoRange?.upper,
            exposureTimeMinNs = exposureRange?.lower,
            exposureTimeMaxNs = exposureRange?.upper,
            minFocusDistanceDiopters = minFocusDistance,
            isFixedFocus = isFixedFocus,
            supportsManualFocus = supportsManualFocus,
            hasOis = hasOis,
            activeArraySize = activeArray,
            availableApertures = apertures,
            focalLengths = focalLengths,
            isRawSupported = isRawSupported,
            hardwareLevel = hwLevel
        )
    }
}
