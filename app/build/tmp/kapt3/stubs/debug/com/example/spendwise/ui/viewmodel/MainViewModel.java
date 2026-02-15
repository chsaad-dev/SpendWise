package com.example.spendwise.ui.viewmodel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000\u00b0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\b\u00106\u001a\u000207H\u0002J\u0006\u00108\u001a\u000209J\u0006\u0010:\u001a\u000209J\u000e\u0010;\u001a\u0002092\u0006\u0010<\u001a\u00020\u0013J\u000e\u0010=\u001a\u0002092\u0006\u0010>\u001a\u00020\u0017J\u000e\u0010?\u001a\u0002092\u0006\u0010@\u001a\u00020\u001dJ\u000e\u0010A\u001a\u0002092\u0006\u0010B\u001a\u00020\u001aJ\u000e\u0010C\u001a\u0002092\u0006\u0010D\u001a\u00020 J\u000e\u0010E\u001a\u0002092\u0006\u0010F\u001a\u00020GJ\u000e\u0010H\u001a\u0002092\u0006\u0010>\u001a\u00020\u0017J\u000e\u0010I\u001a\u00020\u000f2\u0006\u0010J\u001a\u00020\rJ\u0006\u0010K\u001a\u00020LJ$\u0010M\u001a\u0002072\u0006\u0010N\u001a\u00020O2\u0014\u0010P\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u0017\u0012\u0004\u0012\u0002070QJ\u001e\u0010R\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u00122\u0006\u0010S\u001a\u00020\u000b2\u0006\u0010T\u001a\u00020\u000bJ$\u0010U\u001a\u0002072\u0006\u0010V\u001a\u00020O2\u0014\u0010P\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010 \u0012\u0004\u0012\u0002070QJ\u001a\u0010W\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020G0\u00070\u00122\u0006\u0010X\u001a\u00020OJ\u000e\u0010Y\u001a\u0002092\u0006\u0010<\u001a\u00020\u0013J\u000e\u0010Z\u001a\u0002092\u0006\u0010>\u001a\u00020\u0017J\u000e\u0010[\u001a\u0002092\u0006\u0010@\u001a\u00020\u001dJ\u000e\u0010\\\u001a\u0002092\u0006\u0010B\u001a\u00020\u001aJ\"\u0010]\u001a\u0002072\u0006\u0010D\u001a\u00020 2\u0012\u0010P\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u0002070QJ\u000e\u0010^\u001a\u0002092\u0006\u0010F\u001a\u00020GJ\u0018\u0010_\u001a\u0002072\u0006\u0010S\u001a\u00020\u000b2\u0006\u0010T\u001a\u00020\u000bH\u0002J\b\u0010`\u001a\u000207H\u0002J\u0010\u0010a\u001a\u0002072\b\u0010<\u001a\u0004\u0018\u00010\u000fJ\u0016\u0010b\u001a\u0002072\u0006\u0010S\u001a\u00020\u000b2\u0006\u0010T\u001a\u00020\u000bJ\u000e\u0010c\u001a\u0002072\u0006\u0010d\u001a\u00020eJ\u0010\u0010f\u001a\u0002072\b\u0010g\u001a\u0004\u0018\u00010\u000fJ\u000e\u0010h\u001a\u0002092\u0006\u0010>\u001a\u00020\u0017J\u000e\u0010i\u001a\u0002092\u0006\u0010@\u001a\u00020\u001dJ\u000e\u0010j\u001a\u0002092\u0006\u0010F\u001a\u00020GR\u001a\u0010\u0005\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R \u0010\t\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0\n0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\r0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000e\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0010\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00130\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u001d\u0010\u0016\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0015R\u001d\u0010\u0019\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001a0\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u001d\u0010\u001c\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u001d\u0010\u001f\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020 0\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\u0015R\u001d\u0010\"\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\b0\u00070\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u0015R\u0011\u0010$\u001a\u00020%\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\'R#\u0010(\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u000b0\n0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\u0015R\u001d\u0010*\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00170\u00070+\u00a2\u0006\b\n\u0000\u001a\u0004\b,\u0010-R\u0017\u0010.\u001a\b\u0012\u0004\u0012\u00020\r0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u0010\u0015R\u000e\u00100\u001a\u000201X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0019\u00102\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u000f0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b3\u0010\u0015R\u0019\u00104\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\r0\u0012\u00a2\u0006\b\n\u0000\u001a\u0004\b5\u0010\u0015\u00a8\u0006k"}, d2 = {"Lcom/example/spendwise/ui/viewmodel/MainViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "(Landroid/app/Application;)V", "_categoryTotals", "Landroidx/lifecycle/MutableLiveData;", "", "Lcom/example/spendwise/data/CategoryTotal;", "_filterDateRange", "Lkotlin/Pair;", "", "_monthlyTotal", "", "_searchQuery", "", "_selectedCategory", "allCategories", "Landroidx/lifecycle/LiveData;", "Lcom/example/spendwise/data/Category;", "getAllCategories", "()Landroidx/lifecycle/LiveData;", "allExpenses", "Lcom/example/spendwise/data/Expense;", "getAllExpenses", "allRecurringExpenses", "Lcom/example/spendwise/data/RecurringExpense;", "getAllRecurringExpenses", "allSavingsGoals", "Lcom/example/spendwise/data/SavingsGoal;", "getAllSavingsGoals", "allSplitGroups", "Lcom/example/spendwise/data/SplitGroup;", "getAllSplitGroups", "categoryTotals", "getCategoryTotals", "currencyFormat", "Ljava/text/NumberFormat;", "getCurrencyFormat", "()Ljava/text/NumberFormat;", "filterDateRange", "getFilterDateRange", "filteredExpenses", "Landroidx/lifecycle/MediatorLiveData;", "getFilteredExpenses", "()Landroidx/lifecycle/MediatorLiveData;", "monthlyTotal", "getMonthlyTotal", "repository", "Lcom/example/spendwise/data/ExpenseRepository;", "selectedCategory", "getSelectedCategory", "totalExpense", "getTotalExpense", "applyFilters", "", "clearAllCategories", "Lkotlinx/coroutines/Job;", "clearAllData", "deleteCategory", "category", "deleteExpense", "expense", "deleteGoal", "goal", "deleteRecurring", "r", "deleteSplitGroup", "group", "deleteSplitMember", "member", "Lcom/example/spendwise/data/SplitMember;", "duplicateExpense", "formatCurrency", "amount", "getBudgetLimit", "", "getExpenseById", "id", "", "callback", "Lkotlin/Function1;", "getMonthlyExpense", "start", "end", "getSplitGroupByExpenseId", "expenseId", "getSplitMembersByGroupId", "groupId", "insertCategory", "insertExpense", "insertGoal", "insertRecurring", "insertSplitGroup", "insertSplitMember", "loadCategoryTotals", "processRecurringExpenses", "setCategoryFilter", "setDateRangeFilter", "setMonthFilter", "calendar", "Ljava/util/Calendar;", "setSearchQuery", "query", "updateExpense", "updateGoal", "updateSplitMember", "app_debug"})
public final class MainViewModel extends androidx.lifecycle.AndroidViewModel {
    @org.jetbrains.annotations.NotNull()
    private final com.example.spendwise.data.ExpenseRepository repository = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Expense>> allExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.Category>> allCategories = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Double> totalExpense = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.RecurringExpense>> allRecurringExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SavingsGoal>> allSavingsGoals = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SplitGroup>> allSplitGroups = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<kotlin.Pair<java.lang.Long, java.lang.Long>> _filterDateRange = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<kotlin.Pair<java.lang.Long, java.lang.Long>> filterDateRange = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.String> _selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.String> selectedCategory = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.String> _searchQuery = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MediatorLiveData<java.util.List<com.example.spendwise.data.Expense>> filteredExpenses = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.util.List<com.example.spendwise.data.CategoryTotal>> _categoryTotals = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.CategoryTotal>> categoryTotals = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.MutableLiveData<java.lang.Double> _monthlyTotal = null;
    @org.jetbrains.annotations.NotNull()
    private final androidx.lifecycle.LiveData<java.lang.Double> monthlyTotal = null;
    @org.jetbrains.annotations.NotNull()
    private final java.text.NumberFormat currencyFormat = null;
    
    public MainViewModel(@org.jetbrains.annotations.NotNull()
    android.app.Application application) {
        super(null);
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
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SavingsGoal>> getAllSavingsGoals() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SplitGroup>> getAllSplitGroups() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<kotlin.Pair<java.lang.Long, java.lang.Long>> getFilterDateRange() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.String> getSelectedCategory() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.MediatorLiveData<java.util.List<com.example.spendwise.data.Expense>> getFilteredExpenses() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.CategoryTotal>> getCategoryTotals() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Double> getMonthlyTotal() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.text.NumberFormat getCurrencyFormat() {
        return null;
    }
    
    private final void applyFilters() {
    }
    
    private final void loadCategoryTotals(long start, long end) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatCurrency(double amount) {
        return null;
    }
    
    public final void setSearchQuery(@org.jetbrains.annotations.Nullable()
    java.lang.String query) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job updateExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertCategory(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Category category) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteCategory(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Category category) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job clearAllData() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job clearAllCategories() {
        return null;
    }
    
    public final void getExpenseById(int id, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.spendwise.data.Expense, kotlin.Unit> callback) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertRecurring(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.RecurringExpense r) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteRecurring(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.RecurringExpense r) {
        return null;
    }
    
    private final void processRecurringExpenses() {
    }
    
    public final void setMonthFilter(@org.jetbrains.annotations.NotNull()
    java.util.Calendar calendar) {
    }
    
    public final void setDateRangeFilter(long start, long end) {
    }
    
    public final void setCategoryFilter(@org.jetbrains.annotations.Nullable()
    java.lang.String category) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.lang.Double> getMonthlyExpense(long start, long end) {
        return null;
    }
    
    public final float getBudgetLimit() {
        return 0.0F;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertGoal(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SavingsGoal goal) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job updateGoal(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SavingsGoal goal) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteGoal(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SavingsGoal goal) {
        return null;
    }
    
    public final void insertSplitGroup(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitGroup group, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super java.lang.Long, kotlin.Unit> callback) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteSplitGroup(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitGroup group) {
        return null;
    }
    
    public final void getSplitGroupByExpenseId(int expenseId, @org.jetbrains.annotations.NotNull()
    kotlin.jvm.functions.Function1<? super com.example.spendwise.data.SplitGroup, kotlin.Unit> callback) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.lifecycle.LiveData<java.util.List<com.example.spendwise.data.SplitMember>> getSplitMembersByGroupId(int groupId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job insertSplitMember(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitMember member) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job updateSplitMember(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitMember member) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job deleteSplitMember(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.SplitMember member) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.Job duplicateExpense(@org.jetbrains.annotations.NotNull()
    com.example.spendwise.data.Expense expense) {
        return null;
    }
}