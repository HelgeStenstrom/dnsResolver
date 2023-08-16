# Write Your Own DNS Resolver

My solution to https://codingchallenges.fyi/challenges/challenge-dns-resolver/ 

The message that you’re going to build will look like this:

Two bytes of the id - you can generate a random number for this. I’ve used 22 in the example below.

Two bytes for the flags - I’m lumping several fields together here for simplicity, what matters is that we set ‘recursion desired’ bit to 1 because we’re asking a DNS resolver first. You can see which bit that is from the RFC Section 4.1.1.

Two bytes each for the number of questions, answer resource records, authority resource records and additional resource records. The last three of these will be 0 and the number of questions will be 1.

The encoded byte string for the question.

Two bytes for the query type: 1 this time (it is defined in the RFC Section 3.2.2)

Two bytes for the query class: 1 this time (it is defined in the RFC Section 3.2.4)

Which would give us the following (hex) 00160100000100000000000003646e7306676f6f676c6503636f6d0000010001

## Generated string picked apart:

- 0016: id (2 bytes), dec 22 = hex 0016
- 0100: flags (2 bytes)
- 0001: questions (2 bytes)
- 0000: answer resorce records
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