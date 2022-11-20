package org.komputing.dipchecker

class CheckConfig(
    val xipAbbreviation: String,
    _mandatoryHeaders: Map<String, (value: String) -> Unit>,
    optionalHeaders: Map<String, (value: String) -> Unit>
) {
    val mandatoryHeaders = _mandatoryHeaders + (xipAbbreviation to { true })
    val allHeaders = mandatoryHeaders + optionalHeaders
}