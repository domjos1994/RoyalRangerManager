package de.dojodev.royalrangermanager.controller

import com.google.i18n.phonenumbers.PhoneNumberUtil
import de.dojodev.royalrangermanager.db.model.AgeGroup
import de.dojodev.royalrangermanager.db.model.EmergencyContact
import de.dojodev.royalrangermanager.db.model.Person
import de.dojodev.royalrangermanager.db.model.Team
import de.dojodev.royalrangermanager.helper.Converter
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.PeopleRepository
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.*
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback
import org.apache.commons.validator.routines.DomainValidator
import org.apache.commons.validator.routines.EmailValidator
import java.time.LocalDate
import java.time.Period
import java.util.Locale


class HomeController : SubController() {
    @FXML private lateinit var tblPeople: TableView<Person>
    @FXML private lateinit var txtPersonMemberId: TextField
    @FXML private lateinit var txtPersonFirstName: TextField
    @FXML private lateinit var txtPersonMiddleName: TextField
    @FXML private lateinit var txtPersonLastName: TextField
    @FXML private lateinit var txtPersonChildNumber: TextField
    @FXML private lateinit var cmbPersonGender: ComboBox<String>
    @FXML private lateinit var dtPersonBirthdate: DatePicker
    @FXML private lateinit var dtPersonEntryDate: DatePicker
    @FXML private lateinit var txtPersonEmail: TextField
    @FXML private lateinit var txtPersonPhone: TextField
    @FXML private lateinit var txtPersonStreet: TextField
    @FXML private lateinit var txtPersonNo: TextField
    @FXML private lateinit var txtPersonZip: TextField
    @FXML private lateinit var txtPersonLocality: TextField
    @FXML private lateinit var cmbPersonTeam: ComboBox<Team>
    @FXML private lateinit var cmbPersonAgeGroup: ComboBox<AgeGroup>
    @FXML private lateinit var txtPersonMedicine: TextArea
    @FXML private lateinit var txtPersonDescription: TextArea
    @FXML private lateinit var txtPersonNote: TextArea
    @FXML private lateinit var cmdOpenDialog: Button

    @FXML private lateinit var tblEmergencyContacts: TableView<EmergencyContact>
    @FXML private lateinit var nameCol: TableColumn<EmergencyContact, String>
    @FXML private lateinit var phoneCol: TableColumn<EmergencyContact, String>
    @FXML private lateinit var emailCol: TableColumn<EmergencyContact, String>

    private lateinit var cmdNew: Button
    private lateinit var cmdEdit: Button
    private lateinit var cmdDelete: Button
    private lateinit var cmdCancel: Button
    private lateinit var cmdSave: Button

    private var peopleRepository: PeopleRepository? = null
    private var selectedPerson: Person? = null
    private var teams = mutableListOf<Team>()
    private var ageGroups = mutableListOf<AgeGroup>()
    private val emDictionary = mutableMapOf<String, EmergencyContact>()

    override fun initControls() {
        try {
            this.peopleRepository = PeopleRepository()
            this.teams.clear()
            this.teams.addAll(this.peopleRepository?.getTeams() ?: listOf())
            this.ageGroups.clear()
            this.ageGroups.addAll(this.peopleRepository?.getAgeGroups() ?: listOf())

            this.loadComboBoxes()
            this.loadTableView()
            this.reload()
            this.tblPeople.selectionModel.clearSelection()
            this.cmdNew.isDisable = false
            this.tblPeople.isDisable = false
        } catch (_: Exception) {
            this.control(editMode = false, reset = true)
            this.tblPeople.selectionModel.clearSelection()
            this.cmdNew.isDisable = true
            this.cmdEdit.isDisable = true
            this.tblPeople.isDisable = true
        }

        this.tblPeople.selectionModel.selectedItemProperty().addListener { _,_,p ->
            this.cmdEdit.isDisable = this.tblPeople.selectionModel.isEmpty
            this.control(editMode = false, reset = false, person = p)
        }

        this.dtPersonBirthdate.valueProperty().addListener { _,_,value ->
            val now = LocalDate.now()
            if(value != null) {
                val period = Period.between(value, now)
                val lst = this.ageGroups.sortedBy { it.minAge }

                val ageGroup = lst.find {
                    it.minAge <= period.years && it.maxAge >= period.years
                }

                if(ageGroup != null) {
                    this.cmbPersonAgeGroup.selectionModel.select(ageGroup)
                }
            }
        }

        this.cmdOpenDialog.setOnAction { this.openDialog() }
    }

    override fun initButtons() {
        this.cmdNew = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.PLUS) {
            try {
                val newPerson = Person(
                    0, "", "", "", "", 0,0, null, null
                )
                newPerson.emergencyContacts.add(EmergencyContact(0, "", "", ""))
                this.control(
                    editMode = true,
                    reset = false,
                    person = newPerson

                )
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdEdit = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.EDIT) {
            try {
                this.control(
                    editMode = true,
                    reset = false,
                    person = this.tblPeople.selectionModel.selectedItem
                )
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdDelete = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.MINUS) {
            try {
                this.peopleRepository?.deletePerson(this.tblPeople.selectionModel.selectedItem)
                this.reload()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdCancel = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.CLOSE) {
            try {
                this.reload()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdSave = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.SAVE) {
            try {
                this.selectedPerson?.memberId = txtPersonMemberId.text
                this.selectedPerson?.firstName = txtPersonFirstName.text
                this.selectedPerson?.middleName = txtPersonMiddleName.text
                this.selectedPerson?.lastName = txtPersonLastName.text
                this.selectedPerson?.childNumber = Integer.parseInt(txtPersonChildNumber.text)
                this.selectedPerson?.gender = Converter.genderToInt(cmbPersonGender.selectionModel.selectedItem)
                this.selectedPerson?.birthDate = Converter.localDateToDate(this.dtPersonBirthdate.value)
                this.selectedPerson?.entryDate = Converter.localDateToDate(this.dtPersonEntryDate.value)
                this.selectedPerson?.email = this.txtPersonEmail.text
                this.selectedPerson?.phone = this.txtPersonPhone.text
                this.selectedPerson?.street = this.txtPersonStreet.text
                this.selectedPerson?.number = this.txtPersonNo.text
                this.selectedPerson?.postalCode = this.txtPersonZip.text
                this.selectedPerson?.locality = this.txtPersonLocality.text
                this.selectedPerson?.teamId = this.cmbPersonTeam.selectionModel.selectedItem.id
                this.selectedPerson?.ageGroupId = this.cmbPersonAgeGroup.selectionModel.selectedItem.id
                this.selectedPerson?.medicines = this.txtPersonMedicine.text
                this.selectedPerson?.description = this.txtPersonDescription.text
                this.selectedPerson?.notes = this.txtPersonNote.text

                this.peopleRepository?.insertOrUpdatePerson(this.selectedPerson!!)
                this.reload()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    private fun control(editMode: Boolean, reset: Boolean, person: Person? = null) {
        this.cmdNew.isDisable = editMode
        this.cmdEdit.isDisable = editMode
        this.cmdCancel.isDisable = !editMode
        this.cmdSave.isDisable = !editMode || this.validator.containsErrors()
        this.tblPeople.isDisable = editMode
        this.txtPersonFirstName.isDisable = !editMode

        if(reset) {
            this.tblPeople.selectionModel.clearSelection()
            this.txtPersonMemberId.text = ""
            this.txtPersonFirstName.text = ""
            this.txtPersonMiddleName.text = ""
            this.txtPersonLastName.text = ""
            this.txtPersonChildNumber.text = "1"
            this.cmbPersonGender.selectionModel.clearSelection()
            this.dtPersonBirthdate.value = null
            this.dtPersonEntryDate.value = null
            this.txtPersonEmail.text = ""
            this.txtPersonPhone.text = ""
            this.txtPersonStreet.text = ""
            this.txtPersonNo.text = ""
            this.txtPersonZip.text = ""
            this.txtPersonLocality.text = ""
            this.cmbPersonTeam.selectionModel.clearSelection()
            this.cmbPersonAgeGroup.selectionModel.clearSelection()
            this.txtPersonMedicine.text = ""
            this.txtPersonDescription.text = ""
            this.txtPersonNote.text = ""
            this.tblEmergencyContacts.items.clear()
        } else {
            if(person != null) {
                this.selectedPerson = person
                this.txtPersonMemberId.text = this.selectedPerson?.memberId ?: ""
                this.txtPersonFirstName.text = this.selectedPerson?.firstName ?: ""
                this.txtPersonMiddleName.text = this.selectedPerson?.middleName ?: ""
                this.txtPersonLastName.text = this.selectedPerson?.lastName ?: ""
                this.txtPersonChildNumber.text = this.selectedPerson?.childNumber.toString()
                this.cmbPersonGender.selectionModel.select(Converter.intToGender(this.selectedPerson?.gender ?: 0))
                this.dtPersonBirthdate.value = Converter.dateToLocalDate(this.selectedPerson?.birthDate)
                this.dtPersonEntryDate.value = Converter.dateToLocalDate(this.selectedPerson?.entryDate)
                this.txtPersonEmail.text = this.selectedPerson?.email ?: ""
                this.txtPersonPhone.text = this.selectedPerson?.phone ?: ""
                this.txtPersonStreet.text = this.selectedPerson?.street ?: ""
                this.txtPersonNo.text = this.selectedPerson?.number ?: ""
                this.txtPersonZip.text = this.selectedPerson?.postalCode ?: ""
                this.txtPersonLocality.text = this.selectedPerson?.locality ?: ""
                this.cmbPersonTeam.selectionModel.select(this.teams.find {
                    it.id == (this.selectedPerson?.teamId ?: 0)
                })
                this.cmbPersonAgeGroup.selectionModel.select(this.ageGroups.find {
                    it.id == (this.selectedPerson?.ageGroupId ?: 0)
                })
                this.txtPersonMedicine.text = this.selectedPerson?.medicines ?: ""
                this.txtPersonDescription.text = this.selectedPerson?.description ?: ""
                this.txtPersonNote.text = this.selectedPerson?.notes ?: ""
                this.tblEmergencyContacts.items.clear()
                this.tblEmergencyContacts.items.addAll(this.selectedPerson?.emergencyContacts ?: listOf())
            }
        }
    }

    override fun initBindings() {
        this.cmdEdit.disableProperty()?.bindBidirectional(this.cmdDelete.disableProperty())

        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonMemberId.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonMiddleName.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonLastName.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonChildNumber.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmbPersonGender.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.dtPersonBirthdate.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.dtPersonEntryDate.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonEmail.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonPhone.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonStreet.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonNo.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonZip.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonLocality.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmbPersonTeam.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmbPersonAgeGroup.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonMedicine.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonDescription.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonNote.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.tblEmergencyContacts.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmdOpenDialog.disableProperty())
    }

    private fun reload() {
        try {
            // reload data
            val userModel = Project.get().getUser()

            if(userModel != null) {
                this.tblPeople.items.clear()
                this.tblPeople.items.addAll(this.peopleRepository?.getPeople() ?: listOf())
            }
            this.control(editMode = false, reset = true)

            this.emDictionary.clear()
            val contacts = this.peopleRepository?.getEmergencyContacts() ?: listOf()
            contacts.forEach { contact ->
                if(!this.emDictionary.containsKey(contact.name)) {
                    this.emDictionary[contact.name] = contact
                }
            }
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }

    private fun loadComboBoxes() {
        this.cmbPersonGender.items.clear()
        this.cmbPersonGender.items.add(FXHelper.getBundle().getString("user.gender.ns"))
        this.cmbPersonGender.items.add(FXHelper.getBundle().getString("user.gender.m"))
        this.cmbPersonGender.items.add(FXHelper.getBundle().getString("user.gender.f"))

        this.cmbPersonTeam.items.clear()
        this.cmbPersonTeam.items.addAll(this.teams)

        this.cmbPersonAgeGroup.items.clear()
        this.cmbPersonAgeGroup.items.addAll(this.ageGroups)
    }

    private fun loadTableView() {
        this.tblPeople.columns.clear()

        val nameCol = TableColumn<Person, String>()
        nameCol.text = FXHelper.getBundle().getString("person.name")
        nameCol.cellValueFactory = Callback { item ->
            SimpleStringProperty("${item.value.firstName} ${item.value.middleName} ${item.value.lastName}".trim())
        }
        this.tblPeople.columns.add(nameCol)

        val genderCol = TableColumn<Person, String>()
        genderCol.text = FXHelper.getBundle().getString("user.gender")
        genderCol.cellValueFactory = Callback { item ->
            SimpleStringProperty(Converter.intToGender(item.value.gender))
        }
        this.tblPeople.columns.add(genderCol)

        val ageGroupCol = TableColumn<Person, String>()
        ageGroupCol.text = FXHelper.getBundle().getString("ageGroup")
        ageGroupCol.cellValueFactory = Callback { item ->
            SimpleStringProperty(this.ageGroups.find { it.id == item.value.ageGroupId }.toString())
        }
        this.tblPeople.columns.add(ageGroupCol)

        val teamCol = TableColumn<Person, String>()
        teamCol.text = FXHelper.getBundle().getString("person.team")
        teamCol.cellValueFactory = Callback { item ->
            SimpleStringProperty(this.teams.find { it.id == item.value.teamId }.toString())
        }
        this.tblPeople.columns.add(teamCol)

        this.tblPeople.isEditable = false
        this.nameCol.cellValueFactory = PropertyValueFactory("name")
        this.emailCol.cellValueFactory = PropertyValueFactory("email")
        this.phoneCol.cellValueFactory = PropertyValueFactory("phone")
    }

    private fun openDialog() {
        val items = EmergencyContactController.createDialog(this.selectedPerson)
        val emContacts = this.peopleRepository?.getEmergencyContacts()?.filter { items.contains(it.id) } ?: listOf()
        this.selectedPerson?.emergencyContacts?.clear()
        this.selectedPerson?.emergencyContacts?.addAll(emContacts)

        this.tblEmergencyContacts.items.clear()
        this.tblEmergencyContacts.items.addAll(emContacts)
    }

    override fun initValidator() {
        this.validator
            .createCheck()
            .dependsOn("memberId", this.txtPersonMemberId.textProperty())
            .withMethod { c ->
                try {
                    val text = c.get<String>("memberId")
                    if(text.isEmpty()) {
                        throw Exception()
                    }
                } catch (_: Exception) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataMemberId"))
                }
            }
            .decorates(this.txtPersonMemberId)
            .immediate()
        this.validator
            .createCheck()
            .dependsOn("firstName", this.txtPersonFirstName.textProperty())
            .withMethod { c ->
                val firstName = c.get<String>("firstName")
                if(firstName.isEmpty()) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataFirstName"))
                }
            }
            .decorates(this.txtPersonFirstName)
            .immediate()
        this.validator
            .createCheck()
            .dependsOn("lastName", this.txtPersonLastName.textProperty())
            .withMethod { c ->
                val lastName = c.get<String>("lastName")
                if(lastName.isEmpty()) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataLastName"))
                }
            }
            .decorates(this.txtPersonLastName)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("firstName", this.txtPersonFirstName.textProperty())
            .dependsOn("lastName", this.txtPersonLastName.textProperty())
            .withMethod { c ->
                val firstName = c.get<String>("firstName")
                val lastName = c.get<String>("lastName")
                val count = this.tblPeople.items.count {
                    it.id != (this.selectedPerson?.id ?: 0) &&
                            it.firstName == firstName &&
                            it.lastName == lastName
                }
                if(count != 0) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataName"))
                }
            }
            .decorates(this.txtPersonFirstName)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("childNumber", this.txtPersonChildNumber.textProperty())
            .withMethod { c ->
                try {
                    val text = c.get<String>("childNumber")
                    if(text.isEmpty()) {
                        throw Exception()
                    }
                    val childNumber = text.toInt()
                    if(childNumber !in 1..20) {
                        throw Exception()
                    }
                } catch (_: Exception) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataChildNumber"))
                }
            }
            .decorates(this.txtPersonChildNumber)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("birthDate", this.dtPersonBirthdate.valueProperty())
            .withMethod { c ->
                val date = c.get<LocalDate>("birthDate")
                val lst = this.ageGroups.sortedBy { it.minAge }
                val minAge = if(lst.isEmpty()) 0 else lst[0].minAge
                val now = LocalDate.now()

                if(date == null) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataBirthDay"))
                } else {
                    val period = Period.between(date, now)

                    if(minAge > period.years) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataBirthDayToYoung"))
                    }
                }
            }
            .decorates(this.dtPersonBirthdate)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("birthDate", this.dtPersonBirthdate.valueProperty())
            .dependsOn("entryDate", this.dtPersonEntryDate.valueProperty())
            .withMethod { c ->
                val birthDate = c.get<LocalDate>("birthDate")
                val entryDate = c.get<LocalDate>("entryDate")
                val currentDate = LocalDate.now()

                if(entryDate == null) {
                    c.error(FXHelper.getBundle().getString("sys.person.dataEntryDate"))
                } else {
                    if(birthDate == null) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataEntryDataBeforeBirthDayAndNow"))
                    } else {
                        if(birthDate.isAfter(entryDate) || currentDate.isBefore(entryDate)) {
                            c.error(FXHelper.getBundle().getString("sys.person.dataEntryDataBeforeBirthDayAndNow"))
                        }
                    }
                }
            }
            .decorates(this.dtPersonEntryDate)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("team", this.cmbPersonTeam.valueProperty())
            .withMethod { c ->
                val team = c.get<Team?>("team")
                if(team == null) {
                    c.error(FXHelper.getBundle().getString("sys.person.team"))
                }
            }
            .decorates(this.cmbPersonTeam)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("age", this.cmbPersonAgeGroup.valueProperty())
            .withMethod { c ->
                val ageGroup = c.get<AgeGroup?>("age")
                if(ageGroup == null) {
                    c.error(FXHelper.getBundle().getString("sys.person.ageGroup"))
                }
            }
            .decorates(this.cmbPersonAgeGroup)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("email", this.txtPersonEmail.textProperty())
            .withMethod { c ->
                val email = c.get<String>("email")
                if(email.isNotEmpty()) {
                    val validator = EmailValidator(false, false, DomainValidator.getInstance())
                    if(!validator.isValid(email)) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataEmail"))
                    }
                }
            }
            .decorates(this.txtPersonEmail)
            .immediate()

        this.validator
            .createCheck()
            .dependsOn("phone", this.txtPersonPhone.textProperty())
            .withMethod { c ->
                val phone = c.get<String>("phone")
                if(phone.isNotEmpty()) {
                    try {
                        val phoneNumberUtil = PhoneNumberUtil.getInstance()
                        val number = phoneNumberUtil.parse(phone, Locale.getDefault().country)
                        if(number == null) {
                            c.error(FXHelper.getBundle().getString("sys.person.dataPhone"))
                        }
                    } catch (_: Exception) {
                        c.error(FXHelper.getBundle().getString("sys.person.dataPhone"))
                    }
                }
            }
            .decorates(this.txtPersonPhone)
            .immediate()

        this.validator.validationResultProperty().addListener { _,_,c ->
            if(c.messages.isEmpty()) {
                this.cmdSave.isDisable = this.txtPersonFirstName.isDisable
            } else {
                this.cmdSave.isDisable = true
            }
        }
    }
}