rootProject.name = "Hisp"
include(
    ":common",
    ":jvm",
    ":js")

findProject(":jvm")!!.name = "HispJava"
findProject(":js")!!.name = "HispJS"
