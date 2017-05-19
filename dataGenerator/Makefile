JSRC_DIR=src
BIN_DIR=bin


all: java

java:
	javac $(JSRC_DIR)/*.java -d $(BIN_DIR) -classpath .:$(BIN_DIR)/minimal-json-0.9.5.jar:$(BIN_DIR)/unirest-java-1.4.9.jar

lint:
	javac -Xlint $(JSRC_DIR)/*.java -d $(BIN_DIR) -classpath .:$(BIN_DIR)/minimal-json-0.9.5.jar:$(BIN_DIR)/unirest-java-1.4.9.jar
clean:
	rm $(BIN_DIR)/*.class