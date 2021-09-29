import com.mongodb.BasicDBObject
import org.bson.types.ObjectId

class EntityQuery {
    companion object {
        inline fun <reified E>findById(_id: ObjectId): E {
            val filter = BasicDBObject();
            filter["_id"] = _id

            val document = EntityHelper.getClient()
                .getDatabase(EntityHelper.getDatabaseName<E>())
                .getCollection(EntityHelper.getCollectionName<E>())
                .find(filter)
                .first()

            if (document == null) {
                throw Exception("Document not found")
            } else {
                return EntityMapper.createFromDocument(document, E::class)
            }
        }

        inline fun <reified E>find(filter: BasicDBObject): ArrayList<E>
        {
            val documents = EntityHelper.getClient()
                .getDatabase(EntityHelper.getDatabaseName<E>())
                .getCollection(EntityHelper.getCollectionName<E>())
                .find(filter)
            val entities = arrayListOf<E>()

            for (document in documents) {
                entities.add(EntityMapper.createFromDocument(document, E::class))
            }

            return entities
        }
    }
}