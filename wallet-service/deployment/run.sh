#!/usr/bin/env bash

java -cp "wallet-service.jar:lib/*" -Dconfig.file="$1" com.simple.service.wallet.WalletService
