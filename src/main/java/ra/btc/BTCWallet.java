package ra.btc;

import ra.common.currency.crypto.BTC;
import ra.common.currency.crypto.Crypto;
import ra.common.currency.wallet.CryptoWallet;

import java.math.BigInteger;
import java.util.Map;

import static java.util.Objects.nonNull;

public class BTCWallet extends CryptoWallet {

    private String format; // database format - bdb or sqlite
    private Integer keypoololdest; // the timestamp (seconds since Unix epoch) of the oldest pre-generated key in the key pool
    private Integer keypoolsize; // how many new keys are pre-generated (only counts external keys)
    private Integer keypoolsizeHdInternal; // how many new keys are pre-generated for internal use (used for change outputs, only appears if the wallet is using this feature, otherwise external keys are used)
    private Integer unlockedUntil; // the timestamp in seconds since epoch (midnight Jan 1 1970 GMT) that the wallet is unlocked for transfers, or 0 if the wallet is locked
    private Integer paytxfee; // the transaction fee configuration, set in Sats/kB
    private String hdseedid; // the Hash160 of the HD seed (only present when HD is enabled)
    private Boolean privateKeysEnabled; // false if privatekeys are disabled for this wallet (enforced watch-only wallet)

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

    @Override
    public Map<String, Object> toMap() {
        Map<String,Object> m = super.toMap();
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
        super.fromMap(m);
        if(m.get("format")!=null) format = (String)m.get("format");
        if(m.get("keypoololdest")!=null) keypoololdest = (Integer)m.get("keypoololdest");
        if(m.get("keypoolsize")!=null) keypoolsize = (Integer)m.get("keypoolsize");
        if(m.get("keypoolsize_hd_internal")!=null) keypoolsizeHdInternal = (Integer)m.get("keypoolsize_hd_internal");
        if(m.get("unlocked_until")!=null) unlockedUntil = (Integer)m.get("unlocked_until");
        if(m.get("paytxfee")!=null) paytxfee = (Integer)m.get("paytxfee");
        if(m.get("hdseedid")!=null) hdseedid = (String)m.get("hdseedid");
        if(m.get("private_keys_enabled")!=null) privateKeysEnabled = (Boolean)m.get("private_keys_enabled");
        if(nonNull(m.get("unconfirmed_balance"))) unconfirmedBalance = new BTC((Double)m.get("unconfirmed_balance")).value();
        if(nonNull(m.get("immature_balance"))) immatureBalance = new BTC((Double)m.get("immature_balance")).value();
    }

}
