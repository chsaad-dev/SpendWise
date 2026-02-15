package com.example.spendwise.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0007\n\u0002\u0010\t\n\u0002\b\u0004\bg\u0018\u00002\u00020\u0001J\u0016\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u000b\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0014\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\t0\u00110\u0010H\'J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0013\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0018\u0010\u0014\u001a\u0004\u0018\u00010\t2\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u001c\u0010\u0015\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\u00110\u00102\u0006\u0010\u0004\u001a\u00020\u0005H\'J\u001c\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\r0\u00112\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0017\u001a\u00020\u00052\u0006\u0010\u0004\u001a\u00020\u0005H\u00a7@\u00a2\u0006\u0002\u0010\u0006J\u0016\u0010\u0018\u001a\u00020\u00192\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u001a\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u001b\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u00a7@\u00a2\u0006\u0002\u0010\nJ\u0016\u0010\u001c\u001a\u00020\u00032\u0006\u0010\f\u001a\u00020\rH\u00a7@\u00a2\u0006\u0002\u0010\u000e\u00a8\u0006\u001d"}, d2 = {"Lcom/example/spendwise/data/SplitDao;", "", "deleteAllMembersForGroup", "", "groupId", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteGroup", "group", "Lcom/example/spendwise/data/SplitGroup;", "(Lcom/example/spendwise/data/SplitGroup;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteMember", "member", "Lcom/example/spendwise/data/SplitMember;", "(Lcom/example/spendwise/data/SplitMember;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllGroups", "Landroidx/lifecycle/LiveData;", "", "getGroupByExpenseId", "expenseId", "getGroupById", "getMembersByGroupId", "getMembersByGroupIdSync", "getUnpaidCount", "insertGroup", "", "insertMember", "updateGroup", "updateMember", "app_debug"})
@androidx.room.Dao()
public abstract interface SplitDao {
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertGroup(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Long> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateGroup(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteGroup(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM split_groups ORDER BY createdDate DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SplitGroup>> getAllGroups();
    
    @androidx.room.Query(value = "SELECT * FROM split_groups WHERE expenseId = :expenseId LIMIT 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getGroupByExpenseId(int expenseId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.spendwise.data.SplitGroup> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM split_groups WHERE id = :groupId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getGroupById(int groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.spendwise.data.SplitGroup> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertMember(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitMember member, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateMember(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitMember member, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteMember(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitMember member, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM split_members WHERE groupId = :groupId")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SplitMember>> getMembersByGroupId(int groupId);
    
    @androidx.room.Query(value = "SELECT * FROM split_members WHERE groupId = :groupId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getMembersByGroupIdSync(int groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.spendwise.data.SplitMember>> $completion);
    
    @androidx.room.Query(value = "DELETE FROM split_members WHERE groupId = :groupId")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllMembersForGroup(int groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT COUNT(*) FROM split_members WHERE groupId = :groupId AND isPaid = 0")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getUnpaidCount(int groupId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Integer> $completion);
}