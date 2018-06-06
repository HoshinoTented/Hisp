plugins {
	id("kotlin-platform-js")
}

val kotlinVersion: String by rootProject.extra

dependencies {
	val common = project(":common")
	compile(common)
	expectedBy(common)
	compile(kotlin("stdlib-js", kotlinVersion))
}