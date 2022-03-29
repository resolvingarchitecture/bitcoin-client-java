package ra.btc.rpc.tx;

import ra.btc.rpc.blockchain.ScriptPublicKey;
import ra.btc.rpc.blockchain.UTXO;
import ra.common.JSONSerializable;
import ra.common.JSONParser;
import ra.common.JSONPretty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction implements JSONSerializable {

    public enum Category {
        SEND("send"), // Transactions sent.
        RECEIVE("receive"), // Non-coinbase transactions received.
        GENERATE("generate"), // Coinbase transactions received with more than 100 confirmations.
        IMMATURE("immature"), // Coinbase transactions received with 100 or fewer confirmations.
        ORPHAN("orphan"), // Orphaned coinbase transactions received.
        UNKNOWN("unknown");

        private final String catName;

        static Category getInstance(String catName) {
            switch (catName) {
                case "send":
                    return SEND;
                case "receive":
                    return RECEIVE;
                case "generate":
                    return GENERATE;
                case "immature":
                    return IMMATURE;
                case "orphan":
                    return ORPHAN;
                default:
                    return UNKNOWN;
            }
        }

        Category(String name) {
            catName = name;
        }

        public String getCatName() {
            return catName;
        }
    }

    // *** Transaction ***
    public Boolean involvesWatchOnly; // Only returns true if imported addresses were involved in transaction.
    public String address; // The bitcoin address of the transaction.
    public Category category; // The transaction category.
    public Double amount; // The amount in BTC. This is negative for the 'send' category, and is positive for all other categories
    public Integer vOutValue;
    public Double fee; // The amount of the fee in BTC. This is negative and only available for the send category of transactions.
    public Integer confirmations; // The number of confirmations for the transaction. Negative confirmations means the transaction conflicted that many blocks ago.
    public Boolean generated; // Only present if transaction only input is a coinbase one.
    public Boolean trusted; // Only present if we consider transaction to be trusted and so safe to spend from.
    public String blockhash; // The block hash containing the transaction.
    public Integer blockheight; // The block height containing the transaction.
    public Integer blockindex; //  The index of the transaction in the block that includes it.
    public Integer blocktime; // The block time expressed in UNIX epoch time.
    public String txid; // The transaction id in hex.
    public List<String> walletConflicts; // Conflicting transaction ids.
    public Integer time; // The transaction time expressed in UNIX epoch time.
    public Integer timereceived; // The time received expressed in UNIX epoch time.
    public String comment; // If a comment is associated with the transaction, only present if not empty.
    public Boolean supportsReplaceByFee; // ("yes|no|unknown") Whether this transaction could be replaced due to BIP125 (replace-by-fee);
    // may be unknown for unconfirmed transactions not in the mempool
    public Boolean abandoned;

    public Boolean inActiveChain;
    public String hash;
    public Integer size;
    public Integer vsize;
    public Integer weight;
    public Integer version;
    public Integer locktime;
    public List<TXI> vIn;
    public List<UTXO> vOut;

    public Boolean getInvolvesWatchOnly() {
        return involvesWatchOnly;
    }

    public void setInvolvesWatchOnly(Boolean involvesWatchOnly) {
        this.involvesWatchOnly = involvesWatchOnly;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Integer getvOutValue() {
        return vOutValue;
    }

    public void setvOutValue(Integer vOutValue) {
        this.vOutValue = vOutValue;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public Integer getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Integer confirmations) {
        this.confirmations = confirmations;
    }

    public Boolean getGenerated() {
        return generated;
    }

    public void setGenerated(Boolean generated) {
        this.generated = generated;
    }

    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }

    public String getBlockhash() {
        return blockhash;
    }

    public void setBlockhash(String blockhash) {
        this.blockhash = blockhash;
    }

    public Integer getBlockheight() {
        return blockheight;
    }

    public void setBlockheight(Integer blockheight) {
        this.blockheight = blockheight;
    }

    public Integer getBlockindex() {
        return blockindex;
    }

    public void setBlockindex(Integer blockindex) {
        this.blockindex = blockindex;
    }

    public Integer getBlocktime() {
        return blocktime;
    }

    public void setBlocktime(Integer blocktime) {
        this.blocktime = blocktime;
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public List<String> getWalletConflicts() {
        return walletConflicts;
    }

    public void setWalletConflicts(List<String> walletConflicts) {
        this.walletConflicts = walletConflicts;
    }

    public Integer getTime() {
        return time;
    }

    public void setTime(Integer time) {
        this.time = time;
    }

    public Integer getTimereceived() {
        return timereceived;
    }

    public void setTimereceived(Integer timereceived) {
        this.timereceived = timereceived;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getSupportsReplaceByFee() {
        return supportsReplaceByFee;
    }

    public void setSupportsReplaceByFee(Boolean supportsReplaceByFee) {
        this.supportsReplaceByFee = supportsReplaceByFee;
    }

    public Boolean getAbandoned() {
        return abandoned;
    }

    public void setAbandoned(Boolean abandoned) {
        this.abandoned = abandoned;
    }

    public Boolean getInActiveChain() {
        return inActiveChain;
    }

    public void setInActiveChain(Boolean inActiveChain) {
        this.inActiveChain = inActiveChain;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getVsize() {
        return vsize;
    }

    public void setVsize(Integer vsize) {
        this.vsize = vsize;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getLocktime() {
        return locktime;
    }

    public void setLocktime(Integer locktime) {
        this.locktime = locktime;
    }

    public List<TXI> getvIn() {
        return vIn;
    }

    public void setvIn(List<TXI> vIn) {
        this.vIn = vIn;
    }

    public List<UTXO> getvOut() {
        return vOut;
    }

    public void setvOut(List<UTXO> vOut) {
        this.vOut = vOut;
    }

    @Override
    public Map<String, Object> toMap() {
        Map<String, Object> m = new HashMap<>();
        if (involvesWatchOnly != null) m.put("involvesWatchOnlly", involvesWatchOnly);
        if (address != null) m.put("address", address);
        if (category != null) m.put("category", category.getCatName());
        if (amount != null) m.put("amount", amount);
        if (vOutValue != null) m.put("voutValue", vOutValue);
        if (fee != null) m.put("fee", fee);
        if (confirmations != null) m.put("confirmations", confirmations);
        if (generated != null) m.put("generated", generated);
        if (trusted != null) m.put("trusted", trusted);
        if (blockhash != null) m.put("blockhash", blockhash);
        if (blockheight != null) m.put("blockheight", blockheight);
        if (blockindex != null) m.put("blockindex", blockindex);
        if (blocktime != null) m.put("blocktime", blocktime);
        if (txid != null) m.put("txid", txid);
        if (walletConflicts != null) m.put("walletconflicts", walletConflicts);
        if (time != null) m.put("time", time);
        if (timereceived != null) m.put("timereceived", timereceived);
        if (comment != null) m.put("comment", comment);
        if (supportsReplaceByFee != null) m.put("bip125-replaceable", supportsReplaceByFee);
        if (abandoned != null) m.put("abandoned", abandoned);

        if (inActiveChain != null) m.put("in_active_chain", inActiveChain);
        if (hash != null) m.put("hash", hash);
        if (size != null) m.put("size", size);
        if (vsize != null) m.put("vsize", vsize);
        if (weight != null) m.put("weight", weight);
        if (version != null) m.put("version", version);
        if (locktime != null) m.put("locktime", locktime);
        if (vIn != null) {
            List<Map<String, Object>> vinList = new ArrayList<>();
            for (TXI txi : vIn) {
                vinList.add(txi.toMap());
            }
            m.put("vin", vinList);
        }
        if (vOut!=null) {
            List<Map<String,Object>> voutList = new ArrayList<>();
            for(UTXO utxo : vOut) {
                voutList.add(utxo.toMap());
            }
            m.put("vout", voutList);
        }
        return m;
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if (m.size() > 0) {
            if (m.get("involvesWatchOnly") != null) involvesWatchOnly = (Boolean) m.get("involvesWatchOnly");
            if (m.get("address") != null) address = (String) m.get("address");
            if (m.get("category") != null) category = Category.getInstance((String) m.get("category"));
            if (m.get("amount") != null) amount = (Double) m.get("amount");
            if (m.get("voutValue")!=null) vOutValue = (Integer) m.get("voutValue");
            if (m.get("fee") != null) fee = (Double) m.get("fee");
            if (m.get("confirmations") != null) confirmations = (Integer) m.get("confirmations");
            if (m.get("generated") != null) generated = (Boolean) m.get("generated");
            if (m.get("trusted") != null) trusted = (Boolean) m.get("trusted");
            if (m.get("blockhash") != null) blockhash = (String) m.get("blockhash");
            if (m.get("blockheight") != null) blockheight = (Integer) m.get("blockheight");
            if (m.get("blockindex") != null) blockindex = (Integer) m.get("blockindex");
            if (m.get("blocktime") != null) blocktime = (Integer) m.get("blocktime");
            if (m.get("txid") != null) txid = (String) m.get("txid");
            if (m.get("walletconflicts") != null) walletConflicts = (List<String>) m.get("walletconflicts");
            if (m.get("time") != null) time = (Integer) m.get("time");
            if (m.get("timereceived") != null) timereceived = (Integer) m.get("timereceived");
            if (m.get("comment") != null) comment = (String) m.get("comment");
            if (m.get("bip125-replaceable") != null) {
                String bip125Replaceable = (String) m.get("bip125-replaceable");
                if(bip125Replaceable.equals("yes"))
                    supportsReplaceByFee = true;
                else
                    supportsReplaceByFee = false;
            }
            if (m.get("abandoned") != null) abandoned = (Boolean) m.get("abandoned");

            if (m.get("in_active_chain") != null) inActiveChain = (Boolean) m.get("in_active_chain");
            if (m.get("hash") != null) hash = (String) m.get("hash");
            if (m.get("size") != null) size = (Integer) m.get("size");
            if (m.get("vsize") != null) vsize = (Integer) m.get("vsize");
            if (m.get("weight") != null) weight = (Integer) m.get("weight");
            if (m.get("version") != null) version = (Integer) m.get("version");
            if (m.get("locktime") != null) locktime = (Integer) m.get("locktime");
            if (m.get("vin") != null) {
                List<Map<String, Object>> vInML = (List<Map<String, Object>>) m.get("vin");
                vIn = new ArrayList<>();
                TXI txi;
                for (Map<String, Object> vInM : vInML) {
                    txi = new TXI();
                    txi.txid = (String) vInM.get("txid");
                    txi.vout = (Integer) vInM.get("vout");
                    Map<String, Object> vInSSM = (Map<String, Object>) vInM.get("scriptSig");
                    txi.scriptSig = new ScriptSig();
                    txi.scriptSig.asm = (String) vInSSM.get("asm");
                    txi.scriptSig.hex = (String) vInSSM.get("hex");
                    txi.txInWitness = (List<String>) vInM.get("txinwitness");
                    txi.sequence = (Integer) vInM.get("sequence");
                    vIn.add(txi);
                }
            }
            if (m.get("vout") != null) {
                if (m.get("vout") instanceof List) {
                    List<Map<String, Object>> vOutML = (List<Map<String, Object>>) m.get("vout");
                    vOut = new ArrayList<>();
                    UTXO utxo;
                    for (Map<String, Object> vOutM : vOutML) {
                        utxo = new UTXO();
                        utxo.value = (Double) vOutM.get("value");
                        utxo.index = (Integer) vOutM.get("n");
                        Map<String, Object> vOutSPK = (Map<String, Object>) vOutM.get("scriptPubKey");
                        utxo.scriptPubKey = new ScriptPublicKey();
                        utxo.scriptPubKey.asm = (String) vOutSPK.get("asm");
                        utxo.scriptPubKey.hex = (String) vOutSPK.get("hex");
                        utxo.scriptPubKey.reqSigs = (Integer) vOutSPK.get("reqSigs");
                        utxo.scriptPubKey.type = ScriptPublicKey.Type.valueOf((String) vOutSPK.get("type"));
                        utxo.scriptPubKey.addresses = (List<String>) vOutSPK.get("addresses");
                    }
                } else if (m.get("vout") instanceof Integer && vOutValue==null) {
                    vOutValue = (Integer) m.get("vout");
                }
            }
        }
    }

    public String toJSON() {
        return JSONPretty.toPretty(JSONParser.toString(this.toMap()), 4);
    }

    public void fromJSON(String json) {
        this.fromMap((Map) JSONParser.parse(json));
    }
}
