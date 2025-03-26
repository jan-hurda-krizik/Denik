buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Verze pluginu Gradle se může lišit
        classpath("com.android.tools.build:gradle:8.8.0")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
