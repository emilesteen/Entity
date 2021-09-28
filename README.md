# Entity

## What is Entity

Entity is a Kotlin MongoDB ORM.

## How does it work

To define an Entity:

```kotlin
package entity

import CollectionName
import DatabaseName
import Entity
import org.bson.types.ObjectId

@DatabaseName("user")
@CollectionName("user")
class User(
    override val _id: ObjectId?,
    val name: UserName,
    val age: Number,
    var status: UserStatus = UserStatus.ACTIVE
) : Entity(_id)

enum class UserStatus {
    ACTIVE,
    INACTIVE
}

class UserName(val firstName: String, val lastName: String) {}
```

The above defined `User` object will be saved in the database as:
```json
{
  "_id": ObjectId("6152f40c68128318704a7cf3"),
  "age": 23,
  "name": {
    "firstName":"Emile",
    "lastName" :"Steenkamp"
  },
  "status": 0
}
```

After defining an Entity, you can easily create and save an entity using:
```kotlin
val user: User = User(null, UserName("Emile", "Steenkamp"), 23).save()
```

Find an Entity by its `_id`:
```kotlin
val user: User = Entity.findById(ObjectId("6151dcead7627735cba71645"))
```

Update an Entity:
```kotlin
user.status = UserStatus.INACTIVE
user.save()
```