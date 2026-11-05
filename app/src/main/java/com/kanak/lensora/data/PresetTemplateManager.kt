package com.kanak.lensora.data

/**
 * Represents a shareable 1-tap cinematic preset template file.
 */
data class CinematicPresetTemplate(
    val id: String,
    val name: String,
    val author: String = "Kanak Saini",
    val parameters: ColorGradingParameters = ColorGradingParameters()
)

/**
 * Manager handling preset loading, built-in cinematic grades (Green Lake, Maldives, etc.),
 * and shareable 1-tap JSON template serialization/deserialization.
 */
object PresetTemplateManager {

    val BUILTIN_PRESETS: List<CinematicPresetTemplate> = listOf(
        CinematicPresetTemplate(
            id = "green_lake",
            name = "Green Lake",
            parameters = ColorGradingParameters(
                brightness = 0.05f,
                contrast = 1.2f,
                saturation = 1.15f,
                temperature = 0.15f,
                highlights = -0.1f,
                shadows = 0.1f,
                intensity = 1.0f
            )
        ),
        CinematicPresetTemplate(
            id = "maldives",
            name = "Maldives",
            parameters = ColorGradingParameters(
                brightness = 0.10f,
                contrast = 1.25f,
                saturation = 1.30f,
                temperature = -0.20f,
                highlights = 0.1f,
                shadows = 0.05f,
                intensity = 1.0f
            )
        ),
        CinematicPresetTemplate(
            id = "teal_orange",
            name = "Teal & Orange",
            parameters = ColorGradingParameters(
                brightness = 0.02f,
                contrast = 1.35f,
                saturation = 1.20f,
                temperature = 0.25f,
                intensity = 1.0f
            )
        ),
        CinematicPresetTemplate(
            id = "monochrome_noir",
            name = "Monochrome Noir",
            parameters = ColorGradingParameters(
                brightness = -0.05f,
                contrast = 1.40f,
                saturation = 0.0f,
                intensity = 1.0f
            )
        )
    )

    fun getPresetById(id: String): CinematicPresetTemplate? {
        return BUILTIN_PRESETS.find { it.id == id }
    }

    /**
     * Serializes a preset template to JSON string.
     */
    fun toJson(template: CinematicPresetTemplate): String {
        val p = template.parameters
        return """
            {
              "id": "${template.id}",
              "name": "${template.name}",
              "author": "${template.author}",
              "brightness": ${p.brightness},
              "contrast": ${p.contrast},
              "saturation": ${p.saturation},
              "brilliance": ${p.brilliance},
              "sharpen": ${p.sharpen},
              "clarity": ${p.clarity},
              "highlights": ${p.highlights},
              "shadows": ${p.shadows},
              "temperature": ${p.temperature},
              "hue": ${p.hue},
              "intensity": ${p.intensity}
            }
        """.trimIndent()
    }

    /**
     * Deserializes a preset template from JSON string.
     */
    fun fromJson(jsonStr: String): CinematicPresetTemplate? {
        return try {
            val id = extractJsonString(jsonStr, "id") ?: "custom"
            val name = extractJsonString(jsonStr, "name") ?: "Custom Preset"
            val author = extractJsonString(jsonStr, "author") ?: "Kanak Saini"

            val brightness = extractJsonFloat(jsonStr, "brightness") ?: 0.0f
            val contrast = extractJsonFloat(jsonStr, "contrast") ?: 1.0f
            val saturation = extractJsonFloat(jsonStr, "saturation") ?: 1.0f
            val brilliance = extractJsonFloat(jsonStr, "brilliance") ?: 0.0f
            val sharpen = extractJsonFloat(jsonStr, "sharpen") ?: 0.0f
            val clarity = extractJsonFloat(jsonStr, "clarity") ?: 0.0f
            val highlights = extractJsonFloat(jsonStr, "highlights") ?: 0.0f
            val shadows = extractJsonFloat(jsonStr, "shadows") ?: 0.0f
            val temperature = extractJsonFloat(jsonStr, "temperature") ?: 0.0f
            val hue = extractJsonFloat(jsonStr, "hue") ?: 0.0f
            val intensity = extractJsonFloat(jsonStr, "intensity") ?: 1.0f

            CinematicPresetTemplate(
                id = id,
                name = name,
                author = author,
                parameters = ColorGradingParameters(
                    brightness = brightness,
                    contrast = contrast,
                    saturation = saturation,
                    brilliance = brilliance,
                    sharpen = sharpen,
                    clarity = clarity,
                    highlights = highlights,
                    shadows = shadows,
                    temperature = temperature,
                    hue = hue,
                    intensity = intensity
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractJsonString(json: String, key: String): String? {
        val regex = Regex(""""$key"\s*:\s*"([^"]+)"""")
        return regex.find(json)?.groupValues?.get(1)
    }

    private fun extractJsonFloat(json: String, key: String): Float? {
        val regex = Regex(""""$key"\s*:\s*([-+]?[0-9]*\.?[0-9]+)""")
        return regex.find(json)?.groupValues?.get(1)?.toFloatOrNull()
    }
}
