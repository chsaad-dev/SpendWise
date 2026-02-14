# SpendWise App Data Flow

## MVVM Architecture with Room

The app follows the **Model-View-ViewModel (MVVM)** architecture pattern to separate UI logic from data management.

### Data Flow Steps (UI → Database)

1.  **User Action**: The user enters an amount, category, date, and note in the `AddExpenseFragment` and clicks "Save".
2.  **Fragment**: The `AddExpenseFragment` collects the input data and calls `mainViewModel.insertExpense(expense)`.
3.  **ViewModel**: The `MainViewModel` (which is `viewModelScope` aware) launches a coroutine and calls `repository.insertExpense(expense)`.
4.  **Repository**: The `ExpenseRepository` acts as a clean API for data access. It calls `expenseDao.insertExpense(expense)`.
5.  **DAO (Data Access Object)**: The `ExpenseDao` executes the actual SQL `INSERT` query into the Room Database.
6.  **Database**: The `AppDatabase` (Room) stores the data in the SQLite file on the device.

### Data Flow Steps (Database → UI)

1.  **Observation**: In `HomeFragment`, we observe `viewModel.allExpenses` (a `LiveData<List<Expense>>`).
2.  **ViewModel**: `MainViewModel` exposes `allExpenses` which comes directly from `repository.allExpenses`.
3.  **Repository**: `ExpenseRepository` gets this LiveData from `expenseDao.getAllExpenses()`.
4.  **DAO & Room**: Room automatically triggers the LiveData update whenever the underlying database table changes (e.g., after the insert above).
5.  **UI Update**: When the LiveData emits a new list, the Observer in `HomeFragment` receives it and calls `adapter.submitList(expenses)`.
6.  **RecyclerView**: The `ExpenseAdapter` calculates the diff and efficiently updates the list on the screen.

### Summary of Components

*   **View (UI)**: `Activity`, `Fragments`, `XML Layouts`. Displays data and captures user input.
*   **ViewModel**: `MainViewModel`. Holds UI state and business logic. Survives configuration changes.
*   **Model (Data)**:
    *   **Repository**: `ExpenseRepository`. Mediator between ViewModel and Data Sources.
    *   **Room Database**: `AppDatabase`, `ExpenseDao`, `CategoryDao`, `Entities`. The single source of truth.
