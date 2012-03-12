package net.sue445.kulib.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import net.sue445.kulib.model.DummyModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;
import org.slim3.tester.AppEngineTestCase;

import com.google.appengine.api.datastore.Key;

@RunWith(Enclosed.class)
public class MemcacheProxyDaoBaseTest{
	private static final String CURRENT_VERSION_ID = "1.0";

	private static MemcacheProxyDaoBase<DummyModel> dao = new MemcacheProxyDaoBase<DummyModel>(){
	};

	public static class WhenNotExistsData extends AppEngineTestCase{
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

		@Test
		public void get(){
			DummyModel actual = dao.get(createDefaultDatastoreKey());
			assertThat(actual, is(nullValue()));
		}
	}

	public static class WhenExistsOnlyDatastore extends AppEngineTestCase{
		@Before
		public void setUpDatastore() {
			DummyModel model = createDefaultModel();
			Datastore.put(model);
		}

		@Test
		public void get(){
			DummyModel datastoreActual = dao.get(createDefaultDatastoreKey());
			assertThat(datastoreActual, is(notNullValue()));
			assertThat(datastoreActual.getData(), is("100"));

			DummyModel memcacheActual = Memcache.get(createDefaultMemcacheKey());
			assertThat(memcacheActual, is(notNullValue()));
			assertThat(memcacheActual.getData(), is("100"));
		}

	}

	public static class WhenExistsOnlyMemcache extends AppEngineTestCase{
		@Before
		public void setUpMemcache() {
			Memcache.put(createDefaultMemcacheKey(), createDefaultModel());
		}

		@Test
		public void get_FoundInMemcache(){
			setUpMemcache();

			DummyModel memcacheActual = dao.get(createDefaultDatastoreKey());
			assertThat(memcacheActual, is(notNullValue()));
			assertThat(memcacheActual.getData(), is("100"));
		}
	}

	public static class WhenExistsDatastoreAndMemcache extends AppEngineTestCase{
		@Before
		public void setUpDatastore() {
			DummyModel model = createDefaultModel();
			Datastore.put(model);
		}

		@Before
		public void setUpMemcache() {
			Memcache.put(createDefaultMemcacheKey(), createDefaultModel());
		}

		@Test
		public void delete() throws Exception {
			dao.delete(createDefaultDatastoreKey());

			assertThat(tester.count(DummyModel.class), is(0));
			assertThat(Memcache.contains(createDefaultMemcacheKey()), is(false));
		}
	}

	private static DummyModel createDefaultModel() {
		return createModel("name", "100");
	}

	private static DummyModel createModel(String name, String value) {
		DummyModel model = new DummyModel();
		model.setKey(Datastore.createKey(DummyModel.class, name));
		model.setData(value);
		return model;
	}

	private static Key createDefaultDatastoreKey() {
		return Datastore.createKey(DummyModel.class, "name");
	}

	private static String createDefaultMemcacheKey() {
		return dao.createMemcacheKey(createDefaultDatastoreKey());
	}
}
