import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    `java-library`
    java
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("com.github.ben-manes.versions") version "0.42.0"
}

group = "com.github.md"
version = "1.0"

repositories {
    mavenCentral()
}

tasks.withType<ShadowJar> {
    manifest {
//        attributes(mapOf("Main-Class" to "trie.TrieKt"))
        attributes(mapOf("Main-Class" to "trie.ByteTrie"))
    }
}
dependencies {

//    implementation(kotlin("stdlib-jdk8"))
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}
