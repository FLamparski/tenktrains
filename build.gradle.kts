import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.6.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	kotlin("jvm") version "1.6.0"
	kotlin("plugin.spring") version "1.6.0"
}

group = "com.filipwieland"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

sourceSets {
	create("generated")
}

dependencies {
	"generatedApi"("com.sun.xml.bind:jaxb-impl:2.3.3")
	"generatedApi"("com.sun.xml.ws:jaxws-ri:2.3.2")
	implementation(sourceSets["generated"].output)
	implementation("com.sun.xml.bind:jaxb-impl:2.3.3")
	implementation("com.sun.xml.ws:jaxws-ri:2.3.2")
	implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
	implementation("org.springframework.boot:spring-boot-starter-quartz")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.junit.jupiter:junit-jupiter:5.7.0")
	testImplementation("io.kotest:kotest-assertions-core:4.0.7")
	testImplementation("io.kotest:kotest-assertions:4.0.7")
	testImplementation("io.mockk:mockk:1.12.1")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register("generateCodeFromWsdl", Exec::class.java) {
	val wsdl2java = layout.projectDirectory.file("apache-cxf-3.4.5/bin/wsdl2java.bat").toString()
	val wsdlUrl = "https://lite.realtime.nationalrail.co.uk/OpenLDBWS/wsdl.aspx?ver=2017-10-01"
	val packageName = "xjc.nationalrail.ldb"
	val outDir = "src/generated/java"
	commandLine = listOf(wsdl2java, "-client", "-autoNameResolution", "-exsh", "true", "-d", outDir, "-p", packageName, wsdlUrl)
}
