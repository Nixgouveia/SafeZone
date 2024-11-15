// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Add the secrets gradle plugin to manage API keys securely
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.0"
    alias(libs.plugins.android.application) apply false
    // Other plugins...
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0") // Match the version of Android Gradle Plugin
        // Other classpath dependencies
    }
}