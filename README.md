# Entity

## What is Entity

Entity is a Kotlin MongoDB ORM.

## How does it work

To define an Entity:

```kotlin
package entity

import Entity
import org.bson.types.ObjectId
import kotlin.collections.ArrayList

@Entity.DatabaseName("user")
@Entity.CollectionName("user")
class User(
    val name: UserName,
    val age: Number,
    var status: UserStatus = UserStatus.ACTIVE,
    val countriesVisited: ArrayList<String> = arrayListOf(),
    override val _id: ObjectId? = null,
) : Entity()

enum class UserStatus {
    ACTIVE,
    INACTIVE
}

class UserName(val firstName: String, val lastName: String)
```

After defining an Entity, you can easily create and save an Entity using:
```kotlin
var user = User(
    UserName("Emile", "Steenkamp"),
    23,
    UserStatus.ACTIVE,
    arrayListOf("ZA", "NL")
).save()
```

The above defined `User` object will be saved in the database as:
```javascript
{
  "_id": ObjectId("6153263f8aedab15aa1f44d4"),
  "age": 23,
  "countriesVisited": [ "ZA", "NL" ],
  "name": {
    "firstName": "Emile",
    "lastName": "Steenkamp"
  },
  "status": 0
}
```

After saving the Entity, we can find an Entity by its `_id`:
```kotlin
val user = EntityQuery.findById<User>(ObjectId("6153263f8aedab15aa1f44d4"))
```

Or find all Entities by a filter:
```kotlin
val filter = BasicDBObject()
val users = EntityQuery.find<User>(filter)
```

Update an Entity:
```kotlin
user.status = UserStatus.INACTIVE
user = user.save()
```