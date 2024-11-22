@file:Suppress("unused")

package de.dojodev.royalrangermanager.db.mapper

import de.dojodev.royalrangermanager.db.model.Property
import org.apache.ibatis.annotations.Param
import org.apache.ibatis.annotations.Select

interface PropertyMapper {

    @Select("SELECT * FROM properties")
    fun getProperties(): List<Property>

    @Select("SELECT * FROM properties WHERE `key`=#{key}")
    fun getProperty(@Param("key") key: String): List<Property>
}