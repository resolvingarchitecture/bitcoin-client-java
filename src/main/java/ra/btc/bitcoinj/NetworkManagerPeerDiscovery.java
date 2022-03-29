package ra.btc.bitcoinj;

import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.net.discovery.PeerDiscovery;
import org.bitcoinj.net.discovery.PeerDiscoveryException;
import ra.btc.BitcoinService;
import ra.common.tasks.BaseTask;
import ra.common.tasks.TaskRunner;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class NetworkManagerPeerDiscovery extends BaseTask implements PeerDiscovery {

    private BitcoinService service;
    private BitcoinJClient client;
    private Properties config;
    private List<PeerAddress> seeds;

    public NetworkManagerPeerDiscovery() {
        super(NetworkManagerPeerDiscovery.class.getName(), null);
    }

    public void init(BitcoinService service, BitcoinJClient client, Properties config, List<PeerAddress> seeds, TaskRunner taskRunner) {
        this.service = service;
        this.client = client;
        this.config = config;
        this.seeds = seeds;
        super.taskRunner = taskRunner;
    }

    @Override
    public Boolean execute() {

        return true;
    }

    @Override
    public List<InetSocketAddress> getPeers(long l, long l1, TimeUnit timeUnit) throws PeerDiscoveryException {
        return null;
    }

    @Override
    public void shutdown() {

    }
}
