all: debug

emulate:
	emulator-arm -avd NexusS -no-snapshot-save &

debug:
	ant debug

install: debug
	ant debug install
