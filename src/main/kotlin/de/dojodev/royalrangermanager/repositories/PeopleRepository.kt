package de.dojodev.royalrangermanager.repositories

import de.dojodev.royalrangermanager.db.mapper.AgeGroupMapper
import de.dojodev.royalrangermanager.db.mapper.EmergencyContactMapper
import de.dojodev.royalrangermanager.db.mapper.PeopleMapper
import de.dojodev.royalrangermanager.db.mapper.TeamMapper
import de.dojodev.royalrangermanager.db.model.*
import de.dojodev.royalrangermanager.helper.DBHelper
import de.dojodev.royalrangermanager.helper.FXHelper
import org.apache.ibatis.session.SqlSession
import java.util.*
import kotlin.jvm.Throws

class PeopleRepository(
    private val sqlSession: SqlSession? = DBHelper.getSession()
) {
    private var teamMapper: TeamMapper? = null
    private var ageGroupMapper: AgeGroupMapper? = null
    private var peopleMapper: PeopleMapper? = null
    private var emergencyContactMapper: EmergencyContactMapper? = null

    init {
        this.teamMapper = this.sqlSession?.getMapper(TeamMapper::class.java)
        this.ageGroupMapper = this.sqlSession?.getMapper(AgeGroupMapper::class.java)
        this.peopleMapper = this.sqlSession?.getMapper(PeopleMapper::class.java)
        this.emergencyContactMapper = this.sqlSession?.getMapper(EmergencyContactMapper::class.java)
    }

    @Throws(Exception::class)
    fun getPeople(): List<Person> {
        val items = this.peopleMapper?.getPeople() ?: listOf()
        items.forEach { item ->
            this.emergencyContactMapper?.getEmergencyContactsOfPerson(item.id)?.forEach { ref ->
                val tmp = this.emergencyContactMapper?.getEmergencyContact(ref.emergencyContactId)
                if(tmp != null) {
                    item.emergencyContacts.add(tmp)
                }
            }
            item.emergencyContacts.add(EmergencyContact(0, "", "", ""))
        }
        return items
    }

    @Throws(Exception::class)
    fun getTeams(): List<Team> {
        return this.teamMapper?.getTeams() ?: listOf()
    }

    @Throws(Exception::class)
    fun getEmergencyContacts(): List<EmergencyContact> {
        return this.emergencyContactMapper?.getEmergencyContacts() ?: listOf()
    }

    @Throws(Exception::class)
    fun getAgeGroups(): List<AgeGroup> {
        return this.ageGroupMapper?.getAgeGroups() ?: listOf()
    }

    @Suppress("unused")
    @Throws(Exception::class)
    fun getPerson(id: Int): Person? {
        val person = this.peopleMapper?.getPerson(id)
        if(person != null) {
            this.emergencyContactMapper?.getEmergencyContactsOfPerson(person.id)?.forEach { ref ->
                val tmp = this.emergencyContactMapper?.getEmergencyContact(ref.emergencyContactId)
                if(tmp != null) {
                    person.emergencyContacts.add(tmp)
                }
            }
        }
        return person
    }

    @Throws(Exception::class)
    fun insertOrUpdatePerson(person: Person): Int {

        // load old data
        val oldPeople = this.peopleMapper?.getPeople()

        // person already exists
        if(person.id != 0) {
            val count = oldPeople?.count { it.id != person.id && (it.firstName == person.firstName && it.lastName == person.lastName) }
            if(count != 0) {
                throw Exception(FXHelper.getBundle().getString("sys.person.dataName"))
            }
        }


        // name is empty
        if(person.firstName.isEmpty()) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataFirstName"))
        }

        if(person.lastName.isEmpty()) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataLastName"))
        }

        // gender is empty
        if(!(person.gender == 0 || person.gender == 1 || person.gender == 2)) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataGender"))
        }

        // birthday not null or in future
        if(person.birthDate == null) {
            throw Exception(FXHelper.getBundle().getString("sys.person.dataBirthDay"))
        } else {
            val current = Date()
            if(current.before(person.birthDate)) {
                throw Exception(FXHelper.getBundle().getString("sys.person.dataBirthDayInPast"))
            }
        }

        // save
        if(person.id == 0) {
            person.id = this.peopleMapper?.insertPerson(person) ?: 0
        } else {
            this.peopleMapper?.updatePerson(person)
        }

        this.emergencyContactMapper?.deletePeopleEmergencyContact(person.id)

        val contacts = this.emergencyContactMapper?.getEmergencyContacts() ?: listOf()
        person.emergencyContacts.forEach { em ->
            if(em.name.isNotEmpty()) {
                val count = contacts.count { it.name == em.name || it.id == em.id }

                var id = em.id
                if(count == 0) {
                    id = this.emergencyContactMapper?.insertEmergencyContact(em) ?: 0
                } else {
                    this.emergencyContactMapper?.updateEmergencyContact(em)
                }
                this.emergencyContactMapper?.insertPeopleEmergencyContact(PeopleEmergencyContact(0, personId = person.id, emergencyContactId = id))
            }
        }
        return person.id
    }

    @Throws(Exception::class)
    fun deletePerson(person: Person) {
        this.deleteUnusedReferences(person.id)
        this.peopleMapper?.deletePerson(person)
    }

    private fun deleteUnusedReferences(personId: Int) {
        this.emergencyContactMapper?.deletePeopleEmergencyContact(personId = personId)

        val emContacts = this.getEmergencyContacts()
        emContacts.forEach { em ->
            val count = this.emergencyContactMapper?.count(em.id) ?: 0
            if(count == 0) {
                this.emergencyContactMapper?.deleteEmergencyContact(em)
            }
        }
    }
}