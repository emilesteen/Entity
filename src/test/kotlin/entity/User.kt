package entity

import CollectionName
import DatabaseName
import Entity
import org.bson.types.ObjectId
import kotlin.collections.ArrayList

@DatabaseName("user")
@CollectionName("user")
class User(
    override val _id: ObjectId?,
    val name: UserName,
    val age: Number,
    var status: UserStatus = UserStatus.ACTIVE,
    val countriesVisited: ArrayList<String> = arrayListOf()
) : Entity(_id)

enum class UserStatus {
    ACTIVE,
    INACTIVE
}

class UserName(val firstName: String, val lastName: String) {}