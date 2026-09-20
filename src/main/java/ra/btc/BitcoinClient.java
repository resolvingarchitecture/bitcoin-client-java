package ra.btc;

import ra.common.Envelope;

import java.util.Properties;

public interface BitcoinClient {

    // ** Wallet query/send - the operations a host actually needs for a real wallet UI, as
    // opposed to the lower/upper-level use cases below, none of which are implemented yet. **
    String OPERATION_GET_BALANCE = "GET_BALANCE";
    String OPERATION_GET_RECEIVE_ADDRESS = "GET_RECEIVE_ADDRESS";
    String OPERATION_LIST_TRANSACTIONS = "LIST_TRANSACTIONS";
    String OPERATION_SEND = "SEND";
    String OPERATION_SYNC_STATUS = "SYNC_STATUS";

    /**
     * Broadcast an already-finalized, already-signed raw transaction (a plain node/relay
     * operation - no wallet involved on this end at all, unlike {@link #OPERATION_SEND} which
     * spends this client's own wallet funds). bitcoinj 0.17.1 has no BIP-174 PSBT support
     * (confirmed against its actual source - no PSBT-related class exists anywhere in it), so
     * a caller holding a PSBT must finalize and extract the raw transaction itself before
     * submitting it here, same as {@code bitcoind}'s own {@code sendrawtransaction} RPC expects.
     */
    String OPERATION_BROADCAST_TRANSACTION = "BROADCAST_TRANSACTION";

    // Request/response header names for the operations above.
    String HEADER_BALANCE_SATS = "btc.balanceSats";
    String HEADER_AVAILABLE_SATS = "btc.availableSats";
    String HEADER_ADDRESS = "btc.address";
    String HEADER_AMOUNT_SATS = "btc.amountSats";
    String HEADER_TXID = "btc.txid";
    String HEADER_SYNCING = "btc.syncing";
    String HEADER_BEST_HEIGHT = "btc.bestHeight";
    String HEADER_CONNECTED_PEERS = "btc.connectedPeers";
    /** Request header for {@link #OPERATION_BROADCAST_TRANSACTION}: hex-encoded raw signed transaction bytes. */
    String HEADER_RAW_TX_HEX = "btc.rawTxHex";

    // Lower Level Use Case Requests
    String OPERATION_CREATE_2_N_MULTISIG = "CREATE_2_N_MULTISIG";
    String OPERATION_CLOSE_2_N_MULTISIG = "CLOSE_2_N_MULTISIG";

    // Upper Level Use Case Requests
    // ** Wallet **
    String OPERATION_CREATE_HD_WALLET = "CREATE_HD_WALLET";
    String OPERATION_LOCK_HD_WALLET = "LOCK_HD_WALLET";
    String OPERATION_UNLOCK_HD_WALLET = "UNLOCK_HD_WALLET";
    String OPERATION_DESTROY_HD_WALLET = "DESTROY_HD_WALLET";
    String OPERATION_CREATE_CLASSIC_WALLET = "CREATE_CLASSIC_WALLET";
    String OPERATION_LOCK_CLASSIC_WALLET = "LOCK_CLASSIC_WALLET";
    String OPERATION_UNLOCK_CLASSIC_WALLET = "UNLOCK_CLASSIC_WALLET";
    String OPERATION_DESTROY_CLASSIC_WALLET = "DESTROY_CLASSIC_WALLET";
    // ** Escrow **
    String OPERATION_CREATE_ESCROW = "CREATE_ESCROW";
    String OPERATION_CLOSE_ESCROW = "CLOSE_ESCROW";
    // ** Exchange BTC/Assets **
    String OPERATION_MAKE_BTC_BUY_OFFER = "MAKE_BTC_BUY_OFFER";
    String OPERATION_TAKE_BTC_BUY_OFFER = "TAKE_BTC_BUY_OFFER";
    String OPERATION_MAKE_BTC_SELL_OFFER = "MAKE_BTC_SELL_OFFER";
    String OPERATION_TAKE_BTC_SELL_OFFER = "TAKE_BTC_SELL_OFFER";
    String OPERATION_EXCHANGE_FOR_BTC = "EXCHANGE_FOR_BTC";
    // ** BTC Trusts **
    String OPERATION_CREATE_REVOCABLE_TRUST = "CREATE_REVOCABLE_TRUST";
    String OPERATION_UPDATE_REVOCABLE_TRUST= "UPDATE_REVOCABLE_TRUST";
    String OPERATION_CLOSE_REVOCABLE_TRUST = "CLOSE_REVOCABLE_TRUST";
    String OPERATION_CREATE_IRREVOCABLE_TRUST = "CREATE_IRREVOCABLE_TRUST";
    // ** Payment Channels **
    String OPERATION_OPEN_PAYMENT_CHANNEL = "OPEN_PAYMENT_CHANNEL";
    String OPERATION_MAKE_PAYMENT_IN_CHANNEL = "MAKE_PAYMENT_IN_CHANNEL";
    String OPERATION_CLOSE_PAYMENT_CHANNEL = "CLOSE_PAYMENT_CHANNEL";

    boolean init(Properties props) throws Exception;

    void handleDocument(Envelope e);

    boolean destroy() throws Exception;

    boolean destroyGracefully() throws Exception;
}
