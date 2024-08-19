pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
//dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        google()
//        mavenCentral()
//        maven(url ="https://www.jitpack.io")
//        maven(url = "https://repo.eclipse.org/content/repositories/paho-releases/")
//    }
//}

rootProject.name = "AirMonitorApp"
include(":app")
 