plugins {
    id("java")
}

group = "org.processing"
version = "4.4"

repositories {
    mavenCentral()
    maven { url = uri("https://jogamp.org/deployment/maven") }
}

dependencies {
    implementation(project(":app"))
    implementation(project(":core"))

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}