// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "7.4.0" apply false
    id("com.android.library") version "7.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.16.0"
    id("org.jetbrains.kotlin.jvm") version "1.8.0" apply false
}

allprojects {
    apply(plugin = "kover")

    kover {
        // true to disable instrumentation and all Kover tasks in this project
        isDisabled.set(false)
        // to change engine, use kotlinx.kover.api.IntellijEngine("xxx") or kotlinx.kover.api.JacocoEngine("xxx")
        engine.set(kotlinx.kover.api.DefaultIntellijEngine)
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
            // set of test tasks names to exclude from instrumentation. The results of their execution will not be presented in the report
            excludeTasks += "dummy-tests"
        }
        htmlReport {
            // set to true to run koverHtmlReport task during the execution of the check task (if it exists) of the current project
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
            // set to true to run koverMergedHtmlReport task during the execution of the check task (if it exists) of the current project
            onCheck.set(false)
            // change report directory
            reportDir.set(layout.buildDirectory.dir("my-merged-report/html-result"))
        }
    }
}

subprojects {
//    if (plugins.hasPlugin("kotlin-android")) { // TODO verify if needed
//        with(android()) {
//            testOptions.unitTests.all {
//                if (it.name == "testDebugUnitTest") {
//                    it.extensions.configure(kotlinx.kover.api.KoverTaskExtension::class) {
//                        // set to true to disable instrumentation of this task,
//                        // Kover reports will not depend on the results of its execution
//                        isDisabled.set(false)
//                        // set file name of binary report
//                        reportFile.set(file("$buildDir/custom/debug-report.bin"))
//                        // for details, see "Instrumentation inclusion rules" below
//                        includes.addAll(listOf("com.example.*"))
//                        // for details, see "Instrumentation exclusion rules" below
//                        excludes.addAll(listOf("com.example.subpackage.*"))
//                    }
//                }
//            }
//        }
//    }
    kover {
        instrumentation {
            // exclude testReleaseUnitTest from instrumentation
            excludeTasks += "testReleaseUnitTest"
            excludeTasks += "testReleaseUnitTest"
        }
    }
}

pluginBundle {
    website = "<substitute your project website>"
    vcsUrl = "<uri to project source repository>"
    tags = listOf("tags", "for", "your", "plugins")
}
