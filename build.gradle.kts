// Top-level build file for ObjectBox + Kotlin DSL

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.objectbox.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
