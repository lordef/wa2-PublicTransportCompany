package it.polito.wa2.login_service.entities

import javax.persistence.*

@Entity
@Table(name="users")
class User(

        @Column(nullable = false, unique = true)
           var nickname : String = "",
        @Column(nullable = false)
           var password : String = "",
        @Column(nullable = false, unique = true)
            var email : String = "",

        @OneToOne( mappedBy = "user",fetch=FetchType.LAZY, optional = true)
            var activation:Activation? = null,

        @Column(nullable = false)
            var active: Boolean = false,

        @Column(nullable=false)
            var roles: String = Role.CUSTOMER.toString()
    ): EntityBase<Long>() {

    private fun stringToSet(roleString: String): MutableSet<Role>{


        return roleString.split(",").map{
            Role.valueOf(it)
        }.toMutableSet()
    }

    fun getRoles(): Set<Role>{
        // stringToSet returns a mutable set, but when returned by getRoles, the result is not mutable anymore
        return stringToSet(roles)
    }

    }



