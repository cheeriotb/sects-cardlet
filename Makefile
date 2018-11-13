SIMTOOLS_DIR        = ../osmocom-sim-tools

PACKAGE_AID         = 0xA0:0x00:0x00:0x04:0x76:0x00
PACKAGE_NAME        = com.github.cheeriotb.cts.cardlet
PACKAGE_VERSION     = 1.63

APPLET_AID          = 0xA0:0x00:0x00:0x04:0x76:0x41:0x6E:0x64:0x72:0x6F:0x69:0x64:0x43:0x54:0x53:0x31
APPLET_NAME         = com.github.cheeriotb.cts.cardlet.OmapiApplet

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
		-applet $(APPLET_AID) $(APPLET_NAME)              \
		$(PACKAGE_NAME) $(PACKAGE_AID) $(PACKAGE_VERSION)

default: classes

classes: $(SOURCES:.java=.class)

clean:
	$(RM) -rf $(BUILD_DIR)
