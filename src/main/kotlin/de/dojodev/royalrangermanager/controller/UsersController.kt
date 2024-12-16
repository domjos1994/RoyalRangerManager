package de.dojodev.royalrangermanager.controller

import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.FXHelper
import de.dojodev.royalrangermanager.helper.Project
import de.dojodev.royalrangermanager.repositories.UserRepository
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.CheckBoxTableCell
import javafx.scene.control.cell.ComboBoxTableCell
import javafx.scene.control.cell.PropertyValueFactory
import javafx.scene.control.cell.TextFieldTableCell
import javafx.util.Callback

class UsersController : SubController() {

    @FXML
    private lateinit var tblUsers: TableView<User>

    private var userRepository: UserRepository? = null

    override fun initControls() {
        super.cmdNew?.isDisable = false
        super.cmdEdit?.isDisable = true
        super.cmdCancel?.isDisable = true
        super.cmdSave?.isDisable = false
        super.cmdDelete?.isDisable = true

        val userModel = Project.get().getUser()

        if(userModel != null) {
            this.userRepository = UserRepository()
            this.tblUsers.items.clear()

            if(userModel.admin == 1 || userModel.trunkLeader == 1 || userModel.trunkWait == 1 || userModel.trunkHelper == 1) {
                this.tblUsers.items.addAll(this.userRepository!!.getUsers())
            } else {
                this.tblUsers.items.add(userModel)
            }
        }

        super.cmdNew?.setOnAction {
            this.tblUsers.items.add(User(0, "", "", 0, "", 0, 0, 0, 0, 0, 0))
        }

        val userNameCol = TableColumn<User, String>()
        userNameCol.text = FXHelper.getBundle().getString("user.userName")
        userNameCol.cellValueFactory = PropertyValueFactory("userName")
        userNameCol.cellFactory = TextFieldTableCell.forTableColumn()
        userNameCol.onEditCommit = EventHandler {
            try {
                this.tblUsers.items[it.tablePosition.row].userName = it.newValue
                this.userRepository!!.insertOrUpdate(this.tblUsers.items[it.tablePosition.row])
                FXHelper.printSuccess()
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
                this.userRepository!!.insertOrUpdate(this.tblUsers.items[it.tablePosition.row])
                FXHelper.printSuccess()
            } catch (ex: Exception) {
                FXHelper.printNotification(ex)
            }
        }
        this.tblUsers.columns.add(emailCol)

        val genderCol = TableColumn<User, String>()
        genderCol.text = FXHelper.getBundle().getString("user.gender")
        genderCol.cellValueFactory = Callback { data ->
            val user = data.value
            val stringProperty = SimpleStringProperty(if(user.gender == 0) FXHelper.getBundle().getString("user.gender.m") else FXHelper.getBundle().getString("user.gender.m"))
            stringProperty.addListener {_,_, new ->
                user.gender = if(new==FXHelper.getBundle().getString("user.gender.m")) 0 else 1
            }
            stringProperty
        }
        genderCol.cellFactory =
            ComboBoxTableCell.forTableColumn(
                FXCollections.observableArrayList(
                    FXHelper.getBundle().getString("user.gender.m"),
                    FXHelper.getBundle().getString("user.gender.f")
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

    override fun init() {
    }
}