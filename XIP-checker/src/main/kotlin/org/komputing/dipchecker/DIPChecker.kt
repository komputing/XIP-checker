package  org.komputing.dipchecker

import org.komputing.dipchecker.ParseState.* import java.io.File
import java.lang.Exception
import java.time.LocalDate
import java.time.format.DateTimeFormatter
enum class ParseState { START, HEADER, BODY }
fun checkDate(value: String) {
    try {
        LocalDate.from( DateTimeFormatter.ofPattern("yyyy-M-d").parse(value))
    } catch (e: Exception) {
        throw InvalidDateException("Illegal date $value ")
    }
}

fun checkMarkDown(file: File, config: CheckConfig) {
    val prefix = config.xipAbbreviation + "-"
    if (!file.name.startsWith(prefix)) throw MDMustStartWithDIPException()
    val dipNumberFromFile = file.nameWithoutExtension.removePrefix(prefix)
    if (dipNumberFromFile.toIntOrNull() == null) throw MDMustEndWithNUmber(dipNumberFromFile)

    var parseState = START
    val headers = mutableListOf<String>()

    file.readText().lines().forEach { s ->
        when (parseState) {
            START -> {
                if (s != "---")
                    throw InvalidHeaderStart(file.name)
                else
                    parseState = ParseState.HEADER
            }

            HEADER -> {
                val headerKeyValueList = s.split(": ")
                val headerName = headerKeyValueList.first()
                val headerValue = headerKeyValueList.last()
                if (s == "---") parseState = BODY
                else if (headerKeyValueList.size != 2) throw InvalidHeaderException(s, file.name)
                else if (!config.allHeaders.contains(headerName)) throw InvalidHeaderException(s, file.name)
                else if (headerName == "DIP") {
                    val dipNumberFromHeader = headerKeyValueList.last()
                    if (dipNumberFromHeader != dipNumberFromFile) throw DIPHeaderNumberDoesNotMatchFilename(dipNumberFromFile, dipNumberFromHeader)
                }
                else config.allHeaders[headerName]?.invoke(headerValue)
                headers.add(headerName)
            }


            else -> {}
        }
    }

    if (!headers.containsAll(config.mandatoryHeaders.keys)) throw MissingHeaderException(config.mandatoryHeaders.keys.subtract(headers.toSet()).joinToString(), file.name)

    if (parseState != BODY) throw HeaderNotClosed(file.name)

}

fun checkFolder(folder: File, config: CheckConfig): String? {

    if (!folder.exists()) throw FolderMustExist(folder.absolutePath)

    var processed = 0
    folder.walk().filter { it != folder }.forEach {

        when (it.extension) {
            "md" -> checkMarkDown(it, config)
            else -> throw FoundExtraFileException(it.name)
        }
        processed++
    }
    return "Successfully checked $processed files"
}