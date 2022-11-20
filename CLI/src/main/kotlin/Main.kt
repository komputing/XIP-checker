import org.komputing.dipchecker.CheckConfig
import org.komputing.dipchecker.checkDate
import org.komputing.dipchecker.checkFolder
import java.io.File

fun main() {
    println(
        checkFolder(
            File("/home/ligi/git/ChainAgnostic/CAIPs/CAIPs"),
            CheckConfig("caip",
                mapOf(
                    "title" to { },
                    "status" to { value ->
                        if (!listOf("Draft", "Active", "Review", "Final", "Superseded").contains(value)) throw IllegalArgumentException("Invalid status $value")
                    },
                    "type" to { value ->
                        if (!listOf("Meta", "Standard").contains(value)) throw IllegalArgumentException("Invalid status $value")
                    },
                    "author" to { },
                    "created" to { checkDate(it) }),
                mapOf(
                    "requires" to {
                        if (!Regex("[0-9]+(, [0-9]+)*").matches(it)) throw IllegalArgumentException("Invalid requires $it")
                    },
                    "discussions-to" to { },
                    "superseded-by" to { },
                    "updated" to { checkDate(it) }
                )
            )
        )
    )
}