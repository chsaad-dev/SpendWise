package com.example.spendwise.utils;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0006\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002JJ\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00120\u000f2\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u0014H\u0086@\u00a2\u0006\u0002\u0010\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0006X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lcom/example/spendwise/utils/PdfExportHelper;", "", "()V", "MARGIN", "", "PAGE_HEIGHT", "", "PAGE_WIDTH", "generateMonthlyReport", "Ljava/io/File;", "context", "Landroid/content/Context;", "monthYearTitle", "", "expenses", "", "Lcom/example/spendwise/data/Expense;", "categoryTotals", "Lcom/example/spendwise/data/CategoryTotal;", "totalIncome", "", "totalExpense", "(Landroid/content/Context;Ljava/lang/String;Ljava/util/List;Ljava/util/List;DDLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public final class PdfExportHelper {
    private static final int PAGE_WIDTH = 595;
    private static final int PAGE_HEIGHT = 842;
    private static final float MARGIN = 50.0F;
    @org.jetbrains.annotations.NotNull()
    public static final com.example.spendwise.utils.PdfExportHelper INSTANCE = null;
    
    private PdfExportHelper() {
        super();
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object generateMonthlyReport(@org.jetbrains.annotations.NotNull()
    android.content.Context context, @org.jetbrains.annotations.NotNull()
    java.lang.String monthYearTitle, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.spendwise.data.Expense> expenses, @org.jetbrains.annotations.NotNull()
    java.util.List<com.example.spendwise.data.CategoryTotal> categoryTotals, double totalIncome, double totalExpense, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.io.File> $completion) {
        return null;
    }
}