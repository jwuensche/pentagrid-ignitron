.PHONY: build

build:
	@./gradlew build

install:
	@mkdir -p $HOME/.local/bin
	@mkdir -p $HOME/.local/lib
	@tar xfCo ./build/distributions/ignitron.tar /tmp ignitron/bin/ignitron
	@mv /tmp/ignitron/bin/ignitron ~/.local/bin/ignitron
	@tar xfCo ./build/distributions/ignitron.tar /tmp ignitron/lib
	@mv /tmp/ignitron/lib/*.jar ~/.local/lib

uninstall:
	@rm -r ~/.local/bin/ignitron
	@rm ~/.local/lib/ignitron.jar

clean: build
	@rm -rf ./build
