# Kalah Game
Java hypermedia-driven RESTful Web Service that runs a game of 6-stone Kalah. 


### Prerequisites
* [Java](https://www.java.com/) 
* [Docker](https://www.docker.com/)

### Quickstart

1. Clone repository
```
git clone https://github.com/vladanpetrovic/game-kalah
cd game-kalah
```
2. Build the project
```
./gradlew clean build
```
3. Docker compose local MongoDB and MongoDB Express Web UI
```
./gradlew composeUp
```
4. Start the Spring Boot service
```
./gradlew bootRun
```
5. Test the service in HAL Explorer [http://localhost:8080/api/explorer/](http://localhost:8080/api/explorer/)

