package entity

import Entity
import org.bson.types.ObjectId

@Entity.DatabaseName("user")
@Entity.CollectionName("user")
data class User(
    val name: Name,
    val age: Number,
    var status: Status = Status.ACTIVE,
    val countriesVisited: ArrayList<String> = arrayListOf(),
    override val _id: ObjectId = ObjectId()
) : Entity() {
    data class Name(val firstName: String, val lastName: String) {
        data class Nested(val something: String)
    }

    enum class Status {
        ACTIVE,
        INACTIVE
    }

    enum class Something {
        A,
        B
    }
}