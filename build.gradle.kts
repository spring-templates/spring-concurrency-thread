plugins {
    java
    id("org.springframework.boot") version "3.2.4"
    id("io.spring.dependency-management") version "1.1.4"
}

group = "com.thread"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks {
    all {
        outputs.cacheIf { true }
    }
}

tasks.test {
    jvmArgs("-Xshare:off")
    useJUnitPlatform()
}

apply(from = "dumpJsa.gradle.kts")
tasks.withType<JavaExec> {
    dependsOn("dumpJsa")
}