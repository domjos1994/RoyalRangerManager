package de.dojodev.royalrangermanager.repositories

import de.dojodev.royalrangermanager.db.mapper.UserMapper
import de.dojodev.royalrangermanager.db.model.User
import de.dojodev.royalrangermanager.helper.DBHelper
import de.dojodev.royalrangermanager.helper.FXHelper
import org.apache.ibatis.session.SqlSession
import java.security.MessageDigest
import kotlin.jvm.Throws

class UserRepository(
    private val sqlSession: SqlSession? = DBHelper.getSession()
) {
    private var userMapper: UserMapper? = null
    private val md5: MessageDigest


    init {
        this.userMapper = this.sqlSession?.getMapper(UserMapper::class.java)
        this.md5 = MessageDigest.getInstance("MD5")

        val admin = this.encrypt("admin")
        val users = this.userMapper?.getUsers()
        if(users?.isEmpty() != false) {
            val user = User(0, admin, admin, 0, "", 1, 0, 0, 0, 0, 0)
            this.userMapper?.insert(user)
        }
    }

    fun getUsers(): List<User> {
        return this.userMapper?.getUsers() ?: listOf()
    }

    fun getUsersById(id: Int): User? {
        return this.userMapper?.getUserById(id)
    }

    @Throws(Exception::class)
    fun login(user: String, password: String): User {
        val md5User = this.encrypt(user)
        val md5Pwd = this.encrypt(password)

        var userObj = this.userMapper?.login(md5User, md5Pwd)
        if(userObj != null) {
            return userObj
        }
        userObj = this.userMapper?.getUserByName(md5User)
        if(userObj != null) {
            throw Exception(FXHelper.getBundle().getString("sys.user.dataPassword"))
        } else {
            throw Exception(FXHelper.getBundle().getString("sys.user.dataIncorrect"))
        }
    }

    fun insertOrUpdate(user: User): Int {
        user.userName = this.encrypt(user.userName)
        user.password = this.encrypt(user.password)
        if(user.id != 0) {
            this.userMapper?.update(user)
        } else {
            user.id = this.userMapper?.insert(user) ?: 0
        }
        return user.id
    }

    fun delete(user: User) {
        this.userMapper?.delete(user)
    }

    private fun encrypt(value: String): String {
        this.md5.update(value.encodeToByteArray())
        val digest = this.md5.digest()
        return digest.decodeToString()
    }
}