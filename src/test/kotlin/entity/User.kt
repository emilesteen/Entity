package entity

import CollectionName
import DatabaseName
import Entity
import org.bson.types.ObjectId

@DatabaseName("user")
@CollectionName("user")
class User(
    override val _id: ObjectId?,
    val firstName: String,
    val lastName: String,
    val age: Number,
    var status: UserStatus = UserStatus.ACTIVE
) : Entity(_id)

enum class UserStatus {
    ACTIVE,
    INACTIVE
}