package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.Team
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.SelectKey
import org.apache.ibatis.annotations.Update

interface TeamMapper {

    @Select("SELECT * FROM teams")
    fun getTeams(): List<Team>

    @Select("SELECT * FROM teams WHERE id=#{id}")
    fun getTeam(id: Int): Team?

    @Insert(
        "INSERT INTO teams(name, description, note, gender, ageGroupId) " +
        "VALUES(#{name}, #{description}, #{note}, #{gender}, #{ageGroupId})"
    )
    @SelectKey(
        statement=["select max(id) as id from teams"],
        keyProperty="id", before=false, resultType=Int::class
    )
    fun insertTeam(team: Team): Int

    @Update(
        "UPDATE teams SET name=#{name}, description=#{description}, note=#{note}, " +
                "gender=#{gender}, ageGroupId=#{ageGroupId} " +
                "where id=#{id}"
    )
    fun updateTeam(team: Team)

    @Delete("DELETE FROM teams WHERE id=#{id}")
    fun deleteTeam(team: Team)
}