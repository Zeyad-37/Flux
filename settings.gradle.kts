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
    ":domain-pure",
    ":mvvm",
    ":shared-composables",
    ":test-base",
    ":data",
    ":benchmark",
    ":lint-rules",
)
