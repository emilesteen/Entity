import com.mongodb.MongoClient

class EntityHelper {
    companion object {
        private val client: MongoClient? = null

        fun getClient(): MongoClient {
            return client ?: MongoClient()
        }

        inline fun <reified E>getDatabaseName(): String {
            return E::class.annotations.filterIsInstance<DatabaseName>().first().databaseName
        }

        inline fun <reified E>getCollectionName(): String {
            return E::class.annotations.filterIsInstance<CollectionName>().first().collectionName
        }
    }
}