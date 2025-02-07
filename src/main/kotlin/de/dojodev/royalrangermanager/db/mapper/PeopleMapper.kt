package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.Person
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.SelectKey
import org.apache.ibatis.annotations.Update

interface PeopleMapper {

    @Select("SELECT * FROM people")
    fun getPeople(): List<Person>

    @Select("SELECT * FROM people WHERE id=#{id}")
    fun getPerson(id: Int): Person?

    @Insert(
        "INSERT INTO people(" +
            "memberId, firstName, middleName, lastName, childNumber, gender, birthDate, entryDate, notes, description, " +
            "medicines, email, phone, street, number, locality, postalCode, ageGroupId, teamId" +
        ") VALUES(" +
            "#{memberId}, #{firstName}, #{middleName}, #{lastName}, #{childNumber}, #{gender}, #{birthDate}, #{entryDate}, #{notes}, #{description}, " +
            "#{medicines}, #{email}, #{phone}, #{street}, #{number}, #{locality}, #{postalCode}, #{ageGroupId}, #{teamId}" +
        ")"
    )
    @SelectKey(
        statement=["select max(id) as id from people"],
        keyProperty="id", before=false, resultType=Int::class
    )
    fun insertPerson(person: Person): Int

    @Update(
        "UPDATE people SET " +
                "memberId=#{memberId}, firstName=#{firstName}, middleName=#{middleName}, lastName=#{lastName}, childNumber=#{childNumber}, gender=#{gender}, " +
                "birthDate=#{birthDate}, entryDate=#{entryDate}, notes=#{notes}, description=#{description}, " +
                "medicines=#{medicines}, email=#{email}, phone=#{phone}, street=#{street}, number=#{number}, " +
                "locality=#{locality}, postalCode=#{postalCode}, ageGroupId=#{ageGroupId}, teamId=#{teamId} " +
        "WHERE id=#{id}"
    )
    fun updatePerson(person: Person)

    @Delete("DELETE FROM people WHERE id=#{id}")
    fun deletePerson(person: Person)
}