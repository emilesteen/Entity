import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import org.bson.types.ObjectId
import kotlin.reflect.*

abstract class Entity(open val _id: ObjectId?) {
    companion object {
        private val client: MongoClient? = null

        fun getClient(): MongoClient {
            return client ?: MongoClient()
        }

        inline fun <reified E>findById(_id: ObjectId): E {
            val filter = BasicDBObject();
            filter["_id"] = _id

            val document = getClient()
                .getDatabase(getDatabaseName<E>())
                .getCollection(getCollectionName<E>())
                .find(filter)
                .first()

            if (document == null) {
                throw Exception("Document not found")
            } else {
                return EntityMapper.createFromDocument(document, E::class.constructors.first())
            }
        }

        inline fun <reified E>find(filter: BasicDBObject): ArrayList<E>
        {
            val documents = getClient()
                .getDatabase(getDatabaseName<E>())
                .getCollection(getCollectionName<E>())
                .find(filter)
            val entities = arrayListOf<E>()

            for (document in documents) {
                entities.add(EntityMapper.createFromDocument(document, E::class.constructors.first()))
            }

            return entities
        }

        inline fun <reified E>getDatabaseName(): String {
            return E::class.annotations.filterIsInstance<DatabaseName>().first().databaseName
        }

        inline fun <reified E>getCollectionName(): String {
            return E::class.annotations.filterIsInstance<CollectionName>().first().collectionName
        }
    }

    fun getId(): ObjectId
    {
        val id = this._id

        if (id == null) {
            throw Exception("The Entity does not have an id")
        } else {
            return id
        }
    }

    inline fun <reified E: Entity>save(): E {
        return if (this._id == null) {
            this.saveInsert()
        } else {
            this.saveReplace()
        }
    }

    inline fun <reified E: Entity>saveInsert(): E {
        val arguments = mutableMapOf<KParameter, Any?>()
        val constructor = E::class.constructors.first()
        val properties = this.javaClass.kotlin.members.filterIsInstance<KProperty<*>>()

        for (parameter in constructor.parameters) {
            if (parameter.name == "_id") {
                arguments[parameter] = ObjectId()
            } else {
                val property = properties.find { member -> member.name == parameter.name }

                if (property == null) {
                    throw Exception("Property not found on object")
                } else {
                    arguments[parameter] = property.getter.call(this)
                }
            }
        }

        val entity = constructor.callBy(arguments)

        getClient()
            .getDatabase(getDatabaseName<E>())
            .getCollection(getCollectionName<E>())
            .insertOne(EntityMapper.generateDocument(entity))

        return entity
    }

    inline fun <reified E>saveReplace(): E
    {
        val filter = BasicDBObject();
        filter["_id"] = this.getId()

        getClient()
            .getDatabase(getDatabaseName<E>())
            .getCollection(getCollectionName<E>())
            .replaceOne(filter, EntityMapper.generateDocument(this))

        return this as E
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Entity) {
            when {
                this._id == null -> {
                    false
                }
                other._id == null -> {
                    false
                }
                else -> {
                    this._id == other._id
                }
            }
        } else {
            false
        }
    }

    override fun toString(): String {
        return EntityMapper.generateDocument(this).toJson()
    }

    override fun hashCode(): Int {
        return _id.hashCode()
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseName(val databaseName: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CollectionName(val collectionName: String)