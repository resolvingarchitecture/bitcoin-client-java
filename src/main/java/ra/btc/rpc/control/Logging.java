package ra.btc.rpc.control;

import ra.btc.rpc.RPCRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Gets and sets the logging configuration.
 *
 * When called without an argument, returns the list of categories with status that are currently being debug logged or not.
 *
 * When called with arguments, adds or removes categories from debug logging and return the lists above.
 *
 * The arguments are evaluated in order “include”, “exclude”.
 *
 * If an item is both included and excluded, it will thus end up being excluded.
 *
 * The valid logging categories are: net, tor, mempool, http, bench, zmq, db, rpc, estimatefee, addrman, selectcoins, reindex, cmpctblock, rand, prune, proxy, mempoolrej, libevent, coindb, qt, leveldb In addition, the following are available as category names with special meanings:
 *
 *         “all”, “1” : represent all logging categories.
 *
 *         “none”, “0” : even if other logging categories are specified, ignore all of them.
 */
public class Logging extends RPCRequest {
    public static final String NAME = "logging";

    public enum Command {all, none, net, tor, mempool, http, bench, zmq, db, rpc, estimatefee, addrman, selectcoins, reindex, cmpctblock, rand, prune, proxy, mempoolrej, libevent, coindb, qt, leveldb}

    // Request
    public List<Command> include = new ArrayList<>(); // The command to get help on; optional, default=all commands
    public List<Command> exclude = new ArrayList<>();
    // Response
    public Map<Command,Boolean> statuses; // keys are the logging categories, and values indicates its status; "category": true|false,  (bool) if being debug logged or not. false:inactive, true:active

    public Logging() {
        include.add(Command.all);
    }

    public Logging(Command command) {
        include.add(command);
    }

    public Logging(List<Command> commands) {
        include = commands;
    }

    public Logging(List<Command> include, List<Command> exclude) {
        this.include = include;
        this.exclude = exclude;
    }

    @Override
    public Map<String, Object> toMap() {
        if(include!=null && include.size() > 0) {
            List<String> includes = new ArrayList<>();
            for(Command c : include) {
                includes.add(c.name());
            }
            params.add(includes.toArray());
            if(exclude!=null && exclude.size() > 0) {
                List<String> excludes = new ArrayList<>();
                for(Command c : exclude) {
                    excludes.add(c.name());
                }
                params.add(excludes.toArray());
            }
        }
        return super.toMap();
    }

    @Override
    public void fromMap(Map<String, Object> m) {
        if(m.size() > 0) {
            statuses = new HashMap<>();
            for(String cmd : m.keySet()) {
                statuses.put(Command.valueOf(cmd), "true".equals(m.get(cmd)));
            }
        }
    }
}
