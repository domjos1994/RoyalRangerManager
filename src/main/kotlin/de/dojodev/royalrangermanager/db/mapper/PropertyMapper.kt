@file:Suppress("unused")

package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.Property
import org.apache.ibatis.annotations.Delete
import org.apache.ibatis.annotations.Insert
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select
import org.apache.ibatis.annotations.Update

interface PropertyMapper {

    @Select("SELECT * FROM properties")
    fun getProperties(): List<Property>

    @Select("SELECT * FROM properties WHERE `key`=#{key}")
    fun getProperty(@Param("key") key: String): List<Property>

    @Insert
    fun insertProperty(property: Property)

    @Update
    fun updateProperty(property: Property)

    @Delete
    fun deleteProperty(property: Property)
}