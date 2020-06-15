[![Build Status](https://travis-ci.com/resolvingarchitecture/bitcoin-client-java.svg?branch=master)](https://travis-ci.com/resolvingarchitecture/bitcoin-client-java)

# Resolving Architecture - Bitcoin Client - Java
Bitcoin Client as a Service supporting full local node, Private Simple Payment Verification (SPV) client, and fully remote thin client.

It depends on external network service for communications.

It can be used as a lightweight (SPV) client to verify that a transaction is included in the Bitcoin blockchain, 
without downloading the entire blockchain. The client only needs to download the block headers, 
which are much smaller than the full blocks. To verify that a transaction is in a block, it requests 
a proof of inclusion, in the form of a Merkle branch.

Or it can be used as a heavyweight client requiring a full Bitcoin node to be present which is the 
most secure setup.

This implementation borrows heavily from the BitcoinJ project on githhub: https://bitcoinj.github.io/

BitcoinJ was not chosen as an implementation itself as it communicates directly with a Bitcoin node over
the wire and this project requires all communications to go through a pluggable network layer.

## Version

0.1.0-SNAPSHOT

## Licensing

Copyright Unrecognized

## Authors / Developers

* objectorange (Brian Taylor) - [GitHub](https://github.com/objectorange) | [LinkedIn](https://www.linkedin.com/in/decentralizationarchitect/) | brian@resolvingarchitecture.io PGP: 2FA3 9B12 DA50 BD7C E43C 3031 A15D FABB 2579 77DC

## Opportunities

## Solution

