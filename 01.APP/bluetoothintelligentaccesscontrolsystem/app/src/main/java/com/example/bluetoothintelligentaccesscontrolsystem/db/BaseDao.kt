package com.example.bluetoothintelligentaccesscontrolsystem.db

import java.util.Objects

/***
 * 数据库CRUD操作接口
 * @author CP
 */
interface BaseDao {
    /**
     * @brief 添加表数据
     * @param bean 要添加的实体
     * @return Int 0表示成功 1表示已有数据 -1表示添加错误
     */
    fun insert(bean: Any): Int

    /**
     * @brief 修改表数据
     * @param bean 修改以后的实体
     * @param data 修改条件，为可变数据。
     * @return Int 0表示成功 1表示修改失败 -1表示修改错误 同为失败
     */
    fun update(bean: Any, vararg data: String): Int

    /**
     * @brief 查询表数据
     * @param data 修改条件，为可变数据。
     * @return List<Any> 为null 则表示查询错误，长度为0表示没有查询到数据
     */
    fun query(vararg data: String): MutableList<out Any>?

    /**
     * @brief 删除表数据
     * @param data 删除条件，为可变数据。
     * @return Int 0表示成功 1表示失败 -1表示错误同为失败
     */
    fun delete(vararg data: String): Int
}