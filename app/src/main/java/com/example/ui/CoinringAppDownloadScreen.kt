package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R

@Composable
fun CoinringAppDownloadScreen() {
  val context = LocalContext.current
  val githubPagesUrl = "https://cyberchatx.github.io/coinring-legends/"
  val apkDownloadUrl = "https://cyberchatx.github.io/coinring-legends/app-release.apk"
  val base44Url = "https://coinring-legends.base44.app"
  val qrCodeUrl = "https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=$githubPagesUrl"

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(Color(0xFF0A0A0A)),
    contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 100.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
          painter = painterResource(id = R.drawable.logo),
          contentDescription = "Logo",
          modifier = Modifier
            .size(70.dp)
            .clip(CircleShape)
            .border(2.dp, Color(0xFFD4AF37), CircleShape)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text("APP & WEB-VERSION", color = Color(0xFFF1D06A), fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text("Installierbar als native Android APK & Offline PWA", color = Color(0xFFA19F9A), fontSize = 12.sp)
      }
    }

    // Download APK Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("card_download_apk"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E271B))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Download, contentDescription = null, tint = Color(0xFFD4AF37))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Als Android APK herunterladen", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          Text(
            "Lade die eigenständige Installationsdatei herunter. Funktioniert komplett ohne Google Play Store. 'Unbekannte Quellen' bei Installation erlauben.",
            color = Color(0xFFC7C5BD),
            fontSize = 12.sp
          )
          Button(
            onClick = {
              val intent = Intent(Intent.ACTION_VIEW, Uri.parse(apkDownloadUrl))
              context.startActivity(intent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37), contentColor = Color.Black),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth().testTag("btn_download_apk")
          ) {
            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("app-release.apk herunterladen", fontWeight = FontWeight.Bold)
          }
        }
      }
    }

    // QR Code for Mobile Scanning
    item {
      Card(
        modifier = Modifier.fillMaxWidth().testTag("card_qr_code"),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E271B))
      ) {
        Column(
          modifier = Modifier.fillMaxWidth().padding(18.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.QrCode, contentDescription = null, tint = Color(0xFFD4AF37))
            Spacer(modifier = Modifier.width(8.dp))
            Text("QR-Code für Mobilgeräte", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 15.sp)
          }
          Box(
            modifier = Modifier
              .size(160.dp)
              .background(Color.White, RoundedCornerShape(12.dp))
              .padding(8.dp),
            contentAlignment = Alignment.Center
          ) {
            AsyncImage(
              model = qrCodeUrl,
              contentDescription = "QR Code",
              modifier = Modifier.fillMaxSize()
            )
          }
          Text(
            "Scanne den Code mit deinem Smartphone, um die Coinring Legends Web-App & APK direkt zu öffnen.",
            color = Color(0xFF8E8C87),
            fontSize = 11.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
        }
      }
    }

    // Web Links
    item {
      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
          onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(githubPagesUrl)))
          },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD4AF37)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF443B2A)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("btn_open_github_pages")
        ) {
          Icon(Icons.Default.Language, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Im Browser nutzen (GitHub Pages)")
        }

        OutlinedButton(
          onClick = {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(base44Url)))
          },
          colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD4AF37)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF443B2A)),
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.fillMaxWidth().testTag("btn_open_base44")
        ) {
          Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Base44 Webshop öffnen")
        }
      }
    }

    // About Atelier Location
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF141210)),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26221A))
      ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color(0xFFD4AF37), modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("ATELIER & STANDORT", color = Color(0xFFF1D06A), fontWeight = FontWeight.Bold, fontSize = 13.sp)
          }
          Text("COINRING LEGENDS — Köbi Bissegger", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
          Text("Zuswil 9, 6217 Kottwil (Gemeinde Ettiswil)\nKanton Luzern, Schweiz", color = Color(0xFFC7C5BD), fontSize = 12.sp)
          Text("WhatsApp: +41 79 833 93 87\nE-Mail: Coinringlegends@gmail.com", color = Color(0xFFD4AF37), fontSize = 12.sp)
        }
      }
    }
  }
}
