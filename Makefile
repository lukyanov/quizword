all: debug

emulator:
	emulator-arm -avd NexusS -no-snapshot-save &

emulator-old:
	emulator-arm -avd NexusOne -no-snapshot-save &

run: debug
	ant debug install
	adb shell am start com.lingvapps.quizword.renew/.MainMenuActivity

clean:
	ant clean

debug:
	ant debug

release:
	ant release
