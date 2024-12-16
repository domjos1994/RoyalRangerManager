package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.User
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

interface UserMapper {

    @Select("SELECT * FROM users")
    fun getUsers(): List<User>

    @Select("SELECT * FROM users WHERE id=#{id}")
    fun getUserById(@Param("id") id: Int): User?

    @Select("SELECT * FROM users where userName=#{user}")
    fun getUserByName(@Param("user") user: String): User?

    @Select("SELECT * FROM users WHERE userName=#{user} AND password=#{password}")
    fun login(@Param("user") user: String, @Param("password") password: String): User?

    @Insert
    fun insert(user: User): Int

    @Update
    fun update(user: User)

    @Delete
    fun delete(user: User)
}