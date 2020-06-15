package ra.btc.wallet;


import ra.btc.blockchain.Transaction;
import ra.btc.blockchain.TransactionOutput;
import ra.common.Hash;

import java.util.HashSet;
import java.util.Map;

/**
 * Stores keys using them to send and receive {@link ra.btc.Satoshi}
 * encapsulated in a {@link ra.btc.blockchain.Transaction}.
 *
 */
public class Wallet {

    private int version;
    private String description;

    /**
     * Transactions that didn't make it into the best chain yet. Pending transactions can be killed if a
     * double spend against them appears in the best chain, in which case they move to the dead pool.
     * If a double spend appears in the pending state as well, we update the confidence type
     * of all txns in conflict to IN_CONFLICT and wait for the miners to resolve the race.
     */
    private Map<Hash, Transaction> pending;

    /**
     * Transactions that appeared in the best chain and have outputs we can spend. Note that we store the
     * entire transaction in memory even though for spending purposes we only really need the outputs, the
     * reason being that this simplifies handling of re-orgs.
     */
    private Map<Hash, Transaction> unspent;

    /**
     * Transactions that appeared in the best chain but don't have any spendable outputs. They're stored here
     * for history browsing/auditing reasons only and in future will probably be flushed out to some other
     * kind of cold storage or just removed.
     */
    private Map<Hash, Transaction> spent;

    /**
     * Transactions that we believe will never confirm get moved here, out of pending. Note that Bitcoin
     * Core has no notion of dead-ness: the assumption is that double spends won't happen so there's no
     * need to notify the user about them. We take a more pessimistic approach and try to track the fact that
     * transactions have been double spent so applications can do something intelligent (cancel orders, show
     * to the user in the UI, etc). A transaction can leave dead and move into spent/unspent if there is a
     * re-org to a chain that doesn't include the double spend.
     */
    private Map<Hash, Transaction> dead;

    // Transactions combined
    protected Map<Hash, Transaction> transactions;

    // All the TransactionOutput objects that we could spend (ignoring whether we have the private key or not).
    // Used to speed up various calculations.
    protected HashSet<TransactionOutput> myUnspents =  new HashSet<>();
}
