#!/bin/bash

# ---- SETTINGS ----
BIN_DIR="bin"
LIB_DIR="lib"
MAIN_CLASS="App"   

# ---- BUILD CLASSPATH ----
CLASSPATH="$BIN_DIR"

# Add jars from lib/ if they exist
if [ -d "$LIB_DIR" ]; then
  for jar in "$LIB_DIR"/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
  done
fi

# ---- RUN ----
java -cp "$CLASSPATH" "$MAIN_CLASS"
