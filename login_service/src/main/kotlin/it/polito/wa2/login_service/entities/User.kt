package it.polito.wa2.login_service.entities

import javax.persistence.*

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true)
    var nickname: String = "",
    @Column(nullable = false)
    var password: String = "",
    @Column(nullable = false, unique = true)
    var email: String = "",

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY, optional = true)
    var activation: Activation? = null,

    @Column(nullable = false)
    var active: Boolean = false,

    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")]
    )
    var roles: MutableSet<Role> = mutableSetOf()

//        @Column(nullable=false)
//            var roles: String = ERole.CUSTOMER.toString()
) : EntityBase<Long>() {

    private fun stringToSet(roleString: String): MutableSet<ERole> {
        //TODO commentato perchè non dovrebbe mai essere vuoto il ruolo
        //if (roleString == "") return mutableSetOf() : it can never be empty, at least CUSTOMER

        return roleString.split(",").map {
            ERole.valueOf(it)
        }.toMutableSet()
    }

    /*
    TODO se confermato che le funzioni sotto non servono in questo lab, allora il contenuto di stringToSet può essere
       messo  direttamente dentro getRoles, facendo un cast a Set (anzichè a Mutable)
       */
    /*
    fun getRoles(): Set<Role> {
        // roles is a mutable set,
        // but when returned by getRoles, the result is not mutable anymore
        return roles.toSet()
    }
    */

    fun addRole(role: Role) {
        roles.add(role)
        role.users.add(this)
    }
    /*
    fun setRoles(roles: Set<Role?>?) {
        this.roles = roles.toMutableSet()
    }
    */


    //TODO per ora non mi pare che serva qualcosa del genere
    /*
    private fun setToString(roleSet: Set<Role>): String{
        return roleSet.joinToString(",")
    }

    fun addRole(newRole:Role){
        val tmpSet = stringToSet(roles)
        tmpSet.add(newRole)
        roles = setToString(tmpSet)
    }



    fun removeRole(oldRole:Role){
        val tmpSet = stringToSet(roles)
        tmpSet.remove(oldRole)
        roles = setToString(tmpSet)
    }*/
}



