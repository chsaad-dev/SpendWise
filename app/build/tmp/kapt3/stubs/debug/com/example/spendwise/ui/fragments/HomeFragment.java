package com.example.spendwise.ui.fragments;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000n\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0005\u00a2\u0006\u0002\u0010\u0002J&\u0010\u0013\u001a\u0004\u0018\u00010\u00142\u0006\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\u001a\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u00142\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH\u0016J\u0010\u0010\u001e\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 H\u0002J\u0010\u0010!\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\"H\u0002J\b\u0010#\u001a\u00020\u001cH\u0002J,\u0010$\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020 2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020&0\u00062\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u0006H\u0002J,\u0010)\u001a\u00020\u001c2\u0006\u0010\u001f\u001a\u00020\"2\f\u0010%\u001a\b\u0012\u0004\u0012\u00020&0\u00062\f\u0010\'\u001a\b\u0012\u0004\u0012\u00020(0\u0006H\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082.\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0016\u0010\b\u001a\n \n*\u0004\u0018\u00010\t0\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u000b\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u0006X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010\u00a8\u0006*"}, d2 = {"Lcom/example/spendwise/ui/fragments/HomeFragment;", "Landroidx/fragment/app/Fragment;", "()V", "adapter", "Lcom/example/spendwise/ui/fragments/ExpenseAdapter;", "currentCategories", "", "Lcom/example/spendwise/data/Category;", "currentMonth", "Ljava/util/Calendar;", "kotlin.jvm.PlatformType", "currentTotals", "Lcom/example/spendwise/data/CategoryTotal;", "viewModel", "Lcom/example/spendwise/ui/viewmodel/MainViewModel;", "getViewModel", "()Lcom/example/spendwise/ui/viewmodel/MainViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "", "view", "setupChart", "chart", "Lcom/github/mikephil/charting/charts/HorizontalBarChart;", "setupPieChart", "Lcom/github/mikephil/charting/charts/PieChart;", "updateBudgetEnvelopes", "updateChart", "labels", "", "values", "", "updatePieChart", "app_debug"})
public final class HomeFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    private com.example.spendwise.ui.fragments.ExpenseAdapter adapter;
    private final java.util.Calendar currentMonth = null;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<com.example.spendwise.data.Category> currentCategories;
    @org.jetbrains.annotations.Nullable()
    private java.util.List<com.example.spendwise.data.CategoryTotal> currentTotals;
    
    public HomeFragment() {
        super();
    }
    
    private final com.example.spendwise.ui.viewmodel.MainViewModel getViewModel() {
        return null;
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.Nullable()
    public android.view.View onCreateView(@org.jetbrains.annotations.NotNull()
    android.view.LayoutInflater inflater, @org.jetbrains.annotations.Nullable()
    android.view.ViewGroup container, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
        return null;
    }
    
    @java.lang.Override()
    public void onViewCreated(@org.jetbrains.annotations.NotNull()
    android.view.View view, @org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    private final void updateBudgetEnvelopes() {
    }
    
    private final void setupChart(com.github.mikephil.charting.charts.HorizontalBarChart chart) {
    }
    
    private final void updateChart(com.github.mikephil.charting.charts.HorizontalBarChart chart, java.util.List<java.lang.String> labels, java.util.List<java.lang.Float> values) {
    }
    
    private final void setupPieChart(com.github.mikephil.charting.charts.PieChart chart) {
    }
    
    private final void updatePieChart(com.github.mikephil.charting.charts.PieChart chart, java.util.List<java.lang.String> labels, java.util.List<java.lang.Float> values) {
    }
}