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
BitcoinService is designed to work with the RA Service Bus. It sends and expects to receive messages using it.
It currently depends on the ra.http.HTTPService running as a client only (for local Bitcoin node RPC).

[BitcoinJ](https://bitcoinj.org/) was evaluated to be embedded in the service. It creates a Bitcoin node implemented in Java
on your machine and connects out using DNS (TCP), HTTP, and Tor on its own terms. This is undesirable with this Bitcoin Client
as a service as we want to focus all communications through our own network services to minimize leakage while
maintaining the ability to re-route failed routes via a Network Manager service while also supporting a full/pruned node
locally without having to maintain the Java code in-sync with future Bitcoin work (what if BitcoinJ becomes stagnant).
In addition, BitcoinJ adds a considerable amount of complexity requiring a great deal of dependency on its codebase and
is tied to Google libraries and copyrighted by Google.

Bitcoin JSON-RPC interface is used for all communications to the local Bitcoin node.

Local Bitcoin node can be a full node or a pruned node. Open payment channels that will be dereferenced upon pruning
will be lost due to de-referencing. It's recommended to keep the pruning to a minimal by supporting the largest bitcoin
node size possible. Future versions of this component will provide auto closing of payments channels that would be affected
by a prune.

## Implementation

