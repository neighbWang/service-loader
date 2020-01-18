plugins {
    kotlin("jvm") version "1.3.41" apply false
    kotlin("kapt") version "1.3.21" apply false
    id("io.codearte.nexus-staging") version "0.21.2"
    id("de.marcphilipp.nexus-publish") version "0.4.0" apply false
}

val OSSRH_USERNAME = project.properties["OSSRH_USERNAME"] as? String ?: System.getenv("OSSRH_USERNAME")
val OSSRH_PASSWORD = project.properties["OSSRH_PASSWORD"] as? String ?: System.getenv("OSSRH_PASSWORD")

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "de.marcphilipp.nexus-publish")

    group = "io.johnsonlee.booster"
    version = "1.0.0"

    repositories {
        mavenLocal()
        mavenCentral()
        google()
        jcenter()
    }

    java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    val sourcesJar by this@subprojects.tasks.registering(Jar::class) {
        dependsOn(JavaPlugin.CLASSES_TASK_NAME)
        archiveClassifier.set("sources")
        from(sourceSets.main.get().allSource)
    }

    val javadocJar by this@subprojects.tasks.registering(Jar::class) {
        dependsOn(JavaPlugin.JAVADOC_TASK_NAME)
        archiveClassifier.set("javadoc")
        from(tasks["javadoc"])
    }


    nexusPublishing {
        repositories {
            sonatype {
                username.set(OSSRH_USERNAME)
                password.set(OSSRH_PASSWORD)
            }
        }
    }

    publishing {
        repositories {
            maven {
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            }
        }
        publications {
            register("mavenJava", MavenPublication::class) {
                groupId = "${project.group}"
                artifactId = project.name
                version = "${project.version}"

                from(components["java"])

                artifact(sourcesJar.get())
                artifact(javadocJar.get())

                pom.withXml {
                    asNode().apply {
                        appendNode("name", project.name)
                        appendNode("url", "https://github.com/johnsonlee/booster-pngquant-provider")
                        appendNode("description", project.description ?: project.name)
                        appendNode("scm").apply {
                            appendNode("connection", "scm:git:git://github.com/johnsonlee/booster-pngquant-provider.git")
                            appendNode("developerConnection", "scm:git:git@github.com:johnsonlee/booster-pngquant-provider.git")
                            appendNode("url", "https://github.com/johnsonlee/booster-pngquant-provider")
                        }
                        appendNode("licenses").apply {
                            appendNode("license").apply {
                                appendNode("name", "GPL License")
                                appendNode("url", "https://www.gnu.org/licenses/gpl-3.0.txt")
                            }
                        }
                        appendNode("developers").apply {
                            appendNode("developer").apply {
                                appendNode("id", "johnsonlee")
                                appendNode("name", "Johnson Lee")
                                appendNode("email", "g.johnsonlee@gmail.com")
                            }
                        }
                    }
                }
            }
        }
    }

    signing {
        sign(publishing.publications["mavenJava"])
    }
}

nexusStaging {
    packageGroup = "io.johnsonlee"
    username = OSSRH_USERNAME
    password = OSSRH_PASSWORD
    numberOfRetries = 50
    delayBetweenRetriesInMillis = 3000
}

fun Project.java(configure: JavaPluginExtension.() -> Unit): Unit =
        (this as ExtensionAware).extensions.configure("java", configure)

val Project.sourceSets: SourceSetContainer
    get() = (this as ExtensionAware).extensions.getByName("sourceSets") as SourceSetContainer

val SourceSetContainer.main: NamedDomainObjectProvider<SourceSet>
    get() = named<SourceSet>("main")

val Project.nexusPublishing: de.marcphilipp.gradle.nexus.NexusPublishExtension
    get() = (this as ExtensionAware).extensions.getByName("nexusPublishing") as de.marcphilipp.gradle.nexus.NexusPublishExtension

fun Project.nexusPublishing(configure: de.marcphilipp.gradle.nexus.NexusPublishExtension.() -> Unit): Unit =
        (this as ExtensionAware).extensions.configure("nexusPublishing", configure)

val Project.publishing: PublishingExtension
    get() =
        (this as ExtensionAware).extensions.getByName("publishing") as PublishingExtension

fun Project.publishing(configure: PublishingExtension.() -> Unit): Unit =
        (this as ExtensionAware).extensions.configure("publishing", configure)

val Project.signing: SigningExtension
    get() =
        (this as ExtensionAware).extensions.getByName("signing") as SigningExtension

fun Project.signing(configure: SigningExtension.() -> Unit): Unit =
        (this as ExtensionAware).extensions.configure("signing", configure)
