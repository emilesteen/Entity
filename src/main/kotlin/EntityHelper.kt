import com.mongodb.MongoClient
import com.mongodb.client.MongoCollection
import org.bson.Document

class EntityHelper {
    companion object {
        private val client: MongoClient = MongoClient()

        fun getClient(): MongoClient {
            return client
        }

        inline fun <reified E>getCollection(): MongoCollection<Document> {
            return getClient().getDatabase(getDatabaseName<E>()).getCollection(getCollectionName<E>())
        }

        inline fun <reified E>getDatabaseName(): String {
            return E::class.annotations.filterIsInstance<Entity.DatabaseName>().first().databaseName
        }

        inline fun <reified E>getCollectionName(): String {
            return E::class.annotations.filterIsInstance<Entity.CollectionName>().first().collectionName
        }
    }
}