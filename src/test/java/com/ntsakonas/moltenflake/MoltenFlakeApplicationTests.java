package com.ntsakonas.moltenflake;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class MoltenFlakeApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    void testUIDGeneration() {
        Clock clock = Mockito.mock(Clock.class);
        // a regular date: January 28, 2021 14:47:38
        long timestamp = 1611845258000L;
        Mockito.when(clock.millis()).thenReturn(timestamp);
        UIDGenerator generator = new UIDGenerator(clock);
        // millis - EPOCH = 1611845258000−1609459200000 = 2386058000
        // machineId = 1
        // sequence starts with 0
        // first 3 UIDs using the same timestamp 2386058000 --> 8E385B10 HEX
        //   <------------------ timestamp --------------------> <-machine id-><--sequence->
        // 0 0 0000 0000 1000 1110 0011 1000 0101 1011 0001 0000 0000 0000 001 0000 0000 000 = 10007852613634048
        // 0 0 0000 0000 1000 1110 0011 1000 0101 1011 0001 0000 0000 0000 001 0000 0000 001 = 10007852613634049
        // 0 0 0000 0000 1000 1110 0011 1000 0101 1011 0001 0000 0000 0000 001 0000 0000 002 = 10007852613634050

        assertThat(generator.generateUid()).isEqualTo(10007852613634048L);
        assertThat(generator.generateUid()).isEqualTo(10007852613634049L);
        assertThat(generator.generateUid()).isEqualTo(10007852613634050L);

        Mockito.when(clock.millis()).thenReturn(timestamp + 1L);
        // first 3 UIDs using the next timestamp 2386058001 --> 8E385B11 HEX
        //   <------------- timestamp -------------------------> <-machine id-><--sequence->
        // 0 0 0000 0000 1000 1110 0011 1000 0101 1011 0001 0001 0000 0000 001 0000 0000 000 = 10007852617828352
        // 0 0 0000 0000 1000 1110 0011 1000 0101 1011 0001 0001 0000 0000 001 0000 0000 001 = 10007852617828353
        // 0 0 0000 0000 1000 1110 0011 1000 0101 1011 0001 0001 0000 0000 001 0000 0000 002 = 10007852617828354
        assertThat(generator.generateUid()).isEqualTo(10007852617828352L);
        assertThat(generator.generateUid()).isEqualTo(10007852617828353L);
        assertThat(generator.generateUid()).isEqualTo(10007852617828354L);

    }

    @Test
    void testThatCannotGenerateUIDinObservedPast() {
        // the clock is increasing monotonically. if for any reason
        // the uid generator receives an earlier timestamp it should throw an error
        Clock clock = Mockito.mock(Clock.class);
        // a regular date: January 28, 2021 14:47:38
        long timestamp = 1611845258000L;
        Mockito.when(clock.millis()).thenReturn(timestamp);
        UIDGenerator generator = new UIDGenerator(clock);
        assertThat(generator.generateUid()).isEqualTo(10007852613634048L);

        // turn clock back
        Mockito.when(clock.millis()).thenReturn(timestamp - 1);
        assertThatThrownBy(() -> generator.generateUid())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The timestamp returned is in the observed past");

    }

    @Test
    void testThatIdsareMonotonicallyincreasingForCurrentMillisecond() {
        // For any given millisecond we can generate 2^11 monotonically increasing Ids,
        // After that the uid generator should throw an error (because the sequence is exhausted for this host)
        Clock clock = Mockito.mock(Clock.class);
        // a regular date: January 28, 2021 14:47:38
        Mockito.when(clock.millis()).thenReturn(1611845258000L);
        UIDGenerator generator = new UIDGenerator(clock);

        int numOfIds = 1 << 11;

        // generate the first ID
        long lastId = generator.generateUid();
        assertThat(lastId).isEqualTo(10007852613634048L);
        numOfIds--;

        // verify that all the next Ids are in increasing order
        for (int i = 0; i < numOfIds; i++) {
            long nextId = generator.generateUid();
            assertThat(nextId - lastId).isEqualTo(1L);
            lastId = nextId;
        }
    }

    @Test
    void testThatCanUseFullSequenceforCurrentMillisecond() {
        // For any given millisecond we can generate 2^11 Ids.
        // After that the uid generator should throw an error (because the sequence is exhausted for this host)
        Clock clock = Mockito.mock(Clock.class);
        // a regular date: January 28, 2021 14:47:38
        long timestamp = 1611845258000L;
        Mockito.when(clock.millis()).thenReturn(timestamp);
        UIDGenerator generator = new UIDGenerator(clock);

        int numOfIds = 1 << 11;
        // generate the first ID

        long generateUid = generator.generateUid();
        assertThat(generateUid).isEqualTo(10007852613634048L);
        numOfIds--;
        for (int i = 0; i < numOfIds; i++) {
            ; // do nothing, just consume the Ids
            generator.generateUid();
        }

        // the next call exceeds the number of Ids that can be generated for this millisecond
        assertThatThrownBy(() -> generator.generateUid())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The system is overwhelmed and ran out of IDs for this millisecond");

        // behaviour will not change until the next millisecond
        assertThatThrownBy(() -> generator.generateUid())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("The system is overwhelmed and ran out of IDs for this millisecond");

        Mockito.when(clock.millis()).thenReturn(timestamp + 1L);
        assertThat(generator.generateUid()).isEqualTo(10007852617828352L);

    }

}