package blobstore;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Created by suresh on 24/12/17.
 */
public class BlobStore {
    FileInputStream fis;
    FileOutputStream fos;
    File file;
    String path="";
    int size;
    public long createBlobBlock(){
        long pos =-1;
        synchronized (this){
            pos = file.length();
            byte[] copyToWrite = new byte[size];
            try {
                fos.getChannel().position(pos).write(ByteBuffer.wrap(copyToWrite));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return pos;
    }
    public void writeBlobData(long offset, byte[] array, int len){
        byte[] copyToWrite = new byte[size];
        System.arraycopy(array,0,copyToWrite,0,len);
        try {
            fos.getChannel().position(offset).write(ByteBuffer.wrap(copyToWrite));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public byte[] readBlobData(long offset, int len){
        byte[] copyToWrite = new byte[len];
        try {
            fis.getChannel().position(offset).read(ByteBuffer.wrap(copyToWrite));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return copyToWrite;
    }
    public BlobStore(String path,int sizeInBytes){
        this.size=sizeInBytes;
        this.path=path;
        file = new File(path);
        try {
            if(!file.exists()){file.createNewFile();}
            fis = new FileInputStream(file);
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void close(){
        try {
            fis.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void main(String[] args) {
        BlobStore store = new BlobStore("/tmp/test",10*1024);
        long pos = store.createBlobBlock();
        System.out.println(pos);
        int len = "tmpdata".getBytes().length;
        store.writeBlobData(pos,"tmpdata".getBytes(),len);
        byte array[] = new byte[len];
        array=store.readBlobData(pos,len);
        System.out.println(new String(array));
        len="dat".getBytes().length;
        array="dat".getBytes();
        store.writeBlobData(pos,array,len);
        array=store.readBlobData(pos,len);
        System.out.println(array.length+len+new String(array));
    }
}
