package com.challa.core.app.domain

data class AppVersion(val android: Version, val ios: Version) {
    data class Version(val minimumVersion: String, val latestVersion: String, val storeUrl: String) {
        fun isUpdateRequired(currentVersion: String): Boolean = compare(currentVersion, minimumVersion) < 0

        fun isUpdateAvailable(currentVersion: String): Boolean = compare(currentVersion, latestVersion) < 0

        private fun compare(clientVersion: String, policyVersion: String): Int {
            val (clientMajor, clientMinor, clientPatch) = clientVersion.split(".").map(String::toInt)
            val (policyMajor, policyMinor, policyPatch) = policyVersion.split(".").map(String::toInt)

            if (clientMajor != policyMajor) {
                return clientMajor.compareTo(policyMajor)
            }

            if (clientMinor != policyMinor) {
                return clientMinor.compareTo(policyMinor)
            }

            return clientPatch.compareTo(policyPatch)
        }
    }

    fun getVersionByOs(os: AppOs): Version = when (os) {
        AppOs.ANDROID -> android
        AppOs.IOS -> ios
    }
}
