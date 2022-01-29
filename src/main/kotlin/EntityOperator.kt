import kotlin.reflect.KProperty1

infix fun <E : Entity> KProperty1<E, Number>.eq(value: Number): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity> KProperty1<E, String>.eq(value: String): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity> KProperty1<E, Boolean>.eq(value: Boolean): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity, N : Enum<N>> KProperty1<E, Enum<N>>.eq(value: Enum<N>): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity> EntityQuery.Field<E>.eq(value: String): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity> EntityQuery.Field<E>.eq(value: Number): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity> EntityCondition<E>.and(condition: EntityCondition<E>): EntityCondition<E> {
    return EntityCondition()
}

infix fun <E : Entity> EntityCondition<E>.or(condition: EntityCondition<E>): EntityCondition<E> {
    return EntityCondition()
}
