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
    var firstName: String,
    var lastName: String,
    var age: Number,
    override val _id: ObjectId = ObjectId()
) : Entity(_id)
```

After defining an Entity, you can easily save an entity using:
```kotlin
val user = User("Emile", "Steenkamp", 23).save<User>()
```

Find an Entity by its `_id`:
```kotlin
val user = Entity.findById<User>(ObjectId("6151dcead7627735cba71645"))
```