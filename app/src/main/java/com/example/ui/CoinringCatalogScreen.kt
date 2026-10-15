package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import java.net.URLEncoder

@Composable
fun CoinringCatalogScreen(
  products: List<RingProduct>,
  onOrderPlaced: (OrderRecord) -> Unit,
  onNavigateToForge: () -> Unit
) {
  val context = LocalContext.current
  var selectedProductForOrder by remember { mutableStateOf<RingProduct?>(null) }
  var chosenSize by remember { mutableStateOf("56") }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0A0A0A)),
    contentPadding = PaddingValues(bottom = 90.dp)
  ) {
    // Hero Banner
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              colors = listOf(Color(0xFF1C1810), Color(0xFF0A0A0A))
            )
          )
          .padding(vertical = 24.dp, horizontal = 20.dp),
        contentAlignment = Alignment.Center
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Coinring Legends Logo",
            modifier = Modifier
              .size(100.dp)
              .clip(CircleShape)
              .border(2.dp, Color(0xFFD4AF37), CircleShape)
              .testTag("hero_logo")
          )
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "COINRING LEGENDS",
            color = Color(0xFFF1D06A),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
          )
          Text(
            text = "HANDGEFERTIGTE MÜNZRINGE & UNIKATE",
            color = Color(0xFFD4AF37),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "Handmade with Swiss precision in Zuswil / Kottwil, Luzern",
            color = Color(0xFFA19F9A),
            fontSize = 12.sp,
            textAlign = TextAlign.Center
          )
          Spacer(modifier = Modifier.height(16.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
          ) {
            Button(
              onClick = onNavigateToForge,
              colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD4AF37),
                contentColor = Color.Black
              ),
              shape = RoundedCornerShape(20.dp),
              modifier = Modifier.testTag("btn_go_to_forge")
            ) {
              Icon(Icons.Default.Diamond, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Ring nach Mass schmieden", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
          }
        }
      }
    }

    // Quality Trust Indicators
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        TrustBadge(icon = Icons.Default.Verified, title = "100% Echte Münzen")
        TrustBadge(icon = Icons.Default.Shield, title = "Swiss Handmade")
        TrustBadge(icon = Icons.Default.LocalShipping, title = "Post & TWINT")
      }
    }

    // Product Section Header
    item {
      Column(modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)) {
        Text(
          text = "SCHWEIZER MEISTER-KOLLEKTION",
          color = Color(0xFFF1D06A),
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "Jedes Schmuckstück ist ein handgeschmiedetes Unikat aus historischen und modernen Original-Münzen.",
          color = Color(0xFF8E8C87),
          fontSize = 13.sp
        )
      }
    }

    // Product Cards
    items(products) { prod ->
      ProductCard(
        product = prod,
        onOrderClick = { p, size ->
          selectedProductForOrder = p
          chosenSize = size
        }
      )
    }
  }

  // Order Confirmation Dialog / WhatsApp Automation
  selectedProductForOrder?.let { prod ->
    OrderDialog(
      product = prod,
      initialSize = chosenSize,
      onDismiss = { selectedProductForOrder = null },
      onConfirmOrder = { customerName, phone, address, ringSize, saveConsent ->
        val orderId = "CL-${System.currentTimeMillis().toString().takeLast(8)}"
        val order = OrderRecord(
          id = orderId,
          customerName = customerName,
          phone = phone,
          address = address,
          ringType = prod.name,
          size = ringSize,
          priceChf = prod.priceChf,
          paymentMethod = "TWINT / WhatsApp",
          paymentStatus = "pending",
          date = "Heute",
          notes = if (saveConsent) "Tracking zugestimmt" else "Kein Tracking"
        )
        onOrderPlaced(order)
        openWhatsAppOrder(context, prod, ringSize, customerName, address, orderId)
        selectedProductForOrder = null
      }
    )
  }
}

@Composable
fun TrustBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
  Row(
    verticalAlignment = Alignment.CenterVertically,
    modifier = Modifier
      .background(Color(0xFF141311), RoundedCornerShape(12.dp))
      .border(1.dp, Color(0xFF2B2518), RoundedCornerShape(12.dp))
      .padding(horizontal = 8.dp, vertical = 6.dp)
  ) {
    Icon(icon, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(14.dp))
    Spacer(modifier = Modifier.width(4.dp))
    Text(title, color = Color(0xFFC7C5BD), fontSize = 11.sp, fontWeight = FontWeight.Medium)
  }
}

@Composable
fun ProductCard(
  product: RingProduct,
  onOrderClick: (RingProduct, String) -> Unit
) {
  var selectedSize by remember { mutableStateOf(product.availableSizes.firstOrNull() ?: "56") }

  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 10.dp)
      .testTag("product_card_${product.id}"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
    shape = RoundedCornerShape(18.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26221A))
  ) {
    Column {
      Box(modifier = Modifier.fillMaxWidth().height(210.dp)) {
        Image(
          painter = painterResource(id = product.drawableRes),
          contentDescription = product.name,
          contentScale = ContentScale.Crop,
          modifier = Modifier.fillMaxSize()
        )
        // Badge
        Surface(
          color = Color(0xD90A0A0A),
          shape = RoundedCornerShape(bottomEnd = 12.dp),
          modifier = Modifier.align(Alignment.TopStart)
        ) {
          Text(
            text = product.badge.uppercase(),
            color = Color(0xFFF1D06A),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
        // Price Tag
        Surface(
          color = Color(0xD90A0A0A),
          shape = RoundedCornerShape(topStart = 12.dp),
          modifier = Modifier.align(Alignment.BottomEnd)
        ) {
          Text(
            text = "CHF ${product.priceChf}.-",
            color = Color(0xFFD4AF37),
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }

      Column(modifier = Modifier.padding(16.dp)) {
        Text(
          text = product.name,
          color = Color(0xFFFDF6D8),
          fontSize = 17.sp,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = product.description,
          color = Color(0xFFA19F9A),
          fontSize = 13.sp,
          lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (product.availableSizes.size > 1) {
          Text(
            text = "Ringgrösse (Umfang mm):",
            color = Color(0xFF8E8C87),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
          )
          Spacer(modifier = Modifier.height(6.dp))
          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            items(product.availableSizes) { sz ->
              val isSelected = sz == selectedSize
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (isSelected) Color(0xFFD4AF37) else Color(0xFF1E1C18))
                  .border(
                    1.dp,
                    if (isSelected) Color(0xFFF1D06A) else Color(0xFF332D22),
                    RoundedCornerShape(8.dp)
                  )
                  .clickable { selectedSize = sz }
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Text(
                  text = sz,
                  color = if (isSelected) Color.Black else Color(0xFFD4AF37),
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
          Spacer(modifier = Modifier.height(14.dp))
        }

        Button(
          onClick = { onOrderClick(product, selectedSize) },
          enabled = product.status != "Verkauft",
          colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF25D366),
            contentColor = Color.White
          ),
          shape = RoundedCornerShape(12.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(46.dp)
            .testTag("btn_order_${product.id}")
        ) {
          Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (product.status == "Verkauft") "Bereits vergeben" else "Bestellen via WhatsApp (CHF ${product.priceChf}.-)",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }
      }
    }
  }
}

@Composable
fun OrderDialog(
  product: RingProduct,
  initialSize: String,
  onDismiss: () -> Unit,
  onConfirmOrder: (name: String, phone: String, address: String, size: String, consent: Boolean) -> Unit
) {
  var name by remember { mutableStateOf("") }
  var phone by remember { mutableStateOf("") }
  var address by remember { mutableStateOf("Zuswil / Kottwil Region") }
  var ringSize by remember { mutableStateOf(initialSize) }
  var consentTracking by remember { mutableStateOf(true) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = Color(0xFF161412),
    title = {
      Text(
        text = "Bestellung: ${product.name}",
        color = Color(0xFFF1D06A),
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp
      )
    },
    text = {
      Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
          text = "Preis: CHF ${product.priceChf}.- (Grösse $ringSize)",
          color = Color(0xFFD4AF37),
          fontWeight = FontWeight.SemiBold
        )

        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Dein Name") },
          placeholder = { Text("z.B. Hans Muster") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_order_name"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD4AF37),
            unfocusedBorderColor = Color(0xFF443B2A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        OutlinedTextField(
          value = phone,
          onValueChange = { phone = it },
          label = { Text("Telefonnummer (optional)") },
          placeholder = { Text("079 XXX XX XX") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_order_phone"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD4AF37),
            unfocusedBorderColor = Color(0xFF443B2A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        OutlinedTextField(
          value = address,
          onValueChange = { address = it },
          label = { Text("Lieferadresse / Region") },
          modifier = Modifier.fillMaxWidth().testTag("input_order_address"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD4AF37),
            unfocusedBorderColor = Color(0xFF443B2A),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { consentTracking = !consentTracking }
        ) {
          Checkbox(
            checked = consentTracking,
            onCheckedChange = { consentTracking = it },
            colors = CheckboxDefaults.colors(
              checkedColor = Color(0xFFD4AF37),
              checkmarkColor = Color.Black
            )
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Darf ich deine Bestellung speichern für Tracking & Garantiezertifikat?",
            color = Color(0xFFC7C5BD),
            fontSize = 11.sp
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val finalName = if (name.isBlank()) "Kunde" else name.trim()
          onConfirmOrder(finalName, phone.trim(), address.trim(), ringSize, consentTracking)
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF25D366),
          contentColor = Color.White
        ),
        modifier = Modifier.testTag("btn_confirm_order")
      ) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text("In WhatsApp öffnen", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Abbrechen", color = Color(0xFFA19F9A))
      }
    }
  )
}

fun openWhatsAppOrder(
  context: Context,
  product: RingProduct,
  size: String,
  name: String,
  address: String,
  orderId: String
) {
  val message = "Hallo Coinring Legends! Ich möchte bestellen: ${product.name} Grösse $size - CHF ${product.priceChf}. Mein Name: $name. Lieferadresse: $address. Bestell-ID: $orderId"
  val encoded = try {
    URLEncoder.encode(message, "UTF-8")
  } catch (_: Exception) {
    message
  }
  val url = "https://wa.me/41798339387?text=$encoded"
  val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
  try {
    context.startActivity(intent)
  } catch (_: Exception) {
    // If WhatsApp is not installed, open web link
    val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
    context.startActivity(webIntent)
  }
}
