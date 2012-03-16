package net.sue445.kulib.dao;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sue445.kulib.model.Slim3Model;
import net.sue445.kulib.util.MemcacheUtil;

import org.slim3.datastore.Datastore;
import org.slim3.datastore.DatastoreUtil;
import org.slim3.datastore.ModelMeta;
import org.slim3.memcache.Memcache;

import com.google.appengine.api.datastore.Key;

/**
 * Datasrore access proxy dao with Memcache
 * @author sue445
 *
 * @param <M>	ModelClass
 */
public abstract class MemcacheProxyDaoBase<M extends Slim3Model> {
	protected static final Logger logger = Logger.getLogger(MemcacheProxyDaoBase.class.getName());

	/**
	 * The model class.
	 */
	protected Class<M> modelClass;

	/**
	 * The meta data of model.
	 */
	protected ModelMeta<M> m;

	/**
	 * Constructor.
	 */
	@SuppressWarnings("unchecked")
	public MemcacheProxyDaoBase() {
		for (Class<?> c = getClass(); c != Object.class; c = c.getSuperclass()) {
			Type type = c.getGenericSuperclass();
			if (type instanceof ParameterizedType) {
				modelClass =
					((Class<M>) ((ParameterizedType) type)
						.getActualTypeArguments()[0]);
				break;
			}
		}
		if (modelClass == null) {
			throw new IllegalStateException("No model class is found.");
		}
		m = DatastoreUtil.getModelMeta(modelClass);
	}

	/**
	 * put model and clear Memcache
	 * @param model
	 */
	public void put(M model){
		deleteInMemcache(model.getKey());
		Datastore.putAsync(model);
	}

	/**
	 * put models and clear Memcache
	 * @param model
	 */
	public void putAll(Iterable<M> models){
		List<Key> keys = new ArrayList<Key>();
		for(M model : models){
			keys.add(model.getKey());
		}

		deleteAllInMemcache(keys);
		Datastore.putAsync(models);
	}

	/**
	 *
	 * @param key
	 * @return
	 */
	protected String createMemcacheKey(Key key){
		return MemcacheUtil.createKeyPrefix(this.getClass()) + key.toString();
	}

	/**
	 *
	 * @param keys
	 * @return
	 */
	protected List<String> createMemcacheKeys(Iterable<Key> keys){
		List<String> memcacheKeyList = new ArrayList<String>();

		for(Key key : keys){
			memcacheKeyList.add(createMemcacheKey(key));
		}

		return memcacheKeyList;
	}

	protected void deleteInMemcache(Key key) {
		String memcacheKey = createMemcacheKey(key);
		try {
			Memcache.delete(memcacheKey);
		} catch (Exception e) {
			String message = "[FAILED]Memcache delete:key=" + memcacheKey;
			logger.log(Level.WARNING, message, e);
		}
	}

	protected void deleteAllInMemcache(Iterable<Key> keys) {
		List<String> memcacheKeys = createMemcacheKeys(keys);
		try {
			Memcache.deleteAll(memcacheKeys);
		} catch (Exception e) {
			String message = "[FAILED]Memcache delete:key=" + memcacheKeys;
			logger.log(Level.WARNING, message, e);
		}
	}

	/**
	 * get model from Memcache or Datastore.<br>
	 * if not found in Memcache, get from Datastore and put to Memcache.
	 * @param key
	 * @return
	 */
	public M get(Key key){
		M memcacheModel = getFromMemcache(key);
		if(memcacheModel != null){
			logger.log(Level.FINEST, "get from Memcache: key=" + key);
			return memcacheModel;
		}

		M datastoreModel = Datastore.getOrNull(m, key);
		if(datastoreModel == null){
			return null;
		}

		putToMemcache(datastoreModel);

		logger.log(Level.FINEST, "get from Datastore: key=" + key);
		return datastoreModel;
	}

	/**
	 *
	 * @param memcacheKey
	 * @return
	 */
	protected M getFromMemcache(Key key) {
		String memcacheKey = createMemcacheKey(key);
		try {
			return Memcache.<M>get(memcacheKey);

		} catch (Exception e) {
			String message = "[FAILED]Memcache get:key=" + memcacheKey;
			logger.log(Level.WARNING, message, e);
			return null;
		}
	}

	/**
	 *
	 * @param model
	 */
	protected void putToMemcache(M model) {
		String memcacheKey = createMemcacheKey(model.getKey());
		try {
			Memcache.put(memcacheKey, model);

		} catch (Exception e) {
			String message = "[FAILED]Memcache get:key=" + memcacheKey;
			logger.log(Level.WARNING, message, e);
		}
	}

	/**
	 *
	 * @param model
	 */
	protected void putAllToMemcache(Iterable<M> models) {
		Map<Object, Object> modelMap = new LinkedHashMap<Object, Object>();

		for(M model : models){
			String memcacheKey = createMemcacheKey(model.getKey());
			modelMap.put(memcacheKey, model);
		}

		try {
			Memcache.putAll(modelMap);

		} catch (Exception e) {
			String message = "[FAILED]Memcache get:key=" + modelMap.keySet();
			logger.log(Level.WARNING, message, e);
		}
	}

	/**
	 * get model from Memcache or Datastore.<br>
	 * if not found in Memcache, get from Datastore and put to Memcache.
	 * @param key
	 * @return
	 */
	public List<M> getAll(Iterable<Key> keys){
		Map<String, M> memcacheMap = getAllFromMemcache(keys);

		List<Key> datastoreKeys = new ArrayList<Key>();
		for(Key key : keys){
			String memcacheKey = createMemcacheKey(key);
			if(!memcacheMap.containsKey(memcacheKey)){
				// NotFound in Memcache, and get from Datastore
				datastoreKeys.add(key);
			}
		}

		List<M> datastoreList = Datastore.get(m, datastoreKeys);

		List<M> result = new ArrayList<M>(memcacheMap.values());
		result.addAll(datastoreList);

		putAllToMemcache(datastoreList);

		return result;
	}

	/**
	 *
	 * @param memcacheKey
	 * @return
	 */
	protected Map<String, M> getAllFromMemcache(Iterable<Key> keys) {
		List<String> memcacheKeys = createMemcacheKeys(keys);
		try {
			Map<Object, Object> memcacheMap = Memcache.getAll(memcacheKeys);
			Map<String, M> resultMap = new LinkedHashMap<String, M>();

			for(Map.Entry<Object, Object> entry : memcacheMap.entrySet()){
				@SuppressWarnings("unchecked")
				M value = (M)entry.getValue();

				resultMap.put((String)entry.getKey(), value);
			}
			return resultMap;

		} catch (Exception e) {
			String message = "[FAILED]Memcache get:key=" + memcacheKeys;
			logger.log(Level.WARNING, message, e);
			return null;
		}
	}

	/**
	 * delete model in both Datastote and Memcache
	 * @param key
	 */
	public void delete(Key key){
		Datastore.deleteAsync(key);
		deleteInMemcache(key);
	}

	/**
	 * delete models in both Datastote and Memcache
	 * @param key
	 */
	public void deleteAll(Iterable<Key> keys){
		Datastore.deleteAsync(keys);
		deleteAllInMemcache(keys);
	}

}
