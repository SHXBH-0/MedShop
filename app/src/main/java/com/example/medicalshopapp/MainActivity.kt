package com.example.medicalshopapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddBox
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalPharmacy
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.ShoppingCartCheckout
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Vaccines
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// --- 1. LOCALIZATION SYSTEM ---

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी"),
    MARATHI("mr", "मराठी"),
    TAMIL("ta", "தமிழ்")
}

object Strings {
    private val dictionary = mapOf(
        "app_name" to mapOf(AppLanguage.ENGLISH to "MedFlow Pro", AppLanguage.HINDI to "मेडफ्लो प्रो", AppLanguage.MARATHI to "मेडफ्लो प्रो", AppLanguage.TAMIL to "மெட்ஃப்ளோ ப்ரோ"),
        "onboarding_welcome" to mapOf(AppLanguage.ENGLISH to "Manage Pharmacy Smartly", AppLanguage.HINDI to "फार्मेसी का स्मार्ट प्रबंधन", AppLanguage.MARATHI to "फार्मसीचे स्मार्ट व्यवस्थापन", AppLanguage.TAMIL to "மருந்தகத்தை நிர்வகிக்கவும்"),
        "onboarding_tagline" to mapOf(AppLanguage.ENGLISH to "Billing, Inventory & Dealers in one place.", AppLanguage.HINDI to "बिलिंग, इन्वेंटरी और विक्रेता एक ही जगह।", AppLanguage.MARATHI to "बिलिंग, साठा आणि विक्रेते एकाच ठिकाणी.", AppLanguage.TAMIL to "பில்லிங் மற்றும் சரக்கு மேலாண்மை."),
        "get_started" to mapOf(AppLanguage.ENGLISH to "Get Started", AppLanguage.HINDI to "शुरू करें", AppLanguage.MARATHI to "सुरु करा", AppLanguage.TAMIL to "தொடங்குங்கள்"),
        "login_action" to mapOf(AppLanguage.ENGLISH to "Log In", AppLanguage.HINDI to "लॉगिन", AppLanguage.MARATHI to "लॉगिन", AppLanguage.TAMIL to "உள்நுழைய"),
        "login_title" to mapOf(AppLanguage.ENGLISH to "Welcome Back", AppLanguage.HINDI to "स्वागत हे", AppLanguage.MARATHI to "स्वागत आहे", AppLanguage.TAMIL to "வரவேற்கிறோம்"),
        "login_subtitle" to mapOf(AppLanguage.ENGLISH to "Sign in to manage your pharmacy", AppLanguage.HINDI to "अपनी फार्मेसी का प्रबंधन करने के लिए साइन इन करें", AppLanguage.MARATHI to "तुमची फार्मसी व्यवस्थापित करण्यासाठी साइन इन करा", AppLanguage.TAMIL to "உங்கள் மருந்தகத்தை நிர்வகிக்க உள்நுழையவும்"),
        "signup_title" to mapOf(AppLanguage.ENGLISH to "Create Account", AppLanguage.HINDI to "खाता बनाएं", AppLanguage.MARATHI to "खाते तयार करा", AppLanguage.TAMIL to "கணக்கை உருவாக்கு"),
        "dashboard" to mapOf(AppLanguage.ENGLISH to "Dashboard", AppLanguage.HINDI to "डैशबोर्ड", AppLanguage.MARATHI to "डॅशबोर्ड", AppLanguage.TAMIL to "முகப்பு"),
        "inventory" to mapOf(AppLanguage.ENGLISH to "Inventory", AppLanguage.HINDI to "इन्वेंटरी", AppLanguage.MARATHI to "साठा", AppLanguage.TAMIL to "சரக்கு"),
        "add_new" to mapOf(AppLanguage.ENGLISH to "Add Stock", AppLanguage.HINDI to "स्टॉक जोड़ें", AppLanguage.MARATHI to "स्टॉक जोडा", AppLanguage.TAMIL to "சரக்கு சேர்"),
        "dealers" to mapOf(AppLanguage.ENGLISH to "Suppliers", AppLanguage.HINDI to "विक्रेता", AppLanguage.MARATHI to "विक्रेते", AppLanguage.TAMIL to "வியாபாரிகள்"),
        "search_hint" to mapOf(AppLanguage.ENGLISH to "Search...", AppLanguage.HINDI to "खोजें...", AppLanguage.MARATHI to "शोधा...", AppLanguage.TAMIL to "தேடு..."),
        "out_of_stock" to mapOf(AppLanguage.ENGLISH to "OUT OF STOCK", AppLanguage.HINDI to "स्टॉक खत्म", AppLanguage.MARATHI to "स्टॉक संपला", AppLanguage.TAMIL to "கையிருப்பு இல்லை"),
        "low_stock" to mapOf(AppLanguage.ENGLISH to "LOW STOCK", AppLanguage.HINDI to "कम स्टॉक", AppLanguage.MARATHI to "कमी स्टॉक", AppLanguage.TAMIL to "குறைவு"),
        "new_sale" to mapOf(AppLanguage.ENGLISH to "Counter Sale", AppLanguage.HINDI to "बिक्री", AppLanguage.MARATHI to "विक्री", AppLanguage.TAMIL to "விற்பனை"),
        "recent_trans" to mapOf(AppLanguage.ENGLISH to "Recent Transactions", AppLanguage.HINDI to "हाल का लेनदेन", AppLanguage.MARATHI to "अलीकडील व्यवहार", AppLanguage.TAMIL to "சமீபத்திய பரிவர்த்தனைகள்"),
        "total_bill" to mapOf(AppLanguage.ENGLISH to "Total Bill", AppLanguage.HINDI to "कुल बिल", AppLanguage.MARATHI to "एकूण बिल", AppLanguage.TAMIL to "மொத்த ரசீது"),
        "generate_invoice" to mapOf(AppLanguage.ENGLISH to "GENERATE INVOICE", AppLanguage.HINDI to "चालान बनाएं", AppLanguage.MARATHI to "बिल तयार करा", AppLanguage.TAMIL to "ரசீது உருவாக்கு"),
        "save_inventory" to mapOf(AppLanguage.ENGLISH to "SAVE TO INVENTORY", AppLanguage.HINDI to "इन्वेंटरी में सहेजें", AppLanguage.MARATHI to "साठ्यात जतन करा", AppLanguage.TAMIL to "சேமி"),
        "history" to mapOf(AppLanguage.ENGLISH to "History", AppLanguage.HINDI to "इतिहास", AppLanguage.MARATHI to "इतिहास", AppLanguage.TAMIL to "வரலாறு"),
        "reports" to mapOf(AppLanguage.ENGLISH to "Reports", AppLanguage.HINDI to "रिपोर्ट", AppLanguage.MARATHI to "अहवाल", AppLanguage.TAMIL to "அறிக்கைகள்"),
        "view_all" to mapOf(AppLanguage.ENGLISH to "View All", AppLanguage.HINDI to "सभी देखें", AppLanguage.MARATHI to "सर्व पहा", AppLanguage.TAMIL to "அனைத்தையும் பார்")
    )

    fun get(key: String, lang: AppLanguage): String {
        return dictionary[key]?.get(lang) ?: dictionary[key]?.get(AppLanguage.ENGLISH) ?: key
    }
}

// --- 2. DATA MODELS ---

data class CategoryMeta(val name: String, val unit: String, val icon: ImageVector, val color: Color)

object CategoryUtils {
    val categories = listOf(
        CategoryMeta("Tablet", "Strips", Icons.Default.Medication, Color(0xFFE3F2FD)),
        CategoryMeta("Capsule", "Strips", Icons.Default.Science, Color(0xFFF3E5F5)),
        CategoryMeta("Syrup", "Bottles", Icons.Default.WaterDrop, Color(0xFFE0F2F1)),
        CategoryMeta("Injection", "Vials", Icons.Default.Vaccines, Color(0xFFFFEBEE)),
        CategoryMeta("Drops", "Bottles", Icons.Default.Opacity, Color(0xFFE1F5FE)),
        CategoryMeta("Ointment", "Tubes", Icons.Default.Healing, Color(0xFFFFF3E0)),
        CategoryMeta("Surgical", "Units", Icons.Default.ContentCut, Color(0xFFECEFF1)),
        CategoryMeta("Personal Care", "Packs", Icons.Default.Face, Color(0xFFFCE4EC)),
        CategoryMeta("Baby Care", "Packs", Icons.Default.ChildCare, Color(0xFFFFF8E1))
    )

    fun getUnit(categoryName: String): String = categories.find { it.name == categoryName }?.unit ?: "Units"
    fun getIcon(categoryName: String): ImageVector = categories.find { it.name == categoryName }?.icon ?: Icons.Default.Inventory
    fun getColor(categoryName: String): Color = categories.find { it.name == categoryName }?.color ?: Color(0xFFF5F5F5)
}

fun makeCall(context: Context, phoneNumber: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber")))
    } catch (e: Exception) {
        Toast.makeText(context, "Unable to make call", Toast.LENGTH_SHORT).show()
    }
}

// Firestore requires empty constructor for automatic deserialization
data class Dealer(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val agencyName: String = "",
    val phone: String = "",
    val userId: String = "" // For multi-user separation
)

data class Medicine(
    val id: String = UUID.randomUUID().toString(),
    val shortId: String = "",
    val name: String = "",
    val batchNo: String = "",
    val saltName: String = "",
    val category: String = "",
    val quantity: Int = 0,
    val purchasePrice: Double = 0.0,
    val sellingPrice: Double = 0.0,
    val gstPercent: Double = 0.0,
    val expiryDate: String = "",
    val dealerId: String = "",
    val userId: String = ""
)

data class GlobalMedicine(val name: String, val salt: String, val manufacturer: String, val defaultShortId: String, val defaultCategory: String)

data class CartItem(
    val medicine: Medicine = Medicine(),
    val qty: Int = 0,
    val totalAmount: Double = 0.0
)

data class Bill(
    val id: String = UUID.randomUUID().toString().substring(0, 8).uppercase(),
    val customerName: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val totalTax: Double = 0.0,
    val date: Long = System.currentTimeMillis(), // Firestore prefers Long/Timestamp
    val userId: String = ""
)

data class User(val uid: String, val email: String, val storeName: String)

// --- 3. VIEWMODEL WITH FIREBASE INTEGRATION ---

class ShopViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _medicines = MutableStateFlow<List<Medicine>>(emptyList())
    val medicinesFlow: StateFlow<List<Medicine>> = _medicines.asStateFlow()

    private val _dealers = MutableStateFlow<List<Dealer>>(emptyList())
    val dealers: StateFlow<List<Dealer>> = _dealers.asStateFlow()

    private val _bills = MutableStateFlow<List<Bill>>(emptyList())
    val billsFlow: StateFlow<List<Bill>> = _bills.asStateFlow()

    private val _cart = MutableStateFlow<List<CartItem>>(emptyList())
    val cartFlow: StateFlow<List<CartItem>> = _cart.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _isLoading = MutableStateFlow(true) // Start loading for auth check
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _todaysSales = MutableStateFlow(0.0)
    val todaysSales: StateFlow<Double> = _todaysSales.asStateFlow()

    private val _apiResults = MutableStateFlow<List<GlobalMedicine>>(emptyList())
    val apiResults: StateFlow<List<GlobalMedicine>> = _apiResults.asStateFlow()

    private val _isApiLoading = MutableStateFlow(false)
    val isApiLoading: StateFlow<Boolean> = _isApiLoading.asStateFlow()

    private val _categories = MutableStateFlow<List<String>>(CategoryUtils.categories.map { it.name } + "All")
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        // Check for existing session
        val currentAuthUser = auth.currentUser
        if (currentAuthUser != null) {
            // Fetch User Details
            db.collection("users").document(currentAuthUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val storeName = document.getString("storeName") ?: "My Pharmacy"
                        val email = currentAuthUser.email ?: ""
                        _currentUser.value = User(currentAuthUser.uid, email, storeName)
                        startListeners(currentAuthUser.uid)
                    }
                    _isLoading.value = false
                }
                .addOnFailureListener { _isLoading.value = false }
        } else {
            _isLoading.value = false
        }
    }

    private fun startListeners(uid: String) {
        // Listen to Medicines
        db.collection("medicines").whereEqualTo("userId", uid)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val list = value.toObjects(Medicine::class.java)
                    _medicines.value = list
                }
            }

        // Listen to Dealers
        db.collection("dealers").whereEqualTo("userId", uid)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    _dealers.value = value.toObjects(Dealer::class.java)
                }
            }

        // Listen to Bills
        db.collection("bills").whereEqualTo("userId", uid)
            .addSnapshotListener { value, _ ->
                if (value != null) {
                    val list = value.toObjects(Bill::class.java).sortedByDescending { it.date }
                    _bills.value = list
                    calculateTodaysSales(list)
                }
            }
    }

    private fun calculateTodaysSales(bills: List<Bill>) {
        val today = Calendar.getInstance()
        val total = bills.filter {
            val billDate = Calendar.getInstance().apply { timeInMillis = it.date }
            billDate.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) &&
                    billDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
        }.sumOf { it.totalAmount }
        _todaysSales.value = total
    }

    fun setLanguage(lang: AppLanguage) { _currentLanguage.value = lang }

    fun login(email: String, pass: String, onS: () -> Unit, onE: (String) -> Unit) {
        if (email.isBlank() || pass.isBlank()) { onE("Empty fields"); return }
        _isLoading.value = true

        auth.signInWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid ?: return@addOnCompleteListener
                    db.collection("users").document(uid).get().addOnSuccessListener { doc ->
                        val storeName = doc.getString("storeName") ?: "Pharmacy"
                        _currentUser.value = User(uid, email, storeName)
                        startListeners(uid)
                        _isLoading.value = false
                        onS()
                    }
                } else {
                    _isLoading.value = false
                    onE(task.exception?.message ?: "Login failed")
                }
            }
    }

    fun signup(
        name: String,
        store: String,
        email: String,
        phone: String, // Kept for consistency, could store in user doc
        address: String,
        dlNo: String,
        pass: String,
        onSuccess: () -> Unit
    ) {
        _isLoading.value = true
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val userMap = hashMapOf(
                        "name" to name,
                        "storeName" to store,
                        "email" to email,
                        "phone" to phone,
                        "address" to address,
                        "dlNo" to dlNo
                    )

                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            _currentUser.value = User(uid, email, store)
                            startListeners(uid)
                            _isLoading.value = false
                            onSuccess()
                        }
                } else {
                    _isLoading.value = false
                }
            }
    }

    fun logout() {
        auth.signOut()
        _currentUser.value = null
        _medicines.value = emptyList()
        _dealers.value = emptyList()
        _bills.value = emptyList()
    }

    fun addStock(m: Medicine) {
        val uid = _currentUser.value?.uid ?: return
        val newMed = m.copy(userId = uid)
        // If updating existing stock, logic would differ slightly (using document ID)
        // For now, simpler implementation:
        viewModelScope.launch(Dispatchers.IO) {
            val existing = _medicines.value.find { it.name.equals(m.name, true) && it.batchNo == m.batchNo }
            if (existing != null) {
                // Update quantity
                db.collection("medicines").document(existing.id)
                    .update("quantity", existing.quantity + m.quantity)
            } else {
                // Create new
                db.collection("medicines").document(newMed.id).set(newMed)
            }
        }
    }

    fun addDealer(n: String, a: String, p: String) {
        val uid = _currentUser.value?.uid ?: return
        val dealer = Dealer(name = n, agencyName = a, phone = p, userId = uid)
        db.collection("dealers").document(dealer.id).set(dealer)
    }

    // Mock Global Search (unchanged)
    fun searchApi(q: String) {
        _isApiLoading.value = true
        viewModelScope.launch {
            delay(500)
            val globalDatabase = listOf(
                GlobalMedicine("Dolo 650", "Paracetamol 650mg", "Micro Labs", "DOLO650", "Tablet"),
                GlobalMedicine("Crocin Advance", "Paracetamol 500mg", "GSK", "CROCIN", "Tablet"),
                GlobalMedicine("Augmentin 625", "Amoxicillin", "GSK", "AUG625", "Tablet"),
                GlobalMedicine("Benadryl", "Diphenhydramine", "J&J", "BENA", "Syrup"),
                GlobalMedicine("Volini", "Diclofenac", "Sun Pharma", "VOLINI", "Ointment")
            )
            if (q.length < 2) _apiResults.value = emptyList()
            else _apiResults.value = globalDatabase.filter { it.name.contains(q, true) || it.salt.contains(q, true) }
            _isApiLoading.value = false
        }
    }

    fun clearApiResults() { _apiResults.value = emptyList() }

    fun addToCart(m: Medicine, q: Int) {
        if(q in 1..m.quantity) {
            val currentCart = _cart.value.toMutableList()
            val existingItem = currentCart.find { it.medicine.id == m.id }
            if (existingItem != null) {
                val newQty = existingItem.qty + q
                if (newQty <= m.quantity) {
                    currentCart.remove(existingItem)
                    currentCart.add(existingItem.copy(qty = newQty, totalAmount = m.sellingPrice * newQty))
                }
            } else {
                currentCart.add(CartItem(m, q, m.sellingPrice * q))
            }
            _cart.value = currentCart
        }
    }

    fun checkout(cName: String): Bill? {
        val c = _cart.value; if(c.isEmpty()) return null
        val uid = _currentUser.value?.uid ?: return null

        var total = 0.0
        var tax = 0.0

        c.forEach { item ->
            total += item.totalAmount
            tax += (item.totalAmount * item.medicine.gstPercent)/100
        }

        val bill = Bill(
            customerName = cName.ifBlank { "Cash Sale" },
            items = c,
            totalAmount = total,
            totalTax = tax,
            userId = uid
        )

        viewModelScope.launch(Dispatchers.IO) {
            // Batch write for atomicity
            val batch = db.batch()

            // 1. Save Bill
            val billRef = db.collection("bills").document(bill.id)
            batch.set(billRef, bill)

            // 2. Update Inventory
            c.forEach { item ->
                val medRef = db.collection("medicines").document(item.medicine.id)
                val newQty = item.medicine.quantity - item.qty
                batch.update(medRef, "quantity", newQty)
            }

            batch.commit().await()
        }

        _cart.value = emptyList()
        return bill
    }

    fun getSalesAnalysis(): Map<String, Double> {
        val now = Calendar.getInstance()
        val currentMonth = now.get(Calendar.MONTH)
        val currentYear = now.get(Calendar.YEAR)

        val lastMonthCal = Calendar.getInstance()
        lastMonthCal.add(Calendar.MONTH, -1)
        val lastMonth = lastMonthCal.get(Calendar.MONTH)

        val allBills = _bills.value

        val thisMonthTotal = allBills.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == currentMonth && c.get(Calendar.YEAR) == currentYear
        }.sumOf { it.totalAmount }

        val lastMonthTotal = allBills.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.MONTH) == lastMonth && c.get(Calendar.YEAR) == currentYear
        }.sumOf { it.totalAmount }

        val yearlyTotal = allBills.filter {
            val c = Calendar.getInstance().apply { timeInMillis = it.date }
            c.get(Calendar.YEAR) == currentYear
        }.sumOf { it.totalAmount }

        return mapOf(
            "this_month" to thisMonthTotal,
            "last_month" to lastMonthTotal,
            "yearly" to yearlyTotal
        )
    }
}

// --- 5. UI CODE (Unchanged styles, minor logic tweaks for Auth) ---

val BrandPrimary = Color(0xFF0D9488) // Modern Teal
val BrandSecondary = Color(0xFF0F766E) // Darker Teal
val BrandBackground = Color(0xFFF3F4F6) // Soft Grey
val BrandSurface = Color(0xFFFFFFFF)
val BrandAccent = Color(0xFFF59E0B) // Amber for alerts
val BrandTextDark = Color(0xFF111827)
val BrandTextLight = Color(0xFF6B7280)
val AlertRed = Color(0xFFD32F2F)
val WarningOrange = Color(0xFFF57C00)
val GoogleRed = Color(0xFFDB4437)
val FacebookBlue = Color(0xFF1877F2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = BrandPrimary,
                    secondary = BrandSecondary,
                    background = BrandBackground,
                    surface = BrandSurface,
                    onPrimary = Color.White,
                    onSurface = BrandTextDark
                ),
                typography = Typography(
                    headlineLarge = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.ExtraBold, fontSize = 32.sp, color = BrandTextDark),
                    headlineMedium = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.Bold, fontSize = 24.sp, color = BrandTextDark),
                    titleMedium = androidx.compose.ui.text.TextStyle(fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = BrandTextDark),
                    bodyMedium = androidx.compose.ui.text.TextStyle(color = BrandTextLight, fontSize = 14.sp)
                )
            ) {
                MedicalShopApp()
            }
        }
    }
}

@Composable
fun MedicalShopApp() {
    val navController = rememberNavController()
    val viewModel: ShopViewModel = viewModel()
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Auto-navigate if logged in
    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("main") {
                popUpTo("onboarding") { inclusive = true }
                popUpTo("login") { inclusive = true }
            }
        }
    }

    if (isLoading) {
        Box(Modifier.fillMaxSize().background(BrandBackground), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = BrandPrimary)
        }
    } else {
        NavHost(navController = navController, startDestination = if (currentUser != null) "main" else "onboarding") {
            composable("onboarding") { OnboardingScreen(navController, viewModel) }
            composable("login") { LoginScreen(navController, viewModel) }
            composable("signup") { SignupScreen(navController, viewModel) }
            composable("main") { MainScreen(navController, viewModel) }
        }
    }
}

@Composable
fun LanguageSelector(current: AppLanguage, iconTint: Color = BrandPrimary, onSelect: (AppLanguage) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Language, contentDescription = "Change Language", tint = iconTint, modifier = Modifier.size(24.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(BrandSurface)) {
            AppLanguage.values().forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.displayName, color = BrandTextDark) },
                    onClick = { onSelect(lang); expanded = false }
                )
            }
        }
    }
}

@Composable
fun OnboardingScreen(navController: NavController, viewModel: ShopViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandPrimary, BrandSecondary))), contentAlignment = Alignment.Center) {
        Box(Modifier.align(Alignment.TopEnd).padding(16.dp)) { LanguageSelector(lang, iconTint = BrandSurface) { viewModel.setLanguage(it) } }

        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Box(modifier = Modifier.size(100.dp).background(BrandSurface, CircleShape), contentAlignment = Alignment.Center) {
                Icon(Icons.Default.HealthAndSafety, null, tint = BrandPrimary, modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(Strings.get("app_name", lang), style = MaterialTheme.typography.headlineLarge, color = BrandSurface)
            Spacer(modifier = Modifier.height(8.dp))
            Text(Strings.get("onboarding_tagline", lang), style = MaterialTheme.typography.bodyMedium, color = BrandSurface.copy(alpha = 0.8f), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandSurface),
                shape = RoundedCornerShape(16.dp)
            ) { Text(Strings.get("get_started", lang), color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("login") }) {
                Text(Strings.get("login_action", lang), color = BrandSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LoginScreen(navController: NavController, viewModel: ShopViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandPrimary, BrandSecondary))), contentAlignment = Alignment.Center) {
        Box(Modifier.align(Alignment.TopEnd).padding(16.dp)) { LanguageSelector(lang, iconTint = BrandSurface) { viewModel.setLanguage(it) } }

        Card(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            elevation = CardDefaults.cardElevation(24.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Box(modifier = Modifier.size(72.dp).background(BrandPrimary.copy(0.1f), CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.LocalPharmacy, null, tint = BrandPrimary, modifier = Modifier.size(40.dp)) }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(Strings.get("app_name", lang), style = MaterialTheme.typography.headlineLarge)
                    Text(Strings.get("login_title", lang), style = MaterialTheme.typography.titleMedium, color = BrandTextLight)
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    OutlinedTextField(
                        value = email, onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary, unfocusedBorderColor = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    OutlinedTextField(
                        value = password, onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary, unfocusedBorderColor = Color.LightGray)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Button(
                        onClick = { viewModel.login(email, password, { navController.navigate("main") { popUpTo("login") { inclusive = true } } }, { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }) },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(8.dp)
                    ) { if(isLoading) CircularProgressIndicator(color = BrandSurface, modifier = Modifier.size(24.dp)) else Text("LOGIN", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                }
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("New here? ", color = BrandTextLight)
                        Text("Sign Up", color = BrandPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { navController.navigate("signup") })
                    }
                }
            }
        }
    }
}

@Composable
fun SignupScreen(navController: NavController, viewModel: ShopViewModel) {
    var name by remember { mutableStateOf("") }
    var store by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var dlNo by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var pass by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(BrandSecondary, BrandPrimary))), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.padding(24.dp).fillMaxWidth().fillMaxHeight(0.9f),
            elevation = CardDefaults.cardElevation(24.dp),
            colors = CardDefaults.cardColors(containerColor = BrandSurface),
            shape = RoundedCornerShape(24.dp)
        ) {
            LazyColumn(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                item {
                    Text(Strings.get("signup_title", lang), style = MaterialTheme.typography.headlineMedium, color = BrandTextDark)
                    Text("Complete Business Registration", style = MaterialTheme.typography.bodyMedium, color = BrandTextLight, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    SectionHeader("Store Details")
                    OutlinedTextField(value = store, onValueChange = { store = it }, label = { Text("Pharmacy / Store Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = dlNo, onValueChange = { dlNo = it }, label = { Text("Drug License No.") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("Full Store Address") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), minLines = 2)
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    SectionHeader("Owner Information")
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Owner Full Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = phone, onValueChange = { phone = it }, label = { Text("Mobile Number") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email Address") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    SectionHeader("Security")
                    OutlinedTextField(value = pass, onValueChange = { pass = it }, label = { Text("Create Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Button(
                        onClick = { viewModel.signup(name, store, email, phone, address, dlNo, pass) { navController.navigate("main") { popUpTo("onboarding") { inclusive = true } } } },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(16.dp)
                    ) { if(isLoading) CircularProgressIndicator(color = BrandSurface, modifier = Modifier.size(24.dp)) else Text("REGISTER STORE", fontSize = 16.sp, fontWeight = FontWeight.Bold) }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(onClick = { navController.popBackStack() }) { Text("Already registered? Login", color = BrandTextLight) }
                }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController, viewModel: ShopViewModel) {
    val lang by viewModel.currentLanguage.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedScreen by remember { mutableIntStateOf(0) }
    val user by viewModel.currentUser.collectAsState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(drawerContainerColor = BrandSurface) {
                Spacer(Modifier.height(24.dp))
                Box(Modifier.fillMaxWidth().padding(horizontal = 24.dp)) {
                    Column {
                        Text(Strings.get("app_name", lang), style = MaterialTheme.typography.headlineMedium, color = BrandPrimary)
                        Text(user?.storeName ?: "", style = MaterialTheme.typography.bodyMedium, color = BrandTextLight)
                    }
                }
                Spacer(Modifier.height(24.dp))
                HorizontalDivider(color = Color.LightGray.copy(0.3f))
                Spacer(Modifier.height(16.dp))
                val navItems = listOf(Triple(0, Icons.Default.Dashboard, "dashboard"), Triple(1, Icons.Default.ShoppingCart, "new_sale"), Triple(2, Icons.Default.Medication, "inventory"), Triple(3, Icons.Default.AddBox, "add_new"), Triple(4, Icons.Default.Contacts, "dealers"), Triple(5, Icons.Default.History, "history"), Triple(6, Icons.Default.Analytics, "reports"))
                navItems.forEach { (index, icon, key) ->
                    NavigationDrawerItem(
                        label = { Text(Strings.get(key, lang), fontWeight = FontWeight.Medium) },
                        selected = selectedScreen == index,
                        onClick = { selectedScreen = index; scope.launch { drawerState.close() } },
                        icon = { Icon(icon, null) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = NavigationDrawerItemDefaults.colors(selectedContainerColor = BrandPrimary.copy(0.1f), selectedIconColor = BrandPrimary, selectedTextColor = BrandPrimary)
                    )
                }
                Spacer(Modifier.weight(1f))
                NavigationDrawerItem(
                    label = { Text("Logout", color = AlertRed) },
                    selected = false,
                    onClick = { viewModel.logout(); navController.navigate("login") { popUpTo("main") { inclusive = true } } },
                    icon = { Icon(Icons.Default.ExitToApp, null, tint = AlertRed) },
                    modifier = Modifier.padding(12.dp)
                )
            }
        }
    ) {
        when (selectedScreen) {
            0 -> DashboardTab(navController, viewModel, { scope.launch { drawerState.open() } }, { index -> selectedScreen = index })
            1 -> PosScreen(viewModel) { scope.launch { drawerState.open() } }
            2 -> InventoryTab(viewModel) { scope.launch { drawerState.open() } }
            3 -> AddStockTab(viewModel, { selectedScreen = 2 }) { scope.launch { drawerState.open() } }
            4 -> DealersTab(viewModel) { scope.launch { drawerState.open() } }
            5 -> TransactionsScreen(viewModel) { scope.launch { drawerState.open() } }
            6 -> ReportsScreen(viewModel) { scope.launch { drawerState.open() } }
        }
    }
}

// Reuse previously defined composables (DashboardTab, PosScreen, InventoryTab, etc.)
// but ensure they are inside the file scope. I will paste the key UI components here
// ensuring they use the updated VM.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTab(navController: NavController, viewModel: ShopViewModel, onOpenDrawer: () -> Unit, onNavigate: (Int) -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    val recentBills by viewModel.billsFlow.collectAsState()
    val medicines by viewModel.medicinesFlow.collectAsState()
    val dealers by viewModel.dealers.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    val sales by viewModel.todaysSales.collectAsState()
    val context = LocalContext.current

    val outOfStockItems = medicines.filter { it.quantity == 0 }
    val lowStockItems = medicines.filter { it.quantity in 1..5 }
    val criticalItems = outOfStockItems + lowStockItems
    var showAlertDialog by remember { mutableStateOf(false) }
    LaunchedEffect(criticalItems) { if (criticalItems.isNotEmpty()) showAlertDialog = true }

    if (showAlertDialog) {
        AlertDialog(
            onDismissRequest = { showAlertDialog = false },
            icon = { Icon(Icons.Default.NotificationsActive, null, tint = AlertRed) },
            title = { Text(Strings.get("low_stock", lang)) },
            text = {
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    if (criticalItems.isEmpty()) item { Text("No alerts. Inventory is healthy.", color = BrandPrimary) }
                    else items(criticalItems) { item ->
                        val dealer = dealers.find { it.id == item.dealerId }
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text("• ${item.name}", fontWeight = FontWeight.Bold)
                                Text(if(item.quantity==0) Strings.get("out_of_stock", lang) else "${item.quantity} Left", color = AlertRed, style = MaterialTheme.typography.labelSmall)
                            }
                            if (dealer != null) IconButton(onClick = { makeCall(context, dealer.phone) }, modifier = Modifier.size(36.dp).background(BrandBackground, CircleShape)) { Icon(Icons.Default.Call, null, tint = BrandPrimary, modifier = Modifier.size(18.dp)) }
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = { Button(onClick = { showAlertDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)) { Text("ACKNOWLEDGE") } },
            containerColor = BrandSurface
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Column { Text(Strings.get("dashboard", lang), fontWeight = FontWeight.Bold, fontSize = 22.sp); Text("Hi, ${user?.storeName}", style = MaterialTheme.typography.labelMedium, color = BrandTextLight) } },
                navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } },
                actions = { IconButton(onClick = { showAlertDialog = true }) { BadgedBox(badge = { if(criticalItems.isNotEmpty()) Badge { Text("${criticalItems.size}") } }) { Icon(Icons.Default.Notifications, null, tint = if(criticalItems.isNotEmpty()) AlertRed else BrandTextDark) } }; LanguageSelector(lang) { viewModel.setLanguage(it) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface)
            )
        },
        floatingActionButton = { FloatingActionButton(onClick = { onNavigate(1) }, containerColor = BrandPrimary, contentColor = Color.White, shape = CircleShape) { Icon(Icons.Default.ShoppingCartCheckout, "Quick Sale") } }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().background(BrandBackground).padding(16.dp)) {
            item {
                Card(modifier = Modifier.fillMaxWidth().height(160.dp), shape = RoundedCornerShape(24.dp), elevation = CardDefaults.cardElevation(8.dp)) {
                    Box(modifier = Modifier.fillMaxSize().background(Brush.horizontalGradient(listOf(BrandPrimary, BrandSecondary)))) {
                        Column(modifier = Modifier.padding(24.dp).align(Alignment.CenterStart)) {
                            Text("Today's Revenue", color = Color.White.copy(0.8f), fontSize = 16.sp)
                            Spacer(Modifier.height(8.dp))
                            Text("₹${String.format("%.2f", sales)}", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                            Spacer(Modifier.height(8.dp))
                            Text("${recentBills.size} Transactions today", color = Color.White.copy(0.8f), fontSize = 14.sp)
                        }
                        Icon(Icons.Default.TrendingUp, null, tint = Color.White.copy(0.1f), modifier = Modifier.size(120.dp).align(Alignment.CenterEnd).offset(x = 20.dp, y = 20.dp))
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
            item { Text(Strings.get("recent_trans", lang), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = BrandTextDark, modifier = Modifier.padding(bottom = 12.dp)) }
            if (recentBills.isEmpty()) {
                item { Box(modifier = Modifier.fillMaxWidth().height(100.dp).background(BrandSurface, RoundedCornerShape(16.dp)), contentAlignment = Alignment.Center) { Text("No sales today yet.", color = BrandTextLight, fontStyle = FontStyle.Italic) } }
            } else {
                items(recentBills.take(5)) { bill ->
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = BrandSurface)) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).background(BrandBackground, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.Receipt, null, tint = BrandTextLight) }
                                Spacer(Modifier.width(16.dp))
                                Column { Text("Bill #${bill.id}", fontWeight = FontWeight.Bold, color = BrandTextDark); Text(bill.customerName, style = MaterialTheme.typography.bodySmall, color = BrandTextLight) }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold, color = BrandPrimary, fontSize = 16.sp)
                                val date = Date(bill.date)
                                Text(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(date), style = MaterialTheme.typography.labelSmall, color = BrandTextLight)
                            }
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

// Placeholders for other screens to ensure compilation.
// These use standard Compose UI as seen in previous steps.

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(viewModel: ShopViewModel, onOpenDrawer: () -> Unit) {
    val medicines by viewModel.medicinesFlow.collectAsState()
    val cart by viewModel.cartFlow.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var qtyInput by remember { mutableStateOf("1") }
    val filteredList = medicines.filter { it.name.contains(searchQuery, true) && it.quantity > 0 }
    val cartTotal = cart.sumOf { it.totalAmount }

    if (selectedMedicine != null) {
        val med = selectedMedicine!!
        AlertDialog(
            onDismissRequest = { selectedMedicine = null },
            title = { Text(selectedMedicine!!.name) },
            text = { OutlinedTextField(qtyInput, { if(it.all{c->c.isDigit()}) qtyInput = it }, label = { Text("Qty") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)) },
            confirmButton = { Button(onClick = { viewModel.addToCart(selectedMedicine!!, qtyInput.toIntOrNull()?:1); selectedMedicine = null; qtyInput = "1" }, colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)) { Text("Add") } },
            containerColor = BrandSurface
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text(Strings.get("new_sale", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface)) }) { padding ->
        Column(Modifier.padding(padding).background(BrandBackground).fillMaxSize()) {
            OutlinedTextField(searchQuery, { searchQuery = it }, placeholder = { Text(Strings.get("search_hint", lang)) }, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary), shape = RoundedCornerShape(12.dp))
            if (cart.isNotEmpty()) {
                LazyColumn(Modifier.height(150.dp).padding(horizontal = 16.dp)) { items(cart) { item -> Card(Modifier.fillMaxWidth().padding(bottom = 4.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1))) { Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${item.qty} x ${item.medicine.name}"); Text("₹${item.totalAmount}", fontWeight = FontWeight.Bold) } } } }
                Button(onClick = { viewModel.checkout(customerName); customerName = "" }, modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandSecondary), shape = RoundedCornerShape(16.dp)) { Text("CHECKOUT - ₹${String.format("%.2f", cartTotal)}") }
            }
            LazyColumn(contentPadding = PaddingValues(16.dp)) { items(filteredList) { item -> Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { selectedMedicine = item }, colors = CardDefaults.cardColors(containerColor = BrandSurface), shape = RoundedCornerShape(12.dp)) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(item.name, fontWeight = FontWeight.Bold); Text("₹${item.sellingPrice}", color = BrandPrimary, fontWeight = FontWeight.Bold) } } } }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTab(viewModel: ShopViewModel, onOpenDrawer: () -> Unit) {
    val medicines by viewModel.medicinesFlow.collectAsState()
    val apiResults by viewModel.apiResults.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isGlobal by remember { mutableStateOf(false) }

    val filteredList = medicines.filter { (selectedCategory == "All" || it.category == selectedCategory) && (it.name.contains(searchQuery, true) || it.shortId.contains(searchQuery, true)) }

    Column(modifier = Modifier.fillMaxSize().background(BrandBackground)) {
        TopAppBar(title = { Text(Strings.get("inventory", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }, actions = { TextButton(onClick = { isGlobal = !isGlobal; viewModel.clearApiResults() }) { Text(if(isGlobal) "Local" else "Global", color = BrandPrimary) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface))

        OutlinedTextField(value = searchQuery, onValueChange = { searchQuery = it; if (isGlobal) viewModel.searchApi(it) }, placeholder = { Text(Strings.get("search_hint", lang)) }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary, unfocusedBorderColor = Color.LightGray), shape = RoundedCornerShape(12.dp))

        if (!isGlobal) {
            LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat -> FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = BrandPrimary, selectedLabelColor = BrandSurface)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isGlobal) {
                items(apiResults) { item -> Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(item.name, fontWeight = FontWeight.Bold, color = BrandAccent); Text(item.salt, fontSize = 12.sp) }; Icon(Icons.Default.Public, null, tint = BrandAccent) } } }
            } else {
                items(filteredList) { item ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BrandSurface)) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) { Text(item.name, fontWeight = FontWeight.Bold); Text("Exp: ${item.expiryDate}", style = MaterialTheme.typography.labelSmall) }
                            Column(horizontalAlignment = Alignment.End) { Text("₹${item.sellingPrice}", fontWeight = FontWeight.Bold, color = BrandPrimary); Text("${item.quantity} units", fontSize = 12.sp) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockTab(viewModel: ShopViewModel, onAdded: () -> Unit, onOpenDrawer: () -> Unit) {
    val apiResults by viewModel.apiResults.collectAsState()
    val isApiLoading by viewModel.isApiLoading.collectAsState()
    val dealers by viewModel.dealers.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var name by remember { mutableStateOf("") }
    var salt by remember { mutableStateOf("") }
    var shortId by remember { mutableStateOf("") }
    var batch by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var selectedDealer by remember { mutableStateOf<Dealer?>(null) }
    var qty by remember { mutableStateOf("") }
    var cost by remember { mutableStateOf("") }
    var mrp by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var showApiDropdown by remember { mutableStateOf(false) }
    var showCat by remember { mutableStateOf(false) }
    var showDeal by remember { mutableStateOf(false) }
    var isRestockMode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val unitLabel = if(category.isNotEmpty()) CategoryUtils.getUnit(category) else "Units"
    val medicines by viewModel.medicinesFlow.collectAsState()
    val localMatches = if (name.length > 1) { medicines.filter { it.name.contains(name, ignoreCase = true) } } else emptyList()

    Column(modifier = Modifier.fillMaxSize().background(BrandBackground)) {
        TopAppBar(title = { Text(if(isRestockMode) "Restock Item" else Strings.get("add_new", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface))
        LazyColumn(Modifier.padding(16.dp)) {
            item {
                SectionHeader("Smart Search")
                Box {
                    OutlinedTextField(
                        value = name, onValueChange = { name = it; if(it.length>1) { viewModel.searchApi(it); showApiDropdown = true } else { showApiDropdown = false; isRestockMode = false } }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
                    )
                    if (showApiDropdown && (localMatches.isNotEmpty() || apiResults.isNotEmpty())) {
                        Card(modifier = Modifier.padding(top=56.dp).heightIn(max=250.dp), colors = CardDefaults.cardColors(containerColor = BrandSurface), elevation = CardDefaults.cardElevation(8.dp)) {
                            LazyColumn {
                                if(localMatches.isNotEmpty()) {
                                    item { Text("In Your Inventory", style = MaterialTheme.typography.labelSmall, color = BrandPrimary, modifier = Modifier.padding(8.dp).background(BrandBackground).fillMaxWidth()) }
                                    items(localMatches) { item -> Row(Modifier.fillMaxWidth().clickable { name = item.name; salt = item.saltName; shortId = item.shortId; category = item.category; mrp = item.sellingPrice.toString(); cost = item.purchasePrice.toString(); isRestockMode = true; showApiDropdown = false; Toast.makeText(context, "Existing item details loaded!", Toast.LENGTH_SHORT).show() }.padding(12.dp)) { Icon(Icons.Default.Inventory, null, tint = WarningOrange, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(item.name, fontWeight = FontWeight.Bold) } }
                                }
                                if(apiResults.isNotEmpty()) {
                                    item { Text("Global Database", style = MaterialTheme.typography.labelSmall, color = BrandTextLight, modifier = Modifier.padding(8.dp).background(BrandBackground).fillMaxWidth()) }
                                    items(apiResults) { res -> Row(Modifier.fillMaxWidth().clickable { name = res.name; salt = res.salt; shortId = res.defaultShortId; category = res.defaultCategory; isRestockMode = false; showApiDropdown = false; viewModel.clearApiResults() }.padding(12.dp)) { Icon(Icons.Default.Public, null, tint = Color.Gray, modifier = Modifier.size(20.dp)); Spacer(Modifier.width(8.dp)); Text(res.name, fontWeight = FontWeight.Bold) } }
                                }
                            }
                        }
                    }
                }

                SectionHeader("Product Info")
                Card(colors = CardDefaults.cardColors(containerColor = BrandSurface), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Box {
                            OutlinedTextField(value = category, onValueChange = {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { showCat = true }) { Icon(Icons.Default.ArrowDropDown, null) } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary))
                            DropdownMenu(expanded = showCat, onDismissRequest = { showCat = false }, modifier = Modifier.background(BrandSurface)) { CategoryUtils.categories.forEach { c -> DropdownMenuItem(text = { Text(c.name) }, onClick = { category = c.name; showCat = false }) } }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(value = shortId, onValueChange = { shortId = it.uppercase() }, label = { Text("Short ID") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)); OutlinedTextField(value = batch, onValueChange = { batch = it.uppercase() }, label = { Text("Batch No") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)) }
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(value = salt, onValueChange = { salt = it }, label = { Text("Salt Composition") }, modifier = Modifier.fillMaxWidth(), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary))
                    }
                }

                SectionHeader("Stock & Supplier")
                Card(colors = CardDefaults.cardColors(containerColor = BrandSurface), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Qty ($unitLabel)") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)); OutlinedTextField(value = expiry, onValueChange = { expiry = it }, label = { Text("Exp (MM/YY)") }, modifier = Modifier.weight(1f), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)) }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) { OutlinedTextField(value = cost, onValueChange = { cost = it }, label = { Text("Buy Rate") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)); OutlinedTextField(value = mrp, onValueChange = { mrp = it }, label = { Text("MRP") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)) }
                        Spacer(Modifier.height(12.dp))
                        Box {
                            OutlinedTextField(value = selectedDealer?.agencyName?:"", onValueChange = {}, readOnly = true, label = { Text("Supplier") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { showDeal = true }) { Icon(Icons.Default.ArrowDropDown, null) } }, colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary))
                            DropdownMenu(expanded = showDeal, onDismissRequest = { showDeal = false }, modifier = Modifier.background(BrandSurface)) { dealers.forEach { d -> DropdownMenuItem(text = { Text(d.agencyName) }, onClick = { selectedDealer = d; showDeal = false }) } }
                        }
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = { if(name.isNotEmpty()) { viewModel.addStock(Medicine(shortId = shortId, name = name, batchNo = batch, saltName = salt, category = category.ifBlank { "General" }, quantity = qty.toIntOrNull()?:0, purchasePrice = cost.toDoubleOrNull()?:0.0, sellingPrice = mrp.toDoubleOrNull()?:0.0, gstPercent = 12.0, expiryDate = expiry, dealerId = selectedDealer?.id?:"")); Toast.makeText(context, if(isRestockMode) "Stock Updated!" else "Added!", Toast.LENGTH_SHORT).show(); onAdded() } }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = if(isRestockMode) WarningOrange else BrandPrimary), shape = RoundedCornerShape(12.dp)) { Text(if(isRestockMode) "UPDATE STOCK" else Strings.get("save_inventory", lang)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealersTab(viewModel: ShopViewModel, onOpenDrawer: () -> Unit) {
    val dealers by viewModel.dealers.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showAddDialog) {
        var dName by remember { mutableStateOf("") }
        var dAgency by remember { mutableStateOf("") }
        var dPhone by remember { mutableStateOf("") }
        AlertDialog(onDismissRequest = { showAddDialog = false }, title = { Text("Add Dealer") }, text = { Column { OutlinedTextField(dAgency, { dAgency = it }, label = { Text("Agency") }); OutlinedTextField(dPhone, { dPhone = it }, label = { Text("Phone") }) } }, confirmButton = { Button(onClick = { viewModel.addDealer(dName, dAgency, dPhone); showAddDialog = false }) { Text("Save") } }, containerColor = BrandSurface)
    }

    Column(modifier = Modifier.fillMaxSize().background(BrandBackground)) {
        TopAppBar(title = { Text(Strings.get("dealers", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }, actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.PersonAdd, null, tint = BrandPrimary) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dealers) { dealer ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BrandSurface), elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column { Text(dealer.agencyName, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(dealer.phone, color = BrandTextLight) }
                        IconButton(onClick = { makeCall(context, dealer.phone) }, modifier = Modifier.background(BrandPrimary, CircleShape)) { Icon(Icons.Default.Call, null, tint = BrandSurface) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: ShopViewModel, onOpenDrawer: () -> Unit) {
    val bills by viewModel.billsFlow.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var selectedBill by remember { mutableStateOf<Bill?>(null) }
    if (selectedBill != null) { ReceiptDialog(selectedBill!!) { selectedBill = null } }

    Column(modifier = Modifier.fillMaxSize().background(BrandBackground)) {
        TopAppBar(title = { Text(Strings.get("history", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(bills) { bill ->
                Card(modifier = Modifier.fillMaxWidth().clickable { selectedBill = bill }, colors = CardDefaults.cardColors(containerColor = BrandSurface), elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Bill #${bill.id}", fontWeight = FontWeight.Bold); Text(bill.customerName, fontSize = 12.sp, color = BrandTextLight) }
                        Column(horizontalAlignment = Alignment.End) { Text("₹${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold, color = BrandPrimary); Text(SimpleDateFormat("dd MMM", Locale.getDefault()).format(bill.date), fontSize = 12.sp, color = BrandTextLight) }
                    }
                }
            }
        }
    }
}

@Composable
fun ReceiptDialog(x0: Bill, content: @Composable () -> Unit) {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: ShopViewModel, onOpenDrawer: () -> Unit) {
    val lang by viewModel.currentLanguage.collectAsState()
    val stats = viewModel.getSalesAnalysis()
    Column(modifier = Modifier.fillMaxSize().background(BrandBackground)) {
        TopAppBar(title = { Text(Strings.get("reports", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = BrandSurface))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text("Sales Performance", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    StatsCard("This Month", "₹${String.format("%.0f", stats["this_month"])}", BrandPrimary, Icons.Default.TrendingUp, Modifier.weight(1f))
                    StatsCard("Last Month", "₹${String.format("%.0f", stats["last_month"])}", BrandTextLight, Icons.Default.History, Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable fun SectionHeader(title: String) { Text(text = title, style = MaterialTheme.typography.labelLarge, color = BrandPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp, top = 16.dp, start = 4.dp)) }
@Composable fun StatsCard(title: String, value: String, color: Color, icon: ImageVector, modifier: Modifier) { Card(modifier, colors = CardDefaults.cardColors(containerColor = BrandSurface), elevation = CardDefaults.cardElevation(2.dp)) { Column(Modifier.padding(16.dp)) { Icon(icon, null, tint = color); Spacer(Modifier.height(12.dp)); Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = BrandTextDark); Text(title, fontSize = 12.sp, color = BrandTextLight) } } }