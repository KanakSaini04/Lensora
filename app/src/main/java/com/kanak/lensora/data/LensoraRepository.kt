package com.kanak.lensora.data

/**
 * Data model for cinematic look templates.
 */
data class LensoraTemplate(
    val id: String,
    val name: String,
    val description: String,
    val isCinematic: Boolean = true
)

/**
 * Repository providing access to local cinematic color grading templates and presets.
 */
class LensoraRepository {
    fun getTemplates(): List<LensoraTemplate> {
        return listOf(
            LensoraTemplate("vn_cinematic_01", "Monochrome Noir", "High contrast B&W grading"),
            LensoraTemplate("vn_cinematic_02", "Teal & Orange", "Filmic blockbuster color grade"),
            LensoraTemplate("vn_cinematic_03", "Display P3 Pure", "Wide gamut true-to-life sensor output")
        )
    }
}
