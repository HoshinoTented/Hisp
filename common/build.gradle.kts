plugins {
	id ("kotlin-platform-common")
}

val kotlinVersion : String by rootProject.extra

dependencies {
    compile(kotlin("stdlib-common", kotlinVersion))
}
