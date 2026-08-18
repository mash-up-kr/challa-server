package com.challa.core.app.port.output

import com.challa.core.app.domain.AppVersion

interface AppVersionProvider {
    fun getAppVersion(): AppVersion
}
