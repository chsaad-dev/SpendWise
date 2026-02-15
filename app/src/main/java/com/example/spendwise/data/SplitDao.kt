package com.example.spendwise.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface SplitDao {
    // ── Groups ──
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGroup(group: SplitGroup): Long

    @Update
    suspend fun updateGroup(group: SplitGroup)

    @Delete
    suspend fun deleteGroup(group: SplitGroup)

    @Query("SELECT * FROM split_groups ORDER BY createdDate DESC")
    fun getAllGroups(): LiveData<List<SplitGroup>>

    @Query("SELECT * FROM split_groups WHERE expenseId = :expenseId LIMIT 1")
    suspend fun getGroupByExpenseId(expenseId: Int): SplitGroup?

    @Query("SELECT * FROM split_groups WHERE id = :groupId")
    suspend fun getGroupById(groupId: Int): SplitGroup?

    // ── Members ──
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: SplitMember)

    @Update
    suspend fun updateMember(member: SplitMember)

    @Delete
    suspend fun deleteMember(member: SplitMember)

    @Query("SELECT * FROM split_members WHERE groupId = :groupId")
    fun getMembersByGroupId(groupId: Int): LiveData<List<SplitMember>>

    @Query("SELECT * FROM split_members WHERE groupId = :groupId")
    suspend fun getMembersByGroupIdSync(groupId: Int): List<SplitMember>

    @Query("DELETE FROM split_members WHERE groupId = :groupId")
    suspend fun deleteAllMembersForGroup(groupId: Int)

    // ── Aggregates ──
    @Query("SELECT COUNT(*) FROM split_members WHERE groupId = :groupId AND isPaid = 0")
    suspend fun getUnpaidCount(groupId: Int): Int
}
