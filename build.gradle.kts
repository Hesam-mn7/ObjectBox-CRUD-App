// Top-level build file for ObjectBox + Kotlin DSL

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("io.objectbox:objectbox-gradle-plugin:5.0.1")
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}
