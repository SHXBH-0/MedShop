**💊 Pill Point** - Intelligent Pharmacy Operations Platform

"Beyound Your Counter"

**📖 Overview**

**Pill Point** is a mobile-first digital operating system designed specifically for small and medium-sized pharmacies in Tier-2/Tier-3 cities. It bridges the gap between expensive desktop ERP software and manual "red notebooks," empowering pharmacists to manage billing, inventory, and suppliers directly from their smartphones.

**Pill Point** aims to digitize the healthcare supply chain at the grassroots level.


**🚩 The Problem**
  **Local medical stores currently suffer from:**

  ***-Manual Billing:*** Slow, error-prone calculations using calculators and paper.

  ***-Inventory Blindness:*** No real-time visibility on stock levels or expiry dates.

  ***-Supplier Disconnect:*** Contact details and purchase history scattered across visiting cards and diaries.

  ***-Lack of Analytics:*** No data on "Fast-moving" vs. "Slow-moving" medicines.

**💡 The Solution**
  **Pill Point provides an all-in-one Android application that offers:**

    -Smart POS(Point of Sale): Instant bill generation with auto-calculation of totals and taxes.

    -PDF Invoicing: Generates professional digital receipts instantly.

    -Global Medicine Search: Powered by Google Gemini AI to fetch global medicine details (Salt, Manufacturer) automatically.

    -Inventory Intelligence: Categorized tracking (Tablets, Syrups, etc.) with low-stock alerts.

    -Supplier Directory: Direct "Click-to-Call" functionality and centralized dealer records.

**📱 Key Features**

   🔐 Secure Onboarding: Shop registration with Drug License Number verification

   📦 Smart Inventory: Add stock with batch numbers, expiry dates, and categories.

   🤖 AI Integration: Uses Gemini API to auto-fill medicine details, reducing data entry time.

   🧾 Digital Billing: Cart system for counter sales with PDF export to local storage.
 
   📊 Insightful Dashboard: Real-time view of daily revenue, recent transactions, and critical stock alerts.

   🌍 Multilingual: Available in English, Hindi, Marathi.
   
   🧑🏼‍💻Barcode scanning for faster checkout.

  ⏰Automatic notifications for medicines nearing expiry immediately when the app is opened.

**🛠️ Tech Stack**

 - Language: Kotlin

 - UI Framework: Jetpack Compose (Material 3 Design)

 - Architecture: MVVM (Model-View-ViewModel)

 - Backend: Firebase Authentication & Cloud Firestore

 - AI Model: Google Gemini 1.5 Flash (via REST API)

 - PDF Generation: Android Native PDF Document API

**🚀 Getting Started**

  - Prerequisites

  - Android Studio Hedgehog or later.

  - Android Device/Emulator (Min SDK 24).

  - A Google Cloud API Key (for Gemini).

  - google-services.json file (for Firebase).

  - Installation

  - Clone the repository:

  - git clone [[https://github.com/SHXBH-0/PillPoint.git](https://github.com/SHXBH-0/MedShop.git)]


**🔥Firebase Setup:**

  - Create a project on Firebase Console.

  - Enable Authentication (Email/Password) and Firestore Database.

  - Download google-services.json and place it in the app/ directory.

  - API Key Setup:

  - Open MainActivity.kt.
 
  - Replace YOUR_GEMINI_API_KEY_HERE with your actual API key.

  - Build & Run:

  - Sync Gradle files and run on an emulator or physical device.
  

**🔮 Future Roadmap**

  - Integration with WhatsApp for sending bills directly to customers.

  - Automated verification of Drug License Numbers with alerts for license expiration.

  - In-app Help Center featuring step-by-step tutorials and a Q&A support system.

  - Seasonal demand forecasting to optimize inventory stock and prevent medicine wastage.

**👥 Contributors**

 **Rijul Samaiya** - Lead Developer<br>
 
 **Shubh Jain** - Backend Developer<br>
 
 **Rupesh panda** - Frontend Developer<br>
 
 **Shreya Bhagat** - Research & Analysis<br>

📄 License

This project is licensed under the MIT License - see the LICENSE file for details.
