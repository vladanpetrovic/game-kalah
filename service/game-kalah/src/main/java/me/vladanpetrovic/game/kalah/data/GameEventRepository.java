package me.vladanpetrovic.game.kalah.data;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(exported = false)
public interface GameEventRepository extends MongoRepository<GameEvent, String> {

}
