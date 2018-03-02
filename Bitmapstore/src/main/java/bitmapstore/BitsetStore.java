package bitmapstore;



import blobstore.BlobStore;

import java.util.BitSet;

/**
 * Created by suresh on 11/2/18.
 */
public class BitsetStore {
    BlobStore blobStore;
    int size;

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public BitsetStore(String path, int size){
        blobStore = new BlobStore(path,size);
        this.setSize(size);
    }

    public long createBitLocation(){
        long location = blobStore.createBlobBlock();
        return location;
    }

    public void appendBits(long location, int offset,BitSet bitsetToAppend){
        if (offset>size){
            return;
        }
        //ex offset is 10 then rem will be 6
        int rem = offset%8;
        //writing rem in last byte
        if (rem!=0) {
            BitSet fixTamper = readBits(location,offset-rem,offset);
            bitsetToAppend.get(rem,rem+(8-rem)).stream().forEach(t->{
                fixTamper.set((rem)+t);
            });
            byte[] tamperedData = fixTamper.toByteArray();
            blobStore.writeBlobData(location+((offset)/8), tamperedData, tamperedData.length);
            offset=offset+(8-rem);
            byte[] tmp = bitsetToAppend.get(8-rem,bitsetToAppend.length()).toByteArray();
            blobStore.writeBlobData(location+(offset/8),tmp,tmp.length);
            return;
        }
//        writing new byte entries
        byte[] tmp = bitsetToAppend.toByteArray();
        blobStore.writeBlobData(location+(offset/8),tmp,tmp.length);
    }
    public void close(){
        blobStore.close();
    }
    public BitSet readBits(long location,long startOffset,long lastOffset){
        if (lastOffset-startOffset <0 || lastOffset-startOffset > size){
            return null;
        }
        byte[] tmp = blobStore.readBlobData(location+(startOffset/8),(int)((lastOffset/8)-(startOffset/8))+1);
        return BitSet.valueOf(tmp).get((int)startOffset%8,(int)lastOffset-(int)startOffset);
    }
    public static void main(String[] args) {
        BitsetStore store = new BitsetStore("/home/suresh/git/dum/bss",65536);
        
        BitSet tmp = new BitSet(1000);
        tmp.set(0,10,true);
        tmp.set(6,9,false);
        store.appendBits(0,0,tmp);
        store.readBits(0,0,10).stream().forEach(t->{
            System.out.println(t);
        });
        System.out.println("\n");
        tmp.set(6,9,true);
        store.appendBits(0,10,tmp);
        store.readBits(0,0,20).stream().forEach(t->{
                System.out.println(t);
        });

    }
}
