package utils;

import org.nustaq.serialization.FSTConfiguration;
import org.roaringbitmap.RoaringBitmap;

import java.io.*;
import java.util.BitSet;

/**
 * Created by suresh on 9/11/17.
 */
public class Utils {
    static FSTConfiguration conf = FSTConfiguration.createUnsafeBinaryConfiguration();


    public static byte[] serializeBitSet(BitSet bitset){
                try {
            try(ByteArrayOutputStream b = new ByteArrayOutputStream()){
                try(ObjectOutputStream o = new ObjectOutputStream(b)){
                    o.writeObject(bitset);
                }
                return b.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BitSet deserializeBitSet(byte[] bytes){
        try {
            try(ByteArrayInputStream b = new ByteArrayInputStream(bytes)){
                try(ObjectInputStream o = new ObjectInputStream(b)){
                    return (BitSet) o.readObject();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }
    public static byte[] serializeBitmap(RoaringBitmap mrb){
        mrb.runOptimize(); //to improve compression
        byte[] array = new byte[mrb.serializedSizeInBytes()];
        try {
            mrb.serialize(new DataOutputStream(new OutputStream() {
                int c = 0;

                @Override
                public void close() {
                }

                @Override
                public void flush() {
                }

                @Override
                public void write(int b) {
                    array[c++] = (byte)b;
                }

                @Override
                public void write(byte[] b) {
                    write(b,0,b.length);
                }

                @Override
                public void write(byte[] b, int off, int l) {
                    System.arraycopy(b, off, array, c, l);
                    c += l;
                }
            }));
        } catch (IOException ioe) {
            // should never happen because we write to a byte array
            throw new RuntimeException("unexpected error while serializing to a byte array");
        }
        return array;
    }

    public static RoaringBitmap deserializeBitmap(byte[] array){
        RoaringBitmap ret = new RoaringBitmap();
        try {
            ret.deserialize(new DataInputStream(new InputStream() {
                int c = 0;

                @Override
                public int read() {
                    return array[c++] & 0xff;
                }

                @Override
                public int read(byte b[]) {
                    return read(b, 0, b.length);
                }

                @Override
                public int read(byte[] b, int off, int l) {
                    System.arraycopy(array, c, b, off, l);
                    c += l;
                    return l;
                }
            }));
        } catch (IOException ioe) {
            System.out.println("lenght"+array.length);
            // should never happen because we read from a byte array
            throw new RuntimeException("unexpected error while deserializing from a byte array");
        }
        return ret;
    }
    public static byte[] serialize(Object obj) {
        return conf.asByteArray(obj);
    }

    public static Object deserialize(byte[] bytes)  {
        return conf.asObject(bytes);
    }
}
