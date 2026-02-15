package com.example.spendwise.ui.fragments;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\u0018B\u0005\u00a2\u0006\u0002\u0010\u0002J&\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\u001a\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\n2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0016J\b\u0010\u0014\u001a\u00020\u0012H\u0002J\u0010\u0010\u0015\u001a\u00020\u00122\u0006\u0010\u0016\u001a\u00020\u0017H\u0002R\u001b\u0010\u0003\u001a\u00020\u00048BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0005\u0010\u0006\u00a8\u0006\u0019"}, d2 = {"Lcom/example/spendwise/ui/fragments/SavingsGoalFragment;", "Landroidx/fragment/app/Fragment;", "()V", "viewModel", "Lcom/example/spendwise/ui/viewmodel/MainViewModel;", "getViewModel", "()Lcom/example/spendwise/ui/viewmodel/MainViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "onCreateView", "Landroid/view/View;", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "savedInstanceState", "Landroid/os/Bundle;", "onViewCreated", "", "view", "showAddGoalDialog", "showAddSavingsDialog", "goal", "Lcom/example/spendwise/data/SavingsGoal;", "GoalAdapter", "app_debug"})
public final class SavingsGoalFragment extends androidx.fragment.app.Fragment {
    @org.jetbrains.annotations.NotNull()
    private final kotlin.Lazy viewModel$delegate = null;
    
    public SavingsGoalFragment() {
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
    
    private final void showAddGoalDialog() {
    }
    
    private final void showAddSavingsDialog(com.example.spendwise.data.SavingsGoal goal) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001bB-\u0012\u0012\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u0012\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\u0010\bJ\b\u0010\u0010\u001a\u00020\u0011H\u0016J\u0018\u0010\u0012\u001a\u00020\u00062\u0006\u0010\u0013\u001a\u00020\u00022\u0006\u0010\u0014\u001a\u00020\u0011H\u0016J\u0018\u0010\u0015\u001a\u00020\u00022\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0011H\u0016J\u0014\u0010\u0019\u001a\u00020\u00062\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fR\u0016\u0010\t\u001a\n \u000b*\u0004\u0018\u00010\n0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\rX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2 = {"Lcom/example/spendwise/ui/fragments/SavingsGoalFragment$GoalAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/spendwise/ui/fragments/SavingsGoalFragment$GoalAdapter$VH;", "onAddSavings", "Lkotlin/Function1;", "Lcom/example/spendwise/data/SavingsGoal;", "", "onDelete", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "currencyFormat", "Ljava/text/NumberFormat;", "kotlin.jvm.PlatformType", "dateFormat", "Ljava/text/SimpleDateFormat;", "goals", "", "getItemCount", "", "onBindViewHolder", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "submitList", "list", "VH", "app_debug"})
    public static final class GoalAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.spendwise.ui.fragments.SavingsGoalFragment.GoalAdapter.VH> {
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<com.example.spendwise.data.SavingsGoal, kotlin.Unit> onAddSavings = null;
        @org.jetbrains.annotations.NotNull()
        private final kotlin.jvm.functions.Function1<com.example.spendwise.data.SavingsGoal, kotlin.Unit> onDelete = null;
        @org.jetbrains.annotations.NotNull()
        private java.util.List<com.example.spendwise.data.SavingsGoal> goals;
        private final java.text.NumberFormat currencyFormat = null;
        @org.jetbrains.annotations.NotNull()
        private final java.text.SimpleDateFormat dateFormat = null;
        
        public GoalAdapter(@org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super com.example.spendwise.data.SavingsGoal, kotlin.Unit> onAddSavings, @org.jetbrains.annotations.NotNull()
        kotlin.jvm.functions.Function1<? super com.example.spendwise.data.SavingsGoal, kotlin.Unit> onDelete) {
            super();
        }
        
        public final void submitList(@org.jetbrains.annotations.NotNull()
        java.util.List<com.example.spendwise.data.SavingsGoal> list) {
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public com.example.spendwise.ui.fragments.SavingsGoalFragment.GoalAdapter.VH onCreateViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.ViewGroup parent, int viewType) {
            return null;
        }
        
        @java.lang.Override()
        public int getItemCount() {
            return 0;
        }
        
        @java.lang.Override()
        public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
        com.example.spendwise.ui.fragments.SavingsGoalFragment.GoalAdapter.VH holder, int position) {
        }
        
        @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\t\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0013\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0012R\u0011\u0010\u0015\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0011\u0010\u0017\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0012\u00a8\u0006\u0019"}, d2 = {"Lcom/example/spendwise/ui/fragments/SavingsGoalFragment$GoalAdapter$VH;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "btnAddSavings", "Lcom/google/android/material/button/MaterialButton;", "getBtnAddSavings", "()Lcom/google/android/material/button/MaterialButton;", "btnDelete", "getBtnDelete", "progressGoal", "Lcom/google/android/material/progressindicator/LinearProgressIndicator;", "getProgressGoal", "()Lcom/google/android/material/progressindicator/LinearProgressIndicator;", "tvDeadline", "Landroid/widget/TextView;", "getTvDeadline", "()Landroid/widget/TextView;", "tvName", "getTvName", "tvSaved", "getTvSaved", "tvTarget", "getTvTarget", "app_debug"})
        public static final class VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvName = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvDeadline = null;
            @org.jetbrains.annotations.NotNull()
            private final com.google.android.material.progressindicator.LinearProgressIndicator progressGoal = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvSaved = null;
            @org.jetbrains.annotations.NotNull()
            private final android.widget.TextView tvTarget = null;
            @org.jetbrains.annotations.NotNull()
            private final com.google.android.material.button.MaterialButton btnAddSavings = null;
            @org.jetbrains.annotations.NotNull()
            private final com.google.android.material.button.MaterialButton btnDelete = null;
            
            public VH(@org.jetbrains.annotations.NotNull()
            android.view.View view) {
                super(null);
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvName() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvDeadline() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.google.android.material.progressindicator.LinearProgressIndicator getProgressGoal() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvSaved() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final android.widget.TextView getTvTarget() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.google.android.material.button.MaterialButton getBtnAddSavings() {
                return null;
            }
            
            @org.jetbrains.annotations.NotNull()
            public final com.google.android.material.button.MaterialButton getBtnDelete() {
                return null;
            }
        }
    }
}