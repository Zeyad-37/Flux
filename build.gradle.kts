import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import kotlinx.kover.api.DefaultIntellijEngine

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.0.1" apply false
    id("com.android.library") version "8.0.1" apply false
    id("org.jetbrains.kotlin.android") version "1.8.21" apply false
    id("com.google.dagger.hilt.android") version "2.46" apply false
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("org.jetbrains.kotlin.jvm") version "1.8.21" apply false
    id("io.gitlab.arturbosch.detekt") version ("1.22.0") apply false
    id("app.cash.paparazzi") version ("1.2.0") apply false
    id("com.android.test") version "8.0.1" apply false
}

version = "1.0.0"

val detektReportMergeSarif by tasks.registering(ReportMergeTask::class) {
    output.set(layout.buildDirectory.file("reports/detekt/merge.sarif"))
}

allprojects {
    configureDetekt()
    configureKover()
}

subprojects {
    kover {
        instrumentation {
            // exclude testReleaseUnitTest from instrumentation
            excludeTasks += "testReleaseUnitTest"
            excludeTasks += "testReleaseUnitTest"
        }
    }
}

fun Project.configureKover() {
    apply(plugin = "kover")
    kover {
        // true to disable instrumentation and all Kover tasks in this project
        isDisabled.set(false)
        // to change engine, use kotlinx.kover.api.IntellijEngine("xxx") or kotlinx.kover.api.JacocoEngine("xxx")
        engine.set(DefaultIntellijEngine)
        // common filters for all default Kover tasks
        filters {
            // common class filter for all default Kover tasks in this project
            classes {
                // class inclusion rules
                includes += "com.zeyadgasser.*"
                // class exclusion rules
                excludes += "*.databinding.*"
            }
        }
        instrumentation {
            // set of test tasks names to exclude from instrumentation.
            // The results of their execution will not be presented in the report
            excludeTasks += "dummy-tests"
        }
        htmlReport {
            //set true to run koverHtmlReport task during the exec of the check task (if exists) of the current project
            onCheck.set(false)
            // change report directory
            reportDir.set(layout.buildDirectory.dir("my-project-report/html-result"))
        }
    }
    koverMerged {
        // create Kover merged report tasks from this project and subprojects with enabled Kover plugin
        enable()
        // common filters for all default Kover merged tasks
        filters {
            // common class filter for all default Kover merged tasks
            classes {
                // class inclusion rules
                includes += "com.zeyadgasser.*"
                // class exclusion rules
                excludes += listOf(
                    "*Fragment",
                    "*Fragment\$*",
                    "*Activity",
                    "*Activity\$*",
                    "*.databinding.*", // ViewBinding
                    "org.jetbrains.kover_android_kts_example.BuildConfig"
                )
            }
        }
        htmlReport {
            //set to true to run koverMergedHtmlReport task during the exec of the check task (if exists)
            // of the current project
            onCheck.set(false)
            // change report directory
            reportDir.set(layout.buildDirectory.dir("my-merged-report/html-result"))
        }
    }
}

fun Project.configureDetekt() {
    apply(plugin = "io.gitlab.arturbosch.detekt")
    tasks.withType<Detekt>().configureEach {
        buildUponDefaultConfig = true
//        baseline.set(file("$rootDir/config/detekt/baseline.xml"))
        jvmTarget = JavaVersion.VERSION_17.toString()
        reports {
            html.required.set(true)
            sarif.required.set(true)
        }
        basePath = rootDir.absolutePath
        finalizedBy(detektReportMergeSarif)
    }
    detektReportMergeSarif { input.from(tasks.withType<Detekt>().map { it.sarifReportFile }) }
    tasks.withType<DetektCreateBaselineTask>()
        .configureEach { jvmTarget = JavaVersion.VERSION_11.toString() }
}

//tasks.register("incrementVersionName") {
//    val currentVersionName = project.version as String
//    // Parse the current version name into its major, minor, and patch components
//    val regex = Regex("(\\d+)\\.(\\d+)\\.(\\d+)")
//    val matcher =
//        regex.find(currentVersionName) ?: error("Invalid version name: $currentVersionName")
//    val major = matcher.groupValues[1].toInt()
//    var minor = matcher.groupValues[2].toInt()
//    var patch = matcher.groupValues[3].toInt()
//    // Increment the appropriate component based on the input parameter
//    when (val inputParam = project.properties["input"] as? String ?: "") {
//        "hotfix" -> patch++
//        "release" -> {
//            patch = 0
//            minor++
//        }
//        else -> error("Invalid increment mode: $inputParam")
//    }
//    val newVersionName = "$major.$minor.$patch"
//    project.version = newVersionName
//}
