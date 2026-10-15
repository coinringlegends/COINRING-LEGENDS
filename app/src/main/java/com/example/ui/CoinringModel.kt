package com.example.ui

import androidx.annotation.DrawableRes
import com.example.R

data class RingProduct(
  val id: String,
  val name: String,
  val priceChf: Int,
  val status: String = "Verfügbar",
  val description: String,
  val badge: String,
  @DrawableRes val drawableRes: Int,
  val availableSizes: List<String> = listOf("48", "50", "52", "54", "56", "58", "60", "62", "64", "66", "68", "70")
)

data class OrderRecord(
  val id: String,
  val customerName: String,
  val phone: String,
  val address: String,
  val ringType: String,
  val size: String,
  val priceChf: Int,
  val paymentMethod: String,
  val paymentStatus: String, // "paid", "pending", "cancelled"
  val date: String,
  val notes: String = ""
)

object CoinringRepository {
  val defaultProducts = listOf(
    RingProduct(
      id = "prod-20-rappen",
      name = "Swiss 20 Rappen Coinring",
      priceChf = 129,
      description = "Aus originaler Schweizer 20-Rappen Münze (Aluminium-Bronze). Goldener Schimmer, feiner Lorbeerkranz & Libertas/Helvetia.",
      badge = "Klassiker",
      drawableRes = R.drawable.ring_20_rappen
    ),
    RingProduct(
      id = "prod-1-franken",
      name = "1 Franken Classic Coinring",
      priceChf = 149,
      description = "Massives Band mit den ikonischen 22 Sternen und stehender Helvetia. Hochglanzpoliert mit seidenmatter Innenprägung.",
      badge = "Bestseller",
      drawableRes = R.drawable.ring_1_franken
    ),
    RingProduct(
      id = "prod-5-rappen",
      name = "5 Rappen Vintage Coinring",
      priceChf = 119,
      description = "Zartes, elegantes Band mit antiker Patina. Schweizer 5-Rappen Münze mit Wappenschild & Confoederatio Helvetica.",
      badge = "Vintage",
      drawableRes = R.drawable.ring_5_rappen
    ),
    RingProduct(
      id = "b44-royal-woodbox",
      name = "COINRING LEGENDS Royal Woodbox",
      priceChf = 160,
      description = "Massive Royal Nussbaum-Holzbox mit graviertem Messingschild und Samteinlage aus der Heritage Collection.",
      badge = "Heritage",
      drawableRes = R.drawable.woodbox_royal,
      availableSizes = listOf("Standard")
    ),
    RingProduct(
      id = "b44-premium-woodbox",
      name = "COINRING LEGENDS Premium Woodbox",
      priceChf = 120,
      description = "Signature Atelier Woodbox aus der Sovereign Collection mit Samtfütterung & Pflegezertifikat.",
      badge = "Sovereign",
      drawableRes = R.drawable.woodbox_premium,
      availableSizes = listOf("Standard")
    ),
    RingProduct(
      id = "b44-woodbox",
      name = "COINRING LEGENDS Woodbox",
      priceChf = 80,
      description = "Traditionelle Sammlerbox mit goldener Wappenprägung und Samtfütterung. Numismatic Collection.",
      badge = "Numismatic",
      drawableRes = R.drawable.woodbox,
      availableSizes = listOf("Standard")
    ),
    RingProduct(
      id = "b44-premiumbox",
      name = "COINRING LEGENDS Premiumbox",
      priceChf = 50,
      status = "Verkauft",
      description = "Exklusive Schmuckbox (Käufer: Köbi Bissegger, Zuswil). Sovereign Collection.",
      badge = "Verkauft",
      drawableRes = R.drawable.premiumbox,
      availableSizes = listOf("Standard")
    )
  )

  val sampleOrders = listOf(
    OrderRecord(
      id = "CL-20260907-6558",
      customerName = "Köbi Bissegger",
      phone = "0798339387",
      address = "Zuswil 9, 6217 Kottwil",
      ringType = "COINRING LEGENDS PREMIUMBOX",
      size = "Standard",
      priceChf = 50,
      paymentMethod = "TWINT",
      paymentStatus = "paid",
      date = "18.09.2026",
      notes = "Bezahlt via TWINT. Abholung / Lieferung Region Zuswil."
    ),
    OrderRecord(
      id = "CL-20260907-5679",
      customerName = "Köbi Bissegger",
      phone = "0798339387",
      address = "Zuswil 9, 6217 Kottwil",
      ringType = "COINRING LEGENDS PREMIUM WOODBOX",
      size = "Standard",
      priceChf = 120,
      paymentMethod = "TWINT",
      paymentStatus = "pending",
      date = "07.09.2026",
      notes = "Ausstehend. Kunde wünscht Gravur."
    )
  )
}
