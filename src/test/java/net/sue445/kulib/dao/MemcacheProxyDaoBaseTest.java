package net.sue445.kulib.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import net.sue445.kulib.model.DummyModel;

import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;
import org.slim3.tester.AppEngineTestCase;

import com.google.appengine.api.datastore.Key;


public class MemcacheProxyDaoBaseTest extends AppEngineTestCase{
	private MemcacheProxyDaoBase<DummyModel> dao = new MemcacheProxyDaoBase<DummyModel>(){
	};

	private static final String CURRENT_VERSION_ID = "1.0";


	@Test
	public void test() throws Exception {
		assertThat(dao, is(notNullValue()));
	}

	@Test
	public void createMemcacheKey(){
		Key key = Datastore.createKey(DummyModel.class, 1);
		assertThat(dao.createMemcacheKey(key), is(CURRENT_VERSION_ID + "DummyModel(1)"));
	}

	@Test
	public void put(){
		DummyModel model = createDefaultModel();
		dao.put(model);

		Key datastoreKey = createDefaultDatastoreKey();
		DummyModel actual = Datastore.get(DummyModel.class, datastoreKey);
		assertThat(actual, is(notNullValue()));
		assertThat(actual.getKey(), is(datastoreKey));
		assertThat(actual.getData(), is("100"));

		String memcacheKey = createDefaultMemcacheKey();
		assertThat(Memcache.contains(memcacheKey), is(false));
	}

	private DummyModel createDefaultModel() {
		DummyModel model = new DummyModel();
		model.setKey(createDefaultDatastoreKey());
		model.setData("100");
		return model;
	}

	private Key createDefaultDatastoreKey() {
		return Datastore.createKey(DummyModel.class, "name");
	}

	private String createDefaultMemcacheKey() {
		return dao.createMemcacheKey(createDefaultDatastoreKey());
	}

	@Test
	public void get_NotFound(){
		DummyModel actual = dao.get(createDefaultDatastoreKey());
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void get_FoundInDatastore(){
		setUpDatastoreModel();

		DummyModel datastoreActual = dao.get(createDefaultDatastoreKey());
		assertThat(datastoreActual, is(notNullValue()));
		assertThat(datastoreActual.getData(), is("100"));

		DummyModel memcacheActual = Memcache.get(createDefaultMemcacheKey());
		assertThat(memcacheActual, is(notNullValue()));
		assertThat(memcacheActual.getData(), is("100"));
	}

	private void setUpDatastoreModel() {
		DummyModel model = createDefaultModel();
		Datastore.put(model);
	}

	@Test
	public void get_FoundInMemcache(){
		beforeGet_FoundInMemcache();
		setUpMemcacheModel();

		DummyModel memcacheActual = dao.get(createDefaultDatastoreKey());
		assertThat(memcacheActual, is(notNullValue()));
		assertThat(memcacheActual.getData(), is("100"));
	}

	private void beforeGet_FoundInMemcache() {
		DummyModel datastoreModel = new DummyModel();
		datastoreModel.setKey(createDefaultDatastoreKey());
		datastoreModel.setData("200");	// dummy
		Datastore.put(datastoreModel);
	}

	private void setUpMemcacheModel() {
		Memcache.put(createDefaultMemcacheKey(), createDefaultModel());
	}

	@Test
	public void getModelOrNull_Found(){
		setUpDatastoreModel();

		DummyModel actual = Datastore.getOrNull(dao.m, createDefaultDatastoreKey());
		assertThat(actual, is(notNullValue()));
	}

	@Test
	public void getModelOrNull_NotFound(){
		DummyModel actual = Datastore.getOrNull(dao.m, createDefaultDatastoreKey());
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void delete() throws Exception {
		setUpDatastoreModel();
		setUpMemcacheModel();

		dao.delete(createDefaultDatastoreKey());

		assertThat(tester.count(DummyModel.class), is(0));
		assertThat(Memcache.contains(createDefaultMemcacheKey()), is(false));
	}
}
