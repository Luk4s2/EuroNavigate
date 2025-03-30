import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	kotlin("plugin.serialization") version "1.9.22"
}

val localProperties = Properties()
val file = rootProject.file("local.properties")

if (file.exists()) {
	localProperties.load(file.inputStream())
}
val apiKey = requireNotNull(localProperties.getProperty("MAPS_API_KEY")) {
	"MAPS_API_KEY is missing from local.properties"
}

android {
	namespace = "eu.plantpal.euronavigate"
	compileSdk = 35

	defaultConfig {
		applicationId = "eu.plantpal.euronavigate"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

		manifestPlaceholders["MAPS_API_KEY"] = apiKey
	}

	buildTypes {
		release {
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	kotlinOptions {
		jvmTarget = "11"
	}
	buildFeatures {
		compose = true
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	implementation(libs.androidx.activity.compose)
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.ui)
	implementation(libs.androidx.ui.graphics)
	implementation(libs.androidx.ui.tooling.preview)
	implementation(libs.androidx.material3)
	implementation(libs.androidx.navigation.runtime.ktx)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.ui.test.junit4)
	debugImplementation(libs.androidx.ui.tooling)
	debugImplementation(libs.androidx.ui.test.manifest)
	implementation(libs.play.services.location)
	implementation(libs.play.services.maps)
	implementation(libs.kotlinx.serialization.json)
	implementation(libs.androidx.navigation.compose)
	implementation(libs.kotlinx.coroutines.play.services)
	implementation(libs.datastore.preferences)
	implementation("androidx.compose.material:material-icons-extended")

}