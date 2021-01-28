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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.concurrent.atomic.AtomicInteger;


/*
  Design requirements:
 - return unique numerical 64bit IDs
 - IDs are ordered by date
 - ability to generate over 1 million IDs per second

 The generator is inspired by the description of SnowFlake by Twitter.

 The generator is using a combination of the timestamp (milliseconds)
 the machine ID and an increasing sequence to generate a unique IDs.

 It has a planned capacity for 2048 hosts each generating up to 2048 ids
 per millisecond (2048000 ids per second) each. The capacity of the
 distributed system is 2048 * 2048000 = 4,121,440,000 UIDs per second.

 It uses 41 bits for the timestamp, 11 bits for the machine and 11 bits for the sequence.

 The timestamp advances 31536000 seconds per year.
 using 40 bits for the timestamp the system will serve us for up to 17 years
 using 40 bits for the timestamp the system will serve us for up to 34 years
 using 41 bits for the timestamp the system will serve us for up to 69 years

 if we do not plan to be around for that long we can make a trade-off on the timestamp bits
 and increase the number of hosts or the max sequence number to increase the system capacity.
*/

@Component
public class UIDGenerator {

    // to reduce the required number of bits to store the timestamp
    // we use a different epoch (January 1, 2021 0:00:00)
    // no need to use the January 1, 1970 0:00:00
    private final long EPOCH = 1609459200000L;
    private long lastTimestamp = -1;

    private final int BITS_IN_TIMESTAMP = 41;
    private final int BITS_IN_MACHINE_ID = 11;
    private final int BITS_IN_SEQUENCE = 11;

    // MSB (sign) is set to 0 to avoid returning negative numbers
    private final int TIMESTAMP_BIT_SHIFT = 64 - BITS_IN_TIMESTAMP - 1;
    private final int MACHINE_BIT_SHIFT = TIMESTAMP_BIT_SHIFT - BITS_IN_MACHINE_ID;

    private final int MAX_SEQUENCE = (1 << BITS_IN_SEQUENCE) - 1;
    private final int MACHINE_ID;

    private final Clock clock;
    private final AtomicInteger sequence = new AtomicInteger(0);

    public UIDGenerator(Clock clock, @Value("${uid.config.machineid}") int machineId) {

        int maxMachineId = (1 << BITS_IN_MACHINE_ID) - 1;
        if (machineId < 0 || machineId > maxMachineId) {
            throw new RuntimeException("The machine Id is out of range, it must be in the range 0.." + maxMachineId);
        }

        if (clock == null) {
            throw new RuntimeException("No system clock was provided.");
        }

        if (clock.millis() < EPOCH) {
            throw new RuntimeException("The system clock is incorrect. The system clock must be later than January 1, 2021 0:00:00.");
        }

        this.clock = clock;
        this.MACHINE_ID = machineId;
    }

    public long generateUid() {
        long millis = clock.millis();
        if (millis < lastTimestamp) {
            throw new RuntimeException("The system clock may be incorrect. The timestamp returned is in the observed past.");
        }
        if (lastTimestamp != millis) {
            lastTimestamp = millis;
            sequence.set(0);
        }
        int nextSequence = sequence.getAndIncrement();
        if (nextSequence > MAX_SEQUENCE) {
            throw new RuntimeException("The system is overwhelmed and ran out of IDs for this millisecond.");
        }
        // create the UID
        long uid = (millis - EPOCH) << TIMESTAMP_BIT_SHIFT;
        uid = uid | (MACHINE_ID << MACHINE_BIT_SHIFT);
        uid = uid | nextSequence;
        return uid;
    }
}
