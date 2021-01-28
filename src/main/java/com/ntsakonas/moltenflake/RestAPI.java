package com.ntsakonas.moltenflake;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RestAPI {

    private final UIDGenerator uidGenerator;

    RestAPI(@Autowired UIDGenerator generator) {
        uidGenerator = generator;
    }

    @GetMapping("/uid")
    public long generateNewUid() {
        return uidGenerator.generateUid();
    }
}
