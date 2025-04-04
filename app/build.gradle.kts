plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.kotlin.serialization)
	alias(libs.plugins.hilt)
	kotlin("kapt")
	id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

val mapsApiKey = project.findProperty("MAPS_API_KEY") as? String ?: ""

android {
	namespace = "eu.euronavigate"
	compileSdk = 35

	defaultConfig {
		applicationId = "eu.euronavigate"
		minSdk = 24
		targetSdk = 35
		versionCode = 1
		versionName = "1.0"

		buildConfigField("String", "MAPS_API_KEY", "\"$mapsApiKey\"")
		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
		manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
	}

	secrets {
		propertiesFileName = "secrets.properties"
		defaultPropertiesFileName = "local.defaults.properties"
	}
	buildFeatures {
		buildConfig = true
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
	kapt {
		correctErrorTypes = true
	}
	hilt {
		enableAggregatingTask = false
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
	implementation(libs.androidx.material.icons.extended)
	implementation(libs.kotlinx.coroutines.play.services.v164)
	implementation(libs.javapoet)

	// Hilt
	implementation(libs.hilt.core)
	kapt(libs.hilt.compiler)
	implementation(libs.hilt.navigation.compose)
}