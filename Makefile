all: lib

java_build:
	cd IChiMQTT && gradle build

lib: java_build
	cp IChiMQTT/library/build/outputs/aar/library-release.aar platforms/android/IChiMqtt.aar
