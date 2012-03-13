package net.sue445.kulib.dao;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

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
	private static final String DEFAULT_KEY_NAME = "name";
	private static final String NAME1 = "name1";
	private static final String NAME2 = "name2";

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
			putDatastore(NAME1, "100");
			putDatastore(NAME2, "200");
		}

		private void putDatastore(String name, String value) {
			DummyModel model = createModel(name, value);
			Datastore.put(model);
		}

		@Test
		public void get(){
			DummyModel datastoreActual = dao.get(createDatastoreKey(NAME1));
			assertThat(datastoreActual, is(notNullValue()));
			assertThat(datastoreActual.getData(), is("100"));

			DummyModel memcacheActual = Memcache.get(createMemcacheKey(NAME1));
			assertThat(memcacheActual, is(notNullValue()));
			assertThat(memcacheActual.getData(), is("100"));
		}

		@Test
		public void getAll() throws Exception {
			Key[] keys = {
					createDatastoreKey(NAME1),
					createDatastoreKey(NAME2),
			};
			List<DummyModel> actual = dao.getAll(Arrays.asList(keys));

			assertThat(actual, is(notNullValue()));
			assertThat(actual.size(), is(2));

			assertThat(actual.get(0).getKey(), is(createDatastoreKey(NAME1)));
			assertThat(actual.get(1).getKey(), is(createDatastoreKey(NAME2)));

			assertThat(Memcache.contains(createMemcacheKey(NAME1)), is(true));
			assertThat(Memcache.contains(createMemcacheKey(NAME2)), is(true));
		}
	}

	public static class WhenExistsOnlyMemcache extends AppEngineTestCase{
		@Before
		public void setUpMemcache() {
			putMemcache(NAME1, "100");
			putMemcache(NAME2, "200");
		}

		private void putMemcache(String name, String value) {
			DummyModel model = createModel(name, value);
			Memcache.put(createMemcacheKey(name), model);
		}

		@Test
		public void get_FoundInMemcache(){
			DummyModel memcacheActual = dao.get(createDatastoreKey(NAME1));
			assertThat(memcacheActual, is(notNullValue()));
			assertThat(memcacheActual.getData(), is("100"));
		}

		@Test
		public void putAll() throws Exception {
			DummyModel[] models = {
					createModel(NAME1, "100"),
					createModel(NAME2, "200"),
			};
			dao.putAll(Arrays.asList(models));

			assertThat(tester.count(DummyModel.class), is(2));
			assertThat(Memcache.contains(createMemcacheKey(NAME1)), is(false));
			assertThat(Memcache.contains(createMemcacheKey(NAME2)), is(false));
		}

		@Test
		public void getAll() throws Exception {
			Key[] keys = {
					createDatastoreKey(NAME1),
					createDatastoreKey(NAME2),
			};
			List<DummyModel> actual = dao.getAll(Arrays.asList(keys));

			assertThat(actual.size(), is(2));
			assertThat(actual.get(0).getKey(), is(createDatastoreKey(NAME1)));
			assertThat(actual.get(1).getKey(), is(createDatastoreKey(NAME2)));
		}
	}

	public static class WhenExistsDatastoreAndMemcache extends AppEngineTestCase{

		@Before
		public void setUpDatastoreAndMemcache() {
			putDatastoreAndMemcache(NAME1, "100");
			putDatastoreAndMemcache(NAME2, "200");
		}

		private void putDatastoreAndMemcache(String name, String value) {
			DummyModel model = createModel(name, value);
			Datastore.put(model);
			Memcache.put(createMemcacheKey(name), model);
		}

		@Test
		public void delete() throws Exception {
			dao.delete(createDatastoreKey(NAME1));

			assertThat(tester.count(DummyModel.class), is(1));
			assertThat(Memcache.contains(createMemcacheKey(NAME1)), is(false));
			assertThat(Memcache.contains(createMemcacheKey(NAME2)), is(true));
		}

		@Test
		public void deleteAll() throws Exception {
			Key[] keys = {
					createDatastoreKey(NAME1),
					createDatastoreKey(NAME2),
			};
			dao.deleteAll(Arrays.asList(keys));

			assertThat(tester.count(DummyModel.class), is(0));
			assertThat(Memcache.contains(createMemcacheKey(NAME1)), is(false));
			assertThat(Memcache.contains(createMemcacheKey(NAME2)), is(false));
		}
	}

	public static class WhenExistsDatastoreOrMemcache extends AppEngineTestCase{

		@Before
		public void setUpMemcache() {
			putMemcache(NAME1, "100");
		}

		private void putMemcache(String name, String value) {
			DummyModel model = createModel(name, value);
			Memcache.put(createMemcacheKey(name), model);
		}

		@Before
		public void setUpDatastore() {
			putDatastore(NAME2, "200");
		}

		private void putDatastore(String name, String value) {
			DummyModel model = createModel(name, value);
			Datastore.put(model);
		}

		@Test
		public void getAll() throws Exception {
			Key[] keys = {
					createDatastoreKey(NAME1),
					createDatastoreKey(NAME2),
			};
			List<DummyModel> actual = dao.getAll(Arrays.asList(keys));

			assertThat(actual, is(notNullValue()));
			assertThat(actual.size(), is(2));

			assertThat(actual.get(0).getKey(), is(createDatastoreKey(NAME1)));
			assertThat(actual.get(1).getKey(), is(createDatastoreKey(NAME2)));

			assertThat(Memcache.contains(createMemcacheKey(NAME1)), is(true));
			assertThat(Memcache.contains(createMemcacheKey(NAME2)), is(true));
		}

	}

	private static DummyModel createDefaultModel() {
		return createModel(DEFAULT_KEY_NAME, "100");
	}

	private static DummyModel createModel(String name, String value) {
		DummyModel model = new DummyModel();
		model.setKey(createDatastoreKey(name));
		model.setData(value);
		return model;
	}

	private static Key createDefaultDatastoreKey() {
		return createDatastoreKey(DEFAULT_KEY_NAME);
	}

	private static Key createDatastoreKey(String name) {
		return Datastore.createKey(DummyModel.class, name);
	}

	private static String createDefaultMemcacheKey() {
		return createMemcacheKey(DEFAULT_KEY_NAME);
	}

	private static String createMemcacheKey(String name) {
		return dao.createMemcacheKey(createDatastoreKey(name));
	}
}
