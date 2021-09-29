import com.mongodb.BasicDBObject
import org.bson.types.ObjectId
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

inline fun <reified E : EntityInterface> E.save(): E {
    return if (this._id == null) {
        saveInsert(this)
    } else {
        saveReplace(this)
    }
}

inline fun <reified E: EntityInterface>saveInsert(entity: E): E {
    val arguments = mutableMapOf<KParameter, Any?>()
    val constructor = E::class.constructors.first()
    val properties = entity.javaClass.kotlin.members.filterIsInstance<KProperty<*>>()

    for (parameter in constructor.parameters) {
        if (parameter.name == "_id") {
            arguments[parameter] = ObjectId()
        } else {
            val property = properties.find { member -> member.name == parameter.name }

            if (property == null) {
                throw Exception("Property not found on object")
            } else {
                arguments[parameter] = property.getter.call(entity)
            }
        }
    }

    val entityNew = constructor.callBy(arguments)

    EntityHelper.getClient()
        .getDatabase(EntityHelper.getDatabaseName<E>())
        .getCollection(EntityHelper.getCollectionName<E>())
        .insertOne(EntityMapper.generateDocument(entityNew))

    return entityNew
}

inline fun <reified E: EntityInterface>saveReplace(entity: E): E {
    val filter = BasicDBObject();
    filter["_id"] = entity.getId()

    EntityHelper.getClient()
        .getDatabase(EntityHelper.getDatabaseName<E>())
        .getCollection(EntityHelper.getCollectionName<E>())
        .replaceOne(filter, EntityMapper.generateDocument(entity))

    return entity
}
