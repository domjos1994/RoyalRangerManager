package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.Person
import org.apache.ibatis.annotations.Select

interface PersonMapper {

    @Select("SELECT * FROM person")
    fun getPersons(): List<Person>
}