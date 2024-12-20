package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.Converter
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.UserRepository
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.Button
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback

class UsersController : SubController() {
    @FXML private lateinit var tblUsers: TableView<User>

    private lateinit var cmdNew: Button
    private lateinit var cmdDelete: Button
    private lateinit var cmdCancel: Button
    private lateinit var cmdSave: Button

    private var userRepository: UserRepository? = null

    override fun initControls() {
        this.userRepository = UserRepository()
        this.cmdNew.isDisable = false
        this.cmdCancel.isDisable = false
        this.cmdSave.isDisable = true
        this.cmdDelete.isDisable = true
        this.reload()

        this.tblUsers.selectionModel.selectedItemProperty().addListener { _ ->
            this.cmdDelete.isDisable = this.tblUsers.selectionModel.isEmpty
            this.cmdSave.isDisable = this.tblUsers.selectionModel.isEmpty
        }

        val userNameCol = TableColumn<User, String>()
        userNameCol.text = FXHelper.getBundle().getString("user.userName")
        userNameCol.cellValueFactory = PropertyValueFactory("userName")
        userNameCol.cellFactory = TextFieldTableCell.forTableColumn()
        userNameCol.onEditCommit = EventHandler {
            try {
                this.tblUsers.items[it.tablePosition.row].userName = it.newValue
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
        this.tblUsers.columns.add(userNameCol)

        val emailCol = TableColumn<User, String>()
        emailCol.text = FXHelper.getBundle().getString("user.email")
        emailCol.cellValueFactory = PropertyValueFactory("email")
        emailCol.cellFactory = TextFieldTableCell.forTableColumn()
        emailCol.onEditCommit = EventHandler {
            try {
                this.tblUsers.items[it.tablePosition.row].email = it.newValue
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
        this.tblUsers.columns.add(emailCol)

        val genderCol = TableColumn<User, String>()
        genderCol.text = FXHelper.getBundle().getString("user.gender")
        genderCol.cellValueFactory = Callback { data ->
            val user = data.value
            val stringProperty = SimpleStringProperty(Converter.intToGender(user.gender))
            stringProperty.addListener {_,_, new ->
                user.gender = Converter.genderToInt(new)
            }
            stringProperty
        }
        genderCol.cellFactory =
            ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(
                    FXHelper.getBundle().getString("user.gender.m"),
                    FXHelper.getBundle().getString("user.gender.f"),
                    FXHelper.getBundle().getString("user.gender.ns")
                )
            )
        this.tblUsers.columns.add(genderCol)

        val adminCol = TableColumn<User, Boolean>()
        adminCol.text = FXHelper.getBundle().getString("user.admin")
        adminCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.admin == 1)
            booleanProperty.addListener {_, _, new ->
                user.admin = if(new) 1 else 0
            }
            booleanProperty
        }
        adminCol.cellFactory = CheckBoxTableCell.forTableColumn(adminCol)
        this.tblUsers.columns.add(adminCol)

        val trunkLeaderCol = TableColumn<User, Boolean>()
        trunkLeaderCol.text = FXHelper.getBundle().getString("user.trunkLeader")
        trunkLeaderCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.trunkLeader == 1)
            booleanProperty.addListener {_,_,new ->
                user.trunkLeader = if(new) 1 else 0
            }
            booleanProperty
        }
        trunkLeaderCol.cellFactory = CheckBoxTableCell.forTableColumn(trunkLeaderCol)
        this.tblUsers.columns.add(trunkLeaderCol)

        val trunkWaitCol = TableColumn<User, Boolean>()
        trunkWaitCol.text = FXHelper.getBundle().getString("user.trunkWait")
        trunkWaitCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.trunkWait == 1)
            booleanProperty.addListener {_,_,new ->
                user.trunkWait = if(new) 1 else 0
            }
            booleanProperty
        }
        trunkWaitCol.cellFactory = CheckBoxTableCell.forTableColumn(trunkWaitCol)
        this.tblUsers.columns.add(trunkWaitCol)

        val trunkHelperCol = TableColumn<User, Boolean>()
        trunkHelperCol.text = FXHelper.getBundle().getString("user.trunkHelper")
        trunkHelperCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.trunkHelper == 1)
            booleanProperty.addListener {_,_,new ->
                user.trunkHelper = if(new) 1 else 0
            }
            booleanProperty
        }
        trunkHelperCol.cellFactory = CheckBoxTableCell.forTableColumn(trunkHelperCol)
        this.tblUsers.columns.add(trunkHelperCol)

        val leaderCol = TableColumn<User, Boolean>()
        leaderCol.text = FXHelper.getBundle().getString("user.leader")
        leaderCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.leader == 1)
            booleanProperty.addListener {_,_,new ->
                user.leader = if(new) 1 else 0
            }
            booleanProperty
        }
        leaderCol.cellFactory = CheckBoxTableCell.forTableColumn(leaderCol)
        this.tblUsers.columns.add(leaderCol)

        val juniorLeaderCol = TableColumn<User, Boolean>()
        juniorLeaderCol.text = FXHelper.getBundle().getString("user.juniorLeader")
        juniorLeaderCol.cellValueFactory = Callback { data ->
            val user = data.value
            val booleanProperty = SimpleBooleanProperty(user.juniorLeader == 1)
            booleanProperty.addListener {_,_,new ->
                user.juniorLeader = if(new) 1 else 0
            }
            booleanProperty
        }
        juniorLeaderCol.cellFactory = CheckBoxTableCell.forTableColumn(juniorLeaderCol)
        this.tblUsers.columns.add(juniorLeaderCol)
    }

    override fun initButtons() {
        this.cmdNew = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.PLUS) {
            this.tblUsers.items.add(User(0, "", "", 0, "", 0, 0, 0, 0, 0, 0))
        }

        this.cmdDelete = super.addIconButton(org.controlsfx.glyphfont.FontAwesome.Glyph.MINUS) {
            try {
                this.tblUsers.selectionModel.selectedItems.forEach { item ->
                    this.userRepository?.delete(item)
                }
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
                this.tblUsers.selectionModel.selectedItems.forEach { item ->
                    this.userRepository?.insertOrUpdate(item)
                }
                this.reload()
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
    }

    override fun initBindings() {}
    override fun initValidator() {}

    private fun reload() {
        try {
            // reload data
            val userModel = Project.get().getUser()

            if(userModel != null) {
                this.tblUsers.items.clear()

                if(userModel.isTrunkLeadership()) {
                    this.tblUsers.items.addAll(this.userRepository!!.getUsers())
                } else {
                    this.tblUsers.items.add(userModel)
                    this.cmdNew.isDisable = true
                }
            }
        } catch (ex: Exception) {
            FXHelper.printNotification(ex)
        }
    }
}