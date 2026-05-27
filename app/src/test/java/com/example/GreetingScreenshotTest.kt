package com.example

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryFull
import androidx.compose.material.icons.rounded.SignalCellular4Bar
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [33])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        androidx.compose.material3.Card(
          modifier = androidx.compose.ui.Modifier
            .padding(16.dp)
            .fillMaxWidth()
        ) {
          Column(
            modifier = androidx.compose.ui.Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
          ) {
            Text(
              text = "CandyNodes Game Panel",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.Bold,
              color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
            Text(
              text = "Official general server node panel",
              style = MaterialTheme.typography.bodyMedium,
              color = MaterialTheme.colorScheme.onSurface
            )
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun app_launcher_homepage_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier
            .size(width = 360.dp, height = 640.dp),
          color = Color(0xFF07050D)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.verticalGradient(
                  colors = listOf(
                    Color(0xFF0F0B1E),
                    Color(0xFF07050D)
                  )
                )
              )
          ) {
            // Simulated Status Bar
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "12:00",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
              Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Rounded.SignalCellular4Bar,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
                Icon(
                  imageVector = Icons.Rounded.Wifi,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
                Icon(
                  imageVector = Icons.Rounded.BatteryFull,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(14.dp)
                )
              }
            }

            // Simulated App Icon on Homepage (Centered)
            Column(
              modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 80.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              // App Icon with cosmic background gradient
              Box(
                modifier = Modifier
                  .size(80.dp)
                  .clip(RoundedCornerShape(22.dp))
                  .background(
                    Brush.linearGradient(
                      colors = listOf(
                        Color(0xFF0C0A15),
                        Color(0xFF1A1530)
                      )
                    )
                  )
                  .border(1.5.dp, Color(0xFF2C2250), RoundedCornerShape(22.dp))
                  .padding(8.dp),
                contentAlignment = Alignment.Center
              ) {
                Image(
                  painter = painterResource(id = R.drawable.img_candynodes_logo_1779839976847),
                  contentDescription = "CandyNodes Icon",
                  modifier = Modifier.fillMaxSize()
                )
              }
              
              Spacer(modifier = Modifier.height(12.dp))
              
              Text(
                text = "CandyNodes",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
              )
              Text(
                text = "Game Panel",
                color = Color(0xFFA099C0),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
              )
            }

            // Stylized system tray background grid preview at bottom
            Box(
              modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .fillMaxWidth()
                .height(84.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(Color.White.copy(alpha = 0.04f))
                .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(28.dp)),
              contentAlignment = Alignment.Center
            ) {
              Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
              ) {
                // System apps icons placeholders
                repeat(4) { index ->
                  Box(
                    modifier = Modifier
                      .size(48.dp)
                      .clip(CircleShape)
                      .background(
                        if (index == 2) Color(0xFF1E3A8A).copy(alpha = 0.4f)
                        else Color.White.copy(alpha = 0.08f)
                      )
                      .border(0.5.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                    contentAlignment = Alignment.Center
                  ) {
                    if (index == 2) {
                      // Draw a mini version of our app launcher in the doc!
                      Image(
                        painter = painterResource(id = R.drawable.img_candynodes_logo_1779839976847),
                        contentDescription = "CandyNodes Mini Doc Icon",
                        modifier = Modifier.size(28.dp)
                      )
                    }
                  }
                }
              }
            }
          }
        }
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/launcher_homepage.png")
  }
}
