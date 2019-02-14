#!/usr/bin/env bash

assert(){
    if [ "$1" != "$2" ]; then
        echo "Expected result: [$2]"
        echo "Actual result: [$1]"
        exit 1
    fi
    echo 'test passed'
}

assert "$(curl -X POST -H 'Content-Type: application/json' -d '{"playerId": 1}' http://localhost:9080/register)" \
    '{"playerId":1,"balance":{"amount":0.0}}'

assert "$(curl -X POST -H 'Content-Type: application/json' -d '{"playerId": 1, "amount": 100}' http://localhost:9080/deposit)" \
    '{"playerId":1,"balance":{"amount":100.0}}'

assert "$(curl -X POST -H 'Content-Type: application/json' -d '{"playerId": 1, "gameId": 1, "amount": 70}' http://localhost:9090/place-bet)" \
    '{"playerId":1,"balance":{"amount":30.0}}'

assert "$(curl -X POST -H 'Content-Type: application/json' -d '{"playerId": 1, "gameId": 1, "amount": 50}' http://localhost:9090/place-bet)" \
    '{"playerId":1,"reason":"insufficient funds","balance":{"amount":30.0}}'

assert "$(curl -X POST -H 'Content-Type: application/json' -d '{"playerId": 1}' http://localhost:9090/show-bets)" \
    '{"playerId":1,"bets":[{"gameId":1,"amount":70.0}]}'

assert "$(curl -X POST -H 'Content-Type: application/json' -d '{"playerId": 1}' http://localhost:9080/balance)" \
    '{"playerId":1,"balance":{"amount":30.0}}'

echo "All tests passed"
