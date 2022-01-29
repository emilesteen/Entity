package entity

import Entity
import org.bson.types.ObjectId

data class Product(override val _id: ObjectId = ObjectId()) : Entity()