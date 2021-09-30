import com.mongodb.BasicDBObject

inline fun <reified E: EntityInterface>E.insert(): E {
    EntityHelper.getClient()
        .getDatabase(EntityHelper.getDatabaseName<E>())
        .getCollection(EntityHelper.getCollectionName<E>())
        .insertOne(EntityMapper.generateDocument(this))

    return this
}

inline fun <reified E: EntityInterface>E.update(): E {
    val filter = BasicDBObject();
    filter["_id"] = this._id

    EntityHelper.getClient()
        .getDatabase(EntityHelper.getDatabaseName<E>())
        .getCollection(EntityHelper.getCollectionName<E>())
        .replaceOne(filter, EntityMapper.generateDocument(this))

    return this
}
