package entity

import CollectionName
import DatabaseName
import Entity

@DatabaseName("user")
@CollectionName("user")
class User(var firstName: String, var lastName: String, var age: Number) : Entity<User>() {
    public fun sayHello() {
        println("Hello")
    }
}