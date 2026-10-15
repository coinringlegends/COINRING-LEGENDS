package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.net.URLEncoder

data class CoinOption(val name: String, val basePrice: Int, val description: String)

@Composable
fun CoinringCustomizerScreen(
  onOrderCustomRing: (OrderRecord) -> Unit
) {
  val context = LocalContext.current

  val coins = listOf(
    CoinOption("20 Rappen (Alu-Bronze)", 129, "Warmer Goldglanz & Lorbeerkranz"),
    CoinOption("1 Franken (Helvetia)", 149, "Ikonische 22 Sterne & Stehende Helvetia"),
    CoinOption("5 Rappen (Alu-Bronze)", 119, "Fein & elegant, Vintage Confoederatio"),
    CoinOption("2 Franken (Helvetia)", 169, "Breiteres Band, markante Prägung"),
    CoinOption("5 Franken (Tell / Alphirte)", 199, "Grosses massives Meisterstück mit Randschrift"),
    CoinOption("Eigene Münze einsenden", 89, "Dein persönliches Erbstück als Ring")
  )

  val finishes = listOf("Hochglanz Poliert", "Vintage Patina", "Champagne Gold", "Satin Matt")

  var selectedCoin by remember { mutableStateOf(coins[0]) }
  var selectedYear by remember { mutableStateOf("Wunschjahr (z.B. 1978)") }
  var selectedSize by remember { mutableFloatStateOf(56f) }
  var selectedFinish by remember { mutableStateOf(finishes[0]) }
  var engravingText by remember { mutableStateOf("") }
  var customerName by remember { mutableStateOf("") }

  val totalPrice = selectedCoin.basePrice + (if (engravingText.isNotBlank()) 20 else 0)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0A0A0A)),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(Icons.Default.Diamond, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(24.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "DIE MÜNZRING-SCHMIEDE",
            color = Color(0xFFF1D06A),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Konfiguriere deinen persönlichen Schweizer Unikat-Ring. Von Hand gedrückt, gestaucht und veredelt in Zuswil / Kottwil.",
          color = Color(0xFFA19F9A),
          fontSize = 13.sp
        )
      }
    }

    // Live Ring Preview Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("custom_ring_preview_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
        shape = RoundedCornerShape(20.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E271B))
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(130.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  colors = listOf(
                    when (selectedFinish) {
                      "Vintage Patina" -> Color(0xFF6B5826)
                      "Champagne Gold" -> Color(0xFFF1D06A)
                      "Satin Matt" -> Color(0xFFC7B171)
                      else -> Color(0xFFFFE082)
                    },
                    Color(0xFF12100C)
                  )
                )
              )
              .border(
                6.dp,
                Brush.sweepGradient(
                  colors = listOf(Color(0xFFD4AF37), Color(0xFF8C6C15), Color(0xFFF1D06A), Color(0xFFD4AF37))
                ),
                CircleShape
              ),
            contentAlignment = Alignment.Center
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(
                text = "${selectedSize.toInt()} mm",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
              )
              Text(
                text = selectedFinish.take(7),
                color = Color(0xFFD4AF37),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
              if (engravingText.isNotBlank()) {
                Text(
                  text = "\"$engravingText\"",
                  color = Color.White,
                  fontSize = 9.sp
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = selectedCoin.name,
            color = Color(0xFFFDF6D8),
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp
          )
          Text(
            text = "Finish: $selectedFinish • Grösse: ${selectedSize.toInt()} mm",
            color = Color(0xFFD4AF37),
            fontSize = 12.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "CHF $totalPrice.-",
            color = Color(0xFFF1D06A),
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold
          )
        }
      }
    }

    // Step 1: Select Coin
    item {
      Text(
        text = "1. WÄHLE DEINE MÜNZE",
        color = Color(0xFFD4AF37),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        coins.forEach { coin ->
          val isSel = coin.name == selectedCoin.name
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSel) Color(0xFF221D13) else Color(0xFF141210))
              .border(1.dp, if (isSel) Color(0xFFD4AF37) else Color(0xFF221F19), RoundedCornerShape(12.dp))
              .clickable { selectedCoin = coin }
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(coin.name, color = if (isSel) Color(0xFFF1D06A) else Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(coin.description, color = Color(0xFF8E8C87), fontSize = 11.sp)
            }
            Text("CHF ${coin.basePrice}.-", color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }
        }
      }
    }

    // Step 2: Ring Size
    item {
      Text(
        text = "2. RINGGRÖSSE: ${selectedSize.toInt()} mm Umfang",
        color = Color(0xFFD4AF37),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Spacer(modifier = Modifier.height(6.dp))
      Slider(
        value = selectedSize,
        onValueChange = { selectedSize = it },
        valueRange = 48f..72f,
        steps = 23,
        colors = SliderDefaults.colors(
          thumbColor = Color(0xFFD4AF37),
          activeTrackColor = Color(0xFFD4AF37),
          inactiveTrackColor = Color(0xFF332D22)
        ),
        modifier = Modifier.testTag("slider_ring_size")
      )
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("48 mm (Damen zierlich)", color = Color(0xFF706D67), fontSize = 11.sp)
        Text("60 mm (Standard)", color = Color(0xFF706D67), fontSize = 11.sp)
        Text("72 mm (Herren markant)", color = Color(0xFF706D67), fontSize = 11.sp)
      }
    }

    // Step 3: Finish
    item {
      Text(
        text = "3. OBERFLÄCHEN-FINISH",
        color = Color(0xFFD4AF37),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(finishes) { fin ->
          val isSel = fin == selectedFinish
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(10.dp))
              .background(if (isSel) Color(0xFFD4AF37) else Color(0xFF161412))
              .border(1.dp, if (isSel) Color(0xFFF1D06A) else Color(0xFF2C271E), RoundedCornerShape(10.dp))
              .clickable { selectedFinish = fin }
              .padding(horizontal = 14.dp, vertical = 10.dp)
          ) {
            Text(
              text = fin,
              color = if (isSel) Color.Black else Color(0xFFD4AF37),
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )
          }
        }
      }
    }

    // Step 4: Engraving & Year
    item {
      Text(
        text = "4. INDIVIDUELLE DETAILS (OPTIONAL)",
        color = Color(0xFFD4AF37),
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
      Spacer(modifier = Modifier.height(8.dp))
      Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        OutlinedTextField(
          value = selectedYear,
          onValueChange = { selectedYear = it },
          label = { Text("Prägejahr Wunsch") },
          placeholder = { Text("z.B. Geburtsjahr 1985") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_custom_year"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD4AF37),
            unfocusedBorderColor = Color(0xFF332D22),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        OutlinedTextField(
          value = engravingText,
          onValueChange = { engravingText = it },
          label = { Text("Innen-Lasergravur (+ CHF 20.-)") },
          placeholder = { Text("z.B. Initialen & Datum") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_custom_engraving"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD4AF37),
            unfocusedBorderColor = Color(0xFF332D22),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )

        OutlinedTextField(
          value = customerName,
          onValueChange = { customerName = it },
          label = { Text("Dein Name") },
          placeholder = { Text("Für die WhatsApp Anfrage") },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("input_custom_name"),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFD4AF37),
            unfocusedBorderColor = Color(0xFF332D22),
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
          )
        )
      }
    }

    // Order Action
    item {
      Button(
        onClick = {
          val finalName = if (customerName.isBlank()) "Kunde" else customerName.trim()
          val orderId = "CL-FORGE-${System.currentTimeMillis().toString().takeLast(6)}"
          val order = OrderRecord(
            id = orderId,
            customerName = finalName,
            phone = "",
            address = "Zuswil / Luzern Region",
            ringType = "Custom: ${selectedCoin.name} ($selectedFinish)",
            size = "${selectedSize.toInt()} mm",
            priceChf = totalPrice,
            paymentMethod = "TWINT / WhatsApp",
            paymentStatus = "pending",
            date = "Heute",
            notes = "Jahr: $selectedYear, Gravur: $engravingText"
          )
          onOrderCustomRing(order)

          val msg = "Hallo Coinring Legends! Ich möchte einen Unikat-Ring schmieden lassen:\n" +
              "• Münze: ${selectedCoin.name}\n" +
              "• Ringgrösse: ${selectedSize.toInt()} mm\n" +
              "• Finish: $selectedFinish\n" +
              "• Jahr: $selectedYear\n" +
              (if (engravingText.isNotBlank()) "• Gravur: $engravingText\n" else "") +
              "• Preis: CHF $totalPrice.-\n" +
              "• Mein Name: $finalName\n" +
              "Bestell-ID: $orderId"

          val encoded = try { URLEncoder.encode(msg, "UTF-8") } catch (_: Exception) { msg }
          val url = "https://wa.me/41798339387?text=$encoded"
          try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
          } catch (_: Exception) {}
        },
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF25D366),
          contentColor = Color.White
        ),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("btn_order_custom_ring")
      ) {
        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Unikat anfragen via WhatsApp (CHF $totalPrice.-)",
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp
        )
      }
    }
  }
}
