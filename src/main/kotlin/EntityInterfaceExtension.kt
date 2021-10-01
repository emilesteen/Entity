import com.mongodb.BasicDBObject

inline fun <reified E: EntityInterface>E.insert(): E {
    EntityHelper.getCollection<E>()
        .insertOne(EntityMapper.generateDocument(this))

    return this
}

inline fun <reified E: EntityInterface>E.update(): E {
    val filter = BasicDBObject();
    filter["_id"] = this._id

    EntityHelper.getCollection<E>()
        .replaceOne(filter, EntityMapper.generateDocument(this))

    return this
}
