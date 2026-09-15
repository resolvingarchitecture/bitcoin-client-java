package ra.btc;

import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.ECKey;

import java.util.UUID;

public class BTCEscrow {
    public UUID id;
    public ECKey sendingParty;
    public ECKey receivingParty;
    public Transaction tx;
    public Long satsToSend;
    public Long estimatedNetworkFeeSats;
    public Long devGroupFeeSats;
    public ECKey.ECDSASignature receivingPartySig;
}
