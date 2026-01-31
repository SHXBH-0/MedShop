package com.example.medicalshopapp

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction

import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import kotlinx.coroutines.launch


import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.zIndex
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
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

// --- 1. LOCALIZATION SYSTEM ---

enum class AppLanguage(val code: String, val displayName: String) {
    ENGLISH("en", "English"),
    HINDI("hi", "हिंदी"),
    MARATHI("mr", "मराठी")
}

object Strings {
    private val dictionary = mapOf(
        "app_name" to mapOf(AppLanguage.ENGLISH to "PillPoint", AppLanguage.HINDI to "पिनपॉइंट", AppLanguage.MARATHI to "पिनपॉइंट"),
        "login_action" to mapOf(AppLanguage.ENGLISH to "Log In", AppLanguage.HINDI to "लॉगिन", AppLanguage.MARATHI to "लॉगिन"),
        "signup_title" to mapOf(AppLanguage.ENGLISH to "Create Account", AppLanguage.HINDI to "खाता बनाएं", AppLanguage.MARATHI to "खाते तयार करा"),
        "dashboard" to mapOf(AppLanguage.ENGLISH to "Dashboard", AppLanguage.HINDI to "डैशबोर्ड", AppLanguage.MARATHI to "डॅशबोर्ड"),
        "inventory" to mapOf(AppLanguage.ENGLISH to "Inventory", AppLanguage.HINDI to "इन्वेंटरी", AppLanguage.MARATHI to "साठा"),
        "add_new" to mapOf(AppLanguage.ENGLISH to "Add Stock", AppLanguage.HINDI to "स्टॉक जोड़ें", AppLanguage.MARATHI to "स्टॉक जोडा"),
        "dealers" to mapOf(AppLanguage.ENGLISH to "Suppliers", AppLanguage.HINDI to "विक्रेता", AppLanguage.MARATHI to "विक्रेते"),
        "search_hint" to mapOf(AppLanguage.ENGLISH to "Search...", AppLanguage.HINDI to "खोजें...", AppLanguage.MARATHI to "शोधा..."),
        "out_of_stock" to mapOf(AppLanguage.ENGLISH to "OUT OF STOCK", AppLanguage.HINDI to "स्टॉक खत्म", AppLanguage.MARATHI to "स्टॉक संपला"),
        "low_stock" to mapOf(AppLanguage.ENGLISH to "LOW STOCK", AppLanguage.HINDI to "कम स्टॉक", AppLanguage.MARATHI to "कमी स्टॉक"),
        "new_sale" to mapOf(AppLanguage.ENGLISH to "Counter Sale", AppLanguage.HINDI to "बिक्री", AppLanguage.MARATHI to "विक्री"),
        "recent_trans" to mapOf(AppLanguage.ENGLISH to "Recent Transactions", AppLanguage.HINDI to "हाल का लेनदेन", AppLanguage.MARATHI to "अलीकडील व्यवहार"),
        "total_bill" to mapOf(AppLanguage.ENGLISH to "Total Bill", AppLanguage.HINDI to "कुल बिल", AppLanguage.MARATHI to "एकूण बिल"),
        "generate_invoice" to mapOf(AppLanguage.ENGLISH to "GENERATE INVOICE", AppLanguage.HINDI to "चालान बनाएं", AppLanguage.MARATHI to "बिल तयार करा"),
        "save_inventory" to mapOf(AppLanguage.ENGLISH to "SAVE TO INVENTORY", AppLanguage.HINDI to "इन्वेंटरी में सहेजें", AppLanguage.MARATHI to "साठ्यात जतन करा"),
        "history" to mapOf(AppLanguage.ENGLISH to "History", AppLanguage.HINDI to "इतिहास", AppLanguage.MARATHI to "इतिहास"),
        "reports" to mapOf(AppLanguage.ENGLISH to "Reports", AppLanguage.HINDI to "रिपोर्ट", AppLanguage.MARATHI to "अहवाल")
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
    val userId: String = ""
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
    val customerPhone: String = "", // Added this field
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val totalTax: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val userId: String = ""
)

data class User(
    val uid: String,
    val email: String,
    val storeName: String,
    val phone: String = "",
    val address: String = "",
    val dlNo: String = ""
)

// --- 3. VIEWMODEL ---

class ShopViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    // !!! REPLACE WITH YOUR ACTUAL API KEY !!!
    private val GEMINI_API_KEY = "AIzaSyDTlSVpIgUOHb802slOXygVuzYyIGa-KBc"

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

    private val _isLoading = MutableStateFlow(true)
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
        val currentAuthUser = auth.currentUser
        if (currentAuthUser != null) {
            db.collection("users").document(currentAuthUser.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        // Inside ShopViewModel init { ... }
                        val storeName = document.getString("storeName") ?: ""
                        val phone = document.getString("phone") ?: ""
                        val address = document.getString("address") ?: ""
                        val dlNo = document.getString("dlNo") ?: ""
                        val email = currentAuthUser.email ?: ""

                        _currentUser.value = User(currentAuthUser.uid, email, storeName, phone, address, dlNo)
                        startListeners(currentAuthUser.uid)
                    }
                    _isLoading.value = false
                }
                .addOnFailureListener {
                    _isLoading.value = false
                    Log.e("ShopViewModel", "Failed to fetch user: ${it.message}")
                }
        } else {
            _isLoading.value = false
        }
    }

    private fun startListeners(uid: String) {
        db.collection("medicines").whereEqualTo("userId", uid)
            .addSnapshotListener { value, e ->
                if (e != null) { Log.e("ShopViewModel", "Listen failed.", e); return@addSnapshotListener }
                if (value != null) { _medicines.value = value.toObjects(Medicine::class.java) }
            }

        db.collection("dealers").whereEqualTo("userId", uid)
            .addSnapshotListener { value, e ->
                if (e != null) return@addSnapshotListener
                if (value != null) { _dealers.value = value.toObjects(Dealer::class.java) }
            }

        db.collection("bills").whereEqualTo("userId", uid)
            .addSnapshotListener { value, e ->
                if (e != null) return@addSnapshotListener
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
                    }.addOnFailureListener {
                        _isLoading.value = false
                        onE("DB Error: ${it.message}")
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
        phone: String,
        address: String,
        dlNo: String,
        pass: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit // ADDED ERROR CALLBACK
    ) {
        if(email.isBlank() || pass.isBlank()) { onError("Email/Pass required"); return }
        _isLoading.value = true

        auth.createUserWithEmailAndPassword(email, pass)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser!!.uid
                    val userMap = hashMapOf("name" to name, "storeName" to store, "email" to email, "phone" to phone, "address" to address, "dlNo" to dlNo)

                    db.collection("users").document(uid).set(userMap)
                        .addOnSuccessListener {
                            _currentUser.value = User(uid, email, store)
                            startListeners(uid)
                            _isLoading.value = false
                            onSuccess()
                        }
                        .addOnFailureListener {
                            _isLoading.value = false
                            onError("Database save failed: ${it.message}")
                        }
                } else {
                    _isLoading.value = false
                    onError(task.exception?.message ?: "Signup failed")
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
    fun deleteMedicine(id: String) {
        db.collection("medicines").document(id).delete()
    }



    fun addDealer(n: String, a: String, p: String) {
        val uid = _currentUser.value?.uid ?: return
        val dealer = Dealer(name = n, agencyName = a, phone = p, userId = uid)
        db.collection("dealers").document(dealer.id).set(dealer)
    }
    fun deleteDealer(id: String) {
        db.collection("dealers").document(id).delete()
    }

    fun searchApi(q: String) {
        if (q.length < 3) return
        _isApiLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val prompt = """
                    You are a strict JSON API.
                    List 5 medicines matching "$q" available in India.
                    Output ONLY a JSON Array. No markdown, no "here is the list", no ```json.
                    Schema: [{"name": "string", "salt": "string", "manufacturer": "string", "defaultCategory": "Tablet/Syrup/Injection", "defaultShortId": "string"}]
                """.trimIndent()

                val url = URL("[https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$GEMINI_API_KEY](https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$GEMINI_API_KEY)")
                val conn = url.openConnection() as HttpURLConnection
                conn.requestMethod = "POST"
                conn.setRequestProperty("Content-Type", "application/json")
                conn.doOutput = true

                val safePrompt = prompt.replace("\"", "\\\"").replace("\n", " ")
                val jsonPayload = "{\"contents\":[{\"parts\":[{\"text\":\"$safePrompt\"}]}]}"

                conn.outputStream.use { it.write(jsonPayload.toByteArray()) }

                val responseCode = conn.responseCode
                if (responseCode == 200) {
                    val reader = BufferedReader(InputStreamReader(conn.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val root = JSONObject(response)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val text = candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text")
                        val startIndex = text.indexOf('[')
                        val endIndex = text.lastIndexOf(']')

                        if (startIndex != -1 && endIndex != -1) {
                            val jsonString = text.substring(startIndex, endIndex + 1)
                            val jsonArray = JSONArray(jsonString)
                            val results = mutableListOf<GlobalMedicine>()

                            for (i in 0 until jsonArray.length()) {
                                val obj = jsonArray.getJSONObject(i)
                                results.add(GlobalMedicine(
                                    name = obj.optString("name", "Unknown"),
                                    salt = obj.optString("salt", "Unknown"),
                                    manufacturer = obj.optString("manufacturer", "Generic"),
                                    defaultShortId = obj.optString("defaultShortId", "GEN"),
                                    defaultCategory = obj.optString("defaultCategory", "Tablet")
                                ))
                            }
                            _apiResults.value = results
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _apiResults.value = emptyList()
            } finally {
                _isApiLoading.value = false
            }
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

    fun checkout(cName: String, cPhone: String): Bill? {
        val c = _cart.value; if(c.isEmpty()) return null
        val uid = _currentUser.value?.uid ?: return null

        var total = 0.0
        var tax = 0.0

        c.forEach { item ->
            total += item.totalAmount
            tax += (item.totalAmount * item.medicine.gstPercent) / 100
        }

        // Pass the phone number into the Bill object
        val bill = Bill(
            customerName = cName.ifBlank { "Cash Sale" },
            customerPhone = cPhone,
            items = c,
            totalAmount = total,
            totalTax = tax,
            userId = uid
        )

        viewModelScope.launch(Dispatchers.IO) {
            val batch = db.batch()
            val billRef = db.collection("bills").document(bill.id)
            batch.set(billRef, bill)
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
    fun changePassword(newPass: String, onComplete: (Boolean, String?) -> Unit) {
        val user = auth.currentUser
        if (newPass.length < 6) {
            onComplete(false, "Password must be at least 6 characters")
            return
        }
        user?.updatePassword(newPass)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) onComplete(true, null)
                else onComplete(false, task.exception?.message)
            }
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

        return mapOf("this_month" to thisMonthTotal, "last_month" to lastMonthTotal, "yearly" to yearlyTotal)
    }
}

// --- 5. UI CODE ---

val BrandPrimary = Color(0xFF0D9488)
val BrandSecondary = Color(0xFF0F766E)
val BrandBackground = Color(0xFFF3F4F6)
val BrandSurface = Color(0xFFFFFFFF)
val BrandAccent = Color(0xFFF59E0B)
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

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            navController.navigate("main") {
                popUpTo("onboarding") { inclusive = true }
                popUpTo("login") { inclusive = true }
                popUpTo("signup") { inclusive = true }
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
            Text(Strings.get("Beyond Your Counter", lang), style = MaterialTheme.typography.bodyMedium, color = BrandSurface.copy(alpha = 2.0f), textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(64.dp))

            Button(
                onClick = { navController.navigate("signup") },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandSurface),
                shape = RoundedCornerShape(16.dp)
            ) { Text(Strings.get("Sign Up", lang), color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("Login") }) {
                Text(Strings.get("Login", lang), color = BrandSurface, fontWeight = FontWeight.Bold)
            }
        }
    }
}
// --- ADD THIS AT THE VERY BOTTOM OF THE FILE (OUTSIDE ALL OTHER CLASSES) ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: ShopViewModel, onOpen: () -> Unit) {
    val user by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    var showPass by remember { mutableStateOf(false) }
    var newP by remember { mutableStateOf("") }

    if (showPass) {
        AlertDialog(
            onDismissRequest = { showPass = false },
            title = { Text("Change Password") },
            text = {
                OutlinedTextField(
                    value = newP,
                    onValueChange = { newP = it },
                    label = { Text("New Password") },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.changePassword(newP) { s, e ->
                        if(s) {
                            showPass = false
                            Toast.makeText(context, "Password Updated", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, e ?: "Error", Toast.LENGTH_SHORT).show()
                        }
                    }
                }) { Text("Update") }
            },
            dismissButton = { TextButton(onClick = { showPass = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile ") },
                navigationIcon = { IconButton(onClick = onOpen) { Icon(Icons.Default.Menu, null) } }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(BrandBackground)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                SectionHeader("Business Registration Details")
                Card(
                    Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandSurface),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Store Name
                        ProfileDetailRow(Icons.Default.Store, "Pharmacy Name", user?.storeName ?: "N/A")

                        HorizontalDivider(color = Color.LightGray.copy(0.3f))

                        // Drug License
                        ProfileDetailRow(Icons.Default.Badge, "Drug License (D.L.) No.", user?.dlNo ?: "N/A")

                        HorizontalDivider(color = Color.LightGray.copy(0.3f))

                        // Address
                        ProfileDetailRow(Icons.Default.LocationOn, "Full Address", user?.address ?: "N/A")

                        HorizontalDivider(color = Color.LightGray.copy(0.3f))

                        // Contact Phone
                        ProfileDetailRow(Icons.Default.Phone, "Mobile Number", user?.phone ?: "N/A")

                        HorizontalDivider(color = Color.LightGray.copy(0.3f))

                        // Email
                        ProfileDetailRow(Icons.Default.Email, "Registered Email", user?.email ?: "N/A")
                    }
                }
            }

            item {
                SectionHeader("Security")
                Card(
                    Modifier.fillMaxWidth().clickable { showPass = true },
                    colors = CardDefaults.cardColors(containerColor = BrandSurface)
                ) {
                    Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, null, tint = BrandPrimary)
                        Spacer(Modifier.width(16.dp))
                        Text("Change Password", fontWeight = FontWeight.Medium)
                    }
                }
            }

            item {
                SectionHeader("About Pill Point")
                Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = BrandSurface)) {
                    Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.HealthAndSafety, null, Modifier.size(60.dp), BrandPrimary)
                        Spacer(Modifier.height(8.dp))
                        Text("Pill Point", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                        Text("Beyond Your Counter", color = BrandTextLight)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "Running a medical shop involves a lot of juggling, so we built Pill Point to make your daily operations smoother and more efficient. Think of it as your personal digital assistant that moves your work from the old manual registers directly to your smartphone. It allows you to generate accurate bills instantly, keep a close eye on your stock levels to prevent expiry losses, and quickly find medicine details using our smart search. By keeping all your supplier info and sales records in one safe place, Pill Point ensures you can spend less time on paperwork and more time caring for your customers.\n" +
                                    "\n" +
                                    "Version: 1.0.0 Copyright © 2026. All rights reserved.",
                            textAlign = TextAlign.Center,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            item {
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = { viewModel.logout() },
                    colors = ButtonDefaults.buttonColors(containerColor = AlertRed),
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("LOGOUT ACCOUNT", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
@Composable
fun LoginScreen(navController: NavController, viewModel: ShopViewModel) {
    val emailFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
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
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        leadingIcon = { Icon(Icons.Default.Email, null) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(emailFocus),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { passwordFocus.requestFocus() }
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, null) },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passwordFocus),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.login(
                                    email,
                                    password,
                                    { navController.navigate("main") { popUpTo("login") { inclusive = true } } },
                                    { err -> Toast.makeText(context, err, Toast.LENGTH_SHORT).show() }
                                )
                            }
                        ),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = BrandPrimary,
                            unfocusedBorderColor = Color.LightGray
                        )
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
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val storeFocus = remember { FocusRequester() }
    val dlFocus = remember { FocusRequester() }
    val addressFocus = remember { FocusRequester() }
    val nameFocus = remember { FocusRequester() }
    val phoneFocus = remember { FocusRequester() }
    val emailFocus = remember { FocusRequester() }
    val passFocus = remember { FocusRequester() }

    val storeBiv = remember { BringIntoViewRequester() }
    val dlBiv = remember { BringIntoViewRequester() }
    val addressBiv = remember { BringIntoViewRequester() }
    val nameBiv = remember { BringIntoViewRequester() }
    val phoneBiv = remember { BringIntoViewRequester() }
    val emailBiv = remember { BringIntoViewRequester() }
    val passBiv = remember { BringIntoViewRequester() }


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
                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    SectionHeader("Store Details")
                    OutlinedTextField(
                        value = store,
                        onValueChange = { store = it },
                        label = { Text("Pharmacy / Store Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(storeFocus)
                            .bringIntoViewRequester(storeBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { storeBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { dlFocus.requestFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = dlNo,
                        onValueChange = { dlNo = it },
                        label = { Text("Drug License No.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(dlFocus)
                            .bringIntoViewRequester(dlBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { dlBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { addressFocus.requestFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Store Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(addressFocus)
                            .bringIntoViewRequester(addressBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { addressBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { nameFocus.requestFocus() }
                        ),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    SectionHeader("Owner Information")
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Owner Full Name") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(nameFocus)
                            .bringIntoViewRequester(nameBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { nameBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        keyboardActions = KeyboardActions(
                            onNext = { phoneFocus.requestFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(phoneFocus)
                            .bringIntoViewRequester(phoneBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { phoneBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Phone,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { emailFocus.requestFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(emailFocus)
                            .bringIntoViewRequester(emailBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { emailBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { passFocus.requestFocus() }
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
                item {
                    SectionHeader("Security")
                    OutlinedTextField(
                        value = pass,
                        onValueChange = { pass = it },
                        label = { Text("Create Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(passFocus)
                            .bringIntoViewRequester(passBiv)
                            .onFocusChanged {
                                if (it.isFocused) scope.launch { passBiv.bringIntoView() }
                            },
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                viewModel.signup(
                                    name, store, email, phone, address, dlNo, pass,
                                    onSuccess = {
                                        navController.navigate("main") {
                                            popUpTo("onboarding") { inclusive = true }
                                        }
                                    },
                                    onError = { msg ->
                                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                }
                item {
                    Button(
                        onClick = {
                            viewModel.signup(name, store, email, phone, address, dlNo, pass,
                                onSuccess = { navController.navigate("main") { popUpTo("onboarding") { inclusive = true } } },
                                onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                            )
                        },
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
                val navItems = listOf(Triple(0, Icons.Default.Dashboard, "Dashboard"), Triple(1, Icons.Default.ShoppingCart, "Counter"), Triple(2, Icons.Default.Medication, "Inventory"), Triple(3, Icons.Default.AddBox, "Add Meds"), Triple(4, Icons.Default.Contacts, "Dealers"), Triple(5, Icons.Default.History, "History"), Triple(6, Icons.Default.Analytics, "Reports"), Triple(7, Icons.Default.Person, "Profile"))
                navItems.forEach { (index, icon, key) ->
                    NavigationDrawerItem(
                        label = { Text(key) }, // Simplified for code clarity
                        selected = selectedScreen == index,
                        onClick = { selectedScreen = index; scope.launch { drawerState.close() } },
                        icon = { Icon(icon, null) },
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
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
            7 -> ProfileScreen(viewModel) { scope.launch { drawerState.open() } } // This fixes the error
        }
    }
}

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(viewModel: ShopViewModel, onOpenDrawer: () -> Unit) {
    // 1. Properly define Context at the top
    val context = LocalContext.current

    val medicines by viewModel.medicinesFlow.collectAsState()
    val cart by viewModel.cartFlow.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var qtyInput by remember { mutableStateOf("1") }

    // State for the Checkout Dialog
    var showCheckoutDialog by remember { mutableStateOf(false) }

    val filteredList = medicines.filter { it.name.contains(searchQuery, true) && it.quantity > 0 }
    val cartTotal = cart.sumOf { it.totalAmount }

    // --- DIALOG 1: QUANTITY PICKER WITH +/- BUTTONS ---
    if (selectedMedicine != null) {
        val med = selectedMedicine!!
        val currentQty = qtyInput.toIntOrNull() ?: 1

        AlertDialog(
            onDismissRequest = { selectedMedicine = null; qtyInput = "1" },
            title = { Text("Add ${med.name}", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Text("Select Quantity", style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // MINUS BUTTON (Manual background to avoid version errors)
                        IconButton(
                            onClick = { if (currentQty > 1) qtyInput = (currentQty - 1).toString() },
                            modifier = Modifier.background(Color.LightGray.copy(alpha = 0.2f), CircleShape)
                        ) { Icon(Icons.Default.Remove, "Decrease", tint = BrandPrimary) }

                        Text(
                            text = qtyInput,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 24.dp),
                            color = BrandPrimary
                        )

                        // PLUS BUTTON
                        IconButton(
                            onClick = {
                                if (currentQty < med.quantity) qtyInput = (currentQty + 1).toString()
                                else Toast.makeText(context, "Max stock: ${med.quantity}", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.background(Color.LightGray.copy(alpha = 0.2f), CircleShape)
                        ) { Icon(Icons.Default.Add, "Increase", tint = BrandPrimary) }
                    }

                    Spacer(Modifier.height(8.dp))
                    Text("Available: ${med.quantity}", style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.addToCart(med, currentQty)
                        selectedMedicine = null
                        qtyInput = "1"
                    }
                ) { Text("ADD TO CART") }
            }
        )
    }

    // --- DIALOG 2: CUSTOMER DETAILS POPUP ---
    if (showCheckoutDialog) {
        var tempName by remember { mutableStateOf("") }
        var tempPhone by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showCheckoutDialog = false },
            title = { Text("Checkout Details") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = tempName,
                        onValueChange = { tempName = it },
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempPhone,
                        onValueChange = { if(it.length <= 10) tempPhone = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )
                }
            },
            confirmButton = {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        viewModel.checkout(tempName, tempPhone)
                        showCheckoutDialog = false
                        Toast.makeText(context, "Sale Completed!", Toast.LENGTH_SHORT).show()
                    }
                ) { Text("FINALIZE BILL") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(Strings.get("new_sale", lang), fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onOpenDrawer) { Icon(Icons.Default.Menu, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).background(BrandBackground).fillMaxSize()) {

            // Search Bar
            OutlinedTextField(
                searchQuery,
                { searchQuery = it },
                placeholder = { Text(Strings.get("search_hint", lang)) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                leadingIcon = { Icon(Icons.Default.Search, null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary)
            )

            // Items List
            LazyColumn(modifier = Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 16.dp)) {
                items(filteredList) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { selectedMedicine = item },
                        colors = CardDefaults.cardColors(containerColor = BrandSurface)
                    ) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text(item.name, fontWeight = FontWeight.Bold)
                                Text("Stock: ${item.quantity}", style = MaterialTheme.typography.labelSmall)
                            }
                            Text("₹${item.sellingPrice}", color = BrandPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Bottom Cart Summary
            if (cart.isNotEmpty()) {
                Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 8.dp) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("${cart.size} Items", fontWeight = FontWeight.Medium)
                            Text("Total: ₹${String.format("%.2f", cartTotal)}", fontWeight = FontWeight.Bold, color = BrandPrimary)
                        }
                        Spacer(Modifier.height(12.dp))
                        Button(
                            onClick = { showCheckoutDialog = true },
                            modifier = Modifier.fillMaxWidth().height(56.dp)
                        ) {
                            Text("PROCEED TO CHECKOUT")
                        }
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTab(viewModel: ShopViewModel, onOpen: () -> Unit) {
    val meds by viewModel.medicinesFlow.collectAsState()
    val apiResults by viewModel.apiResults.collectAsState()
    var q by remember { mutableStateOf("") }
    var itemToDelete by remember { mutableStateOf<Medicine?>(null) }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Remove Item") },
            text = { Text("Are you sure you want to remove ${itemToDelete?.name} from your inventory?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.id?.let { viewModel.deleteMedicine(it) }
                    itemToDelete = null
                }) { Text("REMOVE", color = AlertRed) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("CANCEL") }
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Inventory") }, navigationIcon = { IconButton(onClick = onOpen) { Icon(Icons.Default.Menu, null) } }) }) { p ->
        Column(Modifier.padding(p).padding(16.dp)) {
            OutlinedTextField(q, { q = it; viewModel.searchApi(it) }, label = { Text("Search Global") }, modifier = Modifier.fillMaxWidth())
            if (apiResults.isNotEmpty()) {
                apiResults.forEach { res -> Card(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { viewModel.addStock(Medicine(name = res.name, saltName = res.salt)); viewModel.clearApiResults() }) { Text(res.name, Modifier.padding(8.dp)) } }
            }
            HorizontalDivider(Modifier.padding(vertical = 16.dp))
            LazyColumn {
                items(meds) { m ->
                    Card(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(m.name, fontWeight = FontWeight.Bold)
                                Text(m.batchNo, style = MaterialTheme.typography.labelSmall)
                            }
                            Text("Qty: ${m.quantity}", color = if(m.quantity < 5) AlertRed else Color.Unspecified, modifier = Modifier.padding(horizontal = 8.dp))
                            IconButton(onClick = { itemToDelete = m }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                            }
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
    var itemToDelete by remember { mutableStateOf<Dealer?>(null) }

    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Remove Dealer") },
            text = { Text("Are you sure you want to remove ${itemToDelete?.name} ?") },
            confirmButton = {
                TextButton(onClick = {
                    itemToDelete?.id?.let { viewModel.deleteDealer(it) }
                    itemToDelete = null
                }) { Text("REMOVE", color = AlertRed) }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) { Text("CANCEL") }
            }
        )
    }
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
                    IconButton(onClick = { itemToDelete = dealer }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AlertRed)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionsScreen(viewModel: ShopViewModel, onOpen: () -> Unit) {
    val bills by viewModel.billsFlow.collectAsState()
    var q by remember { mutableStateOf("") }
    var billView by remember { mutableStateOf<Bill?>(null) }
    val filtered = bills.filter { it.customerName.contains(q, true) || it.customerPhone.contains(q) }

    if (billView != null) ReceiptDialog(billView!!) { billView = null }

    Scaffold(topBar = { TopAppBar(title = { Text("History") }, navigationIcon = { IconButton(onClick = onOpen) { Icon(Icons.Default.Menu, null) } }) }) { p ->
        Column(Modifier.padding(p).fillMaxSize().background(BrandBackground)) {
            OutlinedTextField(q, { q = it }, label = { Text("Search by Name/Phone") }, modifier = Modifier.fillMaxWidth().padding(16.dp))
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { b ->
                    Card(Modifier.fillMaxWidth().clickable { billView = b }) {
                        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column { Text(b.customerName, fontWeight = FontWeight.Bold); Text(b.customerPhone, color = BrandPrimary) }
                            Text("₹${b.totalAmount}", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }
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

// --- UTILS FOR PDF & DIALOG ---

fun saveBillAsPdf(context: Context, bill: Bill) {
    try {
        val pdfDocument = PdfDocument()
        // Standard A4-ish ratio for mobile receipts
        val pageInfo = PdfDocument.PageInfo.Builder(300, 700, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        var y = 40f
        val xMargin = 20f

        // 1. Header
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textSize = 18f
        paint.color = android.graphics.Color.BLACK
        canvas.drawText("PillPoint Receipt", xMargin, y, paint)

        y += 30f
        paint.typeface = Typeface.DEFAULT
        paint.textSize = 12f

        // 2. Bill Info
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(bill.date))
        canvas.drawText("Bill ID: #${bill.id}", xMargin, y, paint)
        y += 20f
        canvas.drawText("Date: $dateStr", xMargin, y, paint)
        y += 20f
        canvas.drawText("Customer: ${bill.customerName}", xMargin, y, paint)
        if (bill.customerPhone.isNotEmpty()) {
            y += 20f
            canvas.drawText("Contact: ${bill.customerPhone}", xMargin, y, paint)
        }

        y += 25f
        paint.strokeWidth = 1f
        canvas.drawLine(xMargin, y, 280f, y, paint) // Separator
        y += 25f

        // 3. Items Header
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Items", xMargin, y, paint)
        canvas.drawText("Price", 220f, y, paint)
        y += 20f
        paint.typeface = Typeface.DEFAULT

        // 4. List Items
        bill.items.forEach { item ->
            val displayName = if (item.medicine.name.length > 22) item.medicine.name.take(19) + "..." else item.medicine.name
            canvas.drawText("${item.qty} x $displayName", xMargin, y, paint)
            canvas.drawText(String.format("₹%.2f", item.totalAmount), 220f, y, paint)
            y += 20f

            // Safety check for page height
            if (y > 650f) return@forEach
        }

        y += 10f
        canvas.drawLine(xMargin, y, 280f, y, paint)
        y += 30f

        // 5. Total
        paint.textSize = 16f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("Total Amount:", xMargin, y, paint)
        canvas.drawText(String.format("₹%.2f", bill.totalAmount), 180f, y, paint)

        pdfDocument.finishPage(page)

        // 6. File Saving (MediaStore API for Android 10+)
        val fileName = "PillPoint_Bill_${bill.id}.pdf"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri)?.use { outputStream ->
                pdfDocument.writeTo(outputStream)
            }
            Toast.makeText(context, "PDF Saved to Downloads", Toast.LENGTH_LONG).show()
        } else {
            throw Exception("Failed to create MediaStore entry")
        }

        pdfDocument.close()
    } catch (e: Exception) {
        Log.e("PDF_ERROR", "Error: ${e.message}")
        Toast.makeText(context, "PDF Error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
    }
}
@Composable
fun ProfileDetailRow(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, modifier = Modifier.fillMaxWidth()) {
        Icon(icon, null, tint = BrandPrimary, modifier = Modifier.size(20.dp).padding(top = 2.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = BrandTextLight)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium, color = BrandTextDark)
        }
    }
}
@Composable
fun ReceiptDialog(bill: Bill, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth().wrapContentHeight(),
            colors = CardDefaults.cardColors(containerColor = BrandSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.CheckCircle, null, tint = BrandPrimary, modifier = Modifier.size(48.dp))
                Spacer(Modifier.height(16.dp))

                Text("Transaction Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Bill #${bill.id}", color = BrandTextLight, fontSize = 14.sp)
                Spacer(Modifier.height(24.dp))

                LazyColumn(modifier = Modifier.heightIn(max = 200.dp).fillMaxWidth()) {
                    items(bill.items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("${item.qty} x ${item.medicine.name}", fontSize = 14.sp, maxLines = 1, modifier = Modifier.weight(1f))
                            Text("₹${String.format("%.2f", item.totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                        HorizontalDivider(color = Color.LightGray.copy(alpha = 0.2f))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Grand Total", fontWeight = FontWeight.Bold)
                    Text("₹${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold, color = BrandPrimary)
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { saveBillAsPdf(context, bill) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Icon(Icons.Default.Download, null)
                    Spacer(Modifier.width(8.dp))
                    Text("DOWNLOAD PDF")
                }

                Spacer(Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("CLOSE")
                }
            }
        }
    }
}
