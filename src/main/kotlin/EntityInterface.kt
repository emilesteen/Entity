import org.bson.types.ObjectId

interface EntityInterface {
    val _id: ObjectId?

    fun getId(): ObjectId
}