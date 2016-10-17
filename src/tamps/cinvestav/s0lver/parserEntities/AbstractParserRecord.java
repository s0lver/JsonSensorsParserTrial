package tamps.cinvestav.s0lver.parserEntities;

public class AbstractParserRecord {
    protected long timestamp;
    private byte type;

    public AbstractParserRecord(long timestamp, byte type) {
        this.timestamp = timestamp;
        this.type = type;
    }

    public byte getType() {
        return type;
    }
}
