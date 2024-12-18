package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.EmergencyContact
import de.dojodev.royalrangermanager.db.model.PeopleEmergencyContact
import org.apache.ibatis.annotations.*

interface EmergencyContactMapper {
    @Select("SELECT * FROM emergencyContacts")
    fun getEmergencyContacts(): List<EmergencyContact>

    @Select("SELECT * FROM emergencyContacts WHERE id=#{id}")
    fun getEmergencyContact(id: Int): EmergencyContact

    @Select("SELECT count(id) FROM people_emergencyContacts WHERE emergencyContactId=#{id}")
    fun count(id: Int): Int

    @Select("SELECT * FROM people_emergencyContacts WHERE id=#{id}")
    fun getEmergencyContactsOfPerson(id: Int): List<PeopleEmergencyContact>

    @Insert(
        "INSERT INTO emergencyContacts(name, email, phone) " +
        "VALUES(#{name}, #{email}, #{phone})"
    )
    @SelectKey(
        statement=["select max(id) as id from emergencyContacts"],
        keyProperty="id", before=false, resultType=Int::class
    )
    fun insertEmergencyContact(emergencyContact: EmergencyContact): Int

    @Insert(
        "INSERT INTO people_emergencyContacts(personId, emergencyContactId) " +
                "VALUES(#{personId}, #{emergencyContactId})"
    )
    fun insertPeopleEmergencyContact(emergencyContact: PeopleEmergencyContact)

    @Update(
        "UPDATE emergencyContacts name=#{name}, email=#{email}, phone=#{phone} WHERE id=#{id}"
    )
    fun updateEmergencyContact(emergencyContact: EmergencyContact)

    @Delete("DELETE FROM people_emergencyContacts WHERE personId=#{personId}")
    fun deletePeopleEmergencyContact(personId: Int)

    @Delete("DELETE FROM emergencyContacts WHERE id=#{id}")
    fun deleteEmergencyContact(emergencyContact: EmergencyContact)
}