package ra.btc;

import ra.common.Envelope;

import java.io.IOException;
import java.util.Properties;

public interface BitcoinClient {

    // Lower Level Use Case Requests
    String OPERATION_CREATE_2_2_MULTISIG = "CREATE_2_2_MULTISIG";
    String OPERATION_CLOSE_2_2_MULTISIG = "CLOSE_2_2_MULTISIG";
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
