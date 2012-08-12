all: debug

emulator:
	emulator-arm -avd NexusS -no-snapshot-save &

debug:
	ant debug

run: debug
	ant debug install
	adb shell am start com.lingvapps.quizword/.Main
