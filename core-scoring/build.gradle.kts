plugins {
    kotlin("jvm") version "2.0.21"
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

dependencies {
    testImplementation(libs.junit)
}
