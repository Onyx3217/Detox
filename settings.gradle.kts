pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "detox"
include(":app")
include(":core:model")
include(":core:domain")
include(":core:data")
include(":core:system")
include(":core:designsystem")
include(":core:common")
include(":feature:dashboard")
include(":feature:blocker")
