# Write Your Own DNS Resolver

My solution to https://codingchallenges.fyi/challenges/challenge-dns-resolver/ 

Also read [RFC 1035, section 
4](https://datatracker.ietf.org/doc/html/rfc1035#section-4).

The message that you’re going to build will look like this:

Two bytes of the id - you can generate a random number for this. I’ve used 22 in the example below.

Two bytes for the flags - I’m lumping several fields together here for simplicity, what matters is that we set ‘recursion desired’ bit to 1 because we’re asking a DNS resolver first. You can see which bit that is from the RFC Section 4.1.1.

Two bytes each for the number of questions, answer resource records, authority resource records and additional resource records. The last three of these will be 0 and the number of questions will be 1.

The encoded byte string for the question.

Two bytes for the query type: 1 this time (it is defined in the RFC Section 3.2.2)

Two bytes for the query class: 1 this time (it is defined in the RFC Section 3.2.4)

Which would give us the following (hex) 00160100000100000000000003646e7306676f6f676c6503636f6d0000010001

To summarize, the parts are

- Dns message
  - Header
    - defined by RFC 1035 [Section 4.1.1](https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.1)
    - Id
    - Flags
    - Number of questions
    - Answer resource records
    - Authority resource records
    - Additional resource records
  - Question
    - defined by RFC 1035 [Section 
      4.1.2](https://datatracker.ietf.org/doc/html/rfc1035#section-4.1.2)
  - Message
    - ?

## Generated string picked apart:

String: 00160100000100000000000003646e7306676f6f676c6503636f6d0000010001

- 0016: id (2 bytes), dec 22 = hex 0016
- 0100: flags (2 bytes). The bit for Recursion Desired is set.
- 0001: questions (2 bytes)
- 0000: answer resource records
- 0000: authority resource records
- 0000: additional resource records
- 03646e7306676f6f676c6503636f6d00: encoded byte string for the question
- 0001: Query type
- 0001: Query class

## Encoded byte string

- 03: number of characters = 3
- 646e73: dns 
  - 64: d
  - 6e: n
  - 73: s
- 06: number of characters = 6
- 676f6f676c65: google
  - 67: g
  - 6f: o
  - 6f: o
  - 67: g
  - 6c: l
  - 65: e
- 03: number of characters = 3
- 636f6d: com
  - 63: c
  - 6f: o
  - 6d: m
- 00: end marker

## Names

Domain names appears in two contexts. (probably)
The first name is in the Question section. It consists of sections separated, and ends with a zero octet.
In other words, each section starts with a length octet and continues with that number of octets.
The last length octet is zero, so no octets follows. The octets that are not length octets are ascii codes.

The number of Question sections is usually 1. It can be >1. Can it be zero?

The other context is in the Resource Record format. This format is used in sections that follow the Question section.

Names except the first one can contain a pointer to a previous name definition. Since no name precedes the first one,
it can't use compression.

A byte that potentially prepends ascii-bytes can be any of:
- 0: marks the end of a name, prepends a zero-size section, a.k.a. no section 
at all.
- 1 - 63 (0x01 to 0x3f): 1 to 63 characters (bytes/octets) follows.
- 192 = 0xC0 = 0b1100 0000 or higher: The 14 LSB of the 2-octet formed by 
  this byte and the following, forms a pointer in the range 0 to 0x3FFF = 0 to 16383, although 0 doesn't make sense.

The pointer refers to the stream/list of octets/bytes.

The first pointer that makes sense is 12, since the header section is 12 bytes long,
and the first question section starts after it.


# Creating a UDP socket

Is this application a server or a client? I think it's a client.

# Further reading
 * https://cabulous.medium.com/dns-message-how-to-read-query-and-response-message-cfebcb4fe817
 * 