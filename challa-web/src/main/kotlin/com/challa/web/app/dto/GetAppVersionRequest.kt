package com.challa.web.app.dto

import com.challa.core.app.domain.AppOs
import com.challa.core.app.port.input.GetAppVersionCommand

data class GetAppVersionRequest(val os: AppOs, val version: String) {
    fun toCommand() = GetAppVersionCommand(
        os = os,
        version = version
    )
}
