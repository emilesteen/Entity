import org.bson.types.ObjectId

abstract class Entity() : EntityInterface {
    abstract override val _id: ObjectId?

    override fun getId(): ObjectId
    {
        val id = this._id

        if (id == null) {
            throw Exception("The Entity does not have an id")
        } else {
            return id
        }
    }

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DatabaseName(val databaseName: String)

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CollectionName(val collectionName: String)
}