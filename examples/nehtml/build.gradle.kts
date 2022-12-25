plugins {
    application
}

dependencies {
    implementation(project(":parsek"))
}

application {
    mainClass.set("ru.itmo.kt.hw4.nehtml.MainKt")
}
