plugins {
    id("java")
    id("maven-publish")
}
group = "com.github.SkriptDev"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains:annotations:15.0")
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.4.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.4.1")
    implementation ("com.google.code.gson:gson:2.13.2")
    testImplementation("junit:junit:4.12")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.4.1")
}

tasks {
    compileJava {
        options.release = 25
        options.compilerArgs.add("-Xlint:unchecked")
        options.compilerArgs.add("-Xlint:deprecation")
    }
    register("sourcesJar", Jar::class) {
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }
    javadoc {
        title = "Skript-Parser API - " + project.version
        options.overview = "src/main/javadoc/overview.html"
        options.encoding = Charsets.UTF_8.name()
        exclude(
            "com/github/skriptdev/skript/plugin/elements",
            "com/github/skriptdev/skript/plugin/command"
        )
        (options as StandardJavadocDocletOptions).links(
            "https://javadoc.io/doc/org.jetbrains/annotations/latest/",
            "https://skriptdev.github.io/docs/skript-parser/latest/"
        )
        (options as StandardJavadocDocletOptions).tags = listOf(
            "attr", // Example: replace "attr" with your unknown tag name
            "todo:X" // Example of explicitly excluding the @todo tag
        )
        (options as StandardJavadocDocletOptions).addBooleanOption("Xdoclint:none", true)
    }
    java {
        withSourcesJar()
    }
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["java"])
                group = "com.github.SkriptDev"
                version = project.version as String
                artifactId = "skript-parser"
            }
        }
    }
}

