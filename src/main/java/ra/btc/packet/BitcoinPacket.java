package ra.btc.packet;


import ra.common.BaseMessage;

/**
 * https://en.bitcoin.it/wiki/Protocol_specification
 *
 */
public abstract class BitcoinPacket extends BaseMessage {

    protected byte[] payload;
    protected BitcoinPacket parent;

}
