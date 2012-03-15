package net.sue445.kulib.util;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slim3.memcache.Memcache;

import com.google.appengine.api.memcache.Expiration;

/**
 * put a compressed serialize object to Memcache
 * @author sue445
 *
 */
public final class CompressMemcacheUtil {
	private static final int END_OF_DATA = -1;

	private static final int UNCOMPRESS_BUFFER_SIZE = 1024*1024*2;	// 2MB

	private static final Logger logger = Logger.getLogger(CompressMemcacheUtil.class.getName());


	private CompressMemcacheUtil(){

	}

	/**
	 * put a compressed object to Memcache
	 * @param key
	 * @param src
	 */
	public static void put(Object key, Object src) {
		try {
			Memcache.put(key, compress(serialize(src)));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "put failed: key=" + key, e);
		}
	}

	/**
	 * put a compressed object to Memcache
	 * @param key
	 * @param src
	 * @param expiration
	 */
	public static void put(Object key, Object src, Expiration expiration) {
		try {
			Memcache.put(key, compress(serialize(src)), expiration);
		} catch (Exception e) {
			logger.log(Level.SEVERE, "put failed: key=" + key, e);
		}
	}

	/**
	 *
	 * @param src
	 * @return
	 * @throws IOException
	 */
	// package private
	static byte[] serialize(Object src) throws IOException {
		if(src == null){
			return null;
		}

		ObjectOutputStream oos = null;
		long start = System.currentTimeMillis();
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(src);
			oos.flush();
			return baos.toByteArray();

		} finally{
			closeQuietly(oos);
			printEndMessage("serialize:", start);
		}
	}

	/**
	 * close stream (ignore null and exception)
	 * @param stream
	 */
	private static void closeQuietly(Closeable stream) {
		if(stream == null){
			return;
		}

		try {
			stream.close();

		} catch (IOException e) {
			logger.log(Level.WARNING, "failed stream close", e);
		}
	}

	/**
	 * print message with processed time
	 * @param msg
	 * @param start
	 */
	private static void printEndMessage(String msg, long start) {
		long time = System.currentTimeMillis() - start;

		if(logger.isLoggable(Level.FINEST)){
			logger.log(Level.FINEST, msg + time + "ms");
		}
	}

	/**
	 *
	 * @param src
	 * @return
	 * @throws IOException
	 */
	// package private
	static byte[] compress(byte[] src) throws IOException {
		if(isEmpty(src)){
			return null;
		}

		long start = System.currentTimeMillis();
		GZIPOutputStream gos = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(baos);
			gos.write(src);
			gos.flush();
			gos.finish();
			byte[] result = baos.toByteArray();

			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "compressed: " + src.length + "bytes -> " + result.length + "bytes");
			}

			return result;

		} finally{
			closeQuietly(gos);
			printEndMessage("compress:", start);
		}
	}

	/**
	 * whether an empty array
	 * @param src
	 * @return
	 */
	private static boolean isEmpty(byte[] src) {
		return src == null || src.length == 0;
	}

	/**
	 * get a compressed object from Memcache
	 * @param key
	 * @return
	 */
	public static <T> T get(Object key) {
		try {
			return (T)deserialize(uncompress((byte[])Memcache.get(key)));
		} catch (Exception e) {
			logger.log(Level.SEVERE, "get failed: key=" + key, e);
			return null;
		}
	}

	/**
	 *
	 * @param src
	 * @return
	 * @throws IOException
	 */
	// package private
	static byte[] uncompress(byte[] src) throws IOException {
		if(isEmpty(src)){
			return null;
		}

		GZIPInputStream gis = null;
		BufferedOutputStream bos = null;
		long start = System.currentTimeMillis();

		try {
			gis =  new GZIPInputStream(new BufferedInputStream(new ByteArrayInputStream(src)));
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bos = new BufferedOutputStream(baos);

			byte[] buffer = new byte[UNCOMPRESS_BUFFER_SIZE];
			while(true){
				int readSize = gis.read(buffer);
				if(readSize == END_OF_DATA){
					break;
				}
				baos.write(buffer, 0, readSize);
			}
			baos.flush();

			byte[] result = baos.toByteArray();

			if(logger.isLoggable(Level.FINEST)){
				logger.log(Level.FINEST, "uncompressed: " + src.length + "bytes -> " + result.length + "bytes");
			}

			return result;

		} finally{
			closeQuietly(bos);
			closeQuietly(gis);
			printEndMessage("uncompressed:", start);
		}
	}

	/**
	 *
	 * @param src
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	static <T> T deserialize(byte[] src) throws IOException, ClassNotFoundException {
		if(isEmpty(src)){
			return null;
		}

		ObjectInputStream ois = null;
		long start = System.currentTimeMillis();

		try {
			ois = new ObjectInputStream(new ByteArrayInputStream(src));
			return (T) ois.readObject();

		} finally{
			closeQuietly(ois);
			printEndMessage("deserialize:", start);
		}
	}

}
