package com.ntsakonas.moltenflake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPI {

    private final UIDGenerator uidGenerator;

    RestAPI(@Autowired UIDGenerator generator) {
        uidGenerator = generator;
    }

    @GetMapping(value = "/uid", produces = "text/plain")
    public ResponseEntity<String> generateNewUid() {
        try {
            long uid = uidGenerator.generateUid();
            return new ResponseEntity<>(String.valueOf(uid), HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
    }
}
