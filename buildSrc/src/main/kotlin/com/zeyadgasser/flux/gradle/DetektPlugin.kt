package com.zeyadgasser.flux.gradle

//import io.gitlab.arturbosch.detekt.DetektPlugin
//import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

class DetektPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        apply<DetektPlugin>()

//        configure<DetektExtension> {
//            ignoreFailures = true
//            config.from(files("$rootDir/config/detekt/detekt.yml"))
//            baseline = file("$projectDir/detekt-baseline.xml")
//
//            reports {
//                html.enabled = true
//                xml.enabled = true
//                txt.enabled = false
//                sarif.enabled = false
//            }
//        }
//
//        dependencies {
//            "detektPlugins"(CoreLib.Detekt.glovoRules)
//        }
    }

}
