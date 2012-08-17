all: debug

emulator:
	emulator-arm -avd NexusS -no-snapshot-save &

emulator-old:
	emulator-arm -avd NexusOne -no-snapshot-save &

debug:
	ant debug

run: debug
	ant debug uninstall
	ant debug install
	adb shell am start com.lingvapps.quizword/.MainMenuActivity
