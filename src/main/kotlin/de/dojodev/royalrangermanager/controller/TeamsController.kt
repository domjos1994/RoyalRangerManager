package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.AgeGroup
import de.dojodev.royalrangermanager.db.model.Team
import de.dojodev.royalrangermanager.helper.Converter
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.TeamRepository
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.ComboBox
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.cell.PropertyValueFactory
import javafx.util.Callback

class TeamsController : SubController() {
    @FXML private lateinit var tblTeams: TableView<Team>

    @FXML private lateinit var txtTeamName: TextField
    @FXML private lateinit var txtTeamDescription: TextArea
    @FXML private lateinit var txtTeamNote: TextArea
    @FXML private lateinit var cmbTeamGroup: ComboBox<AgeGroup>
    @FXML private lateinit var cmbTeamGender: ComboBox<String>

    private var teamRepository: TeamRepository? = null
    private val ageGroups = mutableListOf<AgeGroup>()
    private var selectedTeam: Team? = null

    override fun initControls() {
        this.teamRepository = TeamRepository()
        this.ageGroups.addAll(this.teamRepository?.getAgeGroups() ?: listOf())
        super.cmdEdit?.disableProperty()?.bindBidirectional(super.cmdDelete?.disableProperty())
        super.cmdSave?.disableProperty()?.bindBidirectional(super.cmdCancel?.disableProperty())
        super.cmdNew?.isDisable = false
        super.cmdEdit?.isDisable = true
        super.cmdDelete?.isDisable = true
        super.cmdCancel?.isDisable = true
        super.cmdSave?.isDisable = true
        this.reload()

        this.tblTeams.columns.clear()
        val nameCol = TableColumn<Team, String>()
        nameCol.text = FXHelper.getBundle().getString("team.name")
        nameCol.cellValueFactory = PropertyValueFactory("name")
        this.tblTeams.columns.add(nameCol)

        val ageGroupCol = TableColumn<Team, String>()
        ageGroupCol.text = FXHelper.getBundle().getString("ageGroup")
        ageGroupCol.cellValueFactory = Callback { data ->
            SimpleStringProperty(this.ageGroups.find { it.id == data.value.ageGroupId }.toString())
        }
        this.tblTeams.columns.add(ageGroupCol)

        val genderCol = TableColumn<Team, String>()
        genderCol.text = FXHelper.getBundle().getString("user.gender")
        genderCol.cellValueFactory = Callback { data ->
            SimpleStringProperty(Converter.intToGender(data.value.gender))
        }
        this.tblTeams.columns.add(genderCol)

        this.cmbTeamGender.items.clear()
        this.cmbTeamGender.items.add(FXHelper.getBundle().getString("user.gender.ns"))
        this.cmbTeamGender.items.add(FXHelper.getBundle().getString("user.gender.m"))
        this.cmbTeamGender.items.add(FXHelper.getBundle().getString("user.gender.f"))

        this.cmbTeamGroup.items.clear()
        this.cmbTeamGroup.items.addAll(this.ageGroups)

        this.tblTeams.selectionModel.selectedItemProperty().addListener { _,_,_ ->
            this.cmdEdit?.isDisable = false
            this.control(
                editMode = false,
                reset = false,
                team = this.tblTeams.selectionModel.selectedItem
            )
        }

        super.cmdNew?.setOnAction {
            this.control(
                editMode = true,
                reset = false,
                team = Team(0, "", "", "", 0, 0)
            )
        }

        super.cmdEdit?.setOnAction {
            this.control(
                editMode = true,
                reset = false,
                team = this.tblTeams.selectionModel.selectedItem
            )
        }

        super.cmdDelete?.setOnAction {
            try {
                this.teamRepository?.deleteTeam(this.tblTeams.selectionModel.selectedItem)
                FXHelper.printSuccess()
                this.reload()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        super.cmdCancel?.setOnAction {
            try {
                FXHelper.printSuccess()
                this.reload()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        super.cmdSave?.setOnAction {
            try {
                if(this.selectedTeam != null) {
                    this.selectedTeam?.name = this.txtTeamName.text
                    this.selectedTeam?.description = this.txtTeamDescription.text
                    this.selectedTeam?.note = this.txtTeamNote.text
                    this.selectedTeam?.gender = Converter.genderToInt(this.cmbTeamGender.selectionModel.selectedItem)
                    this.selectedTeam?.ageGroupId = this.cmbTeamGroup.selectionModel.selectedItem.id

                    this.teamRepository?.insertOrUpdateTeam(this.selectedTeam!!)
                    FXHelper.printSuccess()
                    this.reload()
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    override fun init() {}

    private fun control(editMode: Boolean, reset: Boolean, team: Team? = null) {
        this.cmdNew?.isDisable = editMode
        this.cmdSave?.isDisable = !editMode

        this.txtTeamName.isDisable = !editMode
        this.txtTeamDescription.isDisable = !editMode
        this.txtTeamNote.isDisable = !editMode
        this.cmbTeamGroup.isDisable = !editMode
        this.cmbTeamGender.isDisable = !editMode

        if(reset) {
            this.txtTeamName.text = ""
            this.txtTeamDescription.text = ""
            this.txtTeamNote.text = ""
            this.cmbTeamGroup.selectionModel.clearSelection()
            this.cmbTeamGender.selectionModel.clearSelection()
        } else {
            if(team != null) {
                this.selectedTeam = team
                this.txtTeamName.text = this.selectedTeam?.name ?: ""
                this.txtTeamDescription.text = this.selectedTeam?.description ?: ""
                this.txtTeamNote.text = this.selectedTeam?.note ?: ""
                this.cmbTeamGroup.selectionModel.select(this.ageGroups.find { it.id == this.selectedTeam?.ageGroupId })
                this.cmbTeamGender.selectionModel.select(Converter.intToGender(this.selectedTeam?.gender ?: 0))
            }
        }
    }

    private fun reload() {
        try {
            // reload data
            val userModel = Project.get().getUser()

            if(userModel != null) {
                this.tblTeams.items.clear()
                this.tblTeams.items.addAll(this.teamRepository?.getTeams() ?: listOf())
            }
            this.control(editMode = false, reset = true)
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }
}