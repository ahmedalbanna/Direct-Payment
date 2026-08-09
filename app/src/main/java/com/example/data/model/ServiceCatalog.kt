package com.example.data.model

import androidx.compose.ui.graphics.Color

data class TelecomNetwork(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val prefixList: List<String>,
    val primaryColor: Color,
    val secondaryColor: Color,
    val logoSymbol: String
)

data class TelecomPackage(
    val id: String,
    val networkId: String,
    val titleAr: String,
    val descriptionAr: String,
    val priceYer: Double,
    val category: String // "رصيد", "باقات فورجي", "مكس واتساب", "اتصال"
)

data class BillService(
    val id: String,
    val nameAr: String,
    val category: String, // "ثابت", "ADSL", "يمن فورجي", "كهرباء", "ماء"
    val iconName: String,
    val primaryColor: Color
)

data class DigitalCardItem(
    val id: String,
    val nameAr: String,
    val category: String, // "العاب", "انترنت", "بطاقات رقمية"
    val priceYer: Double,
    val valueLabel: String,
    val primaryColor: Color
)

object ServiceCatalogData {

    val networks = listOf(
        TelecomNetwork(
            id = "yemen_mobile",
            nameAr = "يمن موبايل",
            nameEn = "Yemen Mobile",
            prefixList = listOf("77", "78"),
            primaryColor = Color(0xFFD32F2F), // Red
            secondaryColor = Color(0xFFFFEBEE),
            logoSymbol = "YM"
        ),
        TelecomNetwork(
            id = "you",
            nameAr = "يوو (U)",
            nameEn = "YOU Telecom",
            prefixList = listOf("73"),
            primaryColor = Color(0xFFFBC02D), // Yellow
            secondaryColor = Color(0xFFFFFDE7),
            logoSymbol = "YOU"
        ),
        TelecomNetwork(
            id = "sabafon",
            nameAr = "سبأفون",
            nameEn = "Sabafon",
            prefixList = listOf("71"),
            primaryColor = Color(0xFF1976D2), // Blue
            secondaryColor = Color(0xFFE3F2FD),
            logoSymbol = "SAB"
        ),
        TelecomNetwork(
            id = "y_telecom",
            nameAr = "واي (Y)",
            nameEn = "Y Telecom",
            prefixList = listOf("70"),
            primaryColor = Color(0xFFE64A19), // Orange Red
            secondaryColor = Color(0xFFFBE9E7),
            logoSymbol = "Y"
        )
    )

    val packages = listOf(
        TelecomPackage("ym_1", "yemen_mobile", "رصيد مباشر", "تعبئة رصيد مباشر بأي مبلغ", 0.0, "رصيد"),
        TelecomPackage("ym_2", "yemen_mobile", "باقة مزايا فورجي (4G 10GB)", "10 جيجابايت + 300 دقيقة + 300 رسالة - 30 يوم", 4800.0, "باقات فورجي"),
        TelecomPackage("ym_3", "yemen_mobile", "باقة مزايا فورجي (4G 5GB)", "5 جيجابايت + 150 دقيقة - 30 يوم", 2800.0, "باقات فورجي"),
        TelecomPackage("ym_4", "yemen_mobile", "باقة مزايا فورجي أسبوعية (2GB)", "2 جيجابايت + 100 دقيقة - 7 أيام", 1200.0, "باقات فورجي"),
        TelecomPackage("ym_5", "yemen_mobile", "باقة هدايا بلا حدود", "150 دقيقة داخل الشبكة - يومية", 350.0, "اتصال"),

        TelecomPackage("you_1", "you", "رصيد فوري U", "تعبئة رصيد فوري مباشر", 0.0, "رصيد"),
        TelecomPackage("you_2", "you", "باقة فورجي الشاملة 12GB", "12 جيجابايت + دقائق واتساب بلا حدود - 30 يوم", 5200.0, "باقات فورجي"),
        TelecomPackage("you_3", "you", "باقة واتساب بلا حدود", "سعة بلا حدود لتطبيق الواتساب - شهر كامل", 1500.0, "مكس واتساب"),

        TelecomPackage("saba_1", "sabafon", "رصيد سبأفون", "تعبئة رصيد يدوية ومباشرة", 0.0, "رصيد"),
        TelecomPackage("saba_2", "sabafon", "باقة سوبر فورجي 8GB", "8 جيجابايت فورجي + 200 دقيقة - 30 يوم", 4200.0, "باقات فورجي"),
        TelecomPackage("saba_3", "sabafon", "باقة اتصالات سوبر", "250 دقيقة لكل الشبكات - 15 يوم", 2200.0, "اتصال"),

        TelecomPackage("y_1", "y_telecom", "رصيد واي Y", "تعبئة رصيد فوري شبكة واي", 0.0, "رصيد"),
        TelecomPackage("y_2", "y_telecom", "باقة واي فورجي 6GB", "6 جيجابايت داتا - 30 يوم", 3100.0, "باقات فورجي")
    )

    val billServices = listOf(
        BillService("landline", "الهاتف الثابت", "ثابت", "phone", Color(0xFF0288D1)),
        BillService("adsl", "إنترنت ADSL (سوبر يمن)", "ADSL", "wifi", Color(0xFF7B1FA2)),
        BillService("yemen_4g", "يمن فورجي 4G", "يمن فورجي", "cell_tower", Color(0xFF388E3C)),
        BillService("electricity", "فواتير الكهرباء", "كهرباء", "bolt", Color(0xFFF57C00)),
        BillService("water", "فواتير المياه", "ماء", "water_drop", Color(0xFF0097A7))
    )

    val digitalCards = listOf(
        DigitalCardItem("pubg_60", "باجي PUBG Mobile", "العاب", 1400.0, "60 UC", Color(0xFFF57C00)),
        DigitalCardItem("pubg_325", "باجي PUBG Mobile", "العاب", 6800.0, "325 UC", Color(0xFFF57C00)),
        DigitalCardItem("pubg_660", "باجي PUBG Mobile", "العاب", 13200.0, "660 UC", Color(0xFFF57C00)),
        DigitalCardItem("ff_110", "فري فاير Free Fire", "العاب", 1200.0, "110 مجوهرات", Color(0xFFE64A19)),
        DigitalCardItem("ff_583", "فري فاير Free Fire", "العاب", 5900.0, "583 مجوهرات", Color(0xFFE64A19)),
        DigitalCardItem("google_10", "جوجل بلاي Google Play", "بطاقات رقمية", 11500.0, "10$ دولار", Color(0xFF00897B)),
        DigitalCardItem("playstation_10", "بلايستيشن PlayStation", "بطاقات رقمية", 11800.0, "10$ دولار", Color(0xFF1976D2)),
        DigitalCardItem("yemen_wifi_1000", "كروت شبكات الإنترنت المحلية", "انترنت", 1000.0, "كرت 1 جيجا local", Color(0xFF673AB7))
    )
}
