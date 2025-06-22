plugins { `kotlin-dsl` }

repositories {
    google()
    mavenCentral()
}

dependencies {
    val junitJupiterVersion = "5.10.3"
    testImplementation("org.junit.jupiter:junit-jupiter-api:$junitJupiterVersion")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitJupiterVersion")
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("PASSED", "SKIPPED", "FAILED", "STANDARD_OUT", "STANDARD_ERROR")
        showStandardStreams = true
    }
}
