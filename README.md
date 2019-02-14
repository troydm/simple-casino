simple casino
=============

This is simple Akka HTTP microservices example casino application written in Scala.
There are two microservices: *wallet-service* and *game-service*.
This project uses multi-module gradle project structure.
*common* is library used by both microservices.

Requirments:

    bash
    curl
    jdk8
    gradle 4.4 or older

Building:

    gradle clean
    gradle build
    gradle test --info # run automated unit tests
    gradle copyJar # copy jar to deployment folder
    gradle copyLibs # copy dependency libs deployment/lib folder

Start Application:

    cd wallet-service/deployment && ./run.sh ./wallet-service.conf &
    cd game-service/deployment && ./run.sh ./game-service1.conf &
    # optionally can start second instance of game server tho it's not needed by functional test
    cd game-service/deployment && ./run.sh ./game-service2.conf &

Run functional test:

    ./run-functional-test.sh


Notes: to run functional test again you need to stop all applications
and remove wallet database files (wallet.mv.db and wallet.trace.db) in wallet-service/deployment folder
and then start all applications again.
