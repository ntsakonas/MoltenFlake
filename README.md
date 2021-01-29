# MoltenFlake

A distributed unique ID generator

## Description
This is a distributed UID generator implemented in the context of practicing system design.
This is one of the systems that is often considered basic in system design and the most common and obvious 
approach is what is known as Twitter's Snowflake.

This implementation is satisfying the following requirements:
 - return unique numerical 64bit IDs
 - IDs are ordered by date
 - ability to generate over 1 million IDs per second
 
 The generator is using a combination of the timestamp (milliseconds)
 the machine ID and an increasing sequence to generate a unique IDs.

 It uses 41 bits for the timestamp, 11 bits for the machine and 11 bits for the sequence.

 It has a planned capacity for 2048 hosts each generating up to 2048 ids
 per millisecond (2048000 ids per second) each. The capacity of the
 distributed system is 2048 * 2048000 = 4,121,440,000 UIDs per second.

 As the timestamp advances 31536000 seconds per year we can adjust the number of bits
 allocated to the timestamp/machineid/sequence to accommodate for more load, more machines or longer lasting system.
 
 For example, if we do not plan to be around for that long we can make a trade-off on the timestamp bits
 and increase the number of hosts or the max sequence number to increase the system capacity.
 
 Using 40 bits for the timestamp the system will serve us for up to 17 years.
 Using 40 bits for the timestamp the system will serve us for up to 34 years.
 Using 41 bits for the timestamp the system will serve us for up to 69 years.

 ## Requirements
 The implementation is using Spring to provide a very simple HTTP api but other methods may be added (eg gRPC).
 The system requires a host whose time is accurate. 
 The design also assumes that the system may also be load balanced and each instance has its own unique machine id.
 Setting the machine id is a responsibility of the process that maintains the intances.
 
 ## Notes
 The implementation was developed in a "clean room" style but due to its simplicity its similarities with Twitter's Snowflake are unavoidable.
 
 ## Licence/Disclaimer
 This implementation is released under GNU General Public License v3.0.
 Please keep in mind that this implementation is not production tested and comes with absolutely no guarantee that it will work for you.
