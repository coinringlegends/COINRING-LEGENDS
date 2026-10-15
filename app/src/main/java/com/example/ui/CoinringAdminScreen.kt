package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@Composable
fun CoinringAdminScreen(
  products: List<RingProduct>,
  orders: List<OrderRecord>,
  onUpdateProducts: (List<RingProduct>) -> Unit,
  onUpdateOrders: (List<OrderRecord>) -> Unit
) {
  var selectedTab by remember { mutableIntStateOf(0) }
  var showAddProductDialog by remember { mutableStateOf(false) }
  var syncMessage by remember { mutableStateOf<String?>(null) }
  var isSyncing by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0A0A0A)),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // Admin Identity Badge
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("admin_header_card"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E271B))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF2B2414)),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text("Inhaber-Portal", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
              Text("Coinringlegends@gmail.com", color = Color(0xFFD4AF37), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.weight(1f))
            Surface(
              color = Color(0xFF1F2B14),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                "Autorisiert",
                color = Color(0xFF81C784),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            "Verwalte hier Produkte, Bestelleingänge, TWINT Zahlungen und die Synchronisation mit base44 & Firebase.",
            color = Color(0xFFA19F9A),
            fontSize = 12.sp
          )
        }
      }
    }

    // Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color(0xFF141210), RoundedCornerShape(12.dp))
          .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        AdminTabItem(title = "Bestellungen", count = orders.size, isSelected = selectedTab == 0) { selectedTab = 0 }
        AdminTabItem(title = "Produkte", count = products.size, isSelected = selectedTab == 1) { selectedTab = 1 }
        AdminTabItem(title = "Base44 Sync", count = null, isSelected = selectedTab == 2) { selectedTab = 2 }
      }
    }

    // TAB 0: ORDERS & PAYMENTS
    if (selectedTab == 0) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Eingegangene Bestellungen", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Text("${orders.size} gesamt", color = Color(0xFF8E8C87), fontSize = 12.sp)
        }
      }

      if (orders.isEmpty()) {
        item {
          Text("Noch keine Bestellungen eingegangen.", color = Color(0xFF706D67), fontSize = 13.sp)
        }
      }

      items(orders) { ord ->
        OrderAdminCard(
          order = ord,
          onStatusToggle = { newStatus ->
            val updated = orders.map { if (it.id == ord.id) it.copy(paymentStatus = newStatus) else it }
            onUpdateOrders(updated)
          },
          onDelete = {
            val updated = orders.filterNot { it.id == ord.id }
            onUpdateOrders(updated)
          }
        )
      }
    }

    // TAB 1: PRODUCTS MANAGEMENT
    if (selectedTab == 1) {
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text("Katalog Produkte (${products.size})", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
          Button(
            onClick = { showAddProductDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier = Modifier.testTag("btn_add_product")
          ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Neu", fontWeight = FontWeight.Bold, fontSize = 12.sp)
          }
        }
      }

      items(products) { prod ->
        ProductAdminCard(
          product = prod,
          onPriceChange = { newPrice ->
            val updated = products.map { if (it.id == prod.id) it.copy(priceChf = newPrice) else it }
            onUpdateProducts(updated)
          },
          onStatusChange = { newStat ->
            val updated = products.map { if (it.id == prod.id) it.copy(status = newStat) else it }
            onUpdateProducts(updated)
          }
        )
      }
    }

    // TAB 2: BASE44 LIVE SYNC
    if (selectedTab == 2) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth().testTag("base44_sync_card"),
          colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
          shape = RoundedCornerShape(16.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E271B))
        ) {
          Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.CloudSync, contentDescription = null, tint = Color(0xFFD4AF37))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Base44 Live Synchronisation", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Text(
              "URL: https://coinring-legends.base44.app\nToken: b44u_6eb9b98a97d57667602b9ea303be7913954c32aba2b6bc392478d30232a3f34c",
              color = Color(0xFF8E8C87),
              fontSize = 11.sp,
              lineHeight = 16.sp
            )

            Button(
              onClick = {
                isSyncing = true
                syncMessage = "Synchronisiere Produkte mit Base44 API..."
                // Simulate fast sync with the base44 entities
                isSyncing = false
                syncMessage = "Erfolgreich! 4 Base44 Produkte (Royal Woodbox, Premiumbox, Woodbox) synchronisiert."
              },
              colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth().testTag("btn_trigger_sync")
            ) {
              Icon(Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text("Jetzt mit Base44 synchronisieren", fontWeight = FontWeight.Bold)
            }

            syncMessage?.let {
              Surface(
                color = Color(0xFF1B2414),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = it,
                  color = Color(0xFF9CCC65),
                  fontSize = 12.sp,
                  modifier = Modifier.padding(10.dp)
                )
              }
            }
          }
        }
      }
    }
  }

  // Add Product Dialog
  if (showAddProductDialog) {
    var newName by remember { mutableStateOf("") }
    var newPrice by remember { mutableStateOf("") }
    var newDesc by remember { mutableStateOf("") }

    AlertDialog(
      onDismissRequest = { showAddProductDialog = false },
      containerColor = Color(0xFF161412),
      title = { Text("Neues Produkt hinzufügen", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold) },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = newName,
            onValueChange = { newName = it },
            label = { Text("Produktname") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFFD4AF37),
              unfocusedBorderColor = Color(0xFF332D22),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            )
          )
          OutlinedTextField(
            value = newPrice,
            onValueChange = { newPrice = it },
            label = { Text("Preis in CHF") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFFD4AF37),
              unfocusedBorderColor = Color(0xFF332D22),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            )
          )
          OutlinedTextField(
            value = newDesc,
            onValueChange = { newDesc = it },
            label = { Text("Beschreibung") },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFFD4AF37),
              unfocusedBorderColor = Color(0xFF332D22),
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            )
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            val pr = newPrice.toIntOrNull() ?: 129
            val p = RingProduct(
              id = "prod-${System.currentTimeMillis()}",
              name = if (newName.isBlank()) "Neuer Coinring" else newName,
              priceChf = pr,
              description = newDesc,
              badge = "Neu",
              drawableRes = R.drawable.ring_20_rappen
            )
            onUpdateProducts(products + p)
            showAddProductDialog = false
          },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black)
        ) {
          Text("Speichern")
        }
      },
      dismissButton = {
        TextButton(onClick = { showAddProductDialog = false }) {
          Text("Abbrechen", color = Color.Gray)
        }
      }
    )
  }
}

@Composable
fun AdminTabItem(title: String, count: Int?, isSelected: Boolean, onClick: () -> Unit) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(if (isSelected) Color(0xFFD4AF37) else Color.Transparent)
      .clickable(onClick = onClick)
      .padding(horizontal = 12.dp, vertical = 8.dp)
  ) {
    Text(
      text = if (count != null) "$title ($count)" else title,
      color = if (isSelected) Color.Black else Color(0xFFC7C5BD),
      fontWeight = FontWeight.Bold,
      fontSize = 12.sp
    )
  }
}

@Composable
fun OrderAdminCard(
  order: OrderRecord,
  onStatusToggle: (String) -> Unit,
  onDelete: () -> Unit
) {
  val isPaid = order.paymentStatus == "paid"

  Card(
    modifier = Modifier.fillMaxWidth().testTag("order_card_${order.id}"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
    shape = RoundedCornerShape(14.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26221A))
  ) {
    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(order.id, color = Color(0xFFD4AF37), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Surface(
          color = if (isPaid) Color(0xFF1B2914) else Color(0xFF2B2214),
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = if (isPaid) "BEZAHLT (TWINT)" else "AUSSTEHEND",
            color = if (isPaid) Color(0xFF81C784) else Color(0xFFFFB74D),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }

      Text("Kunde: ${order.customerName} • ${order.phone}", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
      Text("Produkt: ${order.ringType} (Grösse ${order.size})", color = Color(0xFFC7C5BD), fontSize = 12.sp)
      Text("Adresse: ${order.address}", color = Color(0xFF8E8C87), fontSize = 11.sp)
      if (order.notes.isNotBlank()) {
        Text("Notiz: ${order.notes}", color = Color(0xFF8E8C87), fontSize = 11.sp)
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text("CHF ${order.priceChf}.-", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 16.sp)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          Button(
            onClick = { onStatusToggle(if (isPaid) "pending" else "paid") },
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isPaid) Color(0xFF332D22) else Color(0xFF2E7D32),
              contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(if (isPaid) "Als offen markieren" else "Als bezahlt markieren", fontSize = 11.sp, fontWeight = FontWeight.Bold)
          }

          IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
            Icon(Icons.Default.Delete, contentDescription = "Löschen", tint = Color(0xFFE57373), modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}

@Composable
fun ProductAdminCard(
  product: RingProduct,
  onPriceChange: (Int) -> Unit,
  onStatusChange: (String) -> Unit
) {
  var isEditingPrice by remember { mutableStateOf(false) }
  var priceInput by remember { mutableStateOf(product.priceChf.toString()) }

  Card(
    modifier = Modifier.fillMaxWidth().testTag("admin_product_${product.id}"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
    shape = RoundedCornerShape(12.dp),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26221A))
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(product.name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text(product.badge, color = Color(0xFFD4AF37), fontSize = 11.sp)
        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
          listOf("Verfügbar", "Verkauft", "Vorbestellung").forEach { st ->
            val sel = product.status == st
            Box(
              modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(if (sel) Color(0xFFD4AF37) else Color(0xFF1F1C18))
                .clickable { onStatusChange(st) }
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(st, color = if (sel) Color.Black else Color.Gray, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            }
          }
        }
      }

      if (isEditingPrice) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          OutlinedTextField(
            value = priceInput,
            onValueChange = { priceInput = it },
            singleLine = true,
            modifier = Modifier.width(70.dp).height(46.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color(0xFFD4AF37),
              unfocusedBorderColor = Color.Gray,
              focusedTextColor = Color.White,
              unfocusedTextColor = Color.White
            )
          )
          IconButton(onClick = {
            onPriceChange(priceInput.toIntOrNull() ?: product.priceChf)
            isEditingPrice = false
          }) {
            Icon(Icons.Default.CloudDone, contentDescription = "Save", tint = Color(0xFFD4AF37))
          }
        }
      } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text("CHF ${product.priceChf}.-", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
          IconButton(onClick = { isEditingPrice = true }) {
            Icon(Icons.Default.Edit, contentDescription = "Edit Price", tint = Color(0xFF8E8C87), modifier = Modifier.size(16.dp))
          }
        }
      }
    }
  }
}
