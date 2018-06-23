import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile
import java.util.concurrent.Callable

plugins {
	id("kotlin-platform-js")
}

val kotlinVersion : String by rootProject.extra

val compileKotlin2Js : Kotlin2JsCompile by tasks
val assemble : DefaultTask by tasks

dependencies {
	val common = project(":common")
	compile(common)
	expectedBy(common)
	compile(kotlin("stdlib-js", kotlinVersion))
}

val assembleWeb = task<Sync>("assembleWeb") {
	group = assemble.group
	configurations.compile.forEach { file ->
		from(zipTree(file.absolutePath))
	}

	from(compileKotlin2Js.destinationDir)
	into(buildDir.resolve("out"))

	dependsOn(tasks["classes"])
}

assemble.dependsOn(assembleWeb)
