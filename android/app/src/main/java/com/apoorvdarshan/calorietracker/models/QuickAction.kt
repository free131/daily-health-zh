package com.apoorvdarshan.calorietracker.models

enum class QuickAction(private val englishTitle: String) {
    CAMERA("Camera + Note"),
    PHOTOS("Photos"),
    VOICE("Voice"),
    TEXT("Text"),
    BARCODE("Barcode"),
    FAVORITES("Favorites"),
    FREQUENT("Frequent"),
    RECENT("Recent"),
    MANUAL("Manual"),
    FASTING("Fasting");

    val title: String get() = if (java.util.Locale.getDefault().language == "zh") when (this) {
        CAMERA -> "拍照识别"
        PHOTOS -> "相册识别"
        TEXT -> "文字记录"
        MANUAL -> "手动记录"
        FAVORITES -> "收藏餐食"
        FREQUENT -> "常吃餐食"
        RECENT -> "最近餐食"
        FASTING -> "断食记录"
        VOICE -> "语音（已移除）"
        BARCODE -> "条码（已移除）"
    } else englishTitle

    companion object {
        val Defaults = listOf(CAMERA, PHOTOS, TEXT)

        fun fromStorage(value: String?, fallback: QuickAction = CAMERA): QuickAction =
            entries.firstOrNull { it.name == value && it != VOICE && it != BARCODE } ?: fallback.takeUnless { it == VOICE || it == BARCODE } ?: CAMERA
    }
}

data class QuickActionRequest(
    val action: QuickAction,
    val id: Long = System.nanoTime()
)
