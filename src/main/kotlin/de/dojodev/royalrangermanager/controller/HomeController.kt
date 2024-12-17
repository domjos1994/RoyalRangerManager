package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.AgeGroup
import de.dojodev.royalrangermanager.db.model.Person
import de.dojodev.royalrangermanager.db.model.Team
import de.dojodev.royalrangermanager.helper.Converter
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.PeopleRepository
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.DatePicker
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.util.Callback

class HomeController : SubController() {
    @FXML private lateinit var tblPeople: TableView<Person>
    @FXML private lateinit var txtPersonFirstName: TextField
    @FXML private lateinit var txtPersonMiddleName: TextField
    @FXML private lateinit var txtPersonLastName: TextField
    @FXML private lateinit var cmbPersonGender: ComboBox<String>
    @FXML private lateinit var dtPersonBirthdate: DatePicker
    @FXML private lateinit var txtPersonEmail: TextField
    @FXML private lateinit var txtPersonPhone: TextField
    @FXML private lateinit var cmbPersonTeam: ComboBox<Team>
    @FXML private lateinit var cmbPersonAgeGroup: ComboBox<AgeGroup>
    @FXML private lateinit var txtPersonMedicine: TextArea
    @FXML private lateinit var txtPersonDescription: TextArea
    @FXML private lateinit var txtPersonNote: TextArea

    private var peopleRepository: PeopleRepository? = null
    private var selectedPerson: Person? = null
    private var teams = mutableListOf<Team>()
    private var ageGroups = mutableListOf<AgeGroup>()

    override fun initControls() {
        try {
            this.peopleRepository = PeopleRepository()
            this.teams.clear()
            this.teams.addAll(this.peopleRepository?.getTeams() ?: listOf())
            this.ageGroups.clear()
            this.ageGroups.addAll(this.peopleRepository?.getAgeGroups() ?: listOf())

            this.initBindings()
            this.loadComboBoxes()
            this.loadTableView()
            this.reload()
            this.tblPeople.selectionModel.clearSelection()
        } catch (_: Exception) {
            this.control(editMode = false, reset = true)
            this.cmdNew?.isDisable = true
            this.tblPeople.selectionModel.clearSelection()
        }

        this.cmdNew?.setOnAction {
            try {
                this.control(
                    editMode = true,
                    reset = false,
                    person =
                        Person(
                            0, "", "", "", 0, null
                        )
                )
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdEdit?.setOnAction {
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

        this.cmdDelete?.setOnAction {
            try {
                this.peopleRepository?.deletePerson(this.tblPeople.selectionModel.selectedItem)
                this.reload()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdCancel?.setOnAction {
            try {
                this.reload()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdSave?.setOnAction {
            try {
                this.selectedPerson?.firstName = txtPersonFirstName.text
                this.selectedPerson?.middleName = txtPersonMiddleName.text
                this.selectedPerson?.lastName = txtPersonLastName.text
                this.selectedPerson?.gender = Converter.genderToInt(cmbPersonGender.selectionModel.selectedItem)
                this.selectedPerson?.birthDate = Converter.localDateToDate(this.dtPersonBirthdate.value)
                this.selectedPerson?.email = this.txtPersonEmail.text
                this.selectedPerson?.phone = this.txtPersonPhone.text
                this.selectedPerson?.teamId = this.cmbPersonTeam.selectionModel.selectedItem.id
                this.selectedPerson?.ageGroupId = this.cmbPersonAgeGroup.selectionModel.selectedItem.id
                this.selectedPerson?.medicines = this.txtPersonMedicine.text
                this.selectedPerson?.description = this.txtPersonDescription.text
                this.selectedPerson?.notes = this.txtPersonNote.text

                this.peopleRepository?.insertOrUpdatePerson(this.selectedPerson!!)
                this.reload()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.tblPeople.selectionModel.selectedItemProperty().addListener { _ ->
            this.cmdEdit?.isDisable = this.tblPeople.selectionModel.isEmpty
        }
    }

    override fun init() {

    }

    private fun control(editMode: Boolean, reset: Boolean, person: Person? = null) {
        this.cmdNew?.isDisable = editMode
        this.cmdEdit?.isDisable = editMode
        this.cmdSave?.isDisable = !editMode
        this.tblPeople.isDisable = editMode
        this.txtPersonFirstName.isDisable = !editMode

        if(reset) {
            this.tblPeople.selectionModel.clearSelection()
            this.txtPersonFirstName.text = ""
            this.txtPersonMiddleName.text = ""
            this.txtPersonLastName.text = ""
            this.cmbPersonGender.selectionModel.clearSelection()
            this.dtPersonBirthdate.value = null
            this.txtPersonEmail.text = ""
            this.txtPersonPhone.text = ""
            this.cmbPersonTeam.selectionModel.clearSelection()
            this.cmbPersonAgeGroup.selectionModel.clearSelection()
            this.txtPersonMedicine.text = ""
            this.txtPersonDescription.text = ""
            this.txtPersonNote.text = ""
        } else {
            if(person != null) {
                this.selectedPerson = person
                this.txtPersonFirstName.text = this.selectedPerson?.firstName ?: ""
                this.txtPersonMiddleName.text = this.selectedPerson?.middleName ?: ""
                this.txtPersonLastName.text = this.selectedPerson?.lastName ?: ""
                this.cmbPersonGender.selectionModel.select(Converter.intToGender(this.selectedPerson?.gender ?: 0))
                this.dtPersonBirthdate.value = Converter.dateToLocalDate(this.selectedPerson?.birthDate)
                this.txtPersonEmail.text = this.selectedPerson?.email ?: ""
                this.txtPersonPhone.text = this.selectedPerson?.phone ?: ""
                this.cmbPersonTeam.selectionModel.select(this.teams.find {
                    it.id == (this.selectedPerson?.teamId ?: 0)
                })
                this.cmbPersonAgeGroup.selectionModel.select(this.ageGroups.find {
                    it.id == (this.selectedPerson?.ageGroupId ?: 0)
                })
                this.txtPersonMedicine.text = this.selectedPerson?.medicines ?: ""
                this.txtPersonDescription.text = this.selectedPerson?.description ?: ""
                this.txtPersonNote.text = this.selectedPerson?.notes ?: ""
            }
        }
    }

    private fun initBindings() {
        this.cmdEdit?.disableProperty()?.bindBidirectional(this.cmdDelete?.disableProperty())
        this.cmdSave?.disableProperty()?.bindBidirectional(this.cmdCancel?.disableProperty())

        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonMiddleName.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonLastName.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmbPersonGender.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.dtPersonBirthdate.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonEmail.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonPhone.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmbPersonTeam.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.cmbPersonAgeGroup.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonMedicine.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonDescription.disableProperty())
        this.txtPersonFirstName.disableProperty()?.bindBidirectional(this.txtPersonNote.disableProperty())
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
    }
}