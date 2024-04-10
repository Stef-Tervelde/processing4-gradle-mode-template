import de.undercouch.gradle.tasks.download.Download
import org.gradle.internal.os.OperatingSystem
import java.util.*

plugins {
    id("java")
    id("application")
    id("antlr")
    id("de.undercouch.download") version "5.6.0"
    id("io.github.fvarrui.javapackager.plugin")
}

group = "org.example"
version = "4.4"

repositories {
    mavenCentral()
}

application {
    mainClass = "processing.app.ui.Splash"
}

javapackager {
    mainClass("processing.app.ui.Splash")
    bundleJre(true)
    displayName("Processing")
    name("Processing")
    url("https://processing.org")
    additionalResources( file("build/resources/main").list()?.map { t->  file("build/resources/main/${t}") }?.toMutableList() ?: mutableListOf())
}

sourceSets{
    main{
        resources {
            srcDirs("../shared", "src/main/resources")
        }
    }
}

tasks.register<Download>("openJDK"){
    val version = "17"
    val distribution = "jre"
    var os = System.getProperty("os.name").lowercase()
    var arch = System.getProperty("os.arch").lowercase()

    os = when {
        os.contains("linux") -> "linux"
        os.contains("windows") -> "windows"
        os.contains("mac") || os.contains("darwin") -> "mac"
        os.contains("sunos") || os.contains("solaris") -> "solaris"
        os.contains("aix") -> "aix"
        os.contains("alpine") -> "alpine-linux"
        else -> "linux"
    }

   arch = when (arch) {
        "amd64", "x86_64" -> "x64"
        "x86", "i386", "i486", "i586", "i686" -> "x86"
        "aarch64" -> "aarch64"
        "ppc64" -> "ppc64"
        "ppc64le" -> "ppc64le"
        "s390x" -> "s390x"
        "sparcv9" -> "sparcv9"
        "riscv64" -> "riscv64"
        else -> "x64"
    }

//    API: https://github.com/adoptium/api.adoptium.net/blob/main/docs/cookbook.adoc#example-two-linking-to-the-latest-jdk-or-jre
    val download = "https://api.adoptium.net/v3/binary/latest/${version}/ga/${os}/${arch}/${distribution}/hotspot/normal/eclipse"
    src(download)
    dest(layout.buildDirectory.file("tmp/openjdk"))
}

tasks.register<Copy>("unzipOpenJDK"){
    val os = OperatingSystem.current()

    dependsOn("openJDK")
    dependsOn(tasks.processResources)
    val file = layout.buildDirectory.file("tmp/openjdk")
    if(os.isWindows){
        from(zipTree(file))
    }else{
        from(tarTree(resources.gzip(file)))
    }

    if(os.isMacOsX) {
        into(layout.buildDirectory.file("resources/PlugIns/"))
    }else{
        eachFile{
            path = Regex("jdk-[^/]+/").replaceFirst(path, "/")
        }
        into(layout.buildDirectory.file("resources/main/java"))
    }
}
tasks.compileJava { dependsOn("unzipOpenJDK")}

tasks.register<Copy>("coreJar") {
    group = "build"
    dependsOn(project(":core").tasks.jar)
    dependsOn(tasks.processResources)
    from(project(":core").layout.buildDirectory.dir("libs"))
    into(layout.buildDirectory.file("resources/main/core/library"))
    include("*.jar")
}
tasks.compileJava { dependsOn("coreJar") }

dependencies {
    implementation("com.google.classpath-explorer:classpath-explorer:1.0")
    implementation("org.antlr:antlr4-runtime:4.13.1")
    implementation("org.netbeans.api:org-netbeans-swing-outline:RELEASE210")
    implementation("org.apache.ant:ant:1.10.14")
    implementation("org.eclipse.lsp4j:org.eclipse.lsp4j:0.22.0")
    implementation("org.jsoup:jsoup:1.17.2")

    implementation("org.eclipse.jdt:org.eclipse.jdt.core:3.37.0")

    implementation(project(":app"))
    implementation(project(":core"))

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    antlr("org.antlr:antlr4:4.13.1")
}


tasks.compileJava{
    options.encoding = "UTF-8"
}
tasks.compileTestJava {
    options.encoding = "UTF-8"
}

tasks.test {
    useJUnitPlatform()
}