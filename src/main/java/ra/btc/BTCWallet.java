package ra.btc;

import ra.common.JSONParser;
import ra.common.JSONPretty;
import ra.common.JSONSerializable;
import ra.common.currency.crypto.BTC;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.nonNull;

public class BTCWallet implements JSONSerializable {

    private String name;
    private Integer version;
    private BigInteger balance;
    private String format; // database format - bdb or sqlite
    private Integer keypoololdest; // the timestamp (seconds since Unix epoch) of the oldest pre-generated key in the key pool
    private Integer keypoolsize; // how many new keys are pre-generated (only counts external keys)
    private Integer keypoolsizeHdInternal; // how many new keys are pre-generated for internal use (used for change outputs, only appears if the wallet is using this feature, otherwise external keys are used)
    private Integer unlockedUntil; // the timestamp in seconds since epoch (midnight Jan 1 1970 GMT) that the wallet is unlocked for transfers, or 0 if the wallet is locked
    private Integer paytxfee; // the transaction fee configuration, set in Sats/kB
    private String hdseedid; // the Hash160 of the HD seed (only present when HD is enabled)
    private Boolean privateKeysEnabled; // false if privatekeys are disabled for this wallet (enforced watch-only wallet)
    private BigInteger unconfirmedBalance; // the total unconfirmed balance of the wallet in Sats
    private BigInteger immatureBalance; // the total immature balance of the wallet in Sats
    private BigInteger txCount; // the total number of transactions in the wallet in Sats

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public Integer getKeypoololdest() {
        return keypoololdest;
    }

    public void setKeypoololdest(Integer keypoololdest) {
        this.keypoololdest = keypoololdest;
    }

    public Integer getKeypoolsize() {
        return keypoolsize;
    }

    public void setKeypoolsize(Integer keypoolsize) {
        this.keypoolsize = keypoolsize;
    }

    public Integer getKeypoolsizeHdInternal() {
        return keypoolsizeHdInternal;
    }

    public void setKeypoolsizeHdInternal(Integer keypoolsizeHdInternal) {
        this.keypoolsizeHdInternal = keypoolsizeHdInternal;
    }

    public Integer getUnlockedUntil() {
        return unlockedUntil;
    }

    public void setUnlockedUntil(Integer unlockedUntil) {
        this.unlockedUntil = unlockedUntil;
    }

    public Integer getPaytxfee() {
        return paytxfee;
    }

    public void setPaytxfee(Integer paytxfee) {
        this.paytxfee = paytxfee;
    }

    public String getHdseedid() {
        return hdseedid;
    }

    public void setHdseedid(String hdseedid) {
        this.hdseedid = hdseedid;
    }

    public Boolean getPrivateKeysEnabled() {
        return privateKeysEnabled;
    }

    public void setPrivateKeysEnabled(Boolean privateKeysEnabled) {
        this.privateKeysEnabled = privateKeysEnabled;
    }

    public BigInteger getUnconfirmedBalance() {
        return unconfirmedBalance;
    }

    public void setUnconfirmedBalance(BigInteger unconfirmedBalance) {
        this.unconfirmedBalance = unconfirmedBalance;
    }

    public BigInteger getImmatureBalance() {
        return immatureBalance;
    }

    public void setImmatureBalance(BigInteger immatureBalance) {
        this.immatureBalance = immatureBalance;
    }

    public BigInteger getTxCount() {
        return txCount;
    }

    public void setTxCount(BigInteger txCount) {
        this.txCount = txCount;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = new HashMap<>();
        m.put("walletname", name);
        m.put("walletversion", version);
        m.put("balance", balance);
        m.put("format", format);
        m.put("keypoololdest", keypoololdest);
        m.put("keypoolsize", keypoolsize);
        m.put("keypoolsize_hd_internal", keypoolsizeHdInternal);
        m.put("unlocked_until", unlockedUntil);
        m.put("paytxfee", paytxfee);
        m.put("hdseedid", hdseedid);
        m.put("private_keys_enabled", privateKeysEnabled);
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(nonNull(m.get("walletname"))) name = (String)m.get("walletname");
        if(nonNull(m.get("walletversion"))) version = (Integer)m.get("walletversion");
        if(nonNull(m.get("balance"))) balance = (BigInteger)m.get("balance");
        if(m.get("format")!=null) format = (String)m.get("format");
        if(m.get("keypoololdest")!=null) keypoololdest = (Integer)m.get("keypoololdest");
        if(m.get("keypoolsize")!=null) keypoolsize = (Integer)m.get("keypoolsize");
        if(m.get("keypoolsize_hd_internal")!=null) keypoolsizeHdInternal = (Integer)m.get("keypoolsize_hd_internal");
        if(m.get("unlocked_until")!=null) unlockedUntil = (Integer)m.get("unlocked_until");
        if(m.get("paytxfee")!=null) paytxfee = (Integer)m.get("paytxfee");
        if(m.get("hdseedid")!=null) hdseedid = (String)m.get("hdseedid");
        if(m.get("private_keys_enabled")!=null) privateKeysEnabled = (Boolean)m.get("private_keys_enabled");
        if(nonNull(m.get("unconfirmed_balance"))) unconfirmedBalance = new BTC((Double)m.get("unconfirmed_balance")).value();
        if(nonNull(m.get("unconfirmedBalance"))) unconfirmedBalance = (BigInteger)m.get("unconfirmedBalance");
        if(nonNull(m.get("immature_balance"))) immatureBalance = new BTC((Double)m.get("immature_balance")).value();
        if(nonNull(m.get("immatureBalance"))) immatureBalance = (BigInteger)m.get("immatureBalance");
        if(nonNull(m.get("txcount"))) txCount = (BigInteger)m.get("txcount");
    }

    @Override
    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(toMap()), 4);
    }

    @Override
    public void fromJSON(String json) {
        fromMap((Map<String,Object>)JSONParser.parse(json));
    }
}
