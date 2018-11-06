SIMTOOLS_DIR        = ../osmocom-sim-tools

PACKAGE_AID         = 0x6F:0x6D:0x61:0x70:0x69:0x63:0x61:0x72:0x64:0x6C:0x65:0x74
PACKAGE_NAME        = com.github.cheeriotb.cts.cardlet
PACKAGE_VERSION     = 1.63

APPLET1_AID         = 0x6F:0x6D:0x61:0x70:0x69:0x4A:0x53:0x52:0x31:0x37:0x37
APPLET1_NAME        = com.github.cheeriotb.cts.cardlet.SelectResponse

APPLET2_AID         = 0x6F:0x6D:0x61:0x70:0x69:0x43:0x61:0x63:0x68:0x69:0x6E:0x67
APPLET2_NAME        = com.github.cheeriotb.cts.cardlet.XXLResponse

SOURCES             = ./src/com/github/cheeriotb/cts/cardlet/*.java

BUILD_DIR           = ./build
BUILD_CLASSES_DIR   = $(BUILD_DIR)/classes
BUILD_JAVACARD_DIR  = $(BUILD_DIR)/javacard
JAVACARD_SDK_DIR    ?= $(SIMTOOLS_DIR)/javacard
JAVACARD_EXPORT_DIR ?= $(JAVACARD_SDK_DIR)/api21_export_files

ifdef COMSPEC
CLASSPATH           = $(JAVACARD_SDK_DIR)/lib/api21.jar;$(JAVACARD_SDK_DIR)/lib/sim.jar
else
CLASSPATH           = $(JAVACARD_SDK_DIR)/lib/api21.jar:$(JAVACARD_SDK_DIR)/lib/sim.jar
endif

JFLAGS              = -target 1.1 -source 1.3 -J-Duser.language=en -g -d $(BUILD_CLASSES_DIR) -classpath "$(CLASSPATH)"
JAVA                ?= java
JC                  ?= javac

.SUFFIXES: .java .class
.java.class:
	mkdir -p $(BUILD_CLASSES_DIR)
	mkdir -p $(BUILD_JAVACARD_DIR)

	$(JC) $(JFLAGS) $*.java

	$(JAVA) -jar $(JAVACARD_SDK_DIR)/bin/converter.jar    \
		-d $(BUILD_JAVACARD_DIR)                          \
		-classdir $(BUILD_CLASSES_DIR)                    \
		-exportpath $(JAVACARD_EXPORT_DIR)                \
		-applet $(APPLET1_AID) $(APPLET1_NAME)            \
		-applet $(APPLET2_AID) $(APPLET2_NAME)            \
		$(PACKAGE_NAME) $(PACKAGE_AID) $(PACKAGE_VERSION)

default: classes

classes: $(SOURCES:.java=.class)

clean:
	$(RM) -rf $(BUILD_DIR)
