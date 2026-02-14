package com.example.spendwise.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u0006\n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u000e\u0010\u0002\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0016\u0010\u0005\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u0014\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\nH\'J\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00070\u000bH\u00a7@\u00a2\u0006\u0002\u0010\u0004J$\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\u000b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0012J\u0018\u0010\u0013\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0014\u001a\u00020\u0015H\u00a7@\u00a2\u0006\u0002\u0010\u0016J\u001c\u0010\u0017\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\n2\u0006\u0010\u0018\u001a\u00020\u0019H\'J,\u0010\u001a\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\n2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\'J$\u0010\u001b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\'J$\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00070\u000b2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\u00a7@\u00a2\u0006\u0002\u0010\u0012J \u0010\u001d\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001e0\n2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u0010H\'J\u0010\u0010\u001f\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u001e0\nH\'J\u0016\u0010 \u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\bJ\u001c\u0010!\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00070\u000b0\n2\u0006\u0010\"\u001a\u00020\u0019H\'J\u0016\u0010#\u001a\u00020\u00032\u0006\u0010\u0006\u001a\u00020\u0007H\u00a7@\u00a2\u0006\u0002\u0010\b\u00a8\u0006$"}, d2 = {"Lcom/example/spendwise/data/ExpenseDao;", "", "deleteAllExpenses", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpense", "expense", "Lcom/example/spendwise/data/Expense;", "(Lcom/example/spendwise/data/Expense;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllExpenses", "Landroidx/lifecycle/LiveData;", "", "getAllExpensesSync", "getCategoryTotals", "Lcom/example/spendwise/data/CategoryTotal;", "startDate", "", "endDate", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getExpenseById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getExpensesByCategory", "category", "", "getExpensesByCategoryAndDateRange", "getExpensesByDateRange", "getExpensesByDateRangeSync", "getMonthlyExpense", "", "getTotalExpense", "insertExpense", "searchExpenses", "query", "updateExpense", "app_debug"})
@androidx.room.Dao()
public abstract interface ExpenseDao {
    
    @androidx.room.Query(value = "SELECT * FROM expenses ORDER BY date DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getAllExpenses();
    
    @androidx.room.Query(value = "SELECT * FROM expenses ORDER BY date DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllExpensesSync(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.spendwise.data.Expense>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE note LIKE \'%\' || :query || \'%\' OR category LIKE \'%\' || :query || \'%\' ORDER BY date DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> searchExpenses(@org.jetbrains.annotations.NotNull()
    java.lang.String query);
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getExpensesByDateRangeSync(long startDate, long endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.spendwise.data.Expense>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getExpensesByDateRange(long startDate, long endDate);
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE category = :category ORDER BY date DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getExpensesByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category);
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE category = :category AND date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getExpensesByCategoryAndDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String category, long startDate, long endDate);
    
    @androidx.room.Query(value = "SELECT SUM(amount) FROM expenses")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Double> getTotalExpense();
    
    @androidx.room.Query(value = "SELECT SUM(amount) FROM expenses WHERE date BETWEEN :startDate AND :endDate")
    @org.jetbrains.annotations.NotNull()
    public abstract androidx.lifecycle.LiveData<java.lang.Double> getMonthlyExpense(long startDate, long endDate);
    
    @androidx.room.Query(value = "SELECT category, SUM(amount) as total FROM expenses WHERE date BETWEEN :startDate AND :endDate GROUP BY category")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getCategoryTotals(long startDate, long endDate, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.spendwise.data.CategoryTotal>> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object insertExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Update()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "DELETE FROM expenses")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteAllExpenses(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM expenses WHERE id = :id")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getExpenseById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.spendwise.data.Expense> $completion);
}