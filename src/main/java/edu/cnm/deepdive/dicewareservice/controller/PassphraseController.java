package edu.cnm.deepdive.dicewareservice.controller;

import edu.cnm.deepdive.dicewareservice.model.dao.PassphraseRepository;
import edu.cnm.deepdive.dicewareservice.model.entity.Passphrase;
import edu.cnm.deepdive.dicewareservice.model.entity.Word;
import edu.cnm.deepdive.dicewareservice.service.PassphraseGenerator;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passphrases")
@ExposesResourceFor(Passphrase.class)
public class PassphraseController {

  private final PassphraseGenerator generator;
  private final PassphraseRepository passphraseRepository;

  @Autowired
  public PassphraseController(PassphraseGenerator generator,
      PassphraseRepository passphraseRepository) {
    this.generator = generator;
    this.passphraseRepository = passphraseRepository;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Passphrase> post(@RequestBody Passphrase passphrase,
      @RequestParam(defaultValue = "6") int length) {
      List<Word> words = passphrase.getWords();
      if (words.isEmpty()) {
        String[] dicewareWords = generator.passphrase(length);
        for (String dw : dicewareWords) {
          Word word = new Word();
          word.setWord(dw);
          words.add(word);
        }
      }
      for (Word word : words) {
        word.setPassphrase(passphrase);
      }
      passphraseRepository.save(passphrase);
      return ResponseEntity.created(passphrase.getHref()).body(passphrase);
  }

  @GetMapping(value = "{key:^\\D.*}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Passphrase get(@PathVariable String key) {
    return passphraseRepository.getFirstByKey(key).get();
  }

  @GetMapping(value = "{id:^\\d+$}", produces = MediaType.APPLICATION_JSON_VALUE)
  public Passphrase get(@PathVariable long id) {
    return passphraseRepository.findById(id).get();
  }

  @DeleteMapping(value = "{id:^\\d+$}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable long id) {
    passphraseRepository.delete(get(id));
  }

  @PutMapping(value = "{id:^\\d+$}",
      consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  public Passphrase put(@PathVariable long id, @RequestBody Passphrase passphrase) {
    Passphrase existing = get(id);
    if (passphrase.getKey() != null) {
      existing.setKey(passphrase.getKey());
    }
    if (!passphrase.getWords().isEmpty()) {
      existing.getWords().forEach((word) -> word.setPassphrase(null));
      existing.getWords().clear();
      passphrase.getWords().forEach((word) -> word.setPassphrase(existing));
      existing.getWords().addAll(passphrase.getWords());
    }

    // TODO Re-generate random passphrase, if requested.
    return passphraseRepository.save(existing);
  }

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public Iterable<Passphrase> getAll() {
    return passphraseRepository.getAllByOrderByKeyAsc();
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler(NoSuchElementException.class)
  public void notFound() {}

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(Exception.class)
  public void badRequest() {}

}

