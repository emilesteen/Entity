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

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class DatabaseName(val databaseName: String)

    @Target(AnnotationTarget.CLASS)
    @Retention(AnnotationRetention.RUNTIME)
    annotation class CollectionName(val collectionName: String)
}