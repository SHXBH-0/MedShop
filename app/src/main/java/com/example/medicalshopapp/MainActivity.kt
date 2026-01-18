package com.example.medicalshopapp

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
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.text.SimpleDateFormat
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
        "app_name" to mapOf(AppLanguage.ENGLISH to "Pharma Pro", AppLanguage.HINDI to "फार्मा प्रो", AppLanguage.MARATHI to "फार्मा प्रो", AppLanguage.TAMIL to "பார்மா ப்ரோ"),
        "login_title" to mapOf(AppLanguage.ENGLISH to "Chemist Login", AppLanguage.HINDI to "केमिस्ट लॉगिन", AppLanguage.MARATHI to "केमिस्ट लॉगिन", AppLanguage.TAMIL to "உள்நுழையவும்"),
        "dashboard" to mapOf(AppLanguage.ENGLISH to "Dashboard", AppLanguage.HINDI to "डैशबोर्ड", AppLanguage.MARATHI to "डॅशबोर्ड", AppLanguage.TAMIL to "முகப்பு"),
        "inventory" to mapOf(AppLanguage.ENGLISH to "Inventory", AppLanguage.HINDI to "इन्वेंटरी", AppLanguage.MARATHI to "साठा", AppLanguage.TAMIL to "சரக்கு"),
        "add_new" to mapOf(AppLanguage.ENGLISH to "Add New", AppLanguage.HINDI to "नया जोड़ें", AppLanguage.MARATHI to "नवीन जोडा", AppLanguage.TAMIL to "சேர்"),
        "dealers" to mapOf(AppLanguage.ENGLISH to "Dealers", AppLanguage.HINDI to "विक्रेता", AppLanguage.MARATHI to "विक्रेते", AppLanguage.TAMIL to "வியாபாரிகள்"),
        "search_hint" to mapOf(AppLanguage.ENGLISH to "Search...", AppLanguage.HINDI to "खोजें...", AppLanguage.MARATHI to "शोधा...", AppLanguage.TAMIL to "தேடு..."),
        "out_of_stock" to mapOf(AppLanguage.ENGLISH to "OUT OF STOCK", AppLanguage.HINDI to "स्टॉक खत्म", AppLanguage.MARATHI to "स्टॉक संपला", AppLanguage.TAMIL to "கையிருப்பு இல்லை"),
        "low_stock" to mapOf(AppLanguage.ENGLISH to "LOW STOCK", AppLanguage.HINDI to "कम स्टॉक", AppLanguage.MARATHI to "कमी स्टॉक", AppLanguage.TAMIL to "குறைவு"),
        "new_sale" to mapOf(AppLanguage.ENGLISH to "NEW SALE", AppLanguage.HINDI to "नई बिक्री", AppLanguage.MARATHI to "नवीन विक्री", AppLanguage.TAMIL to "விற்பனை"),
        "total_bill" to mapOf(AppLanguage.ENGLISH to "Total Bill", AppLanguage.HINDI to "कुल बिल", AppLanguage.MARATHI to "एकूण बिल", AppLanguage.TAMIL to "மொத்த ரசீது"),
        "generate_invoice" to mapOf(AppLanguage.ENGLISH to "GENERATE INVOICE", AppLanguage.HINDI to "चालान बनाएं", AppLanguage.MARATHI to "बिल तयार करा", AppLanguage.TAMIL to "ரசீது உருவாக்கு"),
        "save_inventory" to mapOf(AppLanguage.ENGLISH to "SAVE TO INVENTORY", AppLanguage.HINDI to "इन्वेंटरी में सहेजें", AppLanguage.MARATHI to "साठ्यात जतन करा", AppLanguage.TAMIL to "சேமி"),
        "recent_trans" to mapOf(AppLanguage.ENGLISH to "Recent Transactions", AppLanguage.HINDI to "हाल के लेनदेन", AppLanguage.MARATHI to "अलीकडील व्यवहार", AppLanguage.TAMIL to "பரிவர்த்தனைகள்")
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

data class Dealer(val id: String = UUID.randomUUID().toString(), val name: String, val agencyName: String, val phone: String)
data class Medicine(val id: String = UUID.randomUUID().toString(), val shortId: String, val name: String, val batchNo: String, val saltName: String, val category: String, val quantity: Int, val purchasePrice: Double, val sellingPrice: Double, val gstPercent: Double, val expiryDate: String, val dealerId: String = "")
data class GlobalMedicine(val name: String, val salt: String, val manufacturer: String, val defaultShortId: String, val defaultCategory: String)
data class CartItem(val medicine: Medicine, val qty: Int, val totalAmount: Double)
data class Bill(val id: String = UUID.randomUUID().toString().substring(0, 8).uppercase(), val customerName: String, val items: List<CartItem>, val totalAmount: Double, val totalTax: Double, val date: Date = Date())
data class User(val username: String, val storeId: String)

// --- 3. MOCK API ---
object MedicineApi {
    private val globalDatabase = listOf(
        GlobalMedicine("Dolo 650", "Paracetamol 650mg", "Micro Labs", "DOLO650", "Tablet"),
        GlobalMedicine("Crocin Advance", "Paracetamol 500mg", "GSK", "CROCIN", "Tablet"),
        GlobalMedicine("Augmentin 625", "Amoxicillin", "GSK", "AUG625", "Tablet"),
        GlobalMedicine("Benadryl", "Diphenhydramine", "J&J", "BENA", "Syrup"),
        GlobalMedicine("Volini", "Diclofenac", "Sun Pharma", "VOLINI", "Ointment"),
        GlobalMedicine("Betadine", "Povidone Iodine", "Win-Medicare", "BETA", "Ointment"),
        GlobalMedicine("Insulin Human", "Insulin", "Eli Lilly", "INS-H", "Injection")
    )
    suspend fun search(query: String): List<GlobalMedicine> {
        delay(500)
        if (query.length < 2) return emptyList()
        return globalDatabase.filter { it.name.contains(query, true) || it.salt.contains(query, true) }
    }
}

// --- 4. VIEWMODEL ---

class ShopViewModel : ViewModel() {
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
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _todaysSales = MutableStateFlow(0.0)
    val todaysSales: StateFlow<Double> = _todaysSales.asStateFlow()
    private val _apiResults = MutableStateFlow<List<GlobalMedicine>>(emptyList())
    val apiResults: StateFlow<List<GlobalMedicine>> = _apiResults.asStateFlow()
    private val _isApiLoading = MutableStateFlow(false)
    val isApiLoading: StateFlow<Boolean> = _isApiLoading.asStateFlow()
    private val _categories = MutableStateFlow<List<String>>(CategoryUtils.categories.map { it.name } + "All")
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // LANGUAGE STATE
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    init {
        val d1 = Dealer(name = "Rajesh Gupta", agencyName = "Gupta Pharma", phone = "9876543210")
        val d2 = Dealer(name = "Amit Kumar", agencyName = "Amit Medicos", phone = "9123456789")
        _dealers.value = listOf(d1, d2)
        _medicines.value = listOf(
            Medicine(shortId = "DOLO650", name = "Dolo 650", batchNo = "DL24", saltName = "Paracetamol", category = "Tablet", quantity = 50, purchasePrice = 25.0, sellingPrice = 30.0, gstPercent = 12.0, expiryDate = "12/25", dealerId = d1.id),
            Medicine(shortId = "BENA", name = "Benadryl", batchNo = "BN99", saltName = "Diphenhydramine", category = "Syrup", quantity = 2, purchasePrice = 90.0, sellingPrice = 110.0, gstPercent = 12.0, expiryDate = "10/25", dealerId = d2.id),
            Medicine(shortId = "INS", name = "Insulin R", batchNo = "IN01", saltName = "Insulin", category = "Injection", quantity = 0, purchasePrice = 400.0, sellingPrice = 550.0, gstPercent = 5.0, expiryDate = "01/25", dealerId = d1.id)
        )
    }

    fun setLanguage(lang: AppLanguage) { _currentLanguage.value = lang }
    fun login(u: String, p: String, onS: () -> Unit, onE: (String) -> Unit) {
        if (u.isBlank() || p.isBlank()) { onE("Empty"); return }; _isLoading.value = true
        viewModelScope.launch { delay(1000); _currentUser.value = User(u, p); _isLoading.value = false; onS() }
    }
    fun logout() { _currentUser.value = null }
    fun addStock(m: Medicine) { _medicines.value = listOf(m) + _medicines.value }
    fun addDealer(n: String, a: String, p: String) { _dealers.value = _dealers.value + Dealer(name = n, agencyName = a, phone = p) }
    fun searchApi(q: String) { _isApiLoading.value = true; viewModelScope.launch { _apiResults.value = MedicineApi.search(q); _isApiLoading.value = false } }
    fun clearApiResults() { _apiResults.value = emptyList() }
    fun addToCart(m: Medicine, q: Int) { if(q in 1..m.quantity) _cart.value = _cart.value + CartItem(m, q, m.sellingPrice * q) }
    fun checkout(cName: String): Bill? {
        val c = _cart.value; if(c.isEmpty()) return null
        var total = 0.0; var tax = 0.0; val updated = _medicines.value.toMutableList()
        c.forEach { item ->
            val idx = updated.indexOfFirst { it.id == item.medicine.id }
            if(idx != -1) { updated[idx] = updated[idx].copy(quantity = updated[idx].quantity - item.qty); total += item.totalAmount; tax += (item.totalAmount * item.medicine.gstPercent)/100 }
        }
        _medicines.value = updated; _todaysSales.value += total
        val bill = Bill(customerName = cName.ifBlank { "Cash Sale" }, items = c, totalAmount = total, totalTax = tax); _bills.value = listOf(bill) + _bills.value; _cart.value = emptyList(); return bill
    }
}

// --- 5. UI THEME & MAIN ---
val MedicalTeal = Color(0xFF00796B); val MedicalLight = Color(0xFFE0F2F1); val MedicalDark = Color(0xFF004D40)
val AccentBlue = Color(0xFF1E88E5); val AlertRed = Color(0xFFD32F2F); val WarningOrange = Color(0xFFF57C00)
val CleanWhite = Color(0xFFFFFFFF); val BackgroundGray = Color(0xFFF7F9FB); val TextPrimary = Color(0xFF263238); val TextSecondary = Color(0xFF757575)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(colorScheme = lightColorScheme(primary = MedicalTeal, secondary = MedicalDark, background = BackgroundGray, surface = CleanWhite)) {
                MedicalShopApp()
            }
        }
    }
}

@Composable
fun MedicalShopApp() {
    val navController = rememberNavController()
    val viewModel: ShopViewModel = viewModel()
    NavHost(navController, startDestination = "login") {
        composable("login") { LoginScreen(navController, viewModel) }
        composable("main") { MainScreen(navController, viewModel) }
        composable("pos") { PosScreen(navController, viewModel) }
        composable("reports") { ReportsScreen(navController, viewModel) }
    }
}

// --- 6. LANGUAGE SELECTOR ---
@Composable
fun LanguageSelector(current: AppLanguage, iconTint: Color = MedicalTeal, onSelect: (AppLanguage) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.Language, contentDescription = "Change Language", tint = iconTint, modifier = Modifier.size(28.dp))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(CleanWhite)) {
            AppLanguage.values().forEach { lang ->
                DropdownMenuItem(
                    text = { Text(lang.displayName, color = TextPrimary) },
                    onClick = { onSelect(lang); expanded = false }
                )
            }
        }
    }
}

// --- 7. SCREENS ---

@Composable
fun LoginScreen(navController: NavController, viewModel: ShopViewModel) {
    var username by remember { mutableStateOf("") }
    var storeId by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(MedicalTeal, MedicalDark))), contentAlignment = Alignment.Center) {
        // Language Picker Top Right
        Box(Modifier.align(Alignment.TopEnd).padding(16.dp)) {
            LanguageSelector(lang, iconTint = CleanWhite) { viewModel.setLanguage(it) }
        }

        Card(modifier = Modifier.padding(32.dp).fillMaxWidth(), elevation = CardDefaults.cardElevation(16.dp), colors = CardDefaults.cardColors(containerColor = CleanWhite), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(modifier = Modifier.size(80.dp).background(MedicalLight, CircleShape), contentAlignment = Alignment.Center) { Icon(Icons.Default.LocalPharmacy, null, tint = MedicalTeal, modifier = Modifier.size(48.dp)) }
                Spacer(modifier = Modifier.height(24.dp))
                Text(Strings.get("app_name", lang), style = MaterialTheme.typography.headlineMedium, color = MedicalDark, fontWeight = FontWeight.ExtraBold)
                Text(Strings.get("login_title", lang), style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(modifier = Modifier.height(32.dp))
                OutlinedTextField(value = username, onValueChange = { username = it }, label = { Text("ID") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(value = storeId, onValueChange = { storeId = it }, label = { Text("Password") }, visualTransformation = PasswordVisualTransformation(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                Spacer(modifier = Modifier.height(32.dp))
                Button(onClick = { viewModel.login(username, storeId, { navController.navigate("main") { popUpTo("login") { inclusive = true } } }, {}) }, modifier = Modifier.fillMaxWidth().height(56.dp), colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal), shape = RoundedCornerShape(12.dp)) { if(isLoading) CircularProgressIndicator(color = CleanWhite, modifier = Modifier.size(24.dp)) else Text("LOGIN", fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun MainScreen(navController: NavController, viewModel: ShopViewModel) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val lang by viewModel.currentLanguage.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = CleanWhite, tonalElevation = 10.dp) {
                val items = listOf(Triple(Icons.Default.Dashboard, "dashboard", 0), Triple(Icons.Default.Medication, "inventory", 1), Triple(Icons.Default.AddBox, "add_new", 2), Triple(Icons.Default.Contacts, "dealers", 3))
                items.forEach { (icon, key, index) ->
                    NavigationBarItem(selected = selectedTab == index, onClick = { selectedTab = index }, icon = { Icon(icon, null) }, label = { Text(Strings.get(key, lang), style = MaterialTheme.typography.labelSmall) }, colors = NavigationBarItemDefaults.colors(selectedIconColor = MedicalTeal, selectedTextColor = MedicalTeal, indicatorColor = MedicalLight))
                }
            }
        },
        floatingActionButton = {
            if (selectedTab == 0) {
                ExtendedFloatingActionButton(onClick = { navController.navigate("pos") }, containerColor = MedicalTeal, contentColor = CleanWhite, icon = { Icon(Icons.Default.ShoppingCartCheckout, null) }, text = { Text(Strings.get("new_sale", lang), fontWeight = FontWeight.Bold) })
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when (selectedTab) {
                0 -> DashboardTab(navController, viewModel)
                1 -> InventoryTab(viewModel)
                2 -> AddStockTab(viewModel) { selectedTab = 1 }
                3 -> DealersTab(viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTab(navController: NavController, viewModel: ShopViewModel) {
    val user by viewModel.currentUser.collectAsState()
    val recentBills by viewModel.billsFlow.collectAsState()
    val medicines by viewModel.medicinesFlow.collectAsState()
    val dealers by viewModel.dealers.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
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
                LazyColumn(modifier = Modifier.heightIn(max = 200.dp)) {
                    items(criticalItems) { item ->
                        val dealer = dealers.find { it.id == item.dealerId }
                        Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column(Modifier.weight(1f)) { Text("• ${item.name}", fontWeight = FontWeight.Bold); Text(dealer?.agencyName ?: "No Supplier", fontSize = 12.sp, color = Color.Gray) }
                            if (dealer != null) IconButton(onClick = { makeCall(context, dealer.phone) }, modifier = Modifier.size(32.dp).background(MedicalLight, CircleShape)) { Icon(Icons.Default.Call, null, tint = MedicalTeal, modifier = Modifier.size(16.dp)) }
                        }
                        HorizontalDivider()
                    }
                }
            },
            confirmButton = { Button(onClick = { showAlertDialog = false }, colors = ButtonDefaults.buttonColors(containerColor = MedicalDark)) { Text("OK") } },
            containerColor = CleanWhite
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        TopAppBar(
            title = { Column { Text(Strings.get("dashboard", lang), style = MaterialTheme.typography.titleLarge); Text(user?.username?:"", style = MaterialTheme.typography.labelMedium) } },
            actions = {
                LanguageSelector(lang) { viewModel.setLanguage(it) }
                IconButton(onClick = { viewModel.logout(); navController.navigate("login") }) { Icon(Icons.Outlined.Logout, null, tint = MedicalDark) }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = CleanWhite)
        )

        LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // ALERTS SECTION
            item {
                Text("Restock Alerts", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = AlertRed)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (outOfStockItems.isEmpty() && lowStockItems.isEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9))) {
                        Row(Modifier.padding(24.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                            Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF2E7D32), modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(16.dp))
                            Text("All Stock Levels Healthy!", fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            } else {
                // Out of Stock (Red)
                items(outOfStockItems) { item ->
                    AlertCard(item, dealers.find { it.id == item.dealerId }, true, context, lang)
                }
                // Low Stock (Orange)
                items(lowStockItems) { item ->
                    AlertCard(item, dealers.find { it.id == item.dealerId }, false, context, lang)
                }
            }

            item { Spacer(modifier = Modifier.height(12.dp)) }
            item { Text(Strings.get("recent_trans", lang), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
            items(recentBills) { bill ->
                Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = CleanWhite)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column { Text("Bill #${bill.id}", fontWeight = FontWeight.Bold, color = MedicalTeal); Text(bill.customerName, style = MaterialTheme.typography.bodySmall) }
                        Column(horizontalAlignment = Alignment.End) { Text("$${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold); Text(SimpleDateFormat("HH:mm", Locale.getDefault()).format(bill.date), style = MaterialTheme.typography.labelSmall, color = Color.Gray) }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun AlertCard(item: Medicine, dealer: Dealer?, isCritical: Boolean, context: Context, lang: AppLanguage) {
    Card(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp), colors = CardDefaults.cardColors(containerColor = if(isCritical) Color(0xFFFFEBEE) else Color(0xFFFFF3E0)), elevation = CardDefaults.cardElevation(2.dp)) {
        Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Icon(if(isCritical) Icons.Default.Cancel else Icons.Default.Warning, null, tint = if(isCritical) AlertRed else WarningOrange)
                Spacer(Modifier.width(16.dp))
                Column { Text(item.name, fontWeight = FontWeight.Bold); if(dealer!=null) Text(dealer.agencyName, fontSize = 12.sp) }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(Strings.get(if(isCritical) "out_of_stock" else "low_stock", lang), fontWeight = FontWeight.Bold, color = if(isCritical) AlertRed else WarningOrange, fontSize = 11.sp)
                Text("${item.quantity} Left", fontWeight = FontWeight.Bold)
                if(dealer!=null) { Spacer(Modifier.height(8.dp)); SmallButton("Call", Icons.Default.Call, { makeCall(context, dealer.phone) }, if(isCritical) AlertRed else WarningOrange) }
            }
        }
    }
}

@Composable
fun SmallButton(text: String, icon: ImageVector, onClick: () -> Unit, color: Color) {
    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = CleanWhite, border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.5f))) {
        Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) { Icon(icon, null, tint = color, modifier = Modifier.size(12.dp)); Spacer(Modifier.width(4.dp)); Text(text, fontSize = 11.sp, color = color, fontWeight = FontWeight.Bold) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryTab(viewModel: ShopViewModel) {
    val medicines by viewModel.medicinesFlow.collectAsState()
    val apiResults by viewModel.apiResults.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isGlobal by remember { mutableStateOf(false) }

    val filteredList = medicines.filter { (selectedCategory == "All" || it.category == selectedCategory) && (it.name.contains(searchQuery, true) || it.shortId.contains(searchQuery, true)) }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        TopAppBar(
            title = { Text(Strings.get("inventory", lang), fontWeight = FontWeight.Bold) },
            actions = { TextButton(onClick = { isGlobal = !isGlobal; viewModel.clearApiResults() }) { Text(if(isGlobal) "Local" else "Global", color = MedicalTeal) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = CleanWhite)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it; if (isGlobal) viewModel.searchApi(it) },
            placeholder = { Text(Strings.get("search_hint", lang)) },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CleanWhite, unfocusedContainerColor = CleanWhite),
            trailingIcon = { if(viewModel.isApiLoading.collectAsState().value) CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp) }
        )

        if (!isGlobal) {
            LazyRow(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat -> FilterChip(selected = selectedCategory == cat, onClick = { selectedCategory = cat }, label = { Text(cat) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = MedicalTeal, selectedLabelColor = CleanWhite)) }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            if (isGlobal) {
                items(apiResults) { item -> Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Column { Text(item.name, fontWeight = FontWeight.Bold, color = AccentBlue); Text(item.salt, fontSize = 12.sp) }; Icon(Icons.Default.Public, null, tint = AccentBlue) } } }
            } else {
                items(filteredList) { item ->
                    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CleanWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                        Row(Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(48.dp).background(CategoryUtils.getColor(item.category), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(CategoryUtils.getIcon(item.category), null, tint = TextPrimary) }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) { Text(item.name, fontWeight = FontWeight.Bold); Text(item.saltName, fontSize = 12.sp, color = TextSecondary) }
                            Column(horizontalAlignment = Alignment.End) { Text("$${item.sellingPrice}", fontWeight = FontWeight.Bold, color = MedicalTeal); Text("${item.quantity} ${CategoryUtils.getUnit(item.category)}", fontSize = 12.sp, color = if(item.quantity<5) AlertRed else TextSecondary) }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStockTab(viewModel: ShopViewModel, onAdded: () -> Unit) {
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
    var showCat by remember { mutableStateOf(false) }
    var showDeal by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        TopAppBar(title = { Text(Strings.get("add_new", lang), fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = CleanWhite))
        LazyColumn(Modifier.padding(16.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CleanWhite)) {
                    Column(Modifier.padding(16.dp)) {
                        OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Box {
                            OutlinedTextField(value = category, onValueChange = {}, readOnly = true, label = { Text("Category") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { showCat = true }) { Icon(Icons.Default.ArrowDropDown, null) } })
                            DropdownMenu(expanded = showCat, onDismissRequest = { showCat = false }) { CategoryUtils.categories.forEach { c -> DropdownMenuItem(text = { Text(c.name) }, onClick = { category = c.name; showCat = false }) } }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(value = qty, onValueChange = { qty = it }, label = { Text("Qty") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                            OutlinedTextField(value = mrp, onValueChange = { mrp = it }, label = { Text("MRP") }, modifier = Modifier.weight(1f), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                        }
                        Spacer(Modifier.height(8.dp))
                        Box {
                            OutlinedTextField(value = selectedDealer?.agencyName?:"", onValueChange = {}, readOnly = true, label = { Text("Supplier") }, modifier = Modifier.fillMaxWidth(), trailingIcon = { IconButton(onClick = { showDeal = true }) { Icon(Icons.Default.ArrowDropDown, null) } })
                            DropdownMenu(expanded = showDeal, onDismissRequest = { showDeal = false }) { dealers.forEach { d -> DropdownMenuItem(text = { Text(d.agencyName) }, onClick = { selectedDealer = d; showDeal = false }) } }
                        }
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = {
                            if(name.isNotEmpty()) {
                                viewModel.addStock(Medicine(shortId = shortId, name = name, batchNo = batch, saltName = salt, category = category.ifBlank { "General" }, quantity = qty.toIntOrNull()?:0, purchasePrice = cost.toDoubleOrNull()?:0.0, sellingPrice = mrp.toDoubleOrNull()?:0.0, gstPercent = 12.0, expiryDate = expiry, dealerId = selectedDealer?.id?:""))
                                Toast.makeText(context, "Added!", Toast.LENGTH_SHORT).show()
                                onAdded()
                            }
                        }, modifier = Modifier.fillMaxWidth().height(50.dp), colors = ButtonDefaults.buttonColors(containerColor = MedicalDark)) { Text(Strings.get("save_inventory", lang)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DealersTab(viewModel: ShopViewModel) {
    val dealers by viewModel.dealers.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (showAddDialog) {
        var dName by remember { mutableStateOf("") }
        var dAgency by remember { mutableStateOf("") }
        var dPhone by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Dealer") },
            text = { Column { OutlinedTextField(dAgency, { dAgency = it }, label = { Text("Agency") }); OutlinedTextField(dPhone, { dPhone = it }, label = { Text("Phone") }) } },
            confirmButton = { Button(onClick = { viewModel.addDealer(dName, dAgency, dPhone); showAddDialog = false }) { Text("Save") } }
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(BackgroundGray)) {
        TopAppBar(title = { Text(Strings.get("dealers", lang), fontWeight = FontWeight.Bold) }, actions = { IconButton(onClick = { showAddDialog = true }) { Icon(Icons.Default.PersonAdd, null, tint = MedicalTeal) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = CleanWhite))
        LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(dealers) { dealer ->
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = CleanWhite), elevation = CardDefaults.cardElevation(2.dp)) {
                    Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column { Text(dealer.agencyName, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(dealer.phone, color = TextSecondary) }
                        IconButton(onClick = { makeCall(context, dealer.phone) }, modifier = Modifier.background(MedicalTeal, CircleShape)) { Icon(Icons.Default.Call, null, tint = CleanWhite) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosScreen(navController: NavController, viewModel: ShopViewModel) {
    val medicines by viewModel.medicinesFlow.collectAsState()
    val cart by viewModel.cartFlow.collectAsState()
    val lang by viewModel.currentLanguage.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var customerName by remember { mutableStateOf("") }
    var showReceipt by remember { mutableStateOf<Bill?>(null) }
    var selectedMedicine by remember { mutableStateOf<Medicine?>(null) }
    var qtyInput by remember { mutableStateOf("1") }
    val filteredList = medicines.filter { it.name.contains(searchQuery, true) }
    val cartTotal = cart.sumOf { it.totalAmount }
    val context = LocalContext.current

    if (showReceipt != null) { ReceiptDialog(showReceipt!!) { showReceipt = null } }
    if (selectedMedicine != null) {
        AlertDialog(
            onDismissRequest = { selectedMedicine = null },
            title = { Text(selectedMedicine!!.name) },
            text = { OutlinedTextField(qtyInput, { if(it.all{c->c.isDigit()}) qtyInput = it }, label = { Text("Qty") }) },
            confirmButton = { Button(onClick = { viewModel.addToCart(selectedMedicine!!, qtyInput.toIntOrNull()?:1); selectedMedicine = null; qtyInput = "1" }) { Text("Add") } }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text(Strings.get("new_sale", lang), fontWeight = FontWeight.Bold) }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = CleanWhite)) }) { padding ->
        Column(Modifier.padding(padding).background(BackgroundGray)) {
            OutlinedTextField(searchQuery, { searchQuery = it }, placeholder = { Text(Strings.get("search_hint", lang)) }, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = CleanWhite, unfocusedContainerColor = CleanWhite))
            if (cart.isNotEmpty()) {
                LazyColumn(Modifier.height(150.dp).padding(horizontal = 16.dp)) { items(cart) { item -> Card(Modifier.fillMaxWidth().padding(bottom = 4.dp), colors = CardDefaults.cardColors(containerColor = MedicalLight)) { Row(Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("${item.qty} x ${item.medicine.name}"); Text("$${item.totalAmount}", fontWeight = FontWeight.Bold) } } } }
                Button(onClick = { showReceipt = viewModel.checkout(customerName); customerName = "" }, modifier = Modifier.fillMaxWidth().padding(16.dp), colors = ButtonDefaults.buttonColors(containerColor = MedicalDark)) { Text("${Strings.get("total_bill", lang)}: $${String.format("%.2f", cartTotal)} - ${Strings.get("generate_invoice", lang)}") }
            }
            LazyColumn(contentPadding = PaddingValues(16.dp)) { items(filteredList) { item -> Card(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp).clickable { selectedMedicine = item }, colors = CardDefaults.cardColors(containerColor = CleanWhite)) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(item.name, fontWeight = FontWeight.Bold); Text("$${item.sellingPrice}", color = MedicalTeal, fontWeight = FontWeight.Bold) } } } }
        }
    }
}

// Utils (Same as before)
fun saveBillAsPdf(context: Context, bill: Bill) { try { val pdfDocument = PdfDocument(); val pageInfo = PdfDocument.PageInfo.Builder(300, 600, 1).create(); val page = pdfDocument.startPage(pageInfo); val canvas = page.canvas; val paint = Paint(); paint.textSize = 12f; paint.color = android.graphics.Color.BLACK; paint.typeface = Typeface.DEFAULT_BOLD; var y = 20f; paint.textSize = 16f; canvas.drawText("PHARMA PRO CHEMIST", 50f, y, paint); y += 20f; paint.textSize = 12f; paint.typeface = Typeface.DEFAULT; canvas.drawText("Bill #${bill.id}", 10f, y, paint); y += 15f; canvas.drawText("Date: ${SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(bill.date)}", 10f, y, paint); y += 15f; canvas.drawText("Customer: ${bill.customerName}", 10f, y, paint); y += 20f; canvas.drawLine(10f, y, 290f, y, paint); y += 15f; bill.items.forEach { item -> val line = "${item.medicine.name} x ${item.qty} = ${item.totalAmount}"; canvas.drawText(line, 10f, y, paint); y += 15f }; canvas.drawLine(10f, y, 290f, y, paint); y += 20f; paint.typeface = Typeface.DEFAULT_BOLD; canvas.drawText("Grand Total: $${String.format("%.2f", bill.totalAmount)}", 10f, y, paint); pdfDocument.finishPage(page); val resolver = context.contentResolver; val contentValues = ContentValues().apply { put(MediaStore.MediaColumns.DISPLAY_NAME, "Bill_${bill.id}.pdf"); put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf"); put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS) }; val uri = resolver.insert(MediaStore.Files.getContentUri("external"), contentValues); if (uri != null) { resolver.openOutputStream(uri)?.use { outputStream -> pdfDocument.writeTo(outputStream) }; Toast.makeText(context, "Saved to Downloads!", Toast.LENGTH_SHORT).show() } else { Toast.makeText(context, "Error creating file", Toast.LENGTH_SHORT).show() }; pdfDocument.close() } catch (e: IOException) { e.printStackTrace(); Toast.makeText(context, "Error saving PDF", Toast.LENGTH_SHORT).show() } }
@Composable fun ReceiptDialog(bill: Bill, onDismiss: () -> Unit) { val context = LocalContext.current; Dialog(onDismissRequest = onDismiss) { Card(modifier = Modifier.fillMaxWidth().wrapContentHeight(), colors = CardDefaults.cardColors(containerColor = CleanWhite), shape = RoundedCornerShape(16.dp)) { Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.CheckCircle, null, tint = MedicalTeal, modifier = Modifier.size(48.dp)); Spacer(Modifier.height(16.dp)); Text("Sale Successful!", fontWeight = FontWeight.Bold, fontSize = 18.sp); Text("Bill #${bill.id} generated", color = TextSecondary); Spacer(Modifier.height(24.dp)); Button(onClick = { saveBillAsPdf(context, bill) }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = MedicalTeal)) { Icon(Icons.Default.Download, null); Spacer(Modifier.width(8.dp)); Text("DOWNLOAD PDF") }; Spacer(Modifier.height(8.dp)); OutlinedButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) { Text("CLOSE") } } } } }
@Composable fun StatsCard(title: String, value: String, color: Color, icon: ImageVector, modifier: Modifier) { Card(modifier, colors = CardDefaults.cardColors(containerColor = CleanWhite), elevation = CardDefaults.cardElevation(2.dp)) { Column(Modifier.padding(16.dp)) { Icon(icon, null, tint = color); Spacer(Modifier.height(12.dp)); Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary); Text(title, fontSize = 12.sp, color = TextSecondary) } } }
@OptIn(ExperimentalMaterial3Api::class) @Composable fun ReportsScreen(navController: NavController, viewModel: ShopViewModel) { val medicines by viewModel.medicinesFlow.collectAsState(); val lowStock = medicines.filter { it.quantity <= 5 }; Scaffold(topBar = { TopAppBar(title = { Text("Inventory Risks") }, navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.Default.ArrowBack, null) } }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MedicalTeal, titleContentColor = CleanWhite, navigationIconContentColor = CleanWhite)) }) { p -> LazyColumn(Modifier.padding(p).padding(16.dp)) { item { Text("Low Stock Items", fontWeight = FontWeight.Bold, color = AlertRed); Spacer(Modifier.height(8.dp)) }; items(lowStock) { item -> Card(Modifier.fillMaxWidth().padding(bottom = 8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))) { Row(Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(item.name, fontWeight = FontWeight.Bold); Text("Only ${item.quantity} ${CategoryUtils.getUnit(item.category)}", color = AlertRed) } } } } } }