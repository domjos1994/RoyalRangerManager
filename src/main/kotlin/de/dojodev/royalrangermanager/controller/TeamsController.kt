package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.AgeGroup
import de.dojodev.royalrangermanager.db.model.Team
import de.dojodev.royalrangermanager.helper.Converter
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.TeamRepository
import javafx.beans.property.SimpleStringProperty
import javafx.fxml.FXML
import javafx.scene.control.Button
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

    private lateinit var cmdNew: Button
    private lateinit var cmdEdit: Button
    private lateinit var cmdDelete: Button
    private lateinit var cmdCancel: Button
    private lateinit var cmdSave: Button

    private var teamRepository: TeamRepository? = null
    private val ageGroups = mutableListOf<AgeGroup>()
    private var selectedTeam: Team? = null

    override fun initControls() {
        this.teamRepository = TeamRepository()
        this.ageGroups.addAll(this.teamRepository?.getAgeGroups() ?: listOf())
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
            this.cmdEdit.isDisable = false
            this.control(
                editMode = false,
                reset = false,
                team = this.tblTeams.selectionModel.selectedItem
            )
        }
    }

    override fun initButtons() {
        this.cmdNew = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.PLUS) {
            this.control(
                editMode = true,
                reset = false,
                team = Team(0, "", "", "", 0, 0)
            )
        }

        this.cmdEdit = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.EDIT) {
            this.control(
                editMode = true,
                reset = false,
                team = this.tblTeams.selectionModel.selectedItem
            )
        }

        this.cmdDelete = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.MINUS) {
            try {
                this.teamRepository?.deleteTeam(this.tblTeams.selectionModel.selectedItem)
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
                if(this.selectedTeam != null) {
                    this.selectedTeam?.name = this.txtTeamName.text
                    this.selectedTeam?.description = this.txtTeamDescription.text
                    this.selectedTeam?.note = this.txtTeamNote.text
                    this.selectedTeam?.gender = Converter.genderToInt(this.cmbTeamGender.selectionModel.selectedItem)
                    this.selectedTeam?.ageGroupId = this.cmbTeamGroup.selectionModel.selectedItem.id

                    this.teamRepository?.insertOrUpdateTeam(this.selectedTeam!!)
                    this.reload()
                    FXHelper.printSuccess()
                }
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    override fun initBindings() {
        this.cmdEdit.disableProperty()?.bindBidirectional(this.cmdDelete.disableProperty())
        this.cmdSave.disableProperty()?.bindBidirectional(this.cmdCancel.disableProperty())
    }

    private fun control(editMode: Boolean, reset: Boolean, team: Team? = null) {
        this.cmdNew.isDisable = editMode
        this.cmdSave.isDisable = !editMode

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