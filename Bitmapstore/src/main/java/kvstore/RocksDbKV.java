package kvstore;

import org.rocksdb.CompactionStyle;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import utils.Utils;

import java.util.BitSet;

import static org.fusesource.leveldbjni.JniDBFactory.bytes;

/**
 * Created by suresh on 6/2/18.
 */
public class RocksDbKV {
    RocksDB db=null;
    public RocksDbKV(String filename) {
        RocksDB.loadLibrary();
        Options options = new Options().setCreateIfMissing(true).setMaxWriteBufferNumber(4).setMaxBackgroundFlushes(4).setMaxBackgroundCompactions(64).optimizeLevelStyleCompaction().setAllowConcurrentMemtableWrite(true).setCompactionStyle(CompactionStyle.LEVEL).setNumLevels(9);
        options.setMaxSubcompactions(64);
        try {
            db = RocksDB.open(options, filename);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }

    public RocksDB getDb(){
        return db;
    }
    public void close(){
        try {
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void putOnDb(byte[] key, byte[] value){
        putOnRocksDB(key,value);
    }

    private byte[] getOnDb(byte[] key){
        return getOnRocksDB(key);
    }

    private void putOnRocksDB(byte[] key,byte[] value){
        try {
            db.put(key,value);
        } catch (RocksDBException e) {
            e.printStackTrace();
        }
    }
    private byte[] getOnRocksDB(byte[] key){

        try {
            return db.get(key);
        } catch (RocksDBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void putBytes(String key,byte[] value){
        putOnDb(bytes(key), value);
    }
    public byte[] getBytes(String key){
        byte[] value = getOnDb(bytes(key));
        return value;
    }
    public void putBytes(byte[] key,byte[] value){
        putOnDb(key, value);
    }
    public byte[] getBytes(byte[] key){
        byte[] value = getOnDb(key);
        return value;
    }
    public void putObject(String key,Object value){
        putOnDb(bytes(key), Utils.serialize(value));
    }
    public Object getObject(String key){
        byte[] value = getOnDb(bytes(key));
        if (value==null) return null;
        return Utils.deserialize(value);
    }

    public void putBitset(byte[] key,BitSet value){
        byte[] tmp = getOnDb(key);
        if (tmp==null){
            putOnDb(key, Utils.serializeBitSet(value));
        }else{
            BitSet bs = Utils.deserializeBitSet(tmp);
            bs.or(value);
            putOnDb(key,Utils.serializeBitSet(bs));
        }
    }
    public BitSet getBitset(byte[] key){
        byte[] value = getOnDb(key);
        if (value==null) return null;
        return (BitSet) Utils.deserializeBitSet(value);
    }
    public void putString(String key,String value){
        putOnDb(bytes(key), bytes(value));
    }
    public String getString(String key){
        byte[] value = getOnDb(bytes(key));
        if (value==null) return null;
        return new String(value);
    }
    public void putLong(String key,Long value){
        putObject(key,value);
    }
    public Long getLong(String key){
        return (Long)getObject(key);
    }
}
