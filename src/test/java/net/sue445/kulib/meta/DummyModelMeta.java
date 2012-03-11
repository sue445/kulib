package net.sue445.kulib.meta;


//@javax.annotation.Generated(value = { "slim3-gen", "@VERSION@" }, date = "2012-03-11 14:00:55")
/** */
public final class DummyModelMeta extends org.slim3.datastore.ModelMeta<net.sue445.kulib.model.DummyModel> {

    /** */
    public final org.slim3.datastore.StringAttributeMeta<net.sue445.kulib.model.DummyModel> data = new org.slim3.datastore.StringAttributeMeta<net.sue445.kulib.model.DummyModel>(this, "data", "data");

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.sue445.kulib.model.DummyModel, com.google.appengine.api.datastore.Key> key = new org.slim3.datastore.CoreAttributeMeta<net.sue445.kulib.model.DummyModel, com.google.appengine.api.datastore.Key>(this, "__key__", "key", com.google.appengine.api.datastore.Key.class);

    /** */
    public final org.slim3.datastore.CoreAttributeMeta<net.sue445.kulib.model.DummyModel, java.lang.Long> version = new org.slim3.datastore.CoreAttributeMeta<net.sue445.kulib.model.DummyModel, java.lang.Long>(this, "version", "version", java.lang.Long.class);

    private static final DummyModelMeta slim3_singleton = new DummyModelMeta();

    /**
     * @return the singleton
     */
    public static DummyModelMeta get() {
       return slim3_singleton;
    }

    /** */
    public DummyModelMeta() {
        super("DummyModel", net.sue445.kulib.model.DummyModel.class);
    }

    @Override
    public net.sue445.kulib.model.DummyModel entityToModel(com.google.appengine.api.datastore.Entity entity) {
        net.sue445.kulib.model.DummyModel model = new net.sue445.kulib.model.DummyModel();
        model.setData((java.lang.String) entity.getProperty("data"));
        model.setKey(entity.getKey());
        model.setVersion((java.lang.Long) entity.getProperty("version"));
        return model;
    }

    @Override
    public com.google.appengine.api.datastore.Entity modelToEntity(java.lang.Object model) {
        net.sue445.kulib.model.DummyModel m = (net.sue445.kulib.model.DummyModel) model;
        com.google.appengine.api.datastore.Entity entity = null;
        if (m.getKey() != null) {
            entity = new com.google.appengine.api.datastore.Entity(m.getKey());
        } else {
            entity = new com.google.appengine.api.datastore.Entity(kind);
        }
        entity.setProperty("data", m.getData());
        entity.setProperty("version", m.getVersion());
        entity.setProperty("slim3.schemaVersion", 1);
        return entity;
    }

    @Override
    protected com.google.appengine.api.datastore.Key getKey(Object model) {
        net.sue445.kulib.model.DummyModel m = (net.sue445.kulib.model.DummyModel) model;
        return m.getKey();
    }

    @Override
    protected void setKey(Object model, com.google.appengine.api.datastore.Key key) {
        validateKey(key);
        net.sue445.kulib.model.DummyModel m = (net.sue445.kulib.model.DummyModel) model;
        m.setKey(key);
    }

    @Override
    protected long getVersion(Object model) {
        net.sue445.kulib.model.DummyModel m = (net.sue445.kulib.model.DummyModel) model;
        return m.getVersion() != null ? m.getVersion().longValue() : 0L;
    }

    @Override
    protected void assignKeyToModelRefIfNecessary(com.google.appengine.api.datastore.AsyncDatastoreService ds, java.lang.Object model) {
    }

    @Override
    protected void incrementVersion(Object model) {
        net.sue445.kulib.model.DummyModel m = (net.sue445.kulib.model.DummyModel) model;
        long version = m.getVersion() != null ? m.getVersion().longValue() : 0L;
        m.setVersion(Long.valueOf(version + 1L));
    }

    @Override
    protected void prePut(Object model) {
    }

    @Override
    protected void postGet(Object model) {
    }

    @Override
    public String getSchemaVersionName() {
        return "slim3.schemaVersion";
    }

    @Override
    public String getClassHierarchyListName() {
        return "slim3.classHierarchyList";
    }

    @Override
    protected boolean isCipherProperty(String propertyName) {
        return false;
    }

    @Override
    protected void modelToJson(org.slim3.datastore.json.JsonWriter writer, java.lang.Object model, int maxDepth, int currentDepth) {
        net.sue445.kulib.model.DummyModel m = (net.sue445.kulib.model.DummyModel) model;
        writer.beginObject();
        org.slim3.datastore.json.Default encoder0 = new org.slim3.datastore.json.Default();
        if(m.getData() != null){
            writer.setNextPropertyName("data");
            encoder0.encode(writer, m.getData());
        }
        if(m.getKey() != null){
            writer.setNextPropertyName("key");
            encoder0.encode(writer, m.getKey());
        }
        if(m.getVersion() != null){
            writer.setNextPropertyName("version");
            encoder0.encode(writer, m.getVersion());
        }
        writer.endObject();
    }

    @Override
    protected net.sue445.kulib.model.DummyModel jsonToModel(org.slim3.datastore.json.JsonRootReader rootReader, int maxDepth, int currentDepth) {
        net.sue445.kulib.model.DummyModel m = new net.sue445.kulib.model.DummyModel();
        org.slim3.datastore.json.JsonReader reader = null;
        org.slim3.datastore.json.Default decoder0 = new org.slim3.datastore.json.Default();
        reader = rootReader.newObjectReader("data");
        m.setData(decoder0.decode(reader, m.getData()));
        reader = rootReader.newObjectReader("key");
        m.setKey(decoder0.decode(reader, m.getKey()));
        reader = rootReader.newObjectReader("version");
        m.setVersion(decoder0.decode(reader, m.getVersion()));
        return m;
    }
}