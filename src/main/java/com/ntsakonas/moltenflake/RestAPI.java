/*
    MoltenFlake - Practicing on the design of a distributed unique identifier generator.

    Copyright (C) 2021, Nick Tsakonas


    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.
    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.
    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

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
