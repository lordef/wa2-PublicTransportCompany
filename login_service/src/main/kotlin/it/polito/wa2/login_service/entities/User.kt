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
            var roles: String = Role.CUSTOMER.toString()//TODO : è giusto assegnare CUSTOMER di default appena creo l'entity?
    ): EntityBase<Long>() {

    private fun stringToSet(roleString: String): MutableSet<Role>{
        //TODO commentato perchè non dovrebbe mai essere vuoto il ruolo
        //if (roleString == "") return mutableSetOf() : it can never be empty, at least CUSTOMER

        return roleString.split(" ").map{
            Role.valueOf(it)
        }.toMutableSet()
    }

    /*TODO se confermato che le funzioni sotto non servono in questo lab, allora il contenuto di stringToSet può essere
    TODO messo  direttamente dentro getRoles, facendo un cast a Set (anzichè a Mutable)*/
    fun getRoles(): Set<Role>{
        // stringToSet returns a mutable set, but when returned by getRoles, the result is not mutable anymore
        return stringToSet(roles)
    }

    private fun setToString(roleSet: Set<Role>): String{
        return roleSet.joinToString(" ")
    }




    //TODO per ora non mi pare che serva qualcosa del genere
    /*
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



