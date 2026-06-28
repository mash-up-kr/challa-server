plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "challa-server"

include("challa-core")
include("challa-web")
include("challa-external-in")
include("challa-external-out")
include("challa-persistence")
include("contracts")
include("bootstrap")