import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  kotlin("jvm") version "1.4.20"
  kotlin("plugin.serialization") version "1.4.10"
}

group = "thuytrinh"
version = "1.0-SNAPSHOT"

repositories {
  jcenter()
  maven(url = "https://kotlin.bintray.com/kotlinx/")
}

dependencies {
  implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
  implementation("com.squareup.moshi:moshi-kotlin:1.9.3")
  implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.0.1")
  implementation("com.squareup.okio:okio:2.8.0")
  implementation("org.koin:koin-core:2.2.0-rc-2")
  implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.1.0")

  testImplementation("junit", "junit", "4.12")
  testImplementation("org.assertj:assertj-core:3.17.2")
  testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.2")
  testImplementation("app.cash.turbine:turbine:0.3.0")
}

tasks.withType<KotlinCompile>().all {
  kotlinOptions.freeCompilerArgs += listOf(
    "-Xopt-in=kotlin.time.ExperimentalTime",
    "-Xopt-in=kotlin.ExperimentalUnsignedTypes",
    "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
    "-Xopt-in=kotlinx.coroutines.FlowPreview"
  )
}
