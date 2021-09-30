package entity

import Entity
import org.bson.types.ObjectId
import kotlin.collections.ArrayList

@Entity.DatabaseName("user")
@Entity.CollectionName("user")
data class User(
    val name: UserName,
    val age: Number,
    var status: UserStatus = UserStatus.ACTIVE,
    val countriesVisited: ArrayList<String> = arrayListOf(),
    override val _id: ObjectId = ObjectId()
) : Entity() {
}

enum class UserStatus {
    ACTIVE,
    INACTIVE
}

data class UserName(val firstName: String, val lastName: String)