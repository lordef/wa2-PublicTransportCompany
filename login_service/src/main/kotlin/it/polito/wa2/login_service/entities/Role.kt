package it.polito.wa2.login_service.entities

import javax.persistence.*


@Entity
@Table(name = "roles")
class Role : EntityBase<Long>() {

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    var name: ERole? = null

    @ManyToMany(mappedBy = "roles")
    val users: MutableSet<User> = mutableSetOf()
    fun addUser(s:User) {
        users.add(s)
        s.roles.add(this)
    }

}