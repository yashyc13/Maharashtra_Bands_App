# Maharashtra Bands Android MVP Setup

## 1. Recommended package structure

```
com.maharashtra.bands
├── data
│   ├── model
│   └── repository
├── di
├── domain
│   ├── model
│   └── usecase
├── presentation
│   ├── main
│   │   ├── MainActivity.kt
│   │   └── MainViewModel.kt
│   ├── bands
│   │   ├── BandsFragment.kt
│   │   ├── BandsViewModel.kt
│   │   └── adapter
│   └── details
│       ├── BandDetailsFragment.kt
│       └── BandDetailsViewModel.kt
├── ui
│   ├── base
│   │   ├── BaseActivity.kt
│   │   └── BaseFragment.kt
│   └── binding
└── util
```

## 2. Gradle dependencies list

**Project-level build.gradle (Kotlin DSL or Groovy):**
- Kotlin Android plugin
- Android Gradle Plugin
- Navigation Safe Args plugin

**Module-level `app/build.gradle`:**

```
// AndroidX core
implementation "androidx.core:core-ktx:1.13.1"
implementation "androidx.appcompat:appcompat:1.7.0"
implementation "com.google.android.material:material:1.12.0"
implementation "androidx.constraintlayout:constraintlayout:2.2.0"

// RecyclerView
implementation "androidx.recyclerview:recyclerview:1.3.2"

// Lifecycle
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.8.4"

// Navigation Component
implementation "androidx.navigation:navigation-fragment-ktx:2.7.7"
implementation "androidx.navigation:navigation-ui-ktx:2.7.7"
```

**SDK config:**
```
minSdk 24
targetSdk 34
compileSdk 34
```

## 3. Base Activity and Fragment setup

**BaseActivity:**
```
abstract class BaseActivity<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> VB
) : AppCompatActivity() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: error("Binding is only valid after onCreate")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
```

**BaseFragment:**
```
abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding ?: error("Binding is only valid between onCreateView and onDestroyView")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = bindingInflater.invoke(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
```

## 4. ViewBinding configuration

**`app/build.gradle`:**
```
android {
    buildFeatures {
        viewBinding true
    }
}
```

## 5. Navigation graph outline

**Single-activity + fragments:**

```
res/navigation/nav_graph.xml

- startDestination: BandsFragment

Destinations:
1) BandsFragment
   - shows RecyclerView list of bands
   - action: to BandDetailsFragment

2) BandDetailsFragment
   - shows band details
```

**Activity layout host:**
```
<androidx.fragment.app.FragmentContainerView
    android:id="@+id/nav_host_fragment"
    android:name="androidx.navigation.fragment.NavHostFragment"
    app:navGraph="@navigation/nav_graph"
    app:defaultNavHost="true" />
```

## 6. Firebase setup (Firestore, Storage, Auth)

### 6.1 Firebase console setup steps

1. **Create project** in Firebase Console and add an Android app.
2. **Register app** with the package name (e.g., `com.maharashtra.bands`) and download `google-services.json`.
3. **Add `google-services.json`** to the app module (`app/`).
4. **Enable Authentication**:
   - Firebase Console → Authentication → Sign-in method.
   - Enable **Email/Password**.
5. **Enable Firestore**:
   - Firebase Console → Firestore Database → Create database.
   - Start in **production mode** (recommended) and add rules later.
6. **Enable Storage**:
   - Firebase Console → Storage → Get started.
   - Configure rules for admin-only access later.

### 6.2 Required Gradle dependencies

**Project-level:**
```
classpath "com.google.gms:google-services:4.4.2"
```

**Module-level `app/build.gradle`:**
```
// Firebase BoM
implementation platform("com.google.firebase:firebase-bom:33.2.0")

// Firestore
implementation "com.google.firebase:firebase-firestore-ktx"

// Storage
implementation "com.google.firebase:firebase-storage-ktx"

// Authentication
implementation "com.google.firebase:firebase-auth-ktx"
```

Apply plugin in app module:
```
apply plugin: "com.google.gms.google-services"
```

### 6.3 Firestore initialization in Application class

```
class MaharashtraBandsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        FirebaseFirestore.getInstance().firestoreSettings =
            FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
    }
}
```

Ensure the Application class is registered:
```
<application
    android:name=".MaharashtraBandsApp"
    ... >
</application>
```

### 6.4 Firebase Storage configuration

Initialization is automatic once `google-services.json` is present. Access the singleton when needed:
```
val storage = FirebaseStorage.getInstance()
val storageRef = storage.reference.child("bands")
```

### 6.5 Enable Firestore offline caching

Offline persistence is enabled via `FirestoreSettings`:
```
FirebaseFirestore.getInstance().firestoreSettings =
    FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()
```
