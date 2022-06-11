group = "org.liamjd.bascule"
version = "0.0.13"

val bascule_lib_version = "0.0.28"
val mockk_version = "1.10.0"
val spek_version = "2.0.6"
val flexmark_version = "0.61.0"

plugins {
	kotlin("jvm") version "1.6.21"
	id("com.github.johnrengelman.shadow") version "6.1.0"
	kotlin("plugin.serialization") version "1.6.21"
}

repositories {
	mavenCentral()
	jcenter()
	mavenLocal()
}

dependencies {
	// stdlib
	implementation(kotlin("stdlib"))
	// bascule library
	implementation("org.liamjd.bascule-lib:bascule-lib:$bascule_lib_version")
	// pdf generation
	implementation("org.apache.xmlgraphics:fop:2.3")
	// jsoup HTML parser library
	implementation("org.jsoup:jsoup:1.11.3")
	// amazon aws s3 functions
	implementation("com.amazonaws:aws-java-sdk-s3:1.11.515")
	// kotlinx serialization for lunr json
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3")
	// markdown - probably want to be more selective with this!
	implementation("com.vladsch.flexmark:flexmark-all:$flexmark_version")

	// testing
	testImplementation("org.jetbrains.kotlin:kotlin-test:1.6.21")
	testImplementation("org.spekframework.spek2:spek-dsl-jvm:$spek_version")
	testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:$spek_version")
	testImplementation("io.mockk:mockk:$mockk_version")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.4")
}
