package entity

import Entity
import org.bson.types.ObjectId
import java.sql.Timestamp
import java.time.Instant
import java.util.*
import kotlin.collections.ArrayList

@Entity.DatabaseName("user")
@Entity.CollectionName("user")
class User(
    val name: UserName,
    val age: Number,
    var status: UserStatus = UserStatus.ACTIVE,
    val countriesVisited: ArrayList<String> = arrayListOf(),
    var date: Date = Date(),
    var timestamp: Timestamp = Timestamp.from(Instant.now()),
    override val _id: ObjectId? = null,
) : Entity()

enum class UserStatus {
    ACTIVE,
    INACTIVE
}

class UserName(val firstName: String, val lastName: String)