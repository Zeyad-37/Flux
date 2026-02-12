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
    ":feature:mvi",
    ":feature:domain",
    ":feature:domain-pure",
    ":feature:mvvm",
    ":shared-composables",
    ":test-base",
    ":feature:data",
    ":benchmark",
    ":lint-rules",
)
