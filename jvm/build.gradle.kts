plugins {
	id("kotlin-platform-jvm")
}

val kotlinVersion: String by rootProject.extra

dependencies {
	val common = project(":common")
	compile(common)
	expectedBy(common)
	compile(kotlin("stdlib-jdk8", kotlinVersion))
	testCompile(kotlin("test-junit", kotlinVersion))
}