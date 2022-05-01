all:	compile

compile:
	@cd src/
	@javac App.java	-d ./../bin/

clean:
	@rm -rf bin/*.class
	@rm -rf bin/Client/*.class
	@rm -rf bin/Server/*.class
	@rm -rf bin/Utils/*.class