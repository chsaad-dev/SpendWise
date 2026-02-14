package com.example.spendwise.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\nH\'J\u0016\u0010\f\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\b\u00a8\u0006\r"}, d2 = {"Lcom/example/spendwise/data/CategoryDao;", "", "deleteAllCategories", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteCategory", "category", "Lcom/example/spendwise/data/Category;", "(Lcom/example/spendwise/data/Category;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllCategories", "Landroidx/lifecycle/LiveData;", "", "insertCategory", "app_debug"})
@androidx.room.Dao()
public abstract interface CategoryDao {
    
    @androidx.room.Query(value = "SELECT * FROM categories ORDER BY name ASC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Category>> getAllCategories();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertCategory(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteCategory(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM categories")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllCategories(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}