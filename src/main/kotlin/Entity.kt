import com.mongodb.MongoClient

abstract class Entity {
    companion object {
        private val client: MongoClient? = null

        private fun getClient(): MongoClient {
            return client ?: MongoClient()
        }
    }
}