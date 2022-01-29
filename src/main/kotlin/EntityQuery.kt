import com.mongodb.BasicDBObject
import com.mongodb.client.FindIterable
import org.bson.Document
import org.bson.types.ObjectId
import kotlin.reflect.KProperty1

class EntityQuery<E : Entity> {
    companion object {
        inline fun <reified RE : Entity> findById(_id: ObjectId): RE {
            val filter = BasicDBObject();
            filter["_id"] = _id

            val document = EntityHelper.getCollection<RE>()
                .find(filter)
                .first()

            if (document == null) {
                throw Exception("Document not found")
            } else {
                return EntityMapper.createFromDocument(document, RE::class)
            }
        }

        inline fun <reified RE : Entity> find(filter: BasicDBObject): ArrayList<RE> {
            val documents = EntityHelper
                .getCollection<RE>()
                .find(filter)

            return mapDocumentsToEntities(documents)
        }

        inline fun <reified RE : Entity> mapDocumentsToEntities(documents: FindIterable<Document>): ArrayList<RE> {
            val entities = arrayListOf<RE>()

            for (document in documents) {
                entities.add(EntityMapper.createFromDocument(document, RE::class))
            }

            return entities
        }
    }

    fun where(condition: EntityCondition<E>): EntityQuery<E> {
        return this
    }

    inline fun <reified RE : E> find(): ArrayList<RE> {
        val documents = EntityHelper
            .getCollection<RE>()
            .find(BasicDBObject())

        return mapDocumentsToEntities(documents)
    }

    class Field<E : Entity>(val path: Path<E, *>)

    class Path<K : Any, N : Any>(val field: KProperty1<K, N>, val path: Path<N, *>? = null)
}