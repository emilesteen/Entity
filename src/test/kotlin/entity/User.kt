package entity

import CollectionName
import DatabaseName
import Entity
import org.bson.types.ObjectId

@DatabaseName("user")
@CollectionName("user")
class User(
    var firstName: String,
    var lastName: String,
    var age: Number,
    override val _id: ObjectId = ObjectId()
) : Entity(_id)