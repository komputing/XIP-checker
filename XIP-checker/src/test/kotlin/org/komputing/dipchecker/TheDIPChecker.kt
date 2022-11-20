package org.komputing.dipchecker

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import kotlin.test.assertFailsWith

class TheDIPChecker {

    private val config = CheckConfig("DIP",
        mapOf(
            "Title" to { },
            "Status" to { value ->
                if (!listOf(
                        "DRAFT",
                        "Accepted",
                        "Active",
                        "Draft",
                        "Review",
                        "Final",
                        "Superseded",
                        "Rejected"
                    ).contains(value)
                ) throw IllegalArgumentException("Invalid status $value")
            },
            "Themes" to { },
            "Tags" to { },
            "Authors" to { },
            "Created" to { checkDate(it) }),
        mapOf(
            "requires" to {
                if (!Regex("[0-9]+(, [0-9]+)*").matches(it)) throw IllegalArgumentException("Invalid requires $it")
            },
            "Discussion" to { },
            "Resources Required" to { },
            "Updated" to { checkDate(it) }
        )
    )

    @Test
    fun shouldPassForValidDIPFolder() {
        assertThat(checkFolder(File(javaClass.getResource("/valid").toURI()), config)).startsWith("Successfully")
    }

    @Test
    fun shouldFailOnExtraFile() {
        assertFailsWith(FoundExtraFileException::class) {
            checkFolder(File(javaClass.getResource("/invalid/extraFile").toURI()), config)
        }
    }

    @Test
    fun shouldFailOnHeaderNotClosed() {
        assertFailsWith(HeaderNotClosed::class) {
            checkFolder(File(javaClass.getResource("/invalid/headerNotClosed").toURI()), config)
        }
    }

    @Test
    fun shouldFailOnInvalidHeaderStart() {
        assertFailsWith(InvalidHeaderStart::class) {
            checkFolder(File(javaClass.getResource("/invalid/invalidHeaderStart").toURI()), config)
        }
    }

    @Test
    fun shouldFailOnInvalidHeader() {
        assertFailsWith(InvalidHeaderStart::class) {
            checkFolder(File(javaClass.getResource("/invalid/invalidHeader").toURI()), config)
        }
    }


    @Test
    fun shouldFailOnMissingHeader() {
        assertFailsWith(InvalidHeaderStart::class) {
            checkFolder(File(javaClass.getResource("/invalid/missingHeader").toURI()), config)
        }
    }


    @Test
    fun shouldFailOnInvalidDate() {
        assertFailsWith(InvalidDateException::class) {
            checkFolder(File(javaClass.getResource("/invalid/invalidDate").toURI()), config)
        }
    }

    @Test
    fun shouldFailOnInvalidMDFileName() {
        assertFailsWith(MDMustStartWithDIPException::class) {
            checkFolder(File(javaClass.getResource("/invalid/mdMustStartWithDIP").toURI()), config)
        }
    }

    @Test
    fun shouldFailOnIMDNotEndingWithNumber() {
        assertFailsWith(MDMustEndWithNUmber::class) {
            checkFolder(File(javaClass.getResource("/invalid/mdMustEndWithNumber").toURI()), config)
        }
    }

    @Test
    fun shouldFailOnDIPNumberMissMatch() {
        assertFailsWith(DIPHeaderNumberDoesNotMatchFilename::class) {
            checkFolder(File(javaClass.getResource("/invalid/dipNumbersMustMatch").toURI()), config)
        }
    }

    @Test
    fun shouldFailForNonExistingPath() {
        assertFailsWith(FolderMustExist::class) {
            checkFolder(File("yolo"), config)
        }
    }

}