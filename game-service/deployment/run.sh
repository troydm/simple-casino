#!/usr/bin/env bash

java -cp "game-service.jar:lib/*" -Dconfig.file="$1" com.simple.service.game.GameService
