package me.vladanpetrovic.game.kalah.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "games")
public interface GameRepository extends MongoRepository<Game, String> {
}
