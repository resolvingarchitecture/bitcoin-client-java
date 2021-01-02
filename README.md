[![Build Status](https://travis-ci.com/resolvingarchitecture/bitcoin-client-java.svg?branch=master)](https://travis-ci.com/resolvingarchitecture/bitcoin-client-java)

# Resolving Architecture - Bitcoin Client - Java
Bitcoin Client as a Service

Provides an API as a Service for a local Bitcoin Core node via its RPC API.

## Setup
Ensure contents of bitcoin.conf provided in project config folder are in the bitcoin.conf file located in the .bitcoin
directory within your home directory, e.g. ~/.bitcoin/bitcoin.conf. This enables the JSON RPC - the ability for communications
between the local Bitcoin node and this service.

Local Bitcoin instance should be configured to use local 1M5 proxy at best,
local Tor proxy at least for inter-Bitcoin-node communications.

## Authors / Developers

* objectorange (Brian Taylor) - [GitHub](https://github.com/objectorange) | [LinkedIn](https://www.linkedin.com/in/decentralizationarchitect/) | brian@resolvingarchitecture.io PGP: 2FA3 9B12 DA50 BD7C E43C 3031 A15D FABB 2579 77DC

## Design
[BitcoinService](src/main/java/ra/btc/BitcoinService.java) is designed to work with the RA Service Bus.

[BitcoinJ](https://bitcoinj.org/) is embedded in the service. It creates a Bitcoin node implemented in Java
on your machine and connects out using HTTP and Tor on its own terms.
This is undesirable with this Bitcoin Client as a service as we want to focus all communications through registered
network services to minimize leakage while maintaining the ability to re-route failed routes via a Network Manager service
while also supporting a full node locally without having to maintain the Java code BUT it's much faster integrating
with Bitcoin using this library. So, embedding it is supported while opening the future to support an RPC to local Bitcoin
Core node directly through RPC (started).

## Implementation

