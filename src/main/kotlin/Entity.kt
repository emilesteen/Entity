import com.mongodb.MongoClient
import org.bson.Document

abstract class Entity<T> {
    companion object {
        private val client: MongoClient? = null

        private fun getClient(): MongoClient {
            return client ?: MongoClient()
        }
    }

    fun save(): T
    {
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

    private fun generateDocument(): Document
    {
        return Document()
    }
}

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DatabaseName(val databaseName: String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class CollectionName(val collectionName: String)