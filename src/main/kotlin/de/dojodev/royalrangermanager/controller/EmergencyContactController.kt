package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.EmergencyContact
import de.dojodev.royalrangermanager.db.model.Person
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.repositories.PeopleRepository
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.SelectionMode
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.net.URL
import java.util.*

class EmergencyContactController : Initializable {
    @FXML private lateinit var cmdAddContact: Button
    @FXML private lateinit var cmdDeleteContact: Button
    @FXML private lateinit var cmdSaveContacts: Button

    @FXML private lateinit var tblContacts: TableView<EmergencyContact>

    @FXML private lateinit var cmdSave: Button
    @FXML private lateinit var cmdCancel: Button

    private val peopleRepository = PeopleRepository()

    override fun initialize(p0: URL?, p1: ResourceBundle?) {
        this.initButtons()
        this.initTable()
        this.reload()

        this.cmdAddContact.setOnAction {
            try {
                this.tblContacts.items.add(EmergencyContact(0, "", "", ""))
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdDeleteContact.setOnAction {
            try {
                this.tblContacts.selectionModel.selectedItems.forEach { item ->
                    if(item.id != 0) {
                        this.peopleRepository.deleteEmergencyContact(item)
                    }
                }
                this.reload()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdSaveContacts.setOnAction {
            try {
                this.tblContacts.selectionModel.selectedItems.forEach { item ->
                    this.peopleRepository.saveEmergencyContact(item)
                }
                this.reload()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }

        this.cmdCancel.setOnAction {
            selectedIds.clear()
            (this.cmdSave.scene.window as Stage).close()
        }

        this.cmdSave.setOnAction {
            selectedIds.clear()
            this.tblContacts.selectionModel.selectedItems.forEach { selectedIds.add(it.id) }
            (this.cmdSave.scene.window as Stage).close()
        }
    }

    private fun initButtons() {
        FXHelper.setIcon(this.cmdAddContact, org.controlsfx.glyphfont.FontAwesome.Glyph.PLUS)
        FXHelper.setIcon(this.cmdDeleteContact, org.controlsfx.glyphfont.FontAwesome.Glyph.TRASH)
        FXHelper.setIcon(this.cmdSaveContacts, org.controlsfx.glyphfont.FontAwesome.Glyph.SAVE)
    }

    private fun initTable() {
        this.tblContacts.selectionModel.selectionMode = SelectionMode.MULTIPLE
        this.tblContacts.columns.clear()

        val nameCol = TableColumn<EmergencyContact, String>()
        nameCol.text = FXHelper.getBundle().getString("person.name")
        nameCol.cellValueFactory = PropertyValueFactory("name")
        nameCol.cellFactory = TextFieldTableCell.forTableColumn()
        nameCol.setOnEditCommit {
            it.rowValue.name = it.newValue
        }
        this.tblContacts.columns.add(nameCol)

        val emailCol = TableColumn<EmergencyContact, String>()
        emailCol.text = FXHelper.getBundle().getString("user.email")
        emailCol.cellValueFactory = PropertyValueFactory("email")
        emailCol.cellFactory = TextFieldTableCell.forTableColumn()
        emailCol.setOnEditCommit {
            it.rowValue.email = it.newValue
        }
        this.tblContacts.columns.add(emailCol)

        val phoneCol = TableColumn<EmergencyContact, String>()
        phoneCol.text = FXHelper.getBundle().getString("person.phone")
        phoneCol.cellValueFactory = PropertyValueFactory("phone")
        phoneCol.cellFactory = TextFieldTableCell.forTableColumn()
        phoneCol.setOnEditCommit {
            it.rowValue.phone = it.newValue
        }
        this.tblContacts.columns.add(phoneCol)
    }

    private fun reload() {
        try {
            this.tblContacts.items.clear()
            this.tblContacts.items.addAll(this.peopleRepository.getEmergencyContacts())

            this.tblContacts.selectionModel.clearSelection()
            if(person != null) {
                person?.emergencyContacts?.forEach { item ->
                    this.tblContacts.items.forEach { tbl ->
                        if(item.id == tbl.id) {
                            this.tblContacts.selectionModel.select(tbl)
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }

    companion object {
        private var person: Person? = null
        private var selectedIds = mutableListOf<Int>()

        fun createDialog(person: Person? = null) : List<Int> {
            this.person = person
            this.selectedIds.clear()

            val loader = FXMLLoader(LoginController::class.java.getResource("/fxml/emergencyContacts.fxml"), FXHelper.getBundle())
            val root = loader.load<Parent>()
            val scene = Scene(root)
            val stage = Stage()
            stage.initModality(Modality.NONE)
            stage.initStyle(StageStyle.DECORATED)
            stage.initOwner(FXHelper.getStage())
            stage.scene = scene
            stage.showAndWait()
            return this.selectedIds
        }
    }
}