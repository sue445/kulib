package net.sue445.kulib.service;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import net.sue445.kulib.model.DummyModel;

import org.junit.Before;
import org.junit.Test;
import org.slim3.datastore.Datastore;
import org.slim3.memcache.Memcache;
import org.slim3.tester.AppEngineTestCase;

import com.google.appengine.api.datastore.Key;
import com.google.apphosting.api.ApiProxy;


public class ProxyDatastoreServiceTest extends AppEngineTestCase{
	private ProxyDatastoreService<DummyModel> service = new ProxyDatastoreService<DummyModel>(DummyModel.class);

	private String currentVersionId;


	@Before
	public void initCurrentVersionId(){
		currentVersionId = ApiProxy.getCurrentEnvironment().getVersionId();
	}

	@Test
	public void test() throws Exception {
		assertThat(service, is(notNullValue()));
	}

	@Test
	public void createMemcacheKey(){
		Key key = Datastore.createKey(DummyModel.class, 1);
		assertThat(service.createMemcacheKey(key), is(currentVersionId + "DummyModel(1)"));
	}

	@Test
	public void put(){
		DummyModel model = new DummyModel();
		model.setKey(createDefaultDatastoreKey());
		model.setData("100");
		service.put(model);

		Key datastoreKey = createDefaultDatastoreKey();
		DummyModel actual = Datastore.get(DummyModel.class, datastoreKey);
		assertThat(actual, is(notNullValue()));
		assertThat(actual.getKey(), is(datastoreKey));
		assertThat(actual.getData(), is("100"));

		String memcacheKey = createDefaultMemcacheKey();
		assertThat(Memcache.contains(memcacheKey), is(false));
	}

	private Key createDefaultDatastoreKey() {
		return Datastore.createKey(DummyModel.class, "name");
	}

	private String createDefaultMemcacheKey() {
		return service.createMemcacheKey(createDefaultDatastoreKey());
	}

	@Test
	public void get_NotFound(){
		DummyModel actual = service.get(createDefaultDatastoreKey());
		assertThat(actual, is(nullValue()));
	}

	@Test
	public void get_FoundInDatastore(){
		DummyModel model = new DummyModel();
		model.setKey(createDefaultDatastoreKey());
		model.setData("100");
		Datastore.put(model);

		DummyModel datastoreActual = service.get(createDefaultDatastoreKey());
		assertThat(datastoreActual, is(notNullValue()));
		assertThat(datastoreActual.getData(), is("100"));

		DummyModel memcacheActual = Memcache.get(createDefaultMemcacheKey());
		assertThat(memcacheActual, is(notNullValue()));
		assertThat(memcacheActual.getData(), is("100"));
	}

	@Test
	public void get_FoundInMemcache(){
		DummyModel datastoreModel = new DummyModel();
		datastoreModel.setKey(createDefaultDatastoreKey());
		datastoreModel.setData("200");	// dummy
		Datastore.put(datastoreModel);

		DummyModel memcacheModel = new DummyModel();
		memcacheModel.setKey(createDefaultDatastoreKey());
		memcacheModel.setData("100");
		Memcache.put(createDefaultMemcacheKey(), memcacheModel);

		DummyModel memcacheActual = service.get(createDefaultDatastoreKey());
		assertThat(memcacheActual, is(notNullValue()));
		assertThat(memcacheActual.getData(), is("100"));
	}

	@Test
	public void getModelOrNull_Found(){
		DummyModel model = new DummyModel();
		model.setKey(createDefaultDatastoreKey());
		model.setData("100");
		Datastore.put(model);

		DummyModel actual = service.getOrNullFromDatastore(createDefaultDatastoreKey());
		assertThat(actual, is(notNullValue()));
	}

	@Test
	public void getModelOrNull_NotFound(){
		DummyModel actual = service.getOrNullFromDatastore(createDefaultDatastoreKey());
		assertThat(actual, is(nullValue()));
	}
}
