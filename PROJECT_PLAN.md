# Happy Sappy - Project Plan

## 📁 Full Folder Structure

```
HappySappy/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/happysappy/app/
│   │   │   │   ├── data/
│   │   │   │   │   ├── entity/
│   │   │   │   │   │   └── ExpenseEntity.kt
│   │   │   │   │   ├── dao/
│   │   │   │   │   │   └── ExpenseDao.kt
│   │   │   │   │   ├── database/
│   │   │   │   │   │   └── AppDatabase.kt
│   │   │   │   │   └── repository/
│   │   │   │   │       └── ExpenseRepository.kt
│   │   │   │   ├── ui/
│   │   │   │   │   ├── viewmodel/
│   │   │   │   │   │   └── ExpenseViewModel.kt
│   │   │   │   │   ├── activity/
│   │   │   │   │   │   ├── MainActivity.kt
│   │   │   │   │   │   ├── AddExpenseActivity.kt
│   │   │   │   │   │   ├── RecordsActivity.kt
│   │   │   │   │   │   └── SettingsActivity.kt
│   │   │   │   │   └── adapter/
│   │   │   │   │       └── ExpenseAdapter.kt
│   │   │   │   └── util/
│   │   │   │       ├── NotificationHelper.kt
│   │   │   │       ├── ExpenseWorker.kt
│   │   │   │       ├── NotificationScheduler.kt
│   │   │   │       ├── GamificationManager.kt
│   │   │   │       ├── Achievement.kt
│   │   │   │       └── PreferenceManager.kt
│   │   │   ├── res/
│   │   │   │   ├── layout/
│   │   │   │   │   ├── activity_main.xml
│   │   │   │   │   ├── activity_add_expense.xml
│   │   │   │   │   ├── activity_records.xml
│   │   │   │   │   ├── activity_settings.xml
│   │   │   │   │   └── item_expense.xml
│   │   │   │   ├── values/
│   │   │   │   │   ├── colors.xml
│   │   │   │   │   ├── strings.xml
│   │   │   │   │   └── themes.xml
│   │   │   │   ├── drawable/
│   │   │   │   │   ├── bg_gradient_rainbow.xml
│   │   │   │   │   ├── ic_expense.xml
│   │   │   │   │   ├── ic_records.xml
│   │   │   │   │   ├── ic_settings.xml
│   │   │   │   │   └── progress_bar_rainbow.xml
│   │   │   │   ├── menu/
│   │   │   │   │   └── bottom_navigation_menu.xml
│   │   │   │   ├── mipmap-anydpi-v26/
│   │   │   │   │   └── ic_launcher.xml
│   │   │   │   └── xml/
│   │   │   │       └── backup_rules.xml
│   │   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 📋 Complete File List

### Data Layer
1. `ExpenseEntity.kt` - Room entity for expense data
2. `ExpenseDao.kt` - Data Access Object for database operations
3. `AppDatabase.kt` - Room database configuration

### Repository Layer
4. `ExpenseRepository.kt` - Repository pattern for data access abstraction

### ViewModel Layer
5. `ExpenseViewModel.kt` - ViewModel for UI state management

### UI Layer - Activities
6. `MainActivity.kt` - Dashboard/Home screen
7. `AddExpenseActivity.kt` - Add new expense screen
8. `RecordsActivity.kt` - View all expenses screen
9. `SettingsActivity.kt` - App settings screen

### UI Layer - Adapter
10. `ExpenseAdapter.kt` - RecyclerView adapter for expense list

### Utility Classes
11. `NotificationHelper.kt` - Notification creation and management
12. `ExpenseWorker.kt` - WorkManager worker for scheduled notifications
13. `NotificationScheduler.kt` - Schedule notifications based on frequency
14. `GamificationManager.kt` - Handle gamification logic
15. `Achievement.kt` - Achievement data model
16. `PreferenceManager.kt` - SharedPreferences wrapper for settings

### Layout Files (XML)
17. `activity_main.xml` - Dashboard layout
18. `activity_add_expense.xml` - Add expense form layout
19. `activity_records.xml` - Records list layout
20. `activity_settings.xml` - Settings layout
21. `item_expense.xml` - Individual expense item layout

### Resource Files
22. `colors.xml` - Rainbow-themed color palette
23. `strings.xml` - String resources
24. `themes.xml` - Light/Dark theme definitions
25. `bg_gradient_rainbow.xml` - Rainbow gradient drawable
26. `ic_expense.xml` - Expense icon
27. `ic_records.xml` - Records icon
28. `ic_settings.xml` - Settings icon
29. `progress_bar_rainbow.xml` - Rainbow progress bar
30. `bottom_navigation_menu.xml` - Bottom navigation menu

## 🔄 MVVM Data Flow

```
┌─────────────────────────────────────────────────────────────┐
│                         UI Layer                             │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Activities (MainActivity, AddExpenseActivity, etc.)     ││
│  │ - Observe LiveData/StateFlow from ViewModel             ││
│  │ - Display UI and handle user interactions               ││
│  └─────────────────────────────────────────────────────────┘│
│                           ▲                                  │
│                           │ observes                         │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ ViewModel (ExpenseViewModel)                            ││
│  │ - Holds UI state                                        ││
│  │ - Processes user actions                                ││
│  │ - Exposes LiveData/StateFlow                            ││
│  │ - Business logic for filtering, sorting                 ││
│  └─────────────────────────────────────────────────────────┘│
│                           ▲                                  │
│                           │ uses                             │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Repository (ExpenseRepository)                          ││
│  │ - Single source of truth for data                       ││
│  │ - Abstracts data sources                                ││
│  │ - Handles data operations                               ││
│  └─────────────────────────────────────────────────────────┘│
│                           ▲                                  │
│                           │ uses                             │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐│
│  │ Database Layer (Room)                                   ││
│  │ - AppDatabase                                           ││
│  │ - ExpenseDao                                            ││
│  │ - ExpenseEntity                                         ││
│  └─────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────┘

Data Flow:
1. User interacts with UI (Activity)
2. Activity calls ViewModel method
3. ViewModel calls Repository method
4. Repository executes DAO operation
5. DAO interacts with Room Database
6. Data flows back up through Repository → ViewModel → UI
7. UI observes LiveData/StateFlow and updates automatically
```

## 📊 Data Model Design

### ExpenseEntity
```kotlin
@Entity(tableName = "expenses")
data class ExpenseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountSpent: Double,
    val category: String,
    val date: Long, // Timestamp
    val frequency: String, // "Daily", "Weekly", "Monthly", "Quarterly", "Annually"
    val note: String? = null
)
```

### Category Enum (for reference)
```kotlin
enum class ExpenseCategory {
    FOOD,           // 🍔
    TRANSPORT,      // 🚗
    SHOPPING,       // 🛍️
    ENTERTAINMENT,  // 🎬
    BILLS,          // 📄
    HEALTH,         // 🏥
    EDUCATION,      // 📚
    OTHER           // 📝
}
```

### Frequency Enum (for reference)
```kotlin
enum class ExpenseFrequency {
    DAILY,      // Every day
    WEEKLY,     // Every week
    MONTHLY,    // Every month
    QUARTERLY,  // Every 3 months
    ANNUALLY    // Every year
}
```

### Achievement Model
```kotlin
data class Achievement(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val xpReward: Int,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long? = null
)
```

### User Progress Model
```kotlin
data class UserProgress(
    val noSpendStreak: Int = 0, // Days
    val disciplineScore: Int = 100, // 0-100
    val totalXP: Int = 0,
    val level: Int = 1,
    val achievements: List<Achievement> = emptyList(),
    val lastExpenseDate: Long? = null
)
```

## 🎨 Rainbow Color System

### Primary Rainbow Colors
- **Red**: #FF4444
- **Orange**: #FF6B35
- **Yellow**: #FF9D00 (specified in requirements)
- **Green**: #4CAF50
- **Blue**: #2196F3
- **Purple**: #9C27B0

### Usage Strategy
- **Buttons**: Gradient from orange to yellow
- **Progress Bars**: Full rainbow gradient
- **Highlights**: Color-coded by category
- **Charts**: Rainbow palette for different data points
- **Background**: White (light) / Black (dark)
- **Text**: High contrast (black on light, white on dark)

## 🎮 Gamification System

### XP System
- Log expense: +10 XP
- Daily logging streak: +5 XP bonus per day
- Weekly consistency: +50 XP bonus
- Monthly discipline: +200 XP bonus

### Levels
- Level 1: 0-100 XP (Beginner)
- Level 2: 101-300 XP (Tracker)
- Level 3: 301-600 XP (Saver)
- Level 4: 601-1000 XP (Master)
- Level 5: 1001+ XP (Legend)

### Achievements
1. **First Step** - Log your first expense
2. **Week Warrior** - 7-day logging streak
3. **Budget Master** - Stay under budget for a week
4. **Frequency King** - Use all frequency types
5. **Category Explorer** - Use all categories
6. **Monthly Master** - Complete month of tracking

### Streaks
- **No Spend Streak**: Days without unnecessary spending
- **Logging Streak**: Consecutive days of logging expenses

## 🔔 Notification System

### Notification Types
1. **Daily Reminder** - Log today's expenses
2. **Weekly Summary** - Review weekly spending
3. **Monthly Report** - Monthly expense overview
4. **Streak Alert** - Maintain your streaks
5. **Achievement Unlocked** - Celebrate milestones

### Scheduling
- Based on user-selected frequency
- Configurable in settings
- Uses WorkManager for reliable scheduling
- Respects user's notification preferences

## ⚙️ Settings

### Theme
- Light Mode (default)
- Dark Mode
- Auto (follow system)

### Notifications
- Enable/Disable all notifications
- Daily reminder time
- Weekly summary day
- Monthly report day

### Data
- Export data
- Clear all data
- Backup/Restore

### About
- App version
- Privacy policy
- Terms of service

---

**Ready to proceed to Step 2 - Database Layer implementation.**