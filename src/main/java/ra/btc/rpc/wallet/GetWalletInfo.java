package ra.btc.rpc.wallet;

import ra.btc.rpc.RPCRequest;

import java.util.Map;

public class GetWalletInfo extends RPCRequest {

    public static final String NAME = "getwalletinfo";

    public String walletname; // the wallet name
    public Integer walletversion; // the wallet version
    public Double balance; // the total confirmed balance of the wallet in BTC
    public Double unconfirmedBalance; // the total unconfirmed balance of the wallet in BTC
    public Double immatureBalance; // the total immature balance of the wallet in BTC
    public Integer txcount; // the total number of transactions in the wallet
    public Integer keypoololdest; // the timestamp (seconds since Unix epoch) of the oldest pre-generated key in the key pool
    public Integer keypoolsize; // how many new keys are pre-generated (only counts external keys)
    public Integer keypoolsizeHdInternal; // how many new keys are pre-generated for internal use (used for change outputs, only appears if the wallet is using this feature, otherwise external keys are used)
    public Integer unlockedUntil; // the timestamp in seconds since epoch (midnight Jan 1 1970 GMT) that the wallet is unlocked for transfers, or 0 if the wallet is locked
    public Double paytxfee; // the transaction fee configuration, set in BTC/kB
    public String hdseedid; // the Hash160 of the HD seed (only present when HD is enabled)
    public Boolean privateKeysEnabled; // false if privatekeys are disabled for this wallet (enforced watch-only wallet)

    public GetWalletInfo() {
        super(NAME);
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        // Response
        walletname = (String)m.get("walletname");
        walletversion = (Integer)m.get("walletversion");
        balance = (Double)m.get("balance");
        unconfirmedBalance = (Double)m.get("unconfirmed_balance");
        immatureBalance = (Double)m.get("immature_balance");
        txcount = (Integer)m.get("txcount");
        keypoololdest = (Integer)m.get("keypoololdest");
        keypoolsize = (Integer)m.get("keypoolsize");
        keypoolsizeHdInternal = (Integer)m.get("keypoolsize_hd_internal");
        unlockedUntil = (Integer)m.get("unlocked_until");
        paytxfee = (Double)m.get("paytxfee");
        hdseedid = (String)m.get("hdseedid");
        privateKeysEnabled = (Boolean)m.get("private_keys_enabled");
    }

}
