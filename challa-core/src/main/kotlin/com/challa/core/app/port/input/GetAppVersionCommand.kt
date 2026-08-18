package com.challa.core.app.port.input

import com.challa.core.app.domain.AppOs

data class GetAppVersionCommand(val os: AppOs, val version: String)
