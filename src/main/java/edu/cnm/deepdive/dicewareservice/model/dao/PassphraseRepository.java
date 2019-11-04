package edu.cnm.deepdive.dicewareservice.model.dao;

import edu.cnm.deepdive.dicewareservice.model.entity.Passphrase;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface PassphraseRepository extends CrudRepository<Passphrase, Long> {

  Optional<Passphrase> getFirstByKey(String key);

  Iterable<Passphrase> getAllByOrderByKeyAsc();



}
