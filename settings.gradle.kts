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
rootProject.name = "Flux"
include(
    ":app",
    ":core",
    ":mvi",
    ":domain",
    ":mvvm",
    ":shared-composables",
    ":test-base",
    ":domain-pure",
    ":data",
)
