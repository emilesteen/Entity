import com.mongodb.BasicDBObject
import com.mongodb.MongoClient
import org.bson.Document
import org.bson.json.JsonWriterSettings
import org.bson.types.ObjectId
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

abstract class Entity<T>(open val _id: ObjectId) {
    companion object {
        private val client: MongoClient? = null

        fun getClient(): MongoClient {
            return client ?: MongoClient()
        }

        inline fun <reified E>findById(_id: ObjectId): E {
            val query = BasicDBObject();
            query["_id"] = _id

            val document = getClient()
                .getDatabase(getDatabaseName<E>())
                .getCollection(getCollectionName<E>())
                .find(query)
                .first()

            if (document == null) {
                throw Exception("Document not found")
            } else {
                return createFromDocument(document)
            }
        }

        inline fun <reified E>getDatabaseName(): String {
            return E::class.annotations.filterIsInstance<DatabaseName>().first().databaseName
        }

        inline fun <reified E>getCollectionName(): String {
            return E::class.annotations.filterIsInstance<CollectionName>().first().collectionName
        }

        inline fun <reified E>createFromDocument(document: Document): E {
            val arguments = mutableMapOf<KParameter, Any?>()
            val constructor = E::class.constructors.first()

            for (parameter in constructor.parameters) {
                arguments[parameter] = document[parameter.name]
            }

            return constructor.callBy(arguments)
        }
    }

    fun save(): T {
        getClient()
            .getDatabase(this.getDatabaseName())
            .getCollection(this.getCollectionName())
            .insertOne(this.generateDocument())

        return this as T
    }

    private fun getDatabaseName(): String {
        return this.javaClass.kotlin.annotations.filterIsInstance<DatabaseName>().first().databaseName
    }

    private fun getCollectionName(): String {
        return this.javaClass.kotlin.annotations.filterIsInstance<CollectionName>().first().collectionName
    }

    private fun generateDocument(): Document {
        val document = Document()
        val kProperties = this.javaClass.kotlin.members.filterIsInstance<KProperty<*>>()

        for (kProperty in kProperties) {
            document[kProperty.name] = kProperty.getter.call(this)
        }

        return document
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Entity<*>) {
            this._id == other._id
        } else {
            false
        }
    }

    override fun toString(): String {
        return this.generateDocument().toJson()
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseName(val databaseName: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CollectionName(val collectionName: String)