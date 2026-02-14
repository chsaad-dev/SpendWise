package com.example.spendwise.data;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0004\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\u000e\u0010\u0018\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001aJ\u000e\u0010\u001b\u001a\u00020\u0019H\u0086@\u00a2\u0006\u0002\u0010\u001aJ\u0016\u0010\u001c\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u0010\u001f\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u0010\"\u001a\u00020\u00192\u0006\u0010#\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010$J\u0014\u0010%\u001a\b\u0012\u0004\u0012\u00020\u00130\u000bH\u0086@\u00a2\u0006\u0002\u0010\u001aJ$\u0010&\u001a\b\u0012\u0004\u0012\u00020\'0\u000b2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020)H\u0086@\u00a2\u0006\u0002\u0010+J\u0018\u0010,\u001a\u0004\u0018\u00010\u00102\u0006\u0010-\u001a\u00020.H\u0086@\u00a2\u0006\u0002\u0010/J\u001a\u00100\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000b0\n2\u0006\u0010\u001d\u001a\u000201J*\u00102\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000b0\n2\u0006\u0010\u001d\u001a\u0002012\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020)J\"\u00103\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000b0\n2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020)J\u001e\u00104\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\n2\u0006\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020)J\u0016\u00105\u001a\u00020\u00192\u0006\u0010\u001d\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u001eJ\u0016\u00106\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u00107\u001a\u00020\u00192\u0006\u0010#\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010$J\u001a\u00108\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000b0\n2\u0006\u00109\u001a\u000201J\u0016\u0010:\u001a\u00020\u00192\u0006\u0010 \u001a\u00020\u0010H\u0086@\u00a2\u0006\u0002\u0010!J\u0016\u0010;\u001a\u00020\u00192\u0006\u0010#\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010$R\u001d\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u001d\u0010\u000f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00100\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000eR\u001d\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u000b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000eR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u0010\u0015\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00160\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000e\u00a8\u0006<"}, d2 = {"Lcom/example/spendwise/data/ExpenseRepository;", "", "expenseDao", "Lcom/example/spendwise/data/ExpenseDao;", "categoryDao", "Lcom/example/spendwise/data/CategoryDao;", "recurringExpenseDao", "Lcom/example/spendwise/data/RecurringExpenseDao;", "(Lcom/example/spendwise/data/ExpenseDao;Lcom/example/spendwise/data/CategoryDao;Lcom/example/spendwise/data/RecurringExpenseDao;)V", "allCategories", "Landroidx/lifecycle/LiveData;", "", "Lcom/example/spendwise/data/Category;", "getAllCategories", "()Landroidx/lifecycle/LiveData;", "allExpenses", "Lcom/example/spendwise/data/Expense;", "getAllExpenses", "allRecurringExpenses", "Lcom/example/spendwise/data/RecurringExpense;", "getAllRecurringExpenses", "totalExpense", "", "getTotalExpense", "deleteAllCategories", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAllExpenses", "deleteCategory", "category", "(Lcom/example/spendwise/data/Category;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteExpense", "expense", "(Lcom/example/spendwise/data/Expense;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteRecurring", "r", "(Lcom/example/spendwise/data/RecurringExpense;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllActiveRecurringSync", "getCategoryTotals", "Lcom/example/spendwise/data/CategoryTotal;", "start", "", "end", "(JJLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getExpenseById", "id", "", "(ILkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getExpensesByCategory", "", "getExpensesByCategoryAndDateRange", "getExpensesByDateRange", "getMonthlyExpense", "insertCategory", "insertExpense", "insertRecurring", "searchExpenses", "query", "updateExpense", "updateRecurring", "app_debug"})
public final class ExpenseRepository {
    @org.jetbrains.annotations.NotNull()
    private final com.example.spendwise.data.ExpenseDao expenseDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.spendwise.data.CategoryDao categoryDao = null;
    @org.jetbrains.annotations.NotNull()
    private final com.example.spendwise.data.RecurringExpenseDao recurringExpenseDao = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> allExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Category>> allCategories = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Double> totalExpense = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.RecurringExpense>> allRecurringExpenses = null;
    
    public ExpenseRepository(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.ExpenseDao expenseDao, @org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.CategoryDao categoryDao, @org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.RecurringExpenseDao recurringExpenseDao) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getAllExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Category>> getAllCategories() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Double> getTotalExpense() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.RecurringExpense>> getAllRecurringExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Double> getMonthlyExpense(long start, long end) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getExpensesByDateRange(long start, long end) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getExpensesByCategory(@org.jetbrains.annotations.NotNull()
    java.lang.String category) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> getExpensesByCategoryAndDateRange(@org.jetbrains.annotations.NotNull()
    java.lang.String category, long start, long end) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getCategoryTotals(long start, long end, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.spendwise.data.CategoryTotal>> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> searchExpenses(@org.jetbrains.annotations.NotNull()
    java.lang.String query) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllExpenses(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getExpenseById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super com.example.spendwise.data.Expense> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertCategory(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteCategory(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Category category, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteAllCategories(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object insertRecurring(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.RecurringExpense r, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateRecurring(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.RecurringExpense r, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object deleteRecurring(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.RecurringExpense r, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object getAllActiveRecurringSync(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<com.example.spendwise.data.RecurringExpense>> $completion) {
        return null;
    }
}