package ignitron

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
class Location (
    val file: String,
    val start: Line,
    val end: Line
)

@Serializable
class Line (
    val line: Int,
    val column: Int
)

@Serializable
class Change (
    val location: List<Location>,
    val action: String,
    val changes: String
)

@Serializable
class Warning (
    val location: List<Location>,
    val reason: String,
    val changes_content: String,
    val base_content: String
)

@Serializable
class Report (
    var changes: List<Change>,
    var warnings: List<Warning>
)