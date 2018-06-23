buildscript {
	var kotlinVersion : String by extra
	kotlinVersion = "1.2.50"

	repositories {
		mavenCentral()
	}

	dependencies {
		classpath(kotlin("gradle-plugin", kotlinVersion))
	}
}

allprojects {
	group = "com.github.HoshinoTented"
	version = "1.0-SNAPSHOT"

	repositories {
		jcenter()
	}
}